
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class GameEntity {

    private int tileX;
    private int tileZ;

    private float width;
    private float height;
    private float depth;

    private Geometry geometry;

    public GameEntity(int tileX, int tileZ,
                      float width, float height, float depth) {
        this.tileX = tileX;
        this.tileZ = tileZ;

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Geometry createGeometry(AssetManager assetManager) {
        Box box = new Box(width / 2f, height / 2f, depth / 2f);
        geometry = new Geometry("GameEntity", box);

        Material material = new Material(
            assetManager,
            "Common/MatDefs/Light/Lighting.j3md"
        );

        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse", ColorRGBA.Blue);
        material.setColor("Ambient", ColorRGBA.Blue);

        geometry.setMaterial(material);

        return geometry;
    }

    public void setSelected(boolean selected) {
        ColorRGBA color = selected
            ? new ColorRGBA(0.5f, 0f, 0.5f, 1f)
            : ColorRGBA.Blue;

        geometry.getMaterial().setColor("Diffuse", color);
        geometry.getMaterial().setColor("Ambient", color);
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileZ() {
        return tileZ;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
