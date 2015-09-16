package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.GraphModularity;

import java.io.File;

import static org.junit.Assert.*;

public class GraphModularityTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");
    public static final File TEST_FILE_2 = new File("testData/importData/International-Conference-on-Model-Driven-Engineering-Languages-and-Systems.properties");

    @Test
    public void testCalcualteGraphModularity1() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        GraphModularity gm = new GraphModularity(metricData);

        Integer result = Integer.valueOf(gm.calcualteGraphModularity(metricData.getFullGraph()));
        assertEquals(22, result.intValue());
    }

    @Test
    public void testCalcualteGraphModularity2() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_2);
        GraphModularity gm = new GraphModularity(metricData);

        Integer result = Integer.valueOf(gm.calcualteGraphModularity(metricData.getFullGraph()));
        assertEquals(106, result.intValue());
    }
}