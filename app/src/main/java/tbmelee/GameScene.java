
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.light.AmbientLight;

public class GameScene {

    private final Node rootNode;
    private final AssetManager assetManager;

    public GameScene(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
    }

    public void build() {
        GameMap gameMap = new GameMap(rootNode, assetManager);
        

        // Create the blue cube
        GameEntity box = new GameEntity(0, 0, 1f, 1f, 1f);

        Geometry boxGeometry = box.createGeometry(assetManager);

        Vector3f position = gameMap.tileToWorld(box.getTileX(), box.getTileZ());
        boxGeometry.setLocalTranslation(position.x + 0.5f, position.y, position.z + 0.5f);

        rootNode.attachChild(boxGeometry);

        gameMap.build();

        // Light the scene
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    }

}
