package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageNumberOfPapersPerAuthor extends SQLMetric {

    public AverageNumberOfPapersPerAuthor(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        float average = 0;
        List<Float> yearValue = new LinkedList<Float>();
        try {
            String query = "SELECT ROUND(AVG(avg_num_paper_per_author), 2) as avg " +
                           "FROM _avg_number_papers_per_author_per_conf_per_year  " +
                           "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ")";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getFloat("avg");
            rs.close();
            stmt.close();

            query = "SELECT avg_num_paper_per_author, year " +
                    "FROM _avg_number_papers_per_author_per_conf_per_year  " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "ORDER BY year DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
                yearValue.add(rs.getFloat("avg_num_paper_per_author"));

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
}
