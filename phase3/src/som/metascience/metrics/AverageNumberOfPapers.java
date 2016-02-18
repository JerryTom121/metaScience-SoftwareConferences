package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageNumberOfPapers extends SQLMetric {

    public AverageNumberOfPapers(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        float averageNumberOfPapers = 0;
        try {
            String averageNumberOfPapersQuery = "SELECT ROUND(AVG(num_papers), 3) as avg " +
                                                "FROM ( " +
                                                        "SELECT COUNT(id) as num_papers, source, source_id, year " +
                                                        "FROM dblp_pub_new " +
                                                        "WHERE type = 'inproceedings' AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                                                        "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                                                        "GROUP BY source, source_id, year) AS aux " +
                                                "ORDER BY year DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(averageNumberOfPapersQuery);

            rs.first();
            averageNumberOfPapers = rs.getFloat("avg");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("%.3f", averageNumberOfPapers).replace(",", ".");
    }
}
