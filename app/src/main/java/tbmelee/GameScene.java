
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
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry cube = new Geometry("Cube", box);

        Material cubeMaterial = new Material(
                assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        cubeMaterial.setBoolean("UseMaterialColors", true);
        cubeMaterial.setColor("Diffuse", ColorRGBA.Blue);
        cubeMaterial.setColor("Ambient", ColorRGBA.Blue);

        cube.setMaterial(cubeMaterial);
        Vector3f position = gameMap.tileToWorld(0, 0);
        cube.setLocalTranslation(position.x + 0.5f, 0.5f, position.z + 0.5f);
        rootNode.attachChild(cube);

        gameMap.build();

        // Light the scene
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    }

}
