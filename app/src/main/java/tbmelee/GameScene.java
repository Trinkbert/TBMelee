
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
    private final List<GameEntity> enemies = new ArrayList<>();
    private GameEntity selectedUnit;
    private GameEntity pendingAttackUnit;
    private GameEntity pendingAttackTarget;

    private final Node rootNode;
    private final AssetManager assetManager;
    

    public GameScene(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.gameMap = new GameMap(rootNode, assetManager);
        this.movementController = new UnitMovementController(gameMap, rootNode, assetManager, units, enemies);
    }

    public GameEntity spawnUnit(int tileX, int tileZ, boolean isEnemy) {

        if (!gameMap.isInsideMap(tileX, tileZ)) {
            throw new IllegalArgumentException("Unit position is outside the map");
        }

        GameEntity unit = new GameEntity(tileX, tileZ, 1f, 1f, 1f, isEnemy);
        Node unitNode = unit.createGeometry(assetManager, isEnemy);

        Vector3f position = gameMap.tileToWorld(tileX, tileZ);
        unitNode.setLocalTranslation(
            position.x + 0.5f,
            0f,
            position.z + 0.5f
        );

        rootNode.attachChild(unitNode);
        if (isEnemy) {
            enemies.add(unit);
        } else {
            units.add(unit);
        }
        return unit;
    }

    public void build() {

        //List of Units
        gameMap.build();
        spawnUnit(9, 0, false);
        spawnUnit(11, 0, false);
        spawnUnit(9, 9, true);
        spawnUnit(9, 11, true);
        spawnUnit(11, 9, true);
        spawnUnit(11, 11, true);

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

    public void update(float tpf) {
        GameEntity completedUnit = movementController.update(tpf);

        if (completedUnit != null && completedUnit == pendingAttackUnit) {
            GameEntity target = pendingAttackTarget;
            pendingAttackUnit = null;
            pendingAttackTarget = null;
            handleAttackClick(target);
        }

        removeEnemy();

    }

        
    

    public void handleMoveClick(int tileX, int tileZ) {
        pendingAttackUnit = null;
        pendingAttackTarget = null;
        movementController.handleOrderClick(selectedUnit, tileX, tileZ);
    }

    public void handleAttackClick(GameEntity target) {
        if (selectedUnit == null || target == null) {
            return;
        }

        int distanceX = Math.abs(selectedUnit.getTileX() - target.getTileX());
        int distanceZ = Math.abs(selectedUnit.getTileZ() - target.getTileZ());
        int distance = Math.max(distanceX, distanceZ);

        if (distance <= selectedUnit.getWeaponReach()) {
            selectedUnit.faceTowards(target);
            selectedUnit.playAttackAnimation(target);
            pendingAttackUnit = null;
            pendingAttackTarget = null;
            return;
        }

        pendingAttackUnit = selectedUnit;
        pendingAttackTarget = target;
        movementController.handleOrderClick(
            selectedUnit,
            target.getTileX(),
            target.getTileZ()
        );
    }

    private void removeEnemy() {
        for (GameEntity enemy : List.copyOf(enemies)) {
            if (enemy.getHitPoints() <= 0) {
                enemies.remove(enemy);
                rootNode.detachChild(enemy.getUnitNode());
            }
        }
    }

    // Getters
    public List<GameEntity> getUnits() {
        return List.copyOf(units);
    }

    public List<GameEntity> getEnemies() {
        return List.copyOf(enemies);
    }

    public GameEntity getSelectedUnit() {
        return selectedUnit;
    }
}
