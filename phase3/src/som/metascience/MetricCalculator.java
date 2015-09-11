package som.metascience;

import com.sun.javafx.geom.transform.BaseTransform;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Degree;
import org.openide.util.Lookup;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Metric calculator
 */
public class MetricCalculator {
    public static final String EXTENSION = ".gexf";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private File confPath;
    private File inputGraphs;
    private File outputPath;

    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPass;
    private int dbPort;
    private Connection conn;

    private Phase3Logger logger;

    public MetricCalculator(String dbHost, String dbName, String dbUser, String dbPass, int dbPort, File confPath, File inputGraphs, File outputPath, Phase3Logger logger) {
        if(!confPath.exists() || !confPath.isDirectory())
            throw new IllegalArgumentException("The configuration path has to exists and be a directory");
        if(inputGraphs == null || !inputGraphs.exists() || !inputGraphs.isDirectory())
            throw new IllegalArgumentException("The input graph path has to exists and be a directory");
        if(outputPath == null)
            throw new IllegalArgumentException("The output path cannot be null");
        if(logger == null)
            throw new IllegalArgumentException("The logger cannot be null");

        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbPort = dbPort;

        this.conn = getConnection();

        this.outputPath = outputPath;
        this.inputGraphs = inputGraphs;
        this.confPath = confPath;
        this.logger = logger;
    }

    private Connection getConnection() {

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.dbUser);
            connectionProps.put("password", this.dbPass);

            conn = DriverManager.getConnection("jdbc:mysql://" + this.dbHost + ":" + String.valueOf(this.dbPort) + "/" + this.dbName, connectionProps);
            System.out.println("Connected to database");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * Executes the metrics calculation. First gather the info from the property files, then execute the metrics and
     * finally generates the CSV file
     */
    public void execute() {
        List<MetricData> metricData = getMetricData();

        String firstLine = "ConfName,Rank,AvgDegree_allEditions,AvgDegree_1_LastEdition,AvgDegree_2_LastEdition,AvgDegree_3_LastEdition,AvgDegree_4_LastEdition,AvgDegree_5_LastEdition\n";
        writeResult(firstLine);

        calculateMetrics(metricData);
    }

    public void writeResult(String msg) {
        try {
            FileWriter fw = new FileWriter(outputPath, true);
            fw.write(msg);
            fw.close();
        } catch (Exception e) {
            logger.log("Error when storing results");
        }
    }

    /**
     * Traverses the directory with property files to know the conference names (and ranks).
     * @return List with metric data
     */
    private List<MetricData> getMetricData() {
        List<MetricData> metricDataList = new ArrayList<MetricData>();

        for(File file : this.confPath.listFiles()) {
            if(file.getName().endsWith(".properties")) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file));

                    String name = file.getName().replaceFirst("[.][^.]+$", "");
                    String fullName = properties.getProperty("conferenceName");
                    String rank = properties.getProperty("rank");

                    // Retrieving data for specific editions
                    List<String> editions = Arrays.asList(properties.getProperty("editionQueries").split(","));

                    String sourceInfo = properties.getProperty("sources");
                    String sourceIdInfo = properties.getProperty("source_ids");

                    List<Integer> listEditions = new LinkedList<Integer>();
                    for (String edition : editions)
                        listEditions.add(Integer.parseInt(edition));


