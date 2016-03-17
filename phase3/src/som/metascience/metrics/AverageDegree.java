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
import org.gephi.statistics.plugin.Degree;
import org.openide.util.Lookup;
import som.metascience.MetricData;

import java.io.File;
import java.util.List;

/**
 * Calculates the average degree of the co-authorship graph.
 *
 * The metric calculates two sets of values:
 * <ol>
 *     <li>Average degree per edition of a conference</li>
 *     <li>Average value of the average degree for the full timespan considered</li>
 * </ol>
 *
 * By default, we consider 5 years of period of time to be analyzed
 *
 * This class relies on the Gephi libraries to do the calculations.
 */
public class AverageDegree extends Metric {
    /**
     * Constructs the {@link AverageDegree} class
     * @param metricData Main metric information for performing the calculations
     */
    public AverageDegree(MetricData metricData) {
        super(metricData);
    }

    @Override
    public String getResult() {
        String avgDegreeFull = calculateAverageDegree(metricData.getFullGraph());
        String avgDegreeEdition1 = calculateAverageDegree(metricData.getEditionGraphs().get(0));
        String avgDegreeEdition2 = calculateAverageDegree(metricData.getEditionGraphs().get(1));
        String avgDegreeEdition3 = calculateAverageDegree(metricData.getEditionGraphs().get(2));
        String avgDegreeEdition4 = calculateAverageDegree(metricData.getEditionGraphs().get(3));
        String avgDegreeEdition5 = calculateAverageDegree(metricData.getEditionGraphs().get(4));

        return avgDegreeEdition1 + "," + avgDegreeEdition2 + "," + avgDegreeEdition3 + "," + avgDegreeEdition4 + "," + avgDegreeEdition5 + "," + avgDegreeFull;
    }

    /**
     * Calculates the average degree of a graph.
     *
     * This implementation relies on the Gephi libraries
     *
     * @param graph The path to the graph
     * @return The result of the metric
     */
     public String calculateAverageDegree(File graph) {
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

        // Average Degree
        Degree degree = new Degree();
        degree.execute(gm, am);
        return String.valueOf(degree.getAverageDegree());
    }
}
