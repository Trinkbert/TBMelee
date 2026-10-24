
package tbmelee;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

public class App extends SimpleApplication {
    private GameScene scene;

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

        scene = new GameScene(rootNode, assetManager);
        scene.build();

        GameControls gameControls = new GameControls(inputManager, gameCamera, cam, scene);
        gameControls.register();

        Spatial character = assetManager.loadModel("Models/placeholder_guy.glb");
            rootNode.attachChild(character);

    }

    @Override
    public void simpleUpdate(float tpf) {
        scene.update(tpf);
    }

}
