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
import org.openide.util.Lookup;
import org.gephi.statistics.plugin.GraphDensity;
import som.metascience.MetricData;

import java.io.File;

/**
 * Calculates the graph density.
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Graph density degree per edition of a conference</li>
 *     <li>Average value of the graph density for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class Density extends Metric {
    /**
     * Constructs the {@link Density} class
     * @param metricData Main metric information for performing the calculations
     */
    public Density(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String graphDensityFull = calculateGraphDensity(metricData.getFullGraph());
        String graphDensityEdition1 = calculateGraphDensity(metricData.getEditionGraphs().get(0));
        String graphDensityEdition2 = calculateGraphDensity(metricData.getEditionGraphs().get(1));
        String graphDensityEdition3 = calculateGraphDensity(metricData.getEditionGraphs().get(2));
        String graphDensityEdition4 = calculateGraphDensity(metricData.getEditionGraphs().get(3));
        String graphDensityEdition5 = calculateGraphDensity(metricData.getEditionGraphs().get(4));

        return graphDensityEdition1 + "," + graphDensityEdition2 + "," + graphDensityEdition3 + "," + graphDensityEdition4 + "," + graphDensityEdition5 + "," + graphDensityFull;
    }


    /**
     * Calculates the graph density
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateGraphDensity(File graph) {
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

        // Graph density
        GraphDensity graphDensity = new GraphDensity();
        graphDensity.execute(gm, am);
        return String.valueOf(graphDensity.getDensity());
    }
}
