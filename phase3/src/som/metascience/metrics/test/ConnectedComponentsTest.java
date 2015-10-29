package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.ConnectedComponents;

import java.io.File;

import static org.junit.Assert.*;

public class ConnectedComponentsTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");
    public static final File TEST_FILE_2 = new File("testData/importData/International-Conference-on-Model-Driven-Engineering-Languages-and-Systems.properties");

    @Test
    public void testCalculateGraphComponents1() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        ConnectedComponents cc = new ConnectedComponents(metricData);

        Integer result = Integer.valueOf(cc.calculateGraphComponents(metricData.getFullGraph()));
        assertEquals(19, result.intValue());

        Integer result2 = Integer.valueOf(cc.calculateGraphComponents(metricData.getFullGraph(), 3));
        assertEquals(4, result2.intValue());
    }

    @Test
    public void testCalculateGraphComponents2() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_2);
        ConnectedComponents cc = new ConnectedComponents(metricData);

        Integer result = Integer.valueOf(cc.calculateGraphComponents(metricData.getFullGraph()));
        assertEquals(102, result.intValue());
    }
}