package som.metascience;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Launches Phase 3.
 *
 * This phase takes as input the co-authorship graph generated in phase 2 and calculates a set of metrics which are
 * stored as a CSV file.
 *
 * As some calculationg use the DBLP database, the connection properties have to be declared in the file
 * db.properties, thus including values for the keys: host, db, user, pass and port.
 */
public class Phase3Launcher {
    /**
     * String path to the property file containing the database credentials
     */
    public static final String dbPropertiesPath = "phase3/db.properties";
    /**
     * Co-authorship graph to use as input
     */
    public static final String GRAPHS = "data/graphs";
    /**
     * String path for the file storing the metric results
     */
    public static final String OUTPUT = "data/results.csv";
    /**
     * String path to the folder containing the set of property files
     */
    public static final String CONF = "data/importData";

    public static void main(String[] args) throws Exception {
        Properties dbProperties = new Properties();
        dbProperties.load(new FileInputStream(new File(dbPropertiesPath)));

        String host = dbProperties.getProperty("host");
        String db = dbProperties.getProperty("db");
        String user = dbProperties.getProperty("user");
        String pass = dbProperties.getProperty("pass");
        int port = Integer.valueOf(dbProperties.getProperty("port"));
        DBInfo dbInfo = new DBInfo(host, db, user, pass, port);

        long startTime = System.currentTimeMillis();

        MetricCalculator calculator = new MetricCalculator(new File(CONF), new File(GRAPHS), new File(OUTPUT), dbInfo, new Phase3Logger());
        calculator.execute();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }

}
