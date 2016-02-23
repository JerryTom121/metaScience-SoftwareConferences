package som.metascience.metrics;


import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valerio cosentino <valerio.cosentino@gmail.com> on 19/02/2016.
 */
public class AverageSurvingRate extends SQLMetric {

    public AverageSurvingRate(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }

    private int getDistinctAuthorsYear(int year) {
        Statement stmt = null;
        ResultSet rs = null;
        int distinctAuthors = 0;
        try {
            String query = "SELECT COUNT(DISTINCT auth.author_id) as authors " +
                    "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                    "WHERE source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                    "AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND type = 'inproceedings' AND year = " + year + " ;";
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

    private int getNewAuthorsYear(int currentYear, int previousYear) {
        Statement stmt = null;
        ResultSet rs = null;
        int newAuthors = 0;
        try {
            String query =  "SELECT count(*) AS survived_authors " +
                            "FROM ( " +
                            "SELECT auth.author_id AS current_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year = " + currentYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) as x " +
                            "JOIN " +
                            "(SELECT auth.author_id AS previous_author " +
                            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new auth ON pub.id = auth.id " +
                            "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year = " + previousYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                            "GROUP BY author_id) AS y " +
                            "ON x.current_author = y.previous_author";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            newAuthors = rs.getInt("survived_authors");
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

        List a = metricData.getEditions();
        for (int i = 0; i < metricData.getEditions().size()-1; i++) {
            int previousEdition = metricData.getEditions().get(i+1);
            int currentEdition = metricData.getEditions().get(i);
            int authors = getDistinctAuthorsYear(currentEdition);
            int newAuthors = getNewAuthorsYear(currentEdition, previousEdition);

            growthRates.add(((((float)newAuthors)/authors)*100));

        }

        String results = "";
        float sumGrowthRates = 0;
        Collections.reverse(growthRates);
        for (float growthRate : growthRates) {
            sumGrowthRates = sumGrowthRates + growthRate;
            results = String.format("%.3f", growthRate).replace(",", ".") + "," + results;
        }
        return results + String.format("%.3f", sumGrowthRates/(metricData.getEditions().size()-1)).replace(",", ".");

    }
}
