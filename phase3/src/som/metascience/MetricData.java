package som.metascience;

import java.io.File;
import java.util.List;

/**
 * This class encapsulates the info needed to calculate the metrics for each conference
 *
 */
public class MetricData {
    /**
     * Short name for the conference
     */
    private String name;

    /**
     * Full name of the conference
     */
    private String fullName;

    /**
     * Rank of the conference
     */
    private String rank;

    /**
     * The file which populates this class
     */
    private File sourceFile;

    /**
     * Acronym of the conference
     */
    private String sourceInfo;

    /**
     * Acronym of the conference in DBLP
     */
    private String sourceIdInfo;

    /**
     * List of years of previous editions
     */
    private List<Integer> editions;

    /**
     * Path to the full graph
     */
    private File fullGraph;

    /**
     * Path to the edition graphs
     */
    private List<File> editionGraphs;

    public MetricData(String name, String fullName, String rank, File sourceFile, String sourceInfo, String sourceIdInfo, List<Integer> editions, File fullGraph, List<File> editionGraphs) {
        if(name == null)
            throw new IllegalArgumentException("The name of the import data cannot be null");
        if(fullName == null)
            throw new IllegalArgumentException("The fullName of the import data cannot be null");
        if(rank == null)
            throw new IllegalArgumentException("The rank of the import data cannot be null");
        if(sourceFile == null)
            throw new IllegalArgumentException("The source file cannot be null");
        if(editions.isEmpty())
            throw new IllegalArgumentException("The number of editions must be positive");

        this.name = name;
        this.fullName = fullName;
        this.sourceFile = sourceFile;

        this.fullGraph = fullGraph;
        this.editionGraphs = editionGraphs;

        // selecting only the last 5 editions
        this.editions = editions.subList(0, 5);
        this.sourceInfo = sourceInfo;
        this.sourceIdInfo = sourceIdInfo;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getRank() {
        return rank;
    }

    public List<Integer> getEditions() {
        return editions;
    }

    public String getSourceInfo() { return sourceInfo; }

    public String getSourceIdInfo() { return sourceIdInfo; }

    public File getFullGraph() {
        return fullGraph;
    }

    public List<File> getEditionGraphs() {
        return editionGraphs;
    }
}
