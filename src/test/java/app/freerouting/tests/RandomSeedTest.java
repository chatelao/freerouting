package app.freerouting.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.freerouting.IO;
import app.freerouting.core.scoring.BoardStatistics;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class RandomSeedTest extends TestBasedOnAnIssue {

  @Test
  void testRandomSeed() {
    // Test with a fixed seed
    long fixedSeed = 12345L;
    BoardStatistics firstStats = null;
    IO.println("Testing with fixed seed: " + fixedSeed);
    for (int i = 0; i < 3; i++) {
      var job = GetRoutingJob("Issue026-J2_reference.dsn", fixedSeed);
      job = RunRoutingJob(job, job.routerSettings);
      BoardStatistics currentStats = job.board.get_statistics();
      if (i == 0) {
        firstStats = currentStats;
      } else {
        assertEquals(firstStats.connections.incompleteCount, currentStats.connections.incompleteCount, "Seeded runs should produce identical results. Run " + (i + 1));
      }
    }

    // Test without a seed
    BoardStatistics previousStats = null;
    boolean foundDifference = false;
    IO.println("Testing without seed.");
    // Increase loop iterations to reduce chance of flaky pass
    for (int i = 0; i < 5; i++) {
      var job = GetRoutingJob("Issue026-J2_reference.dsn"); // No seed
      job = RunRoutingJob(job, job.routerSettings);
      BoardStatistics currentStats = job.board.get_statistics();
      if (i > 0) {
        if (previousStats.connections.incompleteCount != currentStats.connections.incompleteCount) {
          foundDifference = true;
          break;
        }
      }
      previousStats = currentStats;
    }
    assertTrue(foundDifference, "Unseeded runs should produce different results. This might fail by chance, try running again.");
  }

  @Test
  void testRepeatSameInputSeedProducesSameHash() {
    long seed = 987654321L;
    // First run
    var job1 = GetRoutingJob("Issue026-J2_reference.dsn", seed);
    job1 = RunRoutingJob(job1, job1.routerSettings);
    BoardStatistics stats1 = job1.board.get_statistics();

    // Second run with the same input and seed
    var job2 = GetRoutingJob("Issue026-J2_reference.dsn", seed);
    job2 = RunRoutingJob(job2, job2.routerSettings);
    BoardStatistics stats2 = job2.board.get_statistics();

    assertEquals(stats1.connections.incompleteCount, stats2.connections.incompleteCount, "Two runs with the same input and seed should produce identical results.");
  }
}
