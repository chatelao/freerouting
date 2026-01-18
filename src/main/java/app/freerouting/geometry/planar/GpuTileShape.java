package app.freerouting.geometry.planar;

import com.aparapi.Kernel;
import com.aparapi.Range;

public final class GpuTileShape {

  private GpuTileShape() {}

  public static boolean[] intersects(TileShape p_shape, TileShape[] p_other_shapes) {
    boolean[] results = new boolean[p_other_shapes.length];
    if (p_other_shapes.length == 0) {
      return results;
    }

    float[] shape_a_box = shapeToBbox(p_shape);

    float[] shapes_b_boxes = new float[p_other_shapes.length * 4];
    for (int i = 0; i < p_other_shapes.length; i++) {
      float[] box = shapeToBbox(p_other_shapes[i]);
      System.arraycopy(box, 0, shapes_b_boxes, i * 4, 4);
    }

    IntersectionKernel kernel = new IntersectionKernel(shape_a_box, shapes_b_boxes, results);

    kernel.execute(Range.create(p_other_shapes.length));
    kernel.dispose();

    return results;
  }

  private static float[] shapeToBbox(TileShape shape) {
    IntBox box = shape.bounding_box();
    return new float[] {box.ll.x, box.ll.y, box.ur.x, box.ur.y};
  }

  private static class IntersectionKernel extends Kernel {
    private final float[] shape_a_box;
    private final float[] shapes_b_boxes;
    private final boolean[] results;

    public IntersectionKernel(
        float[] shape_a_box, float[] shapes_b_boxes, boolean[] results) {
      this.shape_a_box = shape_a_box;
      this.shapes_b_boxes = shapes_b_boxes;
      this.results = results;
    }

    @Override
    public void run() {
      int gid = getGlobalId();
      int offset = gid * 4;

      float minAx = shape_a_box[0];
      float minAy = shape_a_box[1];
      float maxAx = shape_a_box[2];
      float maxAy = shape_a_box[3];

      float minBx = shapes_b_boxes[offset];
      float minBy = shapes_b_boxes[offset + 1];
      float maxBx = shapes_b_boxes[offset + 2];
      float maxBy = shapes_b_boxes[offset + 3];

      results[gid] = (minAx < maxBx && maxAx > minBx && minAy < maxBy && maxAy > minBy);
    }
  }
}
