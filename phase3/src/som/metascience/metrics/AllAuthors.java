package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
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
        List<Integer> authors = new LinkedList<Integer>();
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
            rs.close();
            stmt.close();

            String authorsQuery = "SELECT COUNT(*) AS numAuthors, year " +
                    "FROM (SELECT airn.author_id, pub.year " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND pub.type = 'inproceedings' AND pub.year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
                    "GROUP BY airn.author_id, pub.year) AS x " +
                    "GROUP BY year " +
                    "ORDER BY year DESC;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(authorsQuery);

            while (rs.next())
                authors.add(rs.getInt("numAuthors"));

            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String results = "";
        for (int a : authors)
            results += a + ",";

        return results + allAuthors;
    }
}
