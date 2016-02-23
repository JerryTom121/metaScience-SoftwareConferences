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
            AverageNumberOfPapers.class,
            AllAuthors.class,
            AverageNumberOfAuthors.class,
            AverageNumberOfAuthorsPerPaper.class,
            AverageNumberOfPapersPerAuthor.class,
            AverageNewComersRate.class,
            AverageSurvingRate.class,
            AverageOpennessRate.class//,
//            AverageDegree.class,
//            Density.class,
//            GraphModularity.class,
//            ConnectedComponents.class,
//            AveragePathLength.class,
//            ProminentFigures.class,
//            CommunitySize.class
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
            result = "Papers_1_Edition,Papers_2_Edition,Papers_3_Edition,Papers_4_Edition,Papers_5_Edition";
        } else if (m == AllAuthors.class) {
            result = "Authors_1_Edition,Authors_2_Edition,Authors_3_Edition,Authors_4_Edition,Authors_5_Edition";
        } else if (m == AverageNumberOfPapers.class) {
            result = "Num_Papers";
        } else if (m == AverageNumberOfAuthors.class) {
            result = "Num_Authors";
        } else if (m == AverageNumberOfAuthorsPerPaper.class) {
            result = "Authors_per_paper_1_Edition,Authors_per_paper_2_Edition,Authors_per_paper_3_Edition,Authors_per_paper_4_Edition,Authors_per_paper_5_Edition,Authors_per_Paper";
        } else if (m == AverageNumberOfPapersPerAuthor.class) {
            result = "Papers_per_author_1_Edition,Papers_per_author_2_Edition,Papers_per_author_3_Edition,Papers_per_author_4_Edition,Papers_per_author_5_Edition,Papers_per_Author";
        } else if (m == AverageOpennessRate.class) {
            result = "Newcomer_papers_rate_12_Edition,Newcomer_papers_rate_23_Edition,Newcomer_papers_rate_34_Edition,Newcomer_papers_rate_45_Edition,Newcomer_Papers,Community_papers_12_Edition,Community_papers_23_Edition,Community_papers_34_Edition,Community_papers_45_Edition,Community_Papers";
        } else if (m == AverageNewComersRate.class) {
            result = "Newcomers_rate_12_Edition,Newcomers_rate_23_Edition,Newcomers_rate_34_Edition,Newcomers_rate_45_Edition,Newcomers";
        } else if (m == AverageSurvingRate.class) {
            result = "Surving_rate_12_Edition,Surving_rate_23_Edition,Surving_rate_34_Edition,Surving_rate_45_Edition,Surving";
        } else if (m == AverageDegree.class) {
            result = "AvgDegree_1_LastEdition,AvgDegree_2_LastEdition,AvgDegree_3_LastEdition,AvgDegree_4_LastEdition,AvgDegree_5_LastEdition,Avg_Degree";
        } else if (m == Density.class) {
            result = "GraphDensity_1_LastEdition,GraphDensity_2_LastEdition,GraphDensity_3_LastEdition,GraphDensity_4_LastEdition,GraphDensity_5_LastEdition,Graph_Density";
        } else if (m == GraphModularity.class) {
            result = "GraphModularity_1_LastEdition,GraphModularity_2_LastEdition,GraphModularity_3_LastEdition,GraphModularity_4_LastEdition,GraphModularity_5_LastEdition,Graph_Modularity";
        } else if (m == ConnectedComponents.class) {
            result = "ConnectedComponents_1_LastEdition,ConnectedComponents_2_LastEdition,ConnectedComponents_3_LastEdition,ConnectedComponents_4_LastEdition,ConnectedComponents_5_LastEdition,Connected_Components";
        } else if (m == AveragePathLength.class) {
            result = "AveragePathLength_1_LastEdition,AveragePathLength_2_LastEdition,AveragePathLength_3_LastEdition,AveragePathLength_4_LastEdition,AveragePathLength_5_LastEdition,Avg_Path_Length";
        } else if (m == ProminentFigures.class) {
            result = "ProminentFigures_1_LastEdition,ProminentFigures_2_LastEdition,ProminentFigures_3_LastEdition,ProminentFigures_4_LastEdition,ProminentFigures_5_LastEdition,Prominent_Figures";
        } else if (m == CommunitySize.class) {
            result = "Community_Size";
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
        } else if (m == AverageNewComersRate.class) {
            result = new AverageNewComersRate(metricData, dbInfo);
        } else if (m == AverageSurvingRate.class) {
            result = new AverageSurvingRate(metricData, dbInfo);
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
        } else if (m == ProminentFigures.class) {
            result = new ProminentFigures(metricData);
        } else if (m == CommunitySize.class) {
            result = new CommunitySize(metricData);
        } else
            throw new IllegalArgumentException("There is no builder for such a metric");
        return result;
    }

}
