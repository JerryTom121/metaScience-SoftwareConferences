package som.metascience.metrics.test;

import som.metascience.MetricData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Common implementation for test classes
 */
public class MetricTest {
    /**
     * Builds a test MetricData with useless data.
     * DO NOT USE IT
     * @return
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
     * Rounds a Double value to two decimals
     * @param value The value to round
     * @return the rounded value
     */
    Double roundTo2Decimals(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

}
