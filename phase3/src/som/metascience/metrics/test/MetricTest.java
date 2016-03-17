package som.metascience.metrics.test;

import som.metascience.MetricData;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Common implementation for test classes
 */
public class MetricTest {
    public static final String EXTENSION = ".gexf";

    /**
     * Builds a test MetricData with useless data.
     * DO NOT USE IT in real life
     * @return Mock {@link MetricData}
     */
    MetricData buildTestMetricData() {
        List<Integer> editions = new ArrayList<Integer>();
        editions.add(new Integer(0));
        editions.add(new Integer(0));
        editions.add(new Integer(0));
        editions.add(new Integer(0));
        editions.add(new Integer(0));

        List<File> editionGraphs = new ArrayList<File>();
        editionGraphs.add(new File("test"));

        MetricData metricData = new MetricData("test", "test", "test", new File("test"), "test", "test", editions, new File("test"), editionGraphs);
        return metricData;
    }

    /**
     * Builds a {@link MetricData} for a particular proerty file
     * @param inputGraphs Where to find the co-authorship graphs
     * @param file Where to find the property file with data for a conference
     * @return The {@link MetricData}
     */
    MetricData buildMetricData(File inputGraphs, File file) {
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

    /**
     * Rounds a Double value to two decimals
     *
     * @param value The value to round
     * @return the rounded value
     */
    Double roundTo2Decimals(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

}
