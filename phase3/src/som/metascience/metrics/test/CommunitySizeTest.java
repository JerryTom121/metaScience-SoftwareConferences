package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.CommunitySize;
import som.metascience.metrics.ProminentFigures;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class CommunitySizeTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");

    @Test
    public void testCalculateCommunitySize() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        CommunitySize pf = new CommunitySize(metricData);

        Integer result = Integer.valueOf(pf.calculateCommunitySize(metricData.getFullGraph()));
        assertEquals(78, result.intValue());
    }
}