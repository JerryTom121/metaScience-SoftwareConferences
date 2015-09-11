package som.metascience;

import java.io.File;
import java.util.List;

/**
 * Created by jcanovasi on 07/09/2015.
 */
public class MetricData {
    private String name;
    private String fullName;
    private String rank;
    private File sourceFile;
    private String sourceInfo;
    private String sourceIdInfo;
    private List<Integer> editions;

    public MetricData(String name, String fullName, String rank, File sourceFile, String sourceInfo, String sourceIdInfo, List<Integer> editions) {
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
        //selecting only the last 5 editions
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
}
