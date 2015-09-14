package som.metascience;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Launches the phase 3
 */
public class Phase3Launcher {
    public static final String dbPropertiesPath = "phase3/db.properties";
    public static final String GRAPHS = "data/graphs";
    public static final String OUTPUT = "data/results.csv";
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

        MetricCalculator calculator = new MetricCalculator(new File(CONF), new File(GRAPHS), new File(OUTPUT), dbInfo, new Phase3Logger());
        calculator.execute();
    }

}
