package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetricBuilder {
    MetricData metricData;
    private DBInfo dbInfo;

    private static Class[] metrics = { AllPapers.class, AllAuthors.class, AverageDegree.class };

    public MetricBuilder(MetricData metricData, DBInfo dbInfo) {
        this.metricData = metricData;
        this.dbInfo = dbInfo;
    }

    public static List<Class> getMetrics() {
        return Arrays.asList(metrics);
    }

    /**
     * Returns the string to show as category for this metric.
     * If the metric returns several values, the category will be comma-separated value
     *
     * @param m The metrics from which to get the category
     * @return String with the name/s of the categories to show
     */
    public static String getCategory(Class m) {
        String result = null;
        if (m == AllPapers.class) {
            result = "Num_Papers";
        } else if (m == AllAuthors.class) {
            result = "Num_authors";
        } else if (m == AverageDegree.class) {
            result = "AvgDegree_allEditions,AvgDegree_1_LastEdition,AvgDegree_2_LastEdition,AvgDegree_3_LastEdition,AvgDegree_4_LastEdition,AvgDegree_5_LastEdition";
        }
        return result;
    }

    public Metric buildMetric(Class m) {
        Metric result = null;
        if (m == AllPapers.class) {
            result = new AllPapers(metricData, dbInfo);
        } else if (m == AllAuthors.class) {
            result = new AllAuthors(metricData, dbInfo);
        } else if (m == AverageDegree.class) {
            result = new AverageDegree(metricData);
        }
        return result;
    }

}
