package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Calculates the percentage of newcomers for a conference, that is, people that have never published a paper in there
 * before (where by "before" we mean the first considered edition as starting point).
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Newcomers per edition of a conference</li>
 *     <li>Average newcomers for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 */
public class AverageNewComersRate extends SQLMetric {
    /**
     * Constructs the {@link AverageNewComersRate} class
     * @param metricData Main information to calculate the data
     * @param dbInfo Database credentials
     */
    public AverageNewComersRate(MetricData metricData, DBInfo dbInfo) {
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
     * Calculate the number of newcomers for an edition.
     *
     * @param currentYear The year to consider when detecting newcomers
     * @param firstYear The starting point (time window) to set
     * @return Number of newcomers
     */
    private int getNewAuthorsYear(int currentYear, int firstYear) {
        Statement stmt = null;
        ResultSet rs = null;
        int newAuthors = 0;
        try {
            String query =  "SELECT count(*) AS new_authors " +
                            "FROM ( " +
                            "SELECT auth.author_id AS current_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year = " + currentYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) as x " +
                            "LEFT JOIN " +
                            "(SELECT auth.author_id AS previous_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year < " + currentYear + " AND year >= " + firstYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) AS y " +
                            "ON x.current_author = y.previous_author " +
                            "WHERE previous_author IS NULL;";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            newAuthors = rs.getInt("new_authors");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return newAuthors;
    }

    @Override
    //Note that the result is a percentage. Ex.: Models has on average 28.69% of new authors per edition
    public String getResult() {
        List<Float> growthRates = new LinkedList<Float>();

        //if(metricData.getSourceInfo().contains("MoDELS"))
        //    System.out.print("a");

        int firstEdition = metricData.getEditions().get(metricData.getEditions().size()-1);
        for (int edition: metricData.getEditions().subList(0, metricData.getEditions().size()-1)) {
            int authors = getDistinctAuthorsYear(edition);
            int newAuthors = getNewAuthorsYear(edition, firstEdition);

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
