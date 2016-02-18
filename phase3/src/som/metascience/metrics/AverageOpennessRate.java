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
public class AverageOpennessRate extends SQLMetric {

    public AverageOpennessRate(MetricData metricData, DBInfo dbInfo) {
        super(metricData, dbInfo);
    }


    private int[] getPapersYear(int currentYear, int firstYear) {
        Statement stmt = null;
        ResultSet rs = null;
        int[] result = new int[3];
        int papers = 0;
        int papersFromNewComers = 0;
        int papersFromCommunity = 0;
        try {
            String query =  "SELECT count(*) as papers, " +
                            "SUM(IF (num_of_previous_authors = 0, 1, 0)) AS paper_from_newcomers, " +
                            "SUM(IF (num_of_authors = num_of_previous_authors, 1, 0)) AS paper_from_community " +
                            "FROM (" +
                                "SELECT paper_id, COUNT(x_year.author_id) AS num_of_authors, COUNT(previous_years.author_id) AS num_of_previous_authors, year " +
                                "FROM (" +
                                    "SELECT auth.id AS paper_id, auth.author_id, year " +
                                    "FROM dblp_pub_new pub " +
                                    "JOIN " +
                                    "dblp_authorid_ref_new auth " +
                                    "ON pub.id = auth.id " +
                                    "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year = " + currentYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ")) as x_year " +
                                "LEFT JOIN " +
                                    "(SELECT auth.author_id " +
                                    "FROM dblp_pub_new pub " +
                                    "JOIN " +
                                    "dblp_authorid_ref_new auth " +
                                    "ON pub.id = auth.id " +
                                    "WHERE type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + Integer.toString(super.filter_num_pages) + " AND year < " + currentYear + " AND year >= " + firstYear + " AND source IN (" + metricData.getSourceInfo() + ") AND source_id IN (" + metricData.getSourceIdInfo() + ") " +
                                    "GROUP BY auth.author_id) AS previous_years " +
                                "ON x_year.author_id = previous_years.author_id " +
                                "GROUP BY paper_id) AS openness;";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            rs.first();
            papers = rs.getInt("papers");
            papersFromNewComers = rs.getInt("paper_from_newcomers");
            papersFromCommunity = rs.getInt("paper_from_community");
            rs.close();
            stmt.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        result[0] = papers;
        result[1] = papersFromNewComers;
        result[2] = papersFromCommunity;

        return result;
    }

    @Override
    public String getResult() {
        List<Float> papersFromNewComersPerc = new LinkedList<Float>();
        List<Float> papersFromCommunityPerc = new LinkedList<Float>();

        int firstEdition = metricData.getEditions().get(metricData.getEditions().size()-1);
        for (int edition: metricData.getEditions().subList(0, metricData.getEditions().size()-1)) {
            int[] output = getPapersYear(edition, firstEdition);
            int papers = output[0];
            int papersFromNewComers = output[1];
            int papersFromCommunity = output[2];

            papersFromNewComersPerc.add(((((float)papersFromNewComers)/papers)*100));
            papersFromCommunityPerc.add(((((float)papersFromCommunity)/papers)*100));

        }

        String resultPapersFromNewComers = serializeResults(papersFromNewComersPerc);
        String resultPapersFromCommunity = serializeResults(papersFromCommunityPerc);


        return resultPapersFromNewComers + "," + resultPapersFromCommunity;

    }

    private String serializeResults(List<Float> list) {
        String output = "";
        float sum = 0;
        Collections.reverse(list);
        for (float e : list) {
            sum = sum + e;
            output = String.format("%.3f", e).replace(",", ".") + "," + output;
        }
        return output + String.format("%.3f", sum/(metricData.getEditions().size()-1)).replace(",", ".");
    }
}
