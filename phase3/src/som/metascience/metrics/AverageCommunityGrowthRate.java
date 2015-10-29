package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 15/09/2015.
 */
public class AverageCommunityGrowthRate extends SQLMetric {

    public AverageCommunityGrowthRate(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    private int getDistinctAuthorsYear(int year) {
        Statement stmt = null;
        ResultSet rs = null;
        int distinctAuthors = 0;
        try {
            String query = "SELECT COUNT(auth.author_id) as authors " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") AND type = 'inproceedings' AND year = " + year + " ;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            distinctAuthors = rs.getInt("authors");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return distinctAuthors;
    }

    private int getNewAuthorsYear(int previousYear, int currentYear, int firstYear) {
        Statement stmt = null;
        ResultSet rs = null;
        int newAuthors = 0;
        try {
            String query = "SELECT count(*) AS new_authors " +
                    "FROM ( " +
                    "SELECT auth.author_id AS previous_author " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                    "WHERE type = 'inproceedings' AND year <= " + previousYear + " AND year >= " + firstYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                    "GROUP BY author_id) as x " +
                    "LEFT JOIN " +
                    "(SELECT auth.author_id AS current_author " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                    "WHERE type = 'inproceedings' AND year = " + currentYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                    "GROUP BY author_id) AS y " +
                    "ON x.previous_author = y.current_author " +
                    "WHERE current_author IS NOT NULL;";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            newAuthors = rs.getInt("new_authors");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return newAuthors;
    }

    @Override
    //Note that the result is a percentage. Ex.: Models has on average 28.69% of new authors per edition
    public String getResult() {
        List<Float> growthRates = new LinkedList<Float>();

        Collections.reverse(metricData.getEditions());
        int firstEdition = metricData.getEditions().get(0);
        int previousEdition = firstEdition;
        for (int edition: metricData.getEditions().subList(1, metricData.getEditions().size())) {
            int authors = getDistinctAuthorsYear(edition);
            int newAuthors = getNewAuthorsYear(previousEdition, edition, firstEdition);

            growthRates.add(((((float)newAuthors)/authors)*100));

            previousEdition = edition;
        }

        String results = "";
        float sumGrowthRates = 0;
        for (float growthRate : growthRates) {
            sumGrowthRates = sumGrowthRates + growthRate;
            results = String.format("%.3f", growthRate).replace(",", ".") + "," + results;
        }
        return results + String.format("%.3f", sumGrowthRates/(metricData.getEditions().size()-1)).replace(",", ".");

    }
}
