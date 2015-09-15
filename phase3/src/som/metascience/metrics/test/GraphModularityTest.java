package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.GraphModularity;

import java.io.File;

import static org.junit.Assert.*;

public class GraphModularityTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalcualteGraphModularity() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE);
        GraphModularity gm = new GraphModularity(metricData);

        Integer result = Integer.valueOf(gm.calcualteGraphModularity(metricData.getFullGraph()));
        assertEquals(22, result.intValue());

    }
}