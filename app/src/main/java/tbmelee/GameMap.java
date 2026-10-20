
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class GameMap {

    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 20;

    private final Node rootNode;
    private final AssetManager assetManager;

    public GameMap(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
    }

    public void build() {
        Box groundBox = new Box(MAP_WIDTH / 2f,0.1f,MAP_HEIGHT / 2f);
        Geometry ground = new Geometry("Ground", groundBox);

        Material groundMaterial = new Material(
                assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        groundMaterial.setBoolean("UseMaterialColors", true);
        groundMaterial.setColor("Diffuse", ColorRGBA.Green);
        groundMaterial.setColor("Ambient", ColorRGBA.Green);

        ground.setMaterial(groundMaterial);
        ground.setLocalTranslation(MAP_WIDTH / 2f,-0.1f,MAP_HEIGHT / 2f);

        rootNode.attachChild(ground);

        buildGrid();
    }

    private void buildGrid() {
        Mesh mesh = new Mesh();

        Vector3f[] points = new Vector3f[(MAP_WIDTH + 1 + MAP_HEIGHT + 1) * 2];

        int index = 0;

        // Lines parallel to the Z axis
        for (int x = 0; x <= MAP_WIDTH; x++) {
            points[index++] = new Vector3f(x, 0.01f, 0);
            points[index++] = new Vector3f(x, 0.01f, MAP_HEIGHT);
        }

        // Lines parallel to the X axis
        for (int z = 0; z <= MAP_HEIGHT; z++) {
            points[index++] = new Vector3f(0, 0.01f, z);
            points[index++] = new Vector3f(MAP_WIDTH, 0.01f, z);
        }

        mesh.setMode(Mesh.Mode.Lines);
        mesh.setBuffer(
                VertexBuffer.Type.Position,
                3,
                BufferUtils.createFloatBuffer(points));
        mesh.updateBound();

        Geometry grid = new Geometry("Grid", mesh);

        Material gridMaterial = new Material(
                assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        gridMaterial.setColor("Color", ColorRGBA.Black);

        grid.setMaterial(gridMaterial);
        rootNode.attachChild(grid);
    }

    // Check map boundaries
    public boolean isInsideMap(int x, int z) {
        return x >= 0 && x < MAP_WIDTH
                && z >= 0 && z < MAP_HEIGHT;
    }

    // Converts tile coordinates to world coordinates
    public Vector3f tileToWorld(int x, int z) {
        return new Vector3f(x, 0.5f, z);
        
    }

}
