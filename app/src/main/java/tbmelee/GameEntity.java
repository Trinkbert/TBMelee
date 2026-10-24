
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Spatial;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class GameEntity {

    private int tileX;
    private int tileZ;

    private float width;
    private float height;
    private float depth;

    private static final float CROUCH_HEIGHT = 1f;
    private static final float STAND_HEIGHT = 2f;
    private boolean standing = true;

    private final Node unitNode = new Node("Unit");
    private Geometry pickingGeometry;
    private Geometry frameGeometry;
    private Material frameMaterial;

    public GameEntity(int tileX, int tileZ,
                      float width, float height, float depth) {
        this.tileX = tileX;
        this.tileZ = tileZ;

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Node createGeometry(AssetManager assetManager) {
        Box box = new Box(width / 2f, height / 2f, depth / 2f);
        pickingGeometry = new Geometry("UnitPickingBox", box);

        pickingGeometry.setCullHint(Spatial.CullHint.Always);

        unitNode.attachChild(pickingGeometry);
        createFrame(assetManager);
        updateHeight();

        return unitNode;
    }

    private void createFrame(AssetManager assetManager) {
        Vector3f[] corners = new Vector3f[8];

        for (int i = 0; i < corners.length; i++) {
            float x = (i & 1) == 0 ? -width / 2f : width / 2f;
            float y = (i & 2) == 0 ? -height / 2f : height / 2f;
            float z = (i & 4) == 0 ? -depth / 2f : depth / 2f;
            corners[i] = new Vector3f(x, y, z);
        }

        int[][] edges = {
            {0, 1}, {2, 3}, {4, 5}, {6, 7},
            {0, 2}, {1, 3}, {4, 6}, {5, 7},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        Vector3f[] endpoints = new Vector3f[edges.length * 2];
        int index = 0;

        for (int[] edge : edges) {
            endpoints[index++] = corners[edge[0]];
            endpoints[index++] = corners[edge[1]];
        }

        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Lines);
        mesh.setBuffer(
            VertexBuffer.Type.Position,
            3,
            BufferUtils.createFloatBuffer(endpoints)
        );
        mesh.updateBound();

        frameGeometry = new Geometry("UnitFrame", mesh);
        frameMaterial = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );
        frameMaterial.setColor("Color", ColorRGBA.Blue);
        frameGeometry.setMaterial(frameMaterial);
        unitNode.attachChild(frameGeometry);
    }

    public void setSelected(boolean selected) {
        ColorRGBA color = selected
            ? new ColorRGBA(0.5f, 0f, 0.5f, 1f)
            : ColorRGBA.Blue;

        frameMaterial.setColor(
            "Color",
            selected ? new ColorRGBA(0.5f, 0f, 0.5f, 1f) : ColorRGBA.Blue
        );
    }

    //Movement
    public void moveTo(int tileX, int tileZ, Vector3f position) {
        this.tileX = tileX;
        this.tileZ = tileZ;
        unitNode.setLocalTranslation(position.x, getHeight() / 2f, position.z);
    }

    public boolean moveTowards(
        int tileX, int tileZ, Vector3f target, float maxDistance) {
        Vector3f current = unitNode.getLocalTranslation();
        float dx = target.x - current.x;
        float dz = target.z - current.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        if (distance <= maxDistance || distance == 0f) {
            unitNode.setLocalTranslation(target.x, getHeight() / 2f, target.z);
            this.tileX = tileX;
            this.tileZ = tileZ;
            return true;
        }

        float fraction = maxDistance / distance;
        unitNode.setLocalTranslation(
            current.x + dx * fraction,
            getHeight() / 2f,
            current.z + dz * fraction
        );
        return false;
    }

    private void updateHeight() {
        float height = standing ? STAND_HEIGHT : CROUCH_HEIGHT;
        unitNode.setLocalScale(1f, height, 1f);

        Vector3f position = unitNode.getLocalTranslation();
        position.y = height / 2f;
        unitNode.setLocalTranslation(position);
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
        return pickingGeometry;
    }

    public Node getUnitNode() {
        return unitNode;
    }

    public int getHeight() {
        return standing ? (int) STAND_HEIGHT : (int) CROUCH_HEIGHT;
    }
}
