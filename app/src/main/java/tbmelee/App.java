
package tbmelee;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;

public class App extends SimpleApplication {
    private boolean orbiting;

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

        inputManager.addMapping("OrbitRight",
            new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("OrbitLeft",
            new MouseAxisTrigger(MouseInput.AXIS_X, true));

        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            System.out.println("Analog: " + name + " = " + value);
            if ("ZoomIn".equals(name)) {
                gameCamera.zoomIn();
            } else if ("ZoomOut".equals(name)) {
                gameCamera.zoomOut();
            } else if (orbiting) {
                switch (name) {
                    case "OrbitRight" -> gameCamera.rotate(value, 0f);
                    case "OrbitLeft"  -> gameCamera.rotate(-value, 0f);
                }
            }
        }, "ZoomIn", "ZoomOut", "OrbitRight", "OrbitLeft");

        GameScene scene = new GameScene(rootNode, assetManager);
        scene.build();

        inputManager.addMapping(
            "Orbit",
            new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE)
        );

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            System.out.println("Orbit button: " + isPressed);
            orbiting = isPressed;
        }, "Orbit");
    }

}
