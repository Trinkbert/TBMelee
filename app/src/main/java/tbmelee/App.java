
package tbmelee;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.AnalogListener;

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

    viewPort.setBackgroundColor(
        new ColorRGBA(0.15f, 0.15f, 0.18f, 1f)
    );

    GameCamera gameCamera = new GameCamera(cam);
    gameCamera.setup();

    inputManager.addMapping(
    "ZoomIn",
    new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false)
    );
    inputManager.addMapping(
    "ZoomOut",
    new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true)
    );

    inputManager.addListener((AnalogListener)(name, value, tpf) -> {
        if ("ZoomIn".equals(name)) {
            gameCamera.zoomIn();
        } else if ("ZoomOut".equals(name)) {
        gameCamera.zoomOut();
        }
    }, "ZoomIn", "ZoomOut");

    GameScene scene = new GameScene(rootNode, assetManager);
    scene.build();
}

}
