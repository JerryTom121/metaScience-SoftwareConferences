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
 * Calculates the number of connected components
 */
public class ConnectedComponents extends Metric {

    public ConnectedComponents(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String graphComponentsFull = calculateGraphComponents(metricData.getFullGraph());
        String graphComponentsEdition1 = calculateGraphComponents(metricData.getEditionGraphs().get(0));
        String graphComponentsEdition2 = calculateGraphComponents(metricData.getEditionGraphs().get(1));
        String graphComponentsEdition3 = calculateGraphComponents(metricData.getEditionGraphs().get(2));
        String graphComponentsEdition4 = calculateGraphComponents(metricData.getEditionGraphs().get(3));
        String graphComponentsEdition5 = calculateGraphComponents(metricData.getEditionGraphs().get(4));

        return graphComponentsFull + "," + graphComponentsEdition1 + "," + graphComponentsEdition2 + "," + graphComponentsEdition3 + "," + graphComponentsEdition4 + "," + graphComponentsEdition5;
    }

    /**
     * Calculates the graph componens
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateGraphComponents(File graph) {
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

        // Graph Connected Components
        org.gephi.statistics.plugin.ConnectedComponents cc = new org.gephi.statistics.plugin.ConnectedComponents();
        cc.execute(gm, am);

        return String.valueOf(cc.getConnectedComponentsCount());
    }
}
