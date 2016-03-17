package som.metascience;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the info needed to retrieve the graph data for each conference.
 *
 * Queries to get the full graph are required (used in the constructor) but queries to get specific editions are
 * optional (they have to be added by calling {@link GraphImportData#addEditionQuery(String, String)})
 */
public class GraphImportData {
    /**
     * Short name for the conference
     */
    private String name;

    /**
     * Full name of the conference
     */
    private String fullName;

    /**
     * The file which populates this class
     */
    private File source;

    /**
     * The query to execute to get the full graph (nodes)
     */
    private String fullNodesQuery;

    /**
     * The query to execute to get the full graph (edges)
     */
    private String fullEdgesQuery;

    /**
     * The queries to get particular editions (nodes)
     */
    private List<String> editionNodesQuery;

    /**
     * The queries to get particular editions (edges)
     */
    private List<String> editionEdgesQuery;

    /**
     * Constructs a new set of import data
     *
     * @param name The name of the conference
     * @param fullNodesQuery The query to get the full graph nodes
     * @param fullEdgesQuery The query to get the full graph edges
     */
    GraphImportData(String name, String fullName, File source, String fullNodesQuery, String fullEdgesQuery) {
        if(name == null)
            throw new IllegalArgumentException("The name of the import data cannot be null");
        if(fullName == null)
            throw new IllegalArgumentException("The fullName of the import data cannot be null");
        if(source == null)
            throw new IllegalArgumentException("The source file cannot be null");
        if(fullNodesQuery == null || fullNodesQuery.equals("") || fullEdgesQuery == null || fullEdgesQuery.equals(""))
            throw new IllegalArgumentException("The queries to get nodes/edges cannot be null or empty");

        this.name = name;
        this.fullName = fullName;
        this.source = source;
        this.fullNodesQuery = fullNodesQuery;
        this.fullEdgesQuery = fullEdgesQuery;
        this.editionNodesQuery = new ArrayList<String>();
        this.editionEdgesQuery = new ArrayList<String>();
    }

    /**
     * Adds a new pair of queries for a particular edition
     *
     * @param nodesQuery SQL query to get the nodes
     * @param edgesQuery SQL query to get the edges
     */
    public void addEditionQuery(String nodesQuery, String edgesQuery) {
        if(nodesQuery == null || nodesQuery.equals("") || edgesQuery == null || edgesQuery.equals(""))
            throw new IllegalArgumentException("The query for nodes/edges cannot be null or empty");

        this.editionNodesQuery.add(nodesQuery);
        this.editionEdgesQuery.add(edgesQuery);
    }

    /**
     * Returns the name of the conference
     *
     * @return String The name of the conference
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link File} for the property file
     *
     * @return The {@link File} for the property file
     */
    public File getSource() {
        return source;
    }

    /**
     * Gets the SQL query to to get all the nodes (i.e., authors) of the co-authorship graph
     *
     * @return String of the SQL query
     */
    public String getFullNodesQuery() {
        return fullNodesQuery;
    }

    /**
     * Gets the SQL query to to get all the edges (i.e., co-authorship relations) of the co-authorship graph
     *
     * @return String of the SQL query
     */
    public String getFullEdgesQuery() {
        return fullEdgesQuery;
    }

    /**
     * Returns the number of editions added (with {@link GraphImportData#addEditionQuery(String, String)})
     *
     * @return Number of editions
     */
    public int getEditions() {
        return this.editionNodesQuery.size();
    }

    /**
     * Returns the specific query to get the nodes of a particular edition (previously added with
     * {@link GraphImportData#addEditionQuery(String, String)})
     *
     * @return String of the SQL query
     */
    public String getEditionNodesQuery(int position) {
        if(position < 0 || position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The edition must be a positive integer value");
        if(position > this.editionNodesQuery.size())
            throw new IllegalArgumentException("The edition must have been set before");
        return editionNodesQuery.get(position);
    }

    /**
     * Returns the specific query to get the edges of a particular edition (previously added with
     * {@link GraphImportData#addEditionQuery(String, String)})
     *
     * @return String of the SQL query
     */
    public String getEditionEdgesQuery(int position) {
        if(position < 0 || position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The edition must be a positive integer value");
        if(position > this.editionEdgesQuery.size())
            throw new IllegalArgumentException("The edition must have been set before");
        return editionEdgesQuery.get(position);
    }
}
