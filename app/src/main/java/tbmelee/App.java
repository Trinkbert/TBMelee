
package tbmelee;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.light.AmbientLight;

public class App extends SimpleApplication {

    public static void main(String[] args) {
        App app = new App();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("TBMelee");
        settings.setResolution(1280, 720);
        settings.setFullscreen(false);
        settings.setResizable(false);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        setDisplayFps(false);
        setDisplayStatView(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.15f, 0.15f, 0.18f, 1f));

        Box box = new Box(1f, 1f, 1f);
Geometry cube = new Geometry("Cube", box);

Material material = new Material(
    assetManager,
    "Common/MatDefs/Light/Lighting.j3md"
);
material.setBoolean("UseMaterialColors", true);
material.setColor("Diffuse", ColorRGBA.Blue);
material.setColor("Ambient", ColorRGBA.Blue);

cube.setMaterial(material);
cube.setLocalTranslation(0f, 0f, 0f);

rootNode.attachChild(cube);
AmbientLight ambient = new AmbientLight();
ambient.setColor(ColorRGBA.White);
rootNode.addLight(ambient);
    }
    @Override
public void simpleUpdate(float tpf) {
    rootNode.rotate(0f, tpf, 0f);
}
}
