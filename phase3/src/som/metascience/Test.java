package som.metascience;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Test {

    public static void main(String[] args) throws Exception {
        selectedConferencesTableGenerator();
    }

    public static void selectedConferencesTableGenerator() {
        File confPath = new File("data/importData");

        String finalString = "";
        for(File file : confPath.listFiles()) {
            if(file.getName().endsWith(".properties")) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file));

                    // Getting acronyms
                    String conferenceName = properties.getProperty("conferenceName");
                    String source = properties.getProperty("sources");
                    source = source.substring(1, source.length()-1);
                    String rank = properties.getProperty("rank");
                    String totalEditions = properties.getProperty("editions");
                    String editions = properties.getProperty("editionQueries");

                    String fiveEditions = "";
                    int counter = 0;
                    for(String edition : editions.split(",")) {
                        fiveEditions += edition + ", ";
                        if(counter++ == 4) break;
                    }
                    fiveEditions = fiveEditions.substring(0, fiveEditions.length()-2);

                    finalString = finalString + conferenceName + " & " + source +  " & " + rank + " & " + totalEditions + " & " + fiveEditions + " \\\\ \\hline \n" ;

                } catch (IOException e) {
                    System.err.println("Error");
                }
            }
        }
        System.out.println(finalString);
    }

    public static void selectGenerator() {
        File confPath = new File("data/importData");

        String finalString = "";
        for(File file : confPath.listFiles()) {
            if(file.getName().endsWith(".properties")) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(file));

                    // Getting acronyms
                    String sourceInfo = properties.getProperty("sources");
                    String sourceIdInfo = properties.getProperty("source_ids");

                    finalString = finalString + "((source,source_id) = (" + sourceInfo + "," + sourceIdInfo + ")) OR \n" ;

                } catch (IOException e) {
                    System.err.println("Error");
                }
            }
        }
        finalString = "(" + finalString.substring(0, finalString.length()-4) + ")";
        System.out.println(finalString);
    }
}
