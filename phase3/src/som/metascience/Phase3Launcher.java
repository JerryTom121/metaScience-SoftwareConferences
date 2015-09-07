package som.metascience;

import java.io.File;

/**
 * Launches the phase 3
 */
public class Phase3Launcher {
    public static final String GRAPHS = "data/graphs";
    public static final String OUTPUT = "data/results.csv";
    public static final String CONF = "data/importData";

    public static void main(String[] args) {
        MetricCalculator calculator = new MetricCalculator(new File(CONF), new File(GRAPHS), new File(OUTPUT), new Phase3Logger());
        calculator.execute();

    }
}
