package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AverageDegree;

import java.io.File;

import static org.junit.Assert.*;

public class AverageDegreeTest extends MetricTest {
    public static final File TEST_FILE = new File("../testData/graphs/OCL@Models.gexf");

    @Test
    public void testCalculateAverageDegree() throws Exception {
        MetricData metricData = buildTestMetricData();
        AverageDegree ad = new AverageDegree(metricData);

        Double result = Double.valueOf(ad.calculateAverageDegree(new File(TEST_FILE.getCanonicalPath())));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(4.213, roundedResult.doubleValue(), 0.0);
    }
}