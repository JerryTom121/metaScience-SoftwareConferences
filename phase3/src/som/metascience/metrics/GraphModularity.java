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
 */
public class GraphModularity extends Metric {
    /**
     * Attribute table in the graph holding the info for the nodes (it depends on the language of the system)
     */
    public static final String ATTRIBUTE_TABLE = "Nodos";

    /**
     * The column to get from the table
     */
    public static final String ATTRIBUTE_COLUMN = "modularity_class";


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

        return graphModularityFull + "," + graphModularityEdition1 + "," + graphModularityEdition2 + "," + graphModularityEdition3 + "," + graphModularityEdition4 + "," + graphModularityEdition5;
    }

    /**
     * Calculates the graph modularity classes
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

        AttributeTable at = am.getTable(ATTRIBUTE_TABLE);
        AttributeColumn ac = at.getColumn(ATTRIBUTE_COLUMN);

        NodeIterable ni = gm.getGraph().getNodes();
        HashMap<Integer, Integer> classes = new HashMap<>();
        for(Node node : ni.toArray()) {
            Integer modularityClass = (Integer) node.getAttributes().getValue(ac.getId());
            classes.put(modularityClass, null);
        }
        int totalClasses = classes.keySet().size();
        return String.valueOf(totalClasses);
    }

}

