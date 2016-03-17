package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class calculates the average number of authors published in a conference for the full set of
 * editions considered (by default, we consider the last 5 editions)
 */
public class AverageNumberOfAuthors extends SQLMetric {
    /**
     * Constructs the {@link AverageNumberOfAuthors} class
     * @param metricData Main information to calculate the data
     * @param dbInfo Database credentials
     */
    public AverageNumberOfAuthors(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        float averageNumberOfAuthors = 0;
        try {
            String averageNumberOfAuthorsQuery = "SELECT ROUND(AVG(num_unique_authors), 3) AS avg " +
                                                 "FROM ( " +
                                                        "SELECT count(auth.author_id) AS num_authors, count(distinct auth.author_id) as num_unique_authors, source, source_id, year " +
                                                        "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                                                        "WHERE type = 'inproceedings' AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                                                        "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                                                        "GROUP BY source, source_id, year) " +
                                                 "AS aux";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(averageNumberOfAuthorsQuery);

            rs.first();
            averageNumberOfAuthors = rs.getFloat("avg");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("%.3f", averageNumberOfAuthors).replace(",", ".");
    }
}
