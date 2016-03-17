package som.metascience.metrics;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
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
 * Calculates the metric Prominent Figures in a conference. A prominent figure is an author with 5 or more papers in
 * the co-authorship graph. The metric is usually calculated in the full co-authorship graph and returns a percentage
 * value
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Number of prominent figures per edition of a conference</li>
 *     <li>Average value of the number of prominent figures for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class ProminentFigures extends Metric {
    /**
     * The threshold to be considered prominent
     */
    public static float THRESHOLD = 5.0f;

    /**
     * Constructs the {@link ProminentFigures} class
     * @param metricData Main metric information for performing the calculations
     */
    public ProminentFigures(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String graphProminentFiguresFull = calculateProminentFigures(metricData.getFullGraph());
        String ProminentFiguresEdition1 = calculateProminentFigures(metricData.getEditionGraphs().get(0));
        String ProminentFiguresEdition2 = calculateProminentFigures(metricData.getEditionGraphs().get(1));
        String ProminentFiguresEdition3 = calculateProminentFigures(metricData.getEditionGraphs().get(2));
        String ProminentFiguresEdition4 = calculateProminentFigures(metricData.getEditionGraphs().get(3));
        String ProminentFiguresEdition5 = calculateProminentFigures(metricData.getEditionGraphs().get(4));

        return ProminentFiguresEdition1 + "," + ProminentFiguresEdition2 + "," + ProminentFiguresEdition3 + "," + ProminentFiguresEdition4 + "," + ProminentFiguresEdition5 + "," + graphProminentFiguresFull;
    }

    /**
     * Calculates the prominent figures in the graph
     * @param graph The path to the graph
     * @return The result of the metric
     */
    public String calculateProminentFigures(File graph) {
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

        NodeIterable ni = gm.getGraph().getNodes();
        int prominentFigures = 0;
        for(Node node : ni.toArray()) {
            if(node.getNodeData().getSize() >= THRESHOLD)
                prominentFigures++;
        }
        float ratio = ((float) prominentFigures / (float) gm.getGraph().getNodeCount())*100;

        return String.format("%.3f", ratio).replace(",", ".");
    }
}
