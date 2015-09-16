package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.util.Arrays;
import java.util.List;

/**
 * This class controls the list of metrics to apply. Furthermore, the class knows the category to print
 * for each metric and how to build them.
 *
 * To add new metrics, follow these steps:
 * 1. Implement your metric as a subclass of {@link Metric}
 * 2. Add your class to the "metrics" array (as a class)
 * 3. Add a new case in "getCategory()" method, which will return the category of the metric
 *    (in a CSV style if it covers several categories)
 * 4. Add a new case in "buildMetric()" method to build an instance of your metric
 *
 * The set of metrics will be retrieved and executed by {@link som.metascience.MetricCalculator}
 */
public class MetricBuilder {
    /**
     * Basic metric data (used to build metrics)
     */
    private MetricData metricData;
    /**
     * Database info (used when building metrics)
     */
    private DBInfo dbInfo;

    /**
     * The set of metrics to consider
     * (add here yours if you want to add another one, recall the steps to follows in the description of this class)
     */
    private static Class[] metrics = {
            AllPapers.class,
            AllAuthors.class,
            AverageNumberOfPapers.class,
            AverageNumberOfAuthors.class,
            AverageNumberOfAuthorsPerPaper.class,
            AverageNumberOfPapersPerAuthor.class,
            AverageOpennessRate.class,
            AverageCommunityGrowthRate.class//,
            //AverageDegree.class,
            //Density.class,
            //GraphModularity.class,
            //ConnectedComponents.class,
            //AveragePathLength.class
    };

    public MetricBuilder(MetricData metricData, DBInfo dbInfo) {
        this.metricData = metricData;
        this.dbInfo = dbInfo;
    }

    /**
     * Returns the list of metrics to be calculated
     * @return A lisk of {@link Metric}s
     */
    public static List<Class> getMetrics() {
        return Arrays.asList(metrics);
    }

    /**
     * Returns the string to show as category for this metric.
     * If the metric returns several values, the category will be comma-separated value
     * (add here yours if you want to add another one, recall the steps to follows in the description of this class)
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
        } else if (m == AverageNumberOfPapers.class) {
            result = "Avg_papers";
        } else if (m == AverageNumberOfAuthors.class) {
            result = "Avg_authors";
        } else if (m == AverageNumberOfAuthorsPerPaper.class) {
            result = "Avg_authors_per_paper";
        } else if (m == AverageNumberOfPapersPerAuthor.class) {
            result = "Avg_papers_per_author";
        } else if (m == AverageOpennessRate.class) {
            result = "Avg_newcomers_rate,Average_community_papers";
        } else if (m == AverageCommunityGrowthRate.class) {
            result = "GrowthRate_12_LastEdition,GrowthRate_23_LastEdition,GrowthRate_34_LastEdition,GrowthRate_45_LastEdition,Avg_communityGrowthRate";
        } else if (m == AverageDegree.class) {
            result = "AvgDegree_allEditions,AvgDegree_1_LastEdition,AvgDegree_2_LastEdition,AvgDegree_3_LastEdition,AvgDegree_4_LastEdition,AvgDegree_5_LastEdition";
        } else if (m == Density.class) {
            result = "GraphDensity_allEditions,GraphDensity_1_LastEdition,GraphDensity_2_LastEdition,GraphDensity_3_LastEdition,GraphDensity_4_LastEdition,GraphDensity_5_LastEdition";
        } else if (m == GraphModularity.class) {
            result = "GraphModularity_allEditions,GraphModularity_1_LastEdition,GraphModularity_2_LastEdition,GraphModularity_3_LastEdition,GraphModularity_4_LastEdition,GraphModularity_5_LastEdition";
        } else if (m == ConnectedComponents.class) {
            result = "ConnectedComponents_allEditions,ConnectedComponents_1_LastEdition,ConnectedComponents_2_LastEdition,ConnectedComponents_3_LastEdition,ConnectedComponents_4_LastEdition,ConnectedComponents_5_LastEdition";
        } else if (m == AveragePathLength.class) {
            result = "AveragePathLength_allEditions,AveragePathLength_1_LastEdition,AveragePathLength_2_LastEdition,AveragePathLength_3_LastEdition,AveragePathLength_4_LastEdition,AveragePathLength_5_LastEdition";
        } else
            throw new IllegalArgumentException("There is no category for such a metric");
        return result;
    }

    /**
     * Builds a specific metric
     * (add here yours if you want to add another one, recall the steps to follows in the description of this class)
     *
     * @param m The Metric to be build
     * @return The metric built
     */
    public Metric buildMetric(Class m) {
        Metric result = null;
        if (m == AllPapers.class) {
            result = new AllPapers(metricData, dbInfo);
        } else if (m == AllAuthors.class) {
            result = new AllAuthors(metricData, dbInfo);
        } else if (m == AverageNumberOfPapers.class) {
            result = new AverageNumberOfPapers(metricData, dbInfo);
        } else if (m == AverageNumberOfAuthors.class) {
            result = new AverageNumberOfAuthors(metricData, dbInfo);
        } else if (m == AverageNumberOfAuthorsPerPaper.class) {
            result = new AverageNumberOfAuthorsPerPaper(metricData, dbInfo);
        } else if (m == AverageNumberOfPapersPerAuthor.class) {
            result = new AverageNumberOfPapersPerAuthor(metricData, dbInfo);
        } else if (m == AverageOpennessRate.class) {
            result = new AverageOpennessRate(metricData, dbInfo);
        } else if (m == AverageCommunityGrowthRate.class) {
            result = new AverageCommunityGrowthRate(metricData, dbInfo);
        } else if (m == AverageDegree.class) {
            result = new AverageDegree(metricData);
        } else if (m == Density.class) {
            result = new Density(metricData);
        } else if (m == GraphModularity.class) {
            result = new GraphModularity(metricData);
        } else if (m == ConnectedComponents.class) {
            result = new ConnectedComponents(metricData);
        } else if (m == AveragePathLength.class) {
            result = new AveragePathLength(metricData);
        } else
            throw new IllegalArgumentException("There is no builder for such a metric");
        return result;
    }

}
