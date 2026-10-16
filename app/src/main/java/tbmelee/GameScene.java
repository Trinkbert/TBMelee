
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
    // Create the blue cube
    Box box = new Box(1f, 1f, 1f);
    Geometry cube = new Geometry("Cube", box);

    Material cubeMaterial = new Material(
        assetManager,
        "Common/MatDefs/Light/Lighting.j3md"
    );
    cubeMaterial.setBoolean("UseMaterialColors", true);
    cubeMaterial.setColor("Diffuse", ColorRGBA.Blue);
    cubeMaterial.setColor("Ambient", ColorRGBA.Blue);

    cube.setMaterial(cubeMaterial);
    rootNode.attachChild(cube);

    // Create the green ground
    Box groundBox = new Box(10f, 0.1f, 10f);
    Geometry ground = new Geometry("Ground", groundBox);

    Material groundMaterial = new Material(
        assetManager,
        "Common/MatDefs/Light/Lighting.j3md"
    );
    groundMaterial.setBoolean("UseMaterialColors", true);
    groundMaterial.setColor("Diffuse", ColorRGBA.Green);
    groundMaterial.setColor("Ambient", ColorRGBA.Green);

    ground.setMaterial(groundMaterial);
    ground.setLocalTranslation(0f, -1.1f, 0f);

    rootNode.attachChild(ground);

    // Light the scene
    AmbientLight ambient = new AmbientLight();
    ambient.setColor(ColorRGBA.White);
    rootNode.addLight(ambient);
}

}
