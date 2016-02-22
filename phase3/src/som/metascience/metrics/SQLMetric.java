package som.metascience.metrics;

import som.metascience.DBInfo;
import som.metascience.MetricData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Common implementation for metrics using SQL to do the calculations
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
    protected final int filter_num_pages = 5;

    public SQLMetric(MetricData metricData, DBInfo dbInfo) {
        super(metricData);
        if(dbInfo == null)
            throw new IllegalArgumentException("The database info cannot be null");

        this.dbInfo = dbInfo;
        this.conn = getConnection();
    }

    /**
     * Obtains a connection to the databse
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