                    MetricData metricData = new MetricData(name, fullName, rank, file, sourceInfo, sourceIdInfo, listEditions);
                    metricDataList.add(metricData);
                } catch (IOException e) {
                    logger.log("! The file " + file.getAbsolutePath() + " could not be loaded");
                }
                logger.log("Added metric data from file " + file.getAbsolutePath());
            }
        }

        return metricDataList;
    }

    public void calculateMetrics(List<MetricData> metricDataList) {
        for(MetricData metricData : metricDataList) {
            File fullGraph = new File(inputGraphs.getAbsolutePath() + File.separator + metricData.getName() + EXTENSION);

            List<File> editions = new ArrayList<File>();
            int edition = 1;
            for(; edition <= metricData.getEditions().size() ; edition++) {
                File graph = new File(inputGraphs.getAbsolutePath() + File.separator + metricData.getName() + edition + EXTENSION);
                editions.add(graph);
            }
            logger.log("Metrics for " + inputGraphs.getName() + " calculated");
            String graphMetrics = calculateGraphMetrics(fullGraph, editions);

            String sqlMetrics = calculateSQLMetrics(metricData.getSourceInfo(), metricData.getSourceIdInfo(), metricData.getEditions());
            //TODO integrage sql metrics
            String line = metricData.getName() + "," + metricData.getRank() + "," + graphMetrics + "\n";
            writeResult(line);
        }
    }

    /**
     * Main method calculating the metrics (maybe in the future we should split it)
     * @param fullGraph Path to the main graph
     * @param editions List of paths to the edition graph (should be of size 5)
     * @return A CSV-formatted string
     */
    public String calculateGraphMetrics(File fullGraph, List<File> editions) {
        if(fullGraph == null)
            throw new IllegalArgumentException("A full graph has to be provided");
        if(editions == null || editions.size() < 4)
            throw new IllegalArgumentException("At least 5 editions have to be provided");

        String avgDegreeFull = calculateAverageDegree(fullGraph);
        String avgDegreeEdition1 = calculateAverageDegree(editions.get(0));
        String avgDegreeEdition2 = calculateAverageDegree(editions.get(1));
        String avgDegreeEdition3 = calculateAverageDegree(editions.get(2));
        String avgDegreeEdition4 = calculateAverageDegree(editions.get(3));
        String avgDegreeEdition5 = calculateAverageDegree(editions.get(4));


        return avgDegreeFull + "," + avgDegreeEdition1 + "," + avgDegreeEdition2 + "," + avgDegreeEdition3 + "," + avgDegreeEdition4 + "," + avgDegreeEdition5;
    }


    public String calculateSQLMetrics(String source, String source_id, List<Integer> editions) {
        int allPapers = allPapersMetric(source, source_id, editions);
        int allAuthors = allAuthorsMetric(source, source_id, editions);

        //TODO add other metrics calculated from MetaScience
        //look at
        //VenueActivityServlet.java for average authors, authors per year, average papers, papers per year, average values (AUTHORS per PAPER and PAPERS per AUTHOR)
        //VenueOpennessServlet.java for openness metrics
        //VenueTurnoverServlet.java for Community growth rate

        return String.valueOf(allPapers) + " " + String.valueOf(allAuthors);
    }

    private String toCommaSeparated(List<Integer> list) {
        String output = "";
        for (Integer i : list) {
            if (i == list.get(list.size()-1))
                output = output + String.valueOf(i);
            else
                output = output + String.valueOf(i) + ", ";
        }

        return output;
    }

    public int allPapersMetric(String source, String source_id, List<Integer> editions) {
        Statement stmt = null;
        ResultSet rs = null;
        int allPapers = 0;
        try {
            String allPapersQuery = "SELECT COUNT(*) as numPapers " +
                    "FROM dblp_pub_new " +
                    "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND type = 'inproceedings' AND year IN (" + toCommaSeparated(editions) + ") ;";;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(allPapersQuery);

            rs.first();
            allPapers = rs.getInt("numPapers");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return allPapers;
    }

    public int allAuthorsMetric(String source, String source_id, List<Integer> editions) {
        Statement stmt = null;
        ResultSet rs = null;
        int allAuthors = 0;
        try {
            String allAuthorsQuery = "SELECT COUNT(*) AS numAuthors " +
                                     "FROM (SELECT airn.author_id " +
                                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id " +
                                            "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND pub.type = 'inproceedings' AND pub.year IN (" + toCommaSeparated(editions) + ") " +
                                            "GROUP BY airn.author_id) AS x;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(allAuthorsQuery);

            rs.first();
            allAuthors = rs.getInt("numAuthors");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return allAuthors;
    }


    public String averageNumberAuthorsPerPaper() {
        /*
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        */
        return "a";
    }

    /**
     * Calculates the average degree of a graph
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateAverageDegree(File graph) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        // Import file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            container = importController.importFile(graph);
            container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force DIRECTED
            container.setAllowAutoNode(false);  //Don't create missing nodes
            importController.process(container, new DefaultProcessor(), workspace);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();

        // Average Degree
        Degree degree = new Degree();
        degree.execute(gm, am);
        return String.valueOf(degree.getAverageDegree());
    }

}
