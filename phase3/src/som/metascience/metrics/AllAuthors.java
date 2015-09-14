package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * This metric calculates the number of authors for a conference
 */
public class AllAuthors extends SQLMetric {

    public AllAuthors(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        int allAuthors = 0;
        try {
            String allAuthorsQuery = "SELECT COUNT(*) AS numAuthors " +
                    "FROM (SELECT airn.author_id " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND pub.type = 'inproceedings' AND pub.year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "GROUP BY airn.author_id) AS x;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(allAuthorsQuery);

            rs.first();
            allAuthors = rs.getInt("numAuthors");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(allAuthors);
    }
}
