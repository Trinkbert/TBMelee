package tbmelee;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class GameControls {
    private final InputManager inputManager;
    private final GameCamera gameCamera;
    private final Camera camera;
    private final GameScene gameScene;
    private boolean orbiting;

    public GameControls(InputManager inputManager, GameCamera gameCamera, Camera camera, GameScene gameScene) {
        this.inputManager = inputManager;
        this.gameCamera = gameCamera;
        this.camera = camera;
        this.gameScene = gameScene;
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
        inputManager.addMapping(
            "Select",
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)
        );

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

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (isPressed) {
            selectUnitUnderCursor();
            }
        }, "Select");
    }

    private void selectUnitUnderCursor() {
        Vector2f cursor = inputManager.getCursorPosition();
        Vector3f near = camera.getWorldCoordinates(cursor, 0f);
        Vector3f direction = camera.getWorldCoordinates(cursor, 1f)
            .subtract(near)
            .normalizeLocal();
        Ray ray = new Ray(near, direction);

        GameEntity closestUnit = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        for (GameEntity unit : gameScene.getUnits()) {
            CollisionResults collisions = new CollisionResults();
            unit.getGeometry().collideWith(ray, collisions);

            if (collisions.size() > 0) {
                float distance = collisions.getClosestCollision().getDistance();
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestUnit = unit;
                }
            }
        }

        gameScene.selectUnit(closestUnit);
    }
    
}
