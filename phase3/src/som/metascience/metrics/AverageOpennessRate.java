package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageOpennessRate extends SQLMetric {

    public AverageOpennessRate(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    private String getPercentageNewAuthors() {
        Statement stmt = null;
        ResultSet rs = null;
        int average = 0;
        try {
            String query = "SELECT ROUND(AVG(o.from_outsiders/o.number_of_papers)*100,2) as avg " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ")";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getInt("avg");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(average);
    }


    public String getPercentageCommunityPapers() {
        Statement stmt = null;
        ResultSet rs = null;
        int average = 0;
        try {
            String query = "SELECT ROUND(AVG(o.from_community/o.number_of_papers)*100,2) as avg " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ")";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getInt("avg");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(average);
    }


    @Override
    public String getResult() {
        return getPercentageNewAuthors() + "," + getPercentageCommunityPapers();
    }
}
