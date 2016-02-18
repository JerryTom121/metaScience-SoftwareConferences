package som.metascience.metrics;

import som.metascience.MetricData;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class MyTests {
    public static final String EXTENSION = ".gexf";
    public static final File REAL_GRAPHS = new File("data/graphs");
    public static final File TEST_FILE_3 = new File("data/importData/ACM-Conference-on-Applications-Technologies-Architectures-and-Protocols-for-Computer-Communication.properties");

    public static void main(String[] args) {
        MetricData metricData = buildMetricData(REAL_GRAPHS, TEST_FILE_3);
        AverageDegree ad = new AverageDegree(metricData);


        System.out.println(metricData.getEditionGraphs().get(0).getAbsolutePath());

        Double result = Double.valueOf(ad.calculateAverageDegree(metricData.getEditionGraphs().get(0)));
        Double roundedResult = roundTo2Decimals(result);

        System.out.println(ad.calculateAverageDegree(metricData.getEditionGraphs().get(0)));
    }

    static MetricData buildMetricData(File inputGraphs, File file) {
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
            List<File> editionGraphs = new ArrayList<File>();
            int edition = 1; File editionGraph = null;
            for (; edition <= editions.size(); edition++) {
                editionGraph = new File(inputGraphs.getAbsolutePath() + File.separator + name + edition + EXTENSION);
                editionGraphs.add(editionGraph);
            }

            // We fill until having 5
            if(editions.size() < 5) {
                for(int j = 0; j < 5 - editions.size(); j++) {
                    listEditions.add(listEditions.get(0));
                    editionGraphs.add(editionGraph);
                }
            }

            // Getting acronyms
            String sourceInfo = properties.getProperty("sources");
            String sourceIdInfo = properties.getProperty("source_ids");

            MetricData metricData = new MetricData(name, fullName, rank, file, sourceInfo, sourceIdInfo, listEditions, fullGraph, editionGraphs);
            return metricData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static Double roundTo2Decimals(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
