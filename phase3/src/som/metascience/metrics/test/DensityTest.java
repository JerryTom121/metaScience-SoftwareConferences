package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.Density;

import java.io.File;

import static org.junit.Assert.*;

public class DensityTest extends MetricTest {
    public static final File TEST_FILE = new File("../testData/graphs/OCL@Models.gexf");

    @Test
    public void testCalculateGraphDensity() throws Exception {
        MetricData md = buildTestMetricData();
        Density d = new Density(md);

        Double result = Double.valueOf(d.calculateGraphDensity(new File(TEST_FILE.getCanonicalPath())));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(0.092, roundedResult.doubleValue(), 0.0);

    }
}