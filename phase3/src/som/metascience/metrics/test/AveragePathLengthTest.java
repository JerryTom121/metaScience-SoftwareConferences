package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AveragePathLength;

import java.io.File;

import static org.junit.Assert.*;

public class AveragePathLengthTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateGraphDistance() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE);
        AveragePathLength apl = new AveragePathLength(metricData);

        Double result = Double.valueOf(apl.calculateGraphDistance(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(2.115, roundedResult.doubleValue(), 0.0);
    }
}