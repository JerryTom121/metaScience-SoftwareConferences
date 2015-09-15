package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AverageDegree;

import java.io.File;

import static org.junit.Assert.*;

public class AverageDegreeTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateAverageDegree() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE);
        AverageDegree ad = new AverageDegree(metricData);

        Double result = Double.valueOf(ad.calculateAverageDegree(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(3.769, roundedResult.doubleValue(), 0.0);
    }
}