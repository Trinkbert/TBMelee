package tbmelee;

import java.util.stream.Stream;
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
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class GameControls {
    private final InputManager inputManager;
    private final GameCamera gameCamera;
    private final Camera camera;
    private final GameScene gameScene;
    private boolean orbiting;

    private boolean arrowUpPressed;
    private boolean arrowDownPressed;
    private boolean arrowLeftPressed;
    private boolean arrowRightPressed;

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
        inputManager.addMapping(
            "Orbit",
            new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE)
        );
        inputManager.addMapping("OrbitRight",
            new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("OrbitLeft",
            new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(
            "Select",
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)
        );
        inputManager.addMapping("Crouch", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("Stand", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("ToggleCameraMode", new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addMapping("RotateClockwise", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("RotateCounterClockwise", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("ArrowUp", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("ArrowDown", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("ArrowLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("ArrowRight", new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            if ("ZoomIn".equals(name)) {
                gameCamera.zoomIn();
            } else if ("ZoomOut".equals(name)) {
                gameCamera.zoomOut();
            } else if (orbiting) {
                switch (name) {
                    case "OrbitRight" -> gameCamera.rotate(value);
                    case "OrbitLeft"  -> gameCamera.rotate(-value);
                }
            }
        }, "ZoomIn", "ZoomOut", "OrbitRight", "OrbitLeft");
    
    

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            switch (name) {
                case "Orbit" -> orbiting = isPressed && !gameCamera.isOrthographic();
                case "Select" -> {
                    if (isPressed) {
                        selectUnitUnderCursor();
                    }
                }
                case "Crouch" -> {
                    if (isPressed && gameScene.getSelectedUnit() != null) {
                        gameScene.getSelectedUnit().crouch();
                    }
                }
                case "Stand" -> {
                    if (isPressed && gameScene.getSelectedUnit() != null) {
                        gameScene.getSelectedUnit().stand();
                    }
                }
                case "ToggleCameraMode" -> {
                    if (isPressed) {
                        gameCamera.toggleProjection();
                    }
                }
                case "RotateCounterClockwise" -> {
                    if (isPressed && gameCamera.isOrthographic()) {
                        gameCamera.rotateQuarterTurn(-1f);
                    }
                }
                case "RotateClockwise" -> {
                    if (isPressed && gameCamera.isOrthographic()) {
                        gameCamera.rotateQuarterTurn(1f);
                    }
                }
                case "ArrowUp" -> arrowUpPressed = isPressed;
                case "ArrowDown" -> arrowDownPressed = isPressed;
                case "ArrowLeft" -> arrowLeftPressed = isPressed;
                case "ArrowRight" -> arrowRightPressed = isPressed;
            }
        }, "Orbit", "Select", "Crouch", "Stand","ToggleCameraMode", "RotateCounterClockwise", "RotateClockwise", "ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight");
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

        for (GameEntity unit : Stream.concat(
                gameScene.getUnits().stream(),
                gameScene.getEnemies().stream()).toList()) {
                
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
        if (closestUnit == null) {
            moveSelectedUnitToGround(ray);
            return;
        }

        if (closestUnit.isEnemy()) {
            if (gameScene.getSelectedUnit() != null) {
                gameScene.handleAttackClick(closestUnit);
            }
            return;
        }

        gameScene.selectUnit(closestUnit);
    }

    private void moveSelectedUnitToGround(Ray ray) {
        if (gameScene.getSelectedUnit() == null) {
            return;
        }

        Vector3f direction = ray.getDirection();
        if (Math.abs(direction.y) < 0.0001f) {
            return;
        }

        // The top of the ground plane is at y = 0.
        float distance = -ray.getOrigin().y / direction.y;
        if (distance < 0f) {
            return;
        }

        Vector3f hit = ray.getOrigin().add(ray.getDirection().mult(distance));
        int tileX = (int) Math.floor(hit.x);
        int tileZ = (int) Math.floor(hit.z);

        gameScene.handleMoveClick(tileX, tileZ);
    }

    public void update(float tpf) {
        float horizontal = (arrowRightPressed ? 1f : 0f) - (arrowLeftPressed ? 1f : 0f);
        float forward = (arrowUpPressed ? 1f : 0f) - (arrowDownPressed ? 1f : 0f);

        if (horizontal != 0f || forward != 0f) {
            gameCamera.pan(horizontal, forward, tpf);
        }
    }
    
}
