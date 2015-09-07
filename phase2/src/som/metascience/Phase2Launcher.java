package som.metascience;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 *
 * As the data is obtained from the DBLP database, the connection properties have to be declared in the file
 * db.properties, thus including values for the keys: host, db, user, pass and port.
 */
public class Phase2Launcher {
    public static final String dbPropertiesPath = "phase2/db.properties";
    public static final String CONF = "phase2/importData";
    public static final String OUTPUT = "phase2/graphs";

    public static void main(String[] args) throws Exception {
        Properties dbProperties = new Properties();
        dbProperties.load(new FileInputStream(new File(dbPropertiesPath)));

        String host = dbProperties.getProperty("host");
        String db = dbProperties.getProperty("db");
        String user = dbProperties.getProperty("user");
        String pass = dbProperties.getProperty("pass");
        int port = Integer.valueOf(dbProperties.getProperty("port"));

        GraphImporter importer = new GraphImporter(host, db, user, pass, port, new File(CONF), new File(OUTPUT), new Phase2Logger());
        importer.execute();

    }
}
