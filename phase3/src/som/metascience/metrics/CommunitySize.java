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
import som.metascience.MetricData;

import java.io.File;

/**
 * Calculates the size of the community (number of unique authors of co-authorship graph representing the
 * full timespan).
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class CommunitySize extends Metric {
    /**
     * Constructs the {@link CommunitySize} class
     * @param metricData Main metric information for performing the calculations
     */
    public CommunitySize(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String communitySize = calculateCommunitySize(metricData.getFullGraph());

        return communitySize;
    }

    /**
     * Calculates the community size for a graph
     *
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateCommunitySize(File graph) {
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

        // Community size
        int nodeCount = gm.getGraph().getNodeCount();
        return String.valueOf(nodeCount);
    }
}
