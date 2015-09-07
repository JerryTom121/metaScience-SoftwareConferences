package som.metascience;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.database.drivers.MySQLDriver;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class connects to MySQL and creates a Gephi graph.
 *
 * The importer reads the properties files located in conf folder. Each file represents
 * a conference and indicate the name, the queries to get the full graph and the set of queries to get previous
 * editions (cf. properties files to learn what to put).
 *
 * For each conference, a new graph file is generated for the full period of editions and then one more per edition
 * (if indicated in the property file). The output graphs will be stored in output folder
 *
 */
public class GraphImporter {
    public static final String EXTENSION = ".gexf";

    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPass;
    private int dbPort;

    private File confPath;
    private File outputPath;

    private Phase2Logger logger;

    public GraphImporter(String dbHost, String dbName, String dbUser, String dbPass, int dbPort, File confPath, File outputPath, Phase2Logger logger) {
        if(dbHost == null || dbHost.equals("") || dbName == null || dbName.equals("") || dbUser == null || dbUser.equals("") || dbPass == null || dbPass.equals("") || dbPort < 0 || dbPort > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The database configuration data is not correct");
        if(!confPath.exists() || !confPath.isDirectory())
            throw new IllegalArgumentException("The configuration path has to exists and be a directory");
        if(!outputPath.exists() || !outputPath.isDirectory())
            throw new IllegalArgumentException("The output path has to exists and be a directory");
        if(logger == null)
            throw new IllegalArgumentException("The logger cannot be null");

        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbPort = dbPort;

        this.confPath = confPath;
        this.outputPath = outputPath;

        this.logger = logger;
    }

    public void execute() {
        logger.log("Starting importer at " + new Timestamp((new Date()).getTime()));
        List<GraphImportData> importData = getImportData();
        generateGraphs(importData);
        logger.log("Finished at " + new Timestamp((new Date()).getTime()));
    }

    /**
     * Obtains the importation data configuration. Note that there a check on the last files analyzed
     * @return A list with the import data
     */
    private List<GraphImportData> getImportData() {
        List<GraphImportData> importDataList = new ArrayList<GraphImportData>();

        // We check if there was a file analyzed before
        // If so, we log and we will skip every file until we found the last checked
        String lastControl = logger.lastControl();
        if(lastControl != null)
            logger.log("Last control found, skipping until [" + lastControl+ "]");

        for(File file : confPath.listFiles()) {
            if(file.getName().endsWith(".properties")) {

                // Cheking with the last file analyzed
                if(lastControl != null && !file.getAbsolutePath().equals(lastControl)) {
                    // The last file is not the last one and therefore was already analyzed
                    logger.log("Skipping [" + file.getAbsolutePath() + "]");
                    continue;
                } else if (lastControl != null && file.getAbsolutePath().equals(lastControl)) {
                    // The last file is last one and therefore the last one analyzed, we log
                    logger.log("Control found [" + file.getAbsolutePath() + "]");
                    lastControl = null;
                    continue;
                }

                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file));

                    String name = properties.getProperty("conferenceName");
                    String fullNodes = properties.getProperty("fullNodes");
                    String fullEdges = properties.getProperty("fullEdges");

                    GraphImportData importData = new GraphImportData(name, file, fullNodes, fullEdges);

                    // Retrieving data for specific editions
                    int counter = 1;
                    String nodesKey = "edition" + counter + "Nodes";
                    String edgesKey = "edition" + counter + "Edges";
                    while(properties.containsKey(nodesKey) && properties.containsKey(edgesKey)) {
                        String editionNodes = properties.getProperty(nodesKey);
                        String editionEdges = properties.getProperty(edgesKey);
                        importData.addEditionQuery(editionNodes, editionEdges);

                        counter++;
                        nodesKey = "edition" + counter + "Nodes";
                        edgesKey = "edition" + counter + "Edges";
                    }

                    importDataList.add(importData);
                } catch (IOException e) {
                    logger.log("! The file " + file.getAbsolutePath() + " could not be loaded");
                }
                logger.log("Added import data from file " + file.getAbsolutePath());
            }
        }

        return importDataList;
    }

    /**
     * Generates the graphs
     * @param importDataList The importation data
     */
    private void generateGraphs(List<GraphImportData> importDataList) {
        for(GraphImportData importData : importDataList) {
            File fullGraph = new File(outputPath.getAbsolutePath() + File.separator + importData.getName() + EXTENSION);
            generateGraph(importData.getFullNodesQuery(), importData.getFullEdgesQuery(), fullGraph);

            // Exporting editions
            int edition = 1;
            for(; edition < importData.getEditions(); edition++) {
                String editionNodes = importData.getEditionNodesQuery(edition);
                String editionEdges = importData.getEditionEdgesQuery(edition);

                File graph = new File(outputPath.getAbsolutePath() + File.separator + importData.getName() + edition + EXTENSION);
                generateGraph(editionNodes, editionEdges, graph);
            }
            logger.log("Added graph for " + importData.getName() + " and " + (edition-1) + " editions");
            logger.control(importData.getSource().getAbsolutePath());
        }
    }


    private void generateGraph(String nodes, String edges, File outputPath) {
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setDBName(dbName);
        db.setHost(dbHost);
        db.setUsername(dbUser);
        db.setPasswd(dbPass);
        db.setSQLDriver(new MySQLDriver());
        db.setPort(dbPort);

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        db.setNodeQuery(nodes);
        db.setEdgeQuery(edges);

        // Importing full graph
        ImportController ic = Lookup.getDefault().lookup(ImportController.class);
        Container container = ic.importDatabase(db, new ImporterEdgeList());
        container.setAllowAutoNode(false);  // Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force UNDIRECTED

        // Append imported data to GraphAPI
        ic.process(container, new DefaultProcessor(), workspace);

        // Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(outputPath);
        } catch (IOException ex) {
            logger.log("Error serializing at " + outputPath);
        }

    }
}

