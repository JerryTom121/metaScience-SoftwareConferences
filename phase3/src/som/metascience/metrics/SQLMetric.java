package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Common implementation for metrics using SQL to do calculations
 *
 * @author Javier Canovas (me@jlcanovas.es)
 */
public abstract class SQLMetric extends Metric {
    /**
     * Basic information to connect the database
     */
    DBInfo dbInfo;

    /**
     * Connection to the database. Built once the object is constructed
     * and kept alive all the live cycle
     */
    Connection conn;

    /**
     * Constraint to filter the minimum number of pages for the papers involved in the calculations of the
     * metrics
     */
    protected final int FILTER_NUM_PAGES = 5;

    /**
     * Constructs a new {@link SQLMetric}
     *
     * @param metricData Basic information for the metric
     * @param dbInfo Database credentials
     */
    public SQLMetric(MetricData metricData, DBInfo dbInfo) {
        super(metricData);
        if(dbInfo == null)
            throw new IllegalArgumentException("The database info cannot be null");

        this.dbInfo = dbInfo;
        this.conn = getConnection();
    }

    /**
     * Obtains a connection to the database
     *
     * @return The connection to the database
     */
    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.dbInfo.getDbUser());
            connectionProps.put("password", this.dbInfo.getDbPass());
            conn = DriverManager.getConnection("jdbc:mysql://" + this.dbInfo.getDbHost() + ":" + String.valueOf(this.dbInfo.getDbPort()) + "/" + this.dbInfo.getDbName(), connectionProps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

}
