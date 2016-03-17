package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AveragePathLength;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Test the {@link AveragePathLength} class
 */
public class AveragePathLengthTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");
    public static final File TEST_FILE_2 = new File("testData/importData/International-Conference-on-Model-Driven-Engineering-Languages-and-Systems.properties");

    @Test
    public void testCalculateGraphDistance1() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        AveragePathLength apl = new AveragePathLength(metricData);

        Double result = Double.valueOf(apl.calculateGraphDistance(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(2.115, roundedResult.doubleValue(), 0.0);
    }

    @Test
    public void testCalculateGraphDistance2() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_2);
        AveragePathLength apl = new AveragePathLength(metricData);

        Double result = Double.valueOf(apl.calculateGraphDistance(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(4.257, roundedResult.doubleValue(), 0.0);
    }
}