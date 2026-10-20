
package tbmelee;

import com.jme3.renderer.Camera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class GameCamera {

    private final Vector3f target = new Vector3f(10f, -1f, 10f);
    private float azimuth;
    private float elevation;
    private float orbitRadius;

    private float halfHeight = 12f;

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

        //Vector3f target = new Vector3f(10f, -1f, 10f);
        camera.setLocation(target.add(22f, 22f, 22f));
        camera.lookAt(target, Vector3f.UNIT_Y);

        Vector3f offset = camera.getLocation().subtract(target);
        orbitRadius = offset.length();
        azimuth = FastMath.atan2(offset.x, offset.z);
        elevation = FastMath.asin(offset.y / orbitRadius);

        camera.setParallelProjection(true);
        updateFrustum();
        //camera.setFrustum(1f, 1000f, -21.33f, 21.33f, 12f, -12f);
    }

    public void zoomIn() {
    halfHeight = Math.max(3f, halfHeight - 1f);
    updateFrustum();
    }

    public void zoomOut() {
    halfHeight = Math.min(60f, halfHeight + 1f);
    updateFrustum();
    }

    private void updateFrustum() {
    float aspect = (float) camera.getWidth() / camera.getHeight();

        camera.setFrustum(
            1f, 1000f,
            -halfHeight * aspect, halfHeight * aspect,
            halfHeight, -halfHeight
        );
    }

    public void rotate(float deltaX, float deltaY) {
        float sensitivity = 1.5f;

        azimuth -= deltaX * sensitivity;

        float horizontalRadius = orbitRadius * FastMath.cos(elevation);
        Vector3f location = target.add(
            horizontalRadius * FastMath.sin(azimuth),
            orbitRadius * FastMath.sin(elevation),
            horizontalRadius * FastMath.cos(azimuth)
        );

        camera.setLocation(location);
        camera.lookAt(target, Vector3f.UNIT_Y);
    }
}
