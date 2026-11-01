package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class ModelLoader {

    public Spatial loadModel(AssetManager assetManager, boolean isEnemy) {
        Spatial model = assetManager.loadModel("Models/placeholder_guy1.glb");
        Node rightArm = findNode(model, "Arm.R");

        if (rightArm == null) {
            throw new IllegalStateException(
                "Model is missing Node named Arm.R"
            );
        }
        if (!isEnemy) {
            applyTeamTextures(assetManager, model);
        }

        Spatial weapon = assetManager.loadModel("Models/weapon_placeholder.glb");
        //Reposition the weapon
        weapon.setLocalScale(1f);
        weapon.setLocalTranslation(0f, 1.5f, 1f);
        weapon.setLocalRotation(new Quaternion());
        weapon.setLocalRotation(new Quaternion().fromAngles(
            FastMath.DEG_TO_RAD * 90,
            FastMath.DEG_TO_RAD * 0,
            FastMath.DEG_TO_RAD * 0
        ));

        rightArm.attachChild(weapon);

        return model;
    }

    private void applyTeamTextures(AssetManager assetManager, Spatial model) {
    
        String torsoPath = "Textures/TorsoTextureGreen.png"
            ;
        String armPath = "Textures/ArmTextureGreen.png";
        

        Texture torsoTexture = assetManager.loadTexture(torsoPath);
        Texture armTexture = assetManager.loadTexture(armPath);
        
        model.depthFirstTraversal(spatial -> {
            if (!(spatial instanceof Geometry geometry)) {
                return;
            }

            String texturePath = switch (geometry.getName()) {
                case "Cube_0" -> torsoPath;
                case "Cube.002_0", "Cube.003_0" -> armPath;
                default -> null;
            };

            if (texturePath == null) {
                return;
            }

            Material material = geometry.getMaterial().clone();
            material.setTexture(
                "BaseColorMap",
                texturePath.equals(torsoPath) ? torsoTexture : armTexture
            );
            geometry.setMaterial(material);
        });
       
    }

    private Node findNode(Spatial spatial, String name) {
        if (spatial instanceof Node node) {
            if (name.equals(node.getName())) {
                return node;
            }

            for (Spatial child : node.getChildren()) {
                Node found = findNode(child, name);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}