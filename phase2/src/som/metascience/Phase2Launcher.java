package som.metascience;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * Main access point to the Phase 2. This phase is in charge of generating the co-authorships graphs used to calculate
 * the metrics (defined and executed in phase 3).
 *
 * This phase receives as input an String pointing at a folder where a set of property files have to be located. Each
 * property file includes the required information to build the co-authorship graphs for a specific conference. As
 * output, a co-authorship graph (in GEXF format) is generated per conference.
 *
 * In particular, each property file has to include, at least, the following properties:
 * <ul>
 *     <li><em>conferenceName</em>. The full name of the conference</li>
 *     <li><em>editions</em>. The total number of editions</li>
 *     <li><em>editionQueries</em>. The year for each edition</li>
 *     <li><em>sources</em>. The string/s used in DBLP to index the conference</li>
 *     <li><em>source_ids</em>. The string/s used in DBLP to index the sub-conference</li>
 *     <li><em>fullNodesLast5editions</em>. The SQL query to launch in DBLP database to get all the nodes
 *     (i.e., authors) of the co-authorship graph</li>
 *     <li><em>fullEdgesLast5editions</em>. The SQL query to launch in DBLP database to get all the edges
 *     (i.e., co-authorship relations) of the co-authorship graph</li>
 * </ul>
 *
 * Optionally, the property can include:
 * <ul>
 *     <li><em>rank</em>. The CORE rank</li>
 *     <li><em>SingleEditionLast5EditionsXXXXNodes</em>The SQL query to launch in DBLP database to get all the nodes
 *     (i.e., authors) of the co-authorship graph for the edition taken place in the XXXX year</li>
 *     <li><em>SingleEditionLast5EditionsXXXXEdges</em>The SQL query to launch in DBLP database to get all the edges
 *     (i.e., co-authorship relations) of the co-authorship graph for the edition taken place in the XXXX year</li>
 *     <li><em>UntilEditionLast5EditionsXXXXNodes</em>The SQL query to launch in DBLP database to get all the nodes
 *     (i.e., authors) of the co-authorship graph until the edition taken place in the XXXX year</li>
 *     <li><em>UntilEditionLast5EditionsXXXXEdges</em>The SQL query to launch in DBLP database to get all the edges
 *     (i.e., co-authorship relations) of the co-authorship graph until the edition taken place in the XXXX year</li>
 * </ul>
 *
 * As the data is obtained from the DBLP database, the connection properties have to be declared in the file
 * db.properties, thus including values for the keys: host, db, user, pass and port.
 */
public class Phase2Launcher {
    /**
     * The property file with database credentials
     */
    public static final String dbPropertiesPath = "phase2/db.properties";
    /**
     * String path to the folder containing the property files for the co-authorship graphs
     */
    public static final String CONF = "data/importData";
    /**
     * String path where the co-authorship graphs will be stored.
     */
    public static final String OUTPUT = "data/graphs";

    public static void main(String[] args) throws Exception {
        Properties dbProperties = new Properties();
        dbProperties.load(new FileInputStream(new File(dbPropertiesPath)));

        String host = dbProperties.getProperty("host");
        String db = dbProperties.getProperty("db");
        String user = dbProperties.getProperty("user");
        String pass = dbProperties.getProperty("pass");
        int port = Integer.valueOf(dbProperties.getProperty("port"));

        long startTime = System.currentTimeMillis();

        GraphImporter importer = new GraphImporter(host, db, user, pass, port, new File(CONF), new File(OUTPUT), new Phase2Logger());
        importer.execute();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }
}

