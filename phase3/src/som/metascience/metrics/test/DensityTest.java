package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.Density;

import java.io.File;

import static org.junit.Assert.*;

public class DensityTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateGraphDensity() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE);
        Density d = new Density(metricData);

        Double result = Double.valueOf(d.calculateGraphDensity(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(0.049, roundedResult.doubleValue(), 0.0);

    }
}