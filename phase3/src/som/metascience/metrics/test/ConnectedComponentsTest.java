package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.ConnectedComponents;

import java.io.File;

import static org.junit.Assert.*;

public class ConnectedComponentsTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateGraphComponents() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE);
        ConnectedComponents cc = new ConnectedComponents(metricData);

        Integer result = Integer.valueOf(cc.calculateGraphComponents(metricData.getFullGraph()));
        assertEquals(19, result.intValue());
    }
}