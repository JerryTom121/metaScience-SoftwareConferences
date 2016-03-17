package som.metascience.metrics;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import som.metascience.MetricData;

import java.io.File;

/**
 * Calculate the average path length of the co-authorship graph.
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Average path length per edition of a conference</li>
 *     <li>Average value of the average path length for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class AveragePathLength extends Metric {
    /**
     * Constructs the {@link AveragePathLength} class
     * @param metricData Main metric information for performing the calculations
     */
    public AveragePathLength(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String graphDistanceFull = calculateGraphDistance(metricData.getFullGraph());
        String graphDistanceEdition1 = calculateGraphDistance(metricData.getEditionGraphs().get(0));
        String graphDistanceEdition2 = calculateGraphDistance(metricData.getEditionGraphs().get(1));
        String graphDistanceEdition3 = calculateGraphDistance(metricData.getEditionGraphs().get(2));
        String graphDistanceEdition4 = calculateGraphDistance(metricData.getEditionGraphs().get(3));
        String graphDistanceEdition5 = calculateGraphDistance(metricData.getEditionGraphs().get(4));

        return graphDistanceEdition1 + "," + graphDistanceEdition2 + "," + graphDistanceEdition3 + "," + graphDistanceEdition4 + "," + graphDistanceEdition5 + "," + graphDistanceFull;
    }

    /**
     * Calculates the average path length
     *
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateGraphDistance(File graph) {
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

        // Graph Distance
        GraphDistance distance = new GraphDistance();
        distance.setDirected(false);
        distance.execute(gm, am);

        return String.valueOf(distance.getPathLength());
    }

}
