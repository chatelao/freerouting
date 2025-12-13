package app.freerouting.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Issue558Test extends TestBasedOnAnIssue
{
  @Test
  @Disabled("Failing on CI: java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3")
  void test_Issue_558_Clearance_violation_at_board_edge()
  {
    var job = GetRoutingJob("Issue558-dev-board.dsn");

    job = RunRoutingJob(job, job.routerSettings);

    var statsAfter = GetBoardStatistics(job);

    assertEquals(0, statsAfter.connections.incompleteCount, "The incomplete count should be 0");
    assertEquals(0, statsAfter.clearanceViolations.totalCount, "The total count of clearance violations should be 0");
  }
}