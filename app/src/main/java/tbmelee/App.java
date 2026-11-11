
package tbmelee;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;

public class App extends SimpleApplication {
    private GameScene scene;
    private GameControls gameControls;
    private GameClock gameClock;

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

        gameClock = new GameClock();

        GameCamera gameCamera = new GameCamera(cam);
        gameCamera.setup();

        scene = new GameScene(rootNode, assetManager, gameClock);
        scene.build();

        gameControls = new GameControls(inputManager, gameCamera, cam, scene);
        gameControls.register();

    }

    @Override
    public void simpleUpdate(float tpf) {
        gameControls.update(tpf);
        scene.update(tpf);
    }

}
