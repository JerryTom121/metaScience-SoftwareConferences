package som.metascience.metrics.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AverageDegreeTest.class, AveragePathLengthTest.class, ConnectedComponentsTest.class, DensityTest.class, GraphModularityTest.class})
public class AllTests {
}
