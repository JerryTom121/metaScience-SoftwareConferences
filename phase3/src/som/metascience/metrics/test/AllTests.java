package som.metascience.metrics.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Launches the full set of test cases
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AverageDegreeTest.class, AveragePathLengthTest.class, ConnectedComponentsTest.class, DensityTest.class, GraphModularityTest.class, ProminentFiguresTest.class
})
public class AllTests {
}
