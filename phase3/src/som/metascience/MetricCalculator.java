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
import som.metascience.metrics.AverageDegree;
import som.metascience.metrics.Metric;
import som.metascience.metrics.MetricBuilder;

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
    private DBInfo dbInfo;
    private Phase3Logger logger;

    public MetricCalculator(File confPath, File inputGraphs, File outputPath, DBInfo dbInfo, Phase3Logger logger) {
        if(!confPath.exists() || !confPath.isDirectory())
            throw new IllegalArgumentException("The configuration path has to exists and be a directory");
        if(inputGraphs == null || !inputGraphs.exists() || !inputGraphs.isDirectory())
            throw new IllegalArgumentException("The input graph path has to exists and be a directory");
        if(outputPath == null)
            throw new IllegalArgumentException("The output path cannot be null");
        if(dbInfo == null)
            throw new IllegalArgumentException("Database info is needed");
        if(logger == null)
            throw new IllegalArgumentException("The logger cannot be null");

        this.outputPath = outputPath;
        this.inputGraphs = inputGraphs;
        this.confPath = confPath;
        this.dbInfo = dbInfo;
        this.logger = logger;
    }


    /**
     * Executes the metrics calculation. First gather the info from the property files, then execute the metrics and
     * finally generates the CSV file
     */
    public void execute() {
        // Gathering infor for conferences
        List<MetricData> metricDataList = getMetricData();

        // Building first line
        String firstLine = "ConfName,Rank,";
        List<Class> metrics = MetricBuilder.getMetrics();
        for(Class metric : metrics) {
            String metricCategory = MetricBuilder.getCategory(metric);
            firstLine = firstLine + metricCategory + ",";
        }
        firstLine = firstLine.substring(0, firstLine.length()-1);
        firstLine = firstLine + "\n";
        writeResult(firstLine);

        // Calculating metrics for each conference
        for(MetricData metricData : metricDataList) {

            // Initializing line for the results
            String line = metricData.getName() + "," + metricData.getRank() + ",";

            // Building graph builder for metrics and then calling for each metric
            MetricBuilder metricBuilder = new MetricBuilder(metricData, dbInfo);
            for(Class metric : metrics) {
                Metric builtMetric = metricBuilder.buildMetric(metric);
                String builtMetricResult = builtMetric.getResult();
                line = line + builtMetricResult + ",";
            }
            line = line.substring(0, line.length()-1);
            line = line + "\n";
            writeResult(line);
            logger.log("Metrics for " + inputGraphs.getName() + " calculated");
        }
    }

    /**
     * Appends a new line in the result file
     * @param msg The message to append
     */
    public void writeResult(String msg) {
        if(msg == null)
            throw new IllegalArgumentException("The message cannot be null");

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

                    // Getting basic info
                    String name = file.getName().replaceFirst("[.][^.]+$", "");
                    String fullName = properties.getProperty("conferenceName");
                    String rank = properties.getProperty("rank");

                    // Retrieving data for specific editions
                    List<String> editions = Arrays.asList(properties.getProperty("editionQueries").split(","));

                    List<Integer> listEditions = new LinkedList<Integer>();
                    for (String edition : editions)
                        listEditions.add(Integer.parseInt(edition));

                    // Getting paths to graphs
                    File fullGraph = new File(inputGraphs.getAbsolutePath() + File.separator + name + EXTENSION);
                    //File fullGraph = new File(inputGraphs.getAbsolutePath() + File.separator + name + "_reduced_" + EXTENSION);
                    List<File> editionGraphs = new ArrayList<File>();
                    int edition = 1;
                    for(; edition <= editions.size() ; edition++) {
                        File editionGraph = new File(inputGraphs.getAbsolutePath() + File.separator + name + edition + EXTENSION);
                        //File editionGraph = new File(inputGraphs.getAbsolutePath() + File.separator + name + "_reduced_" + edition + EXTENSION);
                        editionGraphs.add(editionGraph);
                    }

                    // Getting acronyms
                    String sourceInfo = properties.getProperty("sources");
                    String sourceIdInfo = properties.getProperty("source_ids");

                    MetricData metricData = new MetricData(name, fullName, rank, file, sourceInfo, sourceIdInfo, listEditions, fullGraph, editionGraphs);
                    metricDataList.add(metricData);
                } catch (IOException e) {
                    logger.log("! The file " + file.getAbsolutePath() + " could not be loaded");
                }
                logger.log("Added metric data from file " + file.getAbsolutePath());
            }
        }

        return metricDataList;
    }
}
