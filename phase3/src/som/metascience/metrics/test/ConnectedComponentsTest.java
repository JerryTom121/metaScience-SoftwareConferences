package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.ConnectedComponents;

import java.io.File;

import static org.junit.Assert.*;

public class ConnectedComponentsTest extends MetricTest {
    public static final File TEST_FILE = new File("testData/graphs/OCL@Models.gexf");

    @Test
    public void testCalculateGraphComponents() throws Exception {
        MetricData md = buildTestMetricData();
        ConnectedComponents cc = new ConnectedComponents(md);

        Integer result = Integer.valueOf(cc.calculateGraphComponents(new File(TEST_FILE.getCanonicalPath())));
        assertEquals(10, result.intValue());
    }
}