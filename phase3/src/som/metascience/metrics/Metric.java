package som.metascience.metrics;

import som.metascience.MetricData;

import java.util.List;

/**
 * Main root to implement metrics. New metrics has to be subclass of this abstract class, which forces them to
 * include the method "getResult()".
 *
 * @author Javier Canovas (me@jlcanovas.es)
 */
public abstract class Metric {
    /**
     * Basic info of the conference to calculate the metric
     */
    MetricData metricData;

    /**
     * Constructs a new {@link Metric}
     * @param metricData Basic data to be able to calculate the metric
     */
    public Metric(MetricData metricData) {
        if(metricData == null)
            throw new IllegalArgumentException("The metricData cannot be null");

        this.metricData = metricData;
    }

    /**
     * Returns the values for this metric.
     * If the metric returns several values, the category will be comma-separated value
     *
     * @return String with the results to show
     */
    public abstract String getResult();


    /**
     * Converts a list of integers into a comma-separated String
     *
     * @param list List of integer
     * @return CSV String
     */
    String toCommaSeparated(List<Integer> list) {
        String output = "";
        for (Integer i : list) {
            if (i == list.get(list.size()-1))
                output = output + String.valueOf(i);
            else
                output = output + String.valueOf(i) + ", ";
        }
        return output;
    }
}
