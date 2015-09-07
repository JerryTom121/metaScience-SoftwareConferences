package som.metascience;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsules the info needed to retrieve the graph data for each conference.
 *
 * Queries to get the full graph are required but queries to get specific editions are optional
 */
public class GraphImportData {
    private String name;
    private File source;

    private String fullNodesQuery;
    private String fullEdgesQuery;
    private List<String> editionNodesQuery;
    private List<String> editionEdgesQuery;

    /**
     * Constructs a new set of import data
     *
     * @param name The name of the conference
     * @param fullNodesQuery The query to get the full graph nodes
     * @param fullEdgesQuery The query to get the full graph edges
     */
    GraphImportData(String name, File source, String fullNodesQuery, String fullEdgesQuery) {
        if(name == null)
            throw new IllegalArgumentException("The name of the import data cannot be null");
        if(source == null)
            throw new IllegalArgumentException("The source file cannot be null");
        if(fullNodesQuery == null || fullNodesQuery.equals("") || fullEdgesQuery == null || fullEdgesQuery.equals(""))
            throw new IllegalArgumentException("The queries to get nodes/edges cannot be null or empty");

        this.name = name;
        this.source = source;
        this.fullNodesQuery = fullNodesQuery;
        this.fullEdgesQuery = fullEdgesQuery;
        this.editionNodesQuery = new ArrayList<String>();
        this.editionEdgesQuery = new ArrayList<String>();
    }


    public void addEditionQuery(String nodesQuery, String edgesQuery) {
        if(nodesQuery == null || nodesQuery.equals("") || edgesQuery == null || edgesQuery.equals(""))
            throw new IllegalArgumentException("The query for nodes/edges cannot be null or empty");

        this.editionNodesQuery.add(nodesQuery);
        this.editionEdgesQuery.add(edgesQuery);
    }

    public String getName() {
        return name;
    }

    public File getSource() {
        return source;
    }

    public String getFullNodesQuery() {
        return fullNodesQuery;
    }

    public String getFullEdgesQuery() {
        return fullEdgesQuery;
    }

    public int getEditions() {
        return this.editionNodesQuery.size();
    }

    public String getEditionNodesQuery(int position) {
        if(position < 0 || position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The edition must be a positive integer value");
        if(position > this.editionNodesQuery.size())
            throw new IllegalArgumentException("The edition must have been set before");
        return editionNodesQuery.get(position);
    }

    public String getEditionEdgesQuery(int position) {
        if(position < 0 || position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("The edition must be a positive integer value");
        if(position > this.editionEdgesQuery.size())
            throw new IllegalArgumentException("The edition must have been set before");
        return editionEdgesQuery.get(position);
    }
}
