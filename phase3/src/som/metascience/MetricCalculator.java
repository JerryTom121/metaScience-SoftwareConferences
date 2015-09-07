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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Metric calculator
 */
public class MetricCalculator {
    public static final String EXTENSION = ".gexf";

    private File confPath;
    private File inputGraphs;
    private File outputPath;

    private Phase3Logger logger;

    public MetricCalculator(File confPath, File inputGraphs, File outputPath, Phase3Logger logger) {
        if(!confPath.exists() || !confPath.isDirectory())
            throw new IllegalArgumentException("The configuration path has to exists and be a directory");
        if(inputGraphs == null || !inputGraphs.exists() || !inputGraphs.isDirectory())
            throw new IllegalArgumentException("The input graph path has to exists and be a directory");
        if(outputPath == null)
            throw new IllegalArgumentException("The output path cannot be null");
        if(logger == null)
            throw new IllegalArgumentException("The logger cannot be null");

        this.outputPath = outputPath;
        this.inputGraphs = inputGraphs;
        this.confPath = confPath;
        this.logger = logger;
    }

    public void execute() {
        List<MetricData> metricData = getMetricData();
        String result = calculateMetrics(metricData);

        String firstLine = "ConfName,AvgDegree_allEditions,AvgDegree_1_LastEdition,AvgDegree_2_LastEdition,AvgDegree_3_LastEdition,AvgDegree_4_LastEdition,AvgDegree_5_LastEdition\n";
        try {
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(firstLine.getBytes());
            fos.write(result.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            logger.log("Error when storing results");
        }
    }

    private List<MetricData> getMetricData() {
        List<MetricData> metricDataList = new ArrayList<MetricData>();

        for(File file : this.confPath.listFiles()) {
            if(file.getName().endsWith(".properties")) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file));

                    String name = properties.getProperty("conferenceName");
                    String rank = properties.getProperty("rank");

                    // Retrieving data for specific editions

                    int counter = 1;
                    String nodesKey = "edition" + counter + "Nodes";
                    String edgesKey = "edition" + counter + "Edges";
                    while(properties.containsKey(nodesKey) && properties.containsKey(edgesKey)) {
                        counter++;
                        nodesKey = "edition" + counter + "Nodes";
                        edgesKey = "edition" + counter + "Edges";
                    }
                    MetricData metricData = new MetricData(name, rank, file, counter -1);
                    metricDataList.add(metricData);
                } catch (IOException e) {
                    logger.log("! The file " + file.getAbsolutePath() + " could not be loaded");
                }
                logger.log("Added metric data from file " + file.getAbsolutePath());
            }
        }

        return metricDataList;
    }

    public String calculateMetrics(List<MetricData> metricDataList) {
        String result = "";
        for(MetricData metricData : metricDataList) {
            File fullGraph = new File(inputGraphs.getAbsolutePath() + File.separator + metricData.getName() + EXTENSION);

            List<File> editions = new ArrayList<File>();
            int edition = 1;
            for(; edition <= metricData.getEditions() ; edition++) {
                File graph = new File(inputGraphs.getAbsolutePath() + File.separator + metricData.getName() + edition + EXTENSION);
                editions.add(graph);
            }
            logger.log("Metrics for " + inputGraphs.getName() + " calculated");
            String metrics = calculateMetrics(fullGraph, editions);
            String line = metricData.getName() + "," + metricData.getRank() + "," + metrics;
            result = result + line + "\n";
        }
        return result;
    }

    public String calculateMetrics(File fullGraph, List<File> editions) {
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
