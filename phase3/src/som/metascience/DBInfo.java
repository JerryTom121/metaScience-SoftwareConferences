package som.metascience;

/**
 * Basic POJO for DB credentials
 */
public class DBInfo {
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPass;
    private int dbPort;

    public DBInfo(String dbHost, String dbName, String dbUser, String dbPass, int dbPort) {
        if(dbHost == null || dbHost.equals("") || dbName == null || dbName.equals("") || dbUser == null || dbUser.equals("") || dbPass == null || dbPass.equals("") || dbPort < 0 || dbPort > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The database configuration data is not correct");
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbPort = dbPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public int getDbPort() {
        return dbPort;
    }
}
