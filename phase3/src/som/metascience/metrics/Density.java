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
 * Calculates the graph density
 */
public class Density extends Metric {

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

        return graphDensityFull + "," + graphDensityEdition1 + "," + graphDensityEdition2 + "," + graphDensityEdition3 + "," + graphDensityEdition4 + "," + graphDensityEdition5;
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
