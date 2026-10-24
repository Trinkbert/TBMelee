
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class GameEntity {

    private int tileX;
    private int tileZ;

    private float width;
    private float height;
    private float depth;

    private static final float CROUCH_HEIGHT = 1f;
    private static final float STAND_HEIGHT = 2f;
    private boolean standing = true;

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
        updateHeight();

        return geometry;
    }

    public void setSelected(boolean selected) {
        ColorRGBA color = selected
            ? new ColorRGBA(0.5f, 0f, 0.5f, 1f)
            : ColorRGBA.Blue;

        geometry.getMaterial().setColor("Diffuse", color);
        geometry.getMaterial().setColor("Ambient", color);
    }

    //Movement
    public void moveTo(int tileX, int tileZ, Vector3f position) {
        this.tileX = tileX;
        this.tileZ = tileZ;
        geometry.setLocalTranslation(position.x, getHeight() / 2f, position.z);
    }

    public boolean moveTowards(
        int tileX, int tileZ, Vector3f target, float maxDistance) {
        Vector3f current = geometry.getLocalTranslation();
        float dx = target.x - current.x;
        float dz = target.z - current.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        if (distance <= maxDistance || distance == 0f) {
            geometry.setLocalTranslation(target.x, getHeight() / 2f, target.z);
            this.tileX = tileX;
            this.tileZ = tileZ;
            return true;
        }

        float fraction = maxDistance / distance;
        geometry.setLocalTranslation(
            current.x + dx * fraction,
            getHeight() / 2f,
            current.z + dz * fraction
        );
        return false;
    }

    private void updateHeight() {
        float height = standing ? STAND_HEIGHT : CROUCH_HEIGHT;
        geometry.setLocalScale(1f, height, 1f);

        Vector3f position = geometry.getLocalTranslation();
        position.y = height / 2f;
        geometry.setLocalTranslation(position);
    }

    public void crouch() {
        setStanding(false);
    }

    public void stand() {
        setStanding(true);
    }

    private void setStanding(boolean standing) {
        if (this.standing == standing) {
            return;
        }

        this.standing = standing;
        updateHeight();
    }

    // Getters and setters
    public int getTileX() {
        return tileX;
    }

    public int getTileZ() {
        return tileZ;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public int getHeight() {
        return standing ? (int) STAND_HEIGHT : (int) CROUCH_HEIGHT;
    }
}
