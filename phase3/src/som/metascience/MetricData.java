package som.metascience;

import java.io.File;

/**
 * Created by jcanovasi on 07/09/2015.
 */
public class MetricData {
    private String name;
    private String fullName;
    private String rank;
    private File source;
    private int editions;

    public MetricData(String name, String fullName, String rank, File source, int editions) {
        if(name == null)
            throw new IllegalArgumentException("The name of the import data cannot be null");
        if(fullName == null)
            throw new IllegalArgumentException("The fullName of the import data cannot be null");
        if(rank == null)
            throw new IllegalArgumentException("The rank of the import data cannot be null");
        if(source == null)
            throw new IllegalArgumentException("The source file cannot be null");
        if(editions < 0)
            throw new IllegalArgumentException("The number of editions must be positive");

        this.name = name;
        this.fullName = fullName;
        this.source = source;
        this.editions = editions;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public File getSource() {
        return source;
    }

    public String getRank() {
        return rank;
    }

    public int getEditions() {
        return editions;
    }
}
