package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * This metric calculates the number of authors per conference per edition (by default, we perform the calculations
 * for the last 5 editions so this metric will actually return 5 values)
 */
public class AllAuthors extends SQLMetric {

    /**
     * Constructs the {@link AllAuthors} class
     * @param metricData Main information to calculate the data
     * @param dbInfo Database credentials
     */
    public AllAuthors(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        //int allAuthors = 0;
        List<Integer> authors = new LinkedList<Integer>();
        try {
            String authorsQuery = "SELECT COUNT(*) AS numAuthors, year " +
                    "FROM (SELECT airn.author_id, pub.year " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND pub.type = 'inproceedings' " +
                    "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND pub.year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
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

        //return results + allAuthors; // jlcanovas: we return only the per-edition values
        return results.substring(0, results.length()-1);
    }
}
