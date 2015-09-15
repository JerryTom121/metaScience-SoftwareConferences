package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AveragePathLength;

import java.io.File;

import static org.junit.Assert.*;

public class AveragePathLengthTest extends MetricTest {
    public static final File TEST_FILE = new File("../testData/graphs/OCL@Models.gexf");

    @Test
    public void testCalculateGraphDistance() throws Exception {
        MetricData md = buildTestMetricData();
        AveragePathLength apl = new AveragePathLength(md);

        Double result = Double.valueOf(apl.calculateGraphDistance(new File(TEST_FILE.getCanonicalPath())));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(2.017, roundedResult.doubleValue(), 0.0);
    }
}