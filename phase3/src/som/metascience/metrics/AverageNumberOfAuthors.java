package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageNumberOfAuthors extends SQLMetric {

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
                                                        "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
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
