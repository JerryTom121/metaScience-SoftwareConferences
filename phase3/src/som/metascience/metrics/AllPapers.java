package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * This metric calculates the number of papers per conference
 */
public class AllPapers extends SQLMetric {

    public AllPapers(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    @Override
    public String getResult() {
        Statement stmt = null;
        ResultSet rs = null;
        int allPapers = 0;
        try {
            String allPapersQuery = "SELECT COUNT(*) as numPapers " +
                    "FROM dblp_pub_new " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND type = 'inproceedings' AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") ;";;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(allPapersQuery);

            rs.first();
            allPapers = rs.getInt("numPapers");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(allPapers);
    }
}
