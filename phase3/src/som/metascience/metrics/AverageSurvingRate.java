package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Calculates the percentage of survivors for a conference, that is, authors who repeat between two consecutive editions
 * of the conference (i.e., they have at least one paper published in both editions).
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Survivors per edition of a conference</li>
 *     <li>Average survivors for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 */
public class AverageSurvingRate extends SQLMetric {
    /**
     * Constructs the {@link AverageSurvingRate} class
     * @param metricData Main information to calculate the data
     * @param dbInfo Database credentials
     */
    public AverageSurvingRate(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    /**
     * Calculates the number of distinct authors in a conference for a specific edition (i.e., year)
     *
     * @param year The year to consider
     * @return Number of distinct authors
     */
    private int getDistinctAuthorsYear(int year) {
        Statement stmt = null;
        ResultSet rs = null;
        int distinctAuthors = 0;
        try {
            String query = "SELECT COUNT(DISTINCT auth.author_id) as authors " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                    "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND type = 'inproceedings' AND year = " + year + " ;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            distinctAuthors = rs.getInt("authors");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return distinctAuthors;
    }

    /**
     * Calculate the number of survivors for an edition.
     *
     * @param currentYear The year to consider when detecting newcomers
     * @param previousYear The starting point (time window) to set
     * @return Number of survivors
     */
    private int getSurvivedAuthorsYear(int currentYear, int previousYear) {
        Statement stmt = null;
        ResultSet rs = null;
        int survivedAuthors = 0;
        try {
            String query =  "SELECT count(*) AS survived_authors " +
                            "FROM ( " +
                            "SELECT auth.author_id AS current_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year = " + currentYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) as x " +
                            "JOIN " +
                            "(SELECT auth.author_id AS previous_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year = " + previousYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) AS y " +
                            "ON x.current_author = y.previous_author";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            survivedAuthors = rs.getInt("survived_authors");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return survivedAuthors;
    }

    @Override
    //Note that the result is a percentage. Ex.: Models has on average 28.69% of new authors per edition
    public String getResult() {
        List<Float> growthRates = new LinkedList<Float>();

        List a = metricData.getEditions();
        for (int i = 0; i < metricData.getEditions().size()-1; i++) {
            int previousEdition = metricData.getEditions().get(i+1);
            int currentEdition = metricData.getEditions().get(i);
            int authors = getDistinctAuthorsYear(currentEdition);
            int newAuthors = getSurvivedAuthorsYear(currentEdition, previousEdition);

            growthRates.add(((((float)newAuthors)/authors)*100));

        }

        String results = "";
        float sumGrowthRates = 0;
        Collections.reverse(growthRates);
        for (float growthRate : growthRates) {
            sumGrowthRates = sumGrowthRates + growthRate;
            results = String.format("%.3f", growthRate).replace(",", ".") + "," + results;
        }
        return results + String.format("%.3f", sumGrowthRates/(metricData.getEditions().size()-1)).replace(",", ".");

    }
}
