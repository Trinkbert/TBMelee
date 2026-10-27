
package tbmelee;

import com.jme3.renderer.Camera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class GameCamera {

    private static final float PERSPECTIVE_FOV = 5f;

    private boolean orthographic = true;

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
        if (orthographic) {
            updateFrustum();
        } else {            
            orbitRadius = perspectiveDistanceForZoom();
            updateOrbitPosition();
        }
    }

    public void zoomOut() {
            halfHeight = Math.min(60f, halfHeight + 1f);
        if (orthographic) {
            updateFrustum();
        } else {
            orbitRadius = perspectiveDistanceForZoom();
            updateOrbitPosition();
        }
    }

    private void updateFrustum() {
        float aspect = (float) camera.getWidth() / camera.getHeight();

        camera.setFrustum(
            1f, 1000f,
            -halfHeight * aspect, halfHeight * aspect,
            halfHeight, -halfHeight
        );
    }

    public void rotate(float deltaX) {
        float sensitivity = 1.5f;

        azimuth -= deltaX * sensitivity;

        updateOrbitPosition();
    }

    public void rotateQuarterTurn(float direction) {
        azimuth += direction * FastMath.HALF_PI;
        updateOrbitPosition();
    }

    public void toggleProjection() {
        orthographic = !orthographic;

        if (orthographic) {
            halfHeight = orthographicHalfHeightForDistance();
            float quarterTurn = FastMath.HALF_PI;
            float snapOffset = quarterTurn / 2f;
            azimuth = Math.round((azimuth - snapOffset)/ quarterTurn) * quarterTurn + snapOffset;

            camera.setParallelProjection(true);
            updateFrustum();
            updateOrbitPosition();
        } else {
            orbitRadius = perspectiveDistanceForZoom();
            camera.setParallelProjection(false);
            camera.setFrustumPerspective(
                PERSPECTIVE_FOV,
                (float) camera.getWidth() / camera.getHeight(),
                1f,
                1000f
            );
            updateOrbitPosition();
        }
    }

    public boolean isOrthographic() {
        return orthographic;
    }

    private void updateOrbitPosition() {
        float horizontalRadius = orbitRadius * FastMath.cos(elevation);
        Vector3f location = target.add(
            horizontalRadius * FastMath.sin(azimuth),
            orbitRadius * FastMath.sin(elevation),
            horizontalRadius * FastMath.cos(azimuth)
        );

        camera.setLocation(location);
        camera.lookAt(target, Vector3f.UNIT_Y);
    }

    private float perspectiveDistanceForZoom() {
        float halfFovRadians = PERSPECTIVE_FOV * FastMath.DEG_TO_RAD * 0.5f;
        return halfHeight / FastMath.tan(halfFovRadians);
    }

    private float orthographicHalfHeightForDistance() {
        float halfFovRadians = PERSPECTIVE_FOV * FastMath.DEG_TO_RAD * 0.5f;
        return orbitRadius * FastMath.tan(halfFovRadians);
    }
}
