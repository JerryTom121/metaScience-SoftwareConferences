package som.metascience;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
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
import java.util.*;

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

    public static final int SIZE = 3;

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
        if(confPath == null || !confPath.exists() || !confPath.isDirectory())
            throw new IllegalArgumentException("The configuration path has to exists and be a directory");
        if(outputPath == null || !outputPath.exists() || !outputPath.isDirectory())
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

                // Checking with the last file analyzed
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

                        String name = file.getName().replaceFirst("[.][^.]+$", "");
                        String fullName = properties.getProperty("conferenceName");
                        String fullNodes = properties.getProperty("fullNodesLast5Editions");
                        String fullEdges = properties.getProperty("fullEdgesLast5Editions");

                        GraphImportData importData = new GraphImportData(name, fullName, file, fullNodes, fullEdges);

                        // Retrieving data for specific editions

                        String editions = properties.getProperty("editionQueries");
                        if(editions != null) {
                            String editionList[] = editions.split(",");
                            int counter = 0;
                            for(String edition : editionList) {
                                String nodesKey = "SingleEditionLast5Editions" + edition + "Nodes";
                                String edgesKey = "SingleEditionLast5Editions" + edition + "Edges";
                                if(properties.containsKey(nodesKey) && properties.containsKey(edgesKey)) {
                                    String editionNodes = properties.getProperty(nodesKey);
                                    String editionEdges = properties.getProperty(edgesKey);
                                    importData.addEditionQuery(editionNodes, editionEdges);
                                    counter++;
                                    if(counter == 5) break;
                                }
                            }
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

            File reducedfullGraph = new File(outputPath.getAbsolutePath() + File.separator + importData.getName() + "_reduced_" + EXTENSION);
            generateReducedGraph(importData.getFullNodesQuery(), importData.getFullEdgesQuery(), SIZE, reducedfullGraph);

            // Exporting editions
            int edition = 0;
            for(; edition < importData.getEditions(); edition++) {
                String editionNodes = importData.getEditionNodesQuery(edition);
                String editionEdges = importData.getEditionEdgesQuery(edition);

                File graph = new File(outputPath.getAbsolutePath() + File.separator + importData.getName() + (edition+1) + EXTENSION);
                generateGraph(editionNodes, editionEdges, graph);

                File reducedGraph = new File(outputPath.getAbsolutePath() + File.separator + importData.getName() + "_reduced_" + (edition+1) + EXTENSION);
                generateReducedGraph(editionNodes, editionEdges, SIZE, reducedGraph);
            }
            logger.log("Added graph for " + importData.getName() + " and " + edition + " editions");
            logger.control(importData.getSource().getAbsolutePath());
        }
    }

    /**
     * Uses Gephi to create a graph and serializes it as a GEXF
     * @param nodes The query to get the nodes from the database
     * @param edges The query to get the edges from the database
     * @param outputPath The File path to serialize the GEXF
     */
    private void generateGraph(String nodes, String edges, File outputPath) {
        if(nodes == null || nodes.equals(""))
            throw new IllegalArgumentException("The query for nodes cannot be null or empty");
        if(edges == null || edges.equals(""))
            throw new IllegalArgumentException("The query for nodes cannot be null or empty");
        if(outputPath == null)
            throw new IllegalArgumentException("The output file cannot be null");

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
        container.setAutoScale(false);
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

    /**
     * Uses Gephi to create a graph and serializes it as a GEXF. Removes components smaller than a specific
     * size
     * @param nodes The query to get the nodes from the database
     * @param edges The query to get the edges from the database
     * @param size The size for the components (they must be bigger than this number)
     * @param outputPath The File path to serialize the GEXF
     */
    private void generateReducedGraph(String nodes, String edges, int size, File outputPath) {
        if(nodes == null || nodes.equals(""))
            throw new IllegalArgumentException("The query for nodes cannot be null or empty");
        if(edges == null || edges.equals(""))
            throw new IllegalArgumentException("The query for nodes cannot be null or empty");
        if(outputPath == null)
            throw new IllegalArgumentException("The output file cannot be null");

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
        container.setAutoScale(false);
        container.setAllowAutoNode(false);  // Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force UNDIRECTED

        // Append imported data to GraphAPI
        ic.process(container, new DefaultProcessor(), workspace);

        // Graph Connected Components
        GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();
        org.gephi.statistics.plugin.ConnectedComponents cc = new org.gephi.statistics.plugin.ConnectedComponents();
        cc.execute(gm, am);

        NodeIterable ni = gm.getGraph().getNodes();
        HashMap<Integer, List<String>> components = new HashMap<>();
        for(Node node : ni.toArray()) {
            Attributes atts = node.getAttributes();
            Integer componentNumber = (Integer) atts.getValue("componentnumber");
            List<String> nodeList = components.get(componentNumber);
            if(nodeList == null) {
                nodeList = new ArrayList<String>();
                components.put(componentNumber, nodeList);
            }
            String nodeId = (String) atts.getValue("id");
            nodeList.add(nodeId);
        }

        List<Node> nodes2remove = new ArrayList<>();
        for(Integer key : components.keySet()) {
            List<String> nodeList = components.get(key);
            if(nodeList.size() <= size) {
                for(String nodeId : nodeList) {
                    Node node = gm.getGraph().getNode(nodeId);
                    nodes2remove.add(node);
                }
            }
        }

        for(Node node : nodes2remove) {
            gm.getGraph().removeNode(node);
        }

        // Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(outputPath);
        } catch (IOException ex) {
            logger.log("Error serializing at " + outputPath);
        }

    }
}

