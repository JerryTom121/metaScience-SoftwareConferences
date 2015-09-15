package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.GraphModularity;

import java.io.File;

import static org.junit.Assert.*;

public class GraphModularityTest extends MetricTest {
    public static final File TEST_FILE = new File("../testData/graphs/OCL@Models.gexf");

    @Test
    public void testCalcualteGraphModularity() throws Exception {
        MetricData md = buildTestMetricData();
        GraphModularity gm = new GraphModularity(md);

        Integer result = Integer.valueOf(gm.calcualteGraphModularity(new File(TEST_FILE.getCanonicalPath())));
        assertEquals(12, result.intValue());

    }
}