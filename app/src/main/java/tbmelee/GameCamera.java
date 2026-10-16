
package tbmelee;

import com.jme3.renderer.Camera;
import com.jme3.math.Vector3f;

public class GameCamera {

    private final Camera camera;

    public GameCamera(Camera camera) {
        this.camera = camera;
    }

    public void setup() {
        camera.setLocation(new Vector3f(4f, 6f, 8f));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
}
