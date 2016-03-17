package som.metascience.metrics;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.graph.api.*;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import som.metascience.MetricData;

import java.io.File;
import java.util.HashMap;

/**
 * Calculates the graph modularity classes
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Graph modularity degree per edition of a conference</li>
 *     <li>Average value of the graph modularity for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class GraphModularity extends Metric {
    /**
     * The column to get from the table used by Gephi
     */
    public static final String ATTRIBUTE_COLUMN = "modularity_class";

    /**
     * Constructs the {@link Density} class
     *
     * @param metricData Main metric information for performing the calculations
     */
    public GraphModularity(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String graphModularityFull = calcualteGraphModularity(metricData.getFullGraph());
        String graphModularityEdition1 = calcualteGraphModularity(metricData.getEditionGraphs().get(0));
        String graphModularityEdition2 = calcualteGraphModularity(metricData.getEditionGraphs().get(1));
        String graphModularityEdition3 = calcualteGraphModularity(metricData.getEditionGraphs().get(2));
        String graphModularityEdition4 = calcualteGraphModularity(metricData.getEditionGraphs().get(3));
        String graphModularityEdition5 = calcualteGraphModularity(metricData.getEditionGraphs().get(4));

        return graphModularityEdition1 + "," + graphModularityEdition2 + "," + graphModularityEdition3 + "," + graphModularityEdition4 + "," + graphModularityEdition5 + "," + graphModularityFull;
    }

    /**
     * Calculates the graph modularity classes
     *
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calcualteGraphModularity(File graph) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        // Import file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            container = importController.importFile(graph);
            container.setAutoScale(false);
            container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force DIRECTED
            container.setAllowAutoNode(false);  //Don't create missing nodes
            importController.process(container, new DefaultProcessor(), workspace);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();

        // Graph modularity
        Modularity modularity = new Modularity();
        //modularity.setRandom(true);
        //modularity.setUseWeight(true);
        //modularity.setResolution(1.0);
        modularity.execute(gm, am);

        //AttributeTable at = am.getTable(ATTRIBUTE_TABLE);
        //AttributeColumn ac = at.getColumn(ATTRIBUTE_COLUMN);

        NodeIterable ni = gm.getGraph().getNodes();
        HashMap<Integer, Integer> classes = new HashMap<>();
        for(Node node : ni.toArray()) {
            Integer modularityClass = (Integer) node.getAttributes().getValue(ATTRIBUTE_COLUMN);
            classes.put(modularityClass, null);
        }
        int totalClasses = classes.keySet().size();
        return String.valueOf(totalClasses);
    }

}

