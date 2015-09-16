package som.metascience.metrics.test;

import org.junit.Test;
import som.metascience.MetricData;
import som.metascience.metrics.AverageDegree;

import java.io.File;

import static org.junit.Assert.*;

public class AverageDegreeTest extends MetricTest {
    public static final File GRAPHS = new File("testData/graphs");
    public static final File TEST_FILE_1 = new File("testData/importData/OCL-Models.properties");
    public static final File TEST_FILE_2 = new File("testData/importData/International-Conference-on-Model-Driven-Engineering-Languages-and-Systems.properties");

    @Test
    public void testCalculateAverageDegree1() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_1);
        AverageDegree ad = new AverageDegree(metricData);

        Double result = Double.valueOf(ad.calculateAverageDegree(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(3.769, roundedResult.doubleValue(), 0.0);
    }

    @Test
    public void testCalculateAverageDegree2() throws Exception {
        MetricData metricData = buildMetricData(GRAPHS, TEST_FILE_2);
        AverageDegree ad = new AverageDegree(metricData);

        Double result = Double.valueOf(ad.calculateAverageDegree(metricData.getFullGraph()));
        Double roundedResult = roundTo2Decimals(result);

        assertEquals(4.024, roundedResult.doubleValue(), 0.0);
    }
}