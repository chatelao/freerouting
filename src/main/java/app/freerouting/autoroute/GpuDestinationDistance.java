package app.freerouting.autoroute;

import com.aparapi.Kernel;
import com.aparapi.Range;

final class GpuDestinationDistance {

  private GpuDestinationDistance() {}

  static float[] min_weighted_distances(
      int[] p_box, float[] p_dest_boxes, float p_x_factor, float p_y_factor) {
    float[] results = new float[p_dest_boxes.length / 4];
    if (results.length == 0) {
      return results;
    }
    DistanceKernel kernel = new DistanceKernel(p_box, p_dest_boxes, p_x_factor, p_y_factor, results);
    kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
    kernel.execute(Range.create(results.length));
    kernel.dispose();
    return results;
  }

  private static class DistanceKernel extends Kernel {
    private final int[] p_box;
    private final float[] p_dest_boxes;
    private final float p_x_factor;
    private final float p_y_factor;
    private final float[] results;

    public DistanceKernel(
        int[] p_box, float[] p_dest_boxes, float p_x_factor, float p_y_factor, float[] results) {
      this.p_box = p_box;
      this.p_dest_boxes = p_dest_boxes;
      this.p_x_factor = p_x_factor;
      this.p_y_factor = p_y_factor;
      this.results = results;
    }

    @Override
    public void run() {
      int gid = getGlobalId();
      int offset = gid * 4;

      // translated from IntBox.weighted_distance
      float dx;
      if (p_box[0] > p_dest_boxes[offset + 2]) { // this.ll.x > p_other.ur.x
        dx = p_box[0] - p_dest_boxes[offset + 2];
      } else if (p_box[2] < p_dest_boxes[offset]) { // this.ur.x < p_other.ll.x
        dx = p_dest_boxes[offset] - p_box[2];
      } else {
        dx = 0;
      }

      float dy;
      if (p_box[1] > p_dest_boxes[offset + 3]) { // this.ll.y > p_other.ur.y
        dy = p_box[1] - p_dest_boxes[offset + 3];
      } else if (p_box[3] < p_dest_boxes[offset + 1]) { // this.ur.y < p_other.ll.y
        dy = p_dest_boxes[offset + 1] - p_box[3];
      } else {
        dy = 0;
      }
      results[gid] = dx * p_x_factor + dy * p_y_factor;
    }
  }
}
