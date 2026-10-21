package tbmelee;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class GameControls {
    private final InputManager inputManager;
    private final GameCamera gameCamera;
    private boolean orbiting;

    public GameControls(InputManager inputManager, GameCamera gameCamera) {
        this.inputManager = inputManager;
        this.gameCamera = gameCamera;
    }

    public void register() {
        // Add mappings and listeners here.
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
    
    
        inputManager.addMapping(
            "Orbit",
            new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE)
        );

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            orbiting = isPressed;
        }, "Orbit");
    }
    
}
