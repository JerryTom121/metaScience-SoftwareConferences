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
        int averageNumberOfAuthors = 0;
        try {
            String averageNumberOfAuthorsQuery = "SELECT ROUND(AVG(num_unique_authors), 2) as avg " +
                                                 "FROM _num_authors_per_conf_per_year " +
                                                 "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ")";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(averageNumberOfAuthorsQuery);

            rs.first();
            averageNumberOfAuthors = rs.getInt("avg");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(averageNumberOfAuthors);
    }
}
