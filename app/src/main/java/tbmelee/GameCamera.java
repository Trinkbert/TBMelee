
package tbmelee;

import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;

public class GameCamera {

    private final Camera camera;

    public GameCamera(Camera camera) {
        this.camera = camera;
    }

    public void setup() {
        // Test different camera setups
        /*
         * camera.setParallelProjection(true);
         * camera.setLocation(new Vector3f(22f, 16f, 22f));
         * camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
         */

        Vector3f target = new Vector3f(10f, -1f, 10f);
        camera.setLocation(target.add(22f, 22f, 22f));
        camera.lookAt(target, Vector3f.UNIT_Y);

        camera.setParallelProjection(true);
        camera.setFrustum(1f, 1000f, -21.33f, 21.33f, 12f, -12f);
    }

}
