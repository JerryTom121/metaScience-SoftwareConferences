package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        float average = 0;
        List<Float> yearValue = new LinkedList<Float>();
        try {
            String query = "SELECT ROUND(AVG(o.from_outsiders/o.number_of_papers)*100,2) as avg " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ") AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "ORDER BY year ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getFloat("avg");
            rs.close();
            stmt.close();

            query = "SELECT ROUND(SUM((o.from_outsiders/o.number_of_papers)*100),2) as x, year " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ") AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "GROUP BY year " +
                    "ORDER BY year ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
                yearValue.add(rs.getFloat("x"));

            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String results = "";
        for (Float ya : yearValue)
            results += String.format("%.2f", ya).replace(",", ".") + ",";

        return results + String.format("%.2f", average).replace(",", ".");
    }


    public String getPercentageCommunityPapers() {
        Statement stmt = null;
        ResultSet rs = null;
        float average = 0;
        List<Float> yearValue = new LinkedList<Float>();
        try {
            String query = "SELECT ROUND(AVG(o.from_community/o.number_of_papers)*100,2) as avg " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ") AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "ORDER BY year ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getFloat("avg");
            rs.close();
            stmt.close();

            query = "SELECT ROUND(SUM((o.from_community/o.number_of_papers)*100),2) as x, year " +
                    "FROM _openness_conf o  " +
                    "WHERE conf IN (" + metricData.getSourceInfo() + ") AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "GROUP BY year " +
                    "ORDER BY year ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
                yearValue.add(rs.getFloat("x"));

            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String results = "";
        for (Float ya : yearValue)
            results += String.format("%.2f", ya).replace(",", ".") + ",";

        return results + String.format("%.2f", average).replace(",", ".");

    }

    public void callStoredProcedure() {
        try {
            CallableStatement cs = null;
            String source = metricData.getSourceInfo();
            String query = "";
            if (source.contains(",")) {
                for (String s: Arrays.asList(source.split(","))) {
                        query = "{call dblp.get_openness_conf(" + s + ")}";
                        cs = conn.prepareCall(query);
                        cs.execute();
                }
            }
            else {
                query = "{call dblp.get_openness_conf(" + source + ")}";
                cs = conn.prepareCall(query);
                cs.execute();
                cs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getResult() {
        callStoredProcedure();
        return getPercentageNewAuthors() + "," + getPercentageCommunityPapers();
    }
}
