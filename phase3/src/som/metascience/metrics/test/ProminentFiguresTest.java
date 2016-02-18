package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.GraphModularity;
import som.metascience.metrics.ProminentFigures;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ProminentFiguresTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateProminentFigures() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        ProminentFigures pf = new ProminentFigures(metricData);

        Integer result = Integer.valueOf(pf.calculateProminentFigures(metricData.getFullGraph()));
        assertEquals(1, result.intValue());
    }
}
