package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class calculates the average number of papers published in a conference for the full set of
 * editions considered (by default, we consider the last 5 editions)
 */
public class AverageNumberOfPapers extends SQLMetric {

    /**
     * Constructs the {@link AverageNumberOfPapers} class
     * @param metricData Main information to calculate the data
     * @param dbInfo Database credentials
     */
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
                                                        "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.FILTER_NUM_PAGES) + " AND year IN (" + toCommaSeparated(metricData.getEditions()) + ") " +
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
