package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.Density;

import java.io.File;

import static org.junit.Assert.*;

public class DensityTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");
    public static final File TEST_FILE_2 = new File("testData/importData/International-Conference-on-Model-Driven-Engineering-Languages-and-Systems.properties");

    @Test
    public void testCalculateGraphDensity1() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        Density d = new Density(metricData);

        Double result = Double.valueOf(d.calculateGraphDensity(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(0.049, roundedResult.doubleValue(), 0.0);
    }


    @Test
    public void testCalculateGraphDensity2() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_2);
        Density d = new Density(metricData);

        Double result = Double.valueOf(d.calculateGraphDensity(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(0.007, roundedResult.doubleValue(), 0.0);
    }
}