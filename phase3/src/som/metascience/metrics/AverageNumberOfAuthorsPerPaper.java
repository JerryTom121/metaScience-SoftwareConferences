package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.LinkedList;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageNumberOfAuthorsPerPaper extends SQLMetric {

    public AverageNumberOfAuthorsPerPaper(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        float average = 0;
        List<Float> yearValue = new LinkedList<Float>();
        try {
            String query = "SELECT ROUND(AVG(avg_author_per_paper), 3) as avg " +
                           "FROM ( " +
                                "SELECT AVG(author_per_paper) AS avg_author_per_paper, source, source_id, year " +
                                "FROM ( " +
                                        "SELECT COUNT(auth.author_id) AS author_per_paper, pub.id as paper_id, source, source_id, year " +
                                        "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                                        "WHERE type = 'inproceedings' AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                                        "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                                        "GROUP BY paper_id, source, source_id, year) " +
                                "AS count " +
                                "GROUP BY source, source_id, year) " +
                            "AS aux";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            average = rs.getFloat("avg");
            rs.close();
            stmt.close();

            query = "SELECT avg_author_per_paper, year " +
                    "FROM ( " +
                            "SELECT AVG(author_per_paper) AS avg_author_per_paper, source, source_id, year " +
                            "FROM ( " +
                                    "SELECT COUNT(auth.author_id) AS author_per_paper, pub.id as paper_id, source, source_id, year " +
                                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                                    "WHERE type = 'inproceedings' AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                                    "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                                    "GROUP BY paper_id, source, source_id, year) " +
                            "AS count " +
                            "GROUP BY source, source_id, year) " +
                    "AS aux " +
                    "ORDER BY year DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next())
                yearValue.add(rs.getFloat("avg_author_per_paper"));

            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String results = "";
        for (Float ya : yearValue)
            results += String.format("%.3f", ya).replace(",", ".") + ",";

        return results + String.format("%.3f", average).replace(",", ".");

    }
}
