
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.light.AmbientLight;
import java.util.ArrayList;
import java.util.List;

public class GameScene {
    private final GameMap gameMap;
    private final List<GameEntity> units = new ArrayList<>();
    private GameEntity selectedUnit;

    private final Node rootNode;
    private final AssetManager assetManager;
    

    public GameScene(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.gameMap = new GameMap(rootNode, assetManager);
    }

    public GameEntity spawnUnit(int tileX, int tileZ) {
        if (!gameMap.isInsideMap(tileX, tileZ)) {
            throw new IllegalArgumentException("Unit position is outside the map");
        }

        GameEntity unit = new GameEntity(tileX, tileZ, 1f, 1f, 1f);
        Geometry geometry = unit.createGeometry(assetManager);

        Vector3f position = gameMap.tileToWorld(tileX, tileZ);
        geometry.setLocalTranslation(
            position.x + 0.5f,
            position.y,
            position.z + 0.5f
        );

        rootNode.attachChild(geometry);
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
    }

    public List<GameEntity> getUnits() {
        return List.copyOf(units);
    }

    public GameEntity getSelectedUnit() {
        return selectedUnit;
}

    public void selectUnit(GameEntity unit) {
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
        }

        selectedUnit = unit;

        if (selectedUnit != null) {
            selectedUnit.setSelected(true);
        }
    }

    public void moveSelectedUnit(int tileX, int tileZ) {
        if (selectedUnit == null || !gameMap.isInsideMap(tileX, tileZ)) {
            return;
        }

        Vector3f position = gameMap.tileToWorld(tileX, tileZ);
        position.x += 0.5f;
        position.z += 0.5f;

        selectedUnit.moveTo(tileX, tileZ, position);
    }

}
