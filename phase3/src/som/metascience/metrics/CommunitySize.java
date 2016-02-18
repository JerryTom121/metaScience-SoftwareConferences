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
 * Calculates the size of the community (number of unique authors of the 5-edition conference graph)
 */
public class CommunitySize extends Metric {

    public CommunitySize(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String communitySize = calculateCommunitySize(metricData.getFullGraph());

        return communitySize;
    }


    /**
     * Calculates the graph density
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
