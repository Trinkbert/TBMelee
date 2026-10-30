
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;

import java.util.ArrayList;
import java.util.List;

public class GameScene {
    private final GameMap gameMap;
    private final UnitMovementController movementController;
    private final List<GameEntity> units = new ArrayList<>();
    private GameEntity selectedUnit;

    private final Node rootNode;
    private final AssetManager assetManager;
    

    public GameScene(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.gameMap = new GameMap(rootNode, assetManager);
        this.movementController = new UnitMovementController(gameMap, rootNode, assetManager, units);
    }

    public GameEntity spawnUnit(int tileX, int tileZ) {
        boolean useBlueTextures = units.isEmpty();

        if (!gameMap.isInsideMap(tileX, tileZ)) {
            throw new IllegalArgumentException("Unit position is outside the map");
        }

        GameEntity unit = new GameEntity(tileX, tileZ, 1f, 1f, 1f);
        Node unitNode = unit.createGeometry(assetManager, useBlueTextures);

        Vector3f position = gameMap.tileToWorld(tileX, tileZ);
        unitNode.setLocalTranslation(
            position.x + 0.5f,
            0f,
            position.z + 0.5f
        );

        rootNode.attachChild(unitNode);
        units.add(unit);
        return unit;
    }

    public void build() {

        gameMap.build();
        spawnUnit(0, 0);
        spawnUnit(3, 2);

        // Light the scene
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1f, -2f, -1f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }


    public void selectUnit(GameEntity unit) {
        if (selectedUnit != unit) {
            movementController.cancelPendingOrder();
        }

        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
        }

        selectedUnit = unit;

        if (selectedUnit != null) {
            selectedUnit.setSelected(true);
        }
    }

    public void handleMoveClick(int tileX, int tileZ) {
        movementController.handleMoveClick(selectedUnit, tileX, tileZ);
    }

    public void update(float tpf) {
        movementController.update(tpf);
    }

    // Getters
    public List<GameEntity> getUnits() {
        return List.copyOf(units);
    }

    public GameEntity getSelectedUnit() {
        return selectedUnit;
    }
}
