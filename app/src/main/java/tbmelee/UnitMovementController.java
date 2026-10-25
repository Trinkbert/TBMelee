package tbmelee;

import com.jme3.math.Vector3f;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Deque;

public class UnitMovementController {
    private final Node previewNode;
    private final Material previewMaterial;
    private final GameMap gameMap;

    private static final float MOVE_SPEED = 3f;
    private final Deque<Tile> activePath = new ArrayDeque<>();
    private GameEntity activeUnit;

    private final List<Tile> pendingPath = new ArrayList<>();
    private GameEntity pendingUnit;
    private int pendingTileX;
    private int pendingTileZ;

    private record Tile(int x, int z) {}

    public UnitMovementController(GameMap gameMap, Node rootNode, AssetManager assetManager) {
        this.gameMap = gameMap;

        previewNode = new Node("MovePathPreview");
        rootNode.attachChild(previewNode);

        previewMaterial = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );
        previewMaterial.setColor("Color", ColorRGBA.Yellow);
    }

    public void handleMoveClick(GameEntity unit, int tileX, int tileZ) {
        if (unit == null || !gameMap.isInsideMap(tileX, tileZ)) {
            return;
        }

        boolean confirming =
            pendingUnit == unit
            && pendingTileX == tileX
            && pendingTileZ == tileZ;

        if (!confirming) {
            pendingUnit = unit;
            pendingTileX = tileX;
            pendingTileZ = tileZ;
            pendingPath.clear();
            pendingPath.addAll(buildPath(unit, tileX, tileZ));
            showPath(pendingPath);
            return;
        }

        Vector3f destination = gameMap.tileToWorld(tileX, tileZ);
        destination.x += 0.5f;
        destination.z += 0.5f;

        if (activeUnit != null && activeUnit != unit) {
            activeUnit.setWalking(false);
        }

        activeUnit = unit;
        activePath.clear();
        activePath.addAll(pendingPath);
        if (activePath.isEmpty()) {
            activeUnit = null;
            cancelPendingOrder();
            return;
        }
        activeUnit.setWalking(true);
        cancelPendingOrder();
    }

    public void cancelPendingOrder() {
        pendingUnit = null;
        pendingPath.clear();
        previewNode.detachAllChildren();
    }

    private List<Tile> buildPath(GameEntity unit, int destinationX, int destinationZ) {
        List<Tile> path = new ArrayList<>();
        int x = unit.getTileX();
        int z = unit.getTileZ();

        while (x != destinationX) {
            x += Integer.compare(destinationX, x);
            path.add(new Tile(x, z));
        }

        while (z != destinationZ) {
            z += Integer.compare(destinationZ, z);
            path.add(new Tile(x, z));
        }

        return path;
    }

    private void showPath(List<Tile> path) {
        previewNode.detachAllChildren();

        for (Tile tile : path) {
            Geometry marker = new Geometry(
                "PathTile",
                new Box(0.4f, 0.025f, 0.4f)
            );
            marker.setMaterial(previewMaterial);
            marker.setLocalTranslation(tile.x() + 0.5f, 0.04f, tile.z() + 0.5f);
            previewNode.attachChild(marker);
        }
    }

    public void update(float tpf) {
        if (activeUnit == null || activePath.isEmpty()) {
            return;
        }

        float distanceLeft = MOVE_SPEED * tpf;

        while (distanceLeft > 0f && !activePath.isEmpty()) {
            Tile next = activePath.peekFirst();
            Vector3f target = gameMap.tileToWorld(next.x(), next.z());
            target.x += 0.5f;
            target.z += 0.5f;

            Vector3f current = activeUnit.getUnitNode().getLocalTranslation();
            float dx = target.x - current.x;
            float dz = target.z - current.z;
            float segmentDistance = (float) Math.sqrt(dx * dx + dz * dz);

            if (segmentDistance <= distanceLeft) {
                activeUnit.moveTowards(
                    next.x(), next.z(), target, distanceLeft
                );
                distanceLeft -= segmentDistance;
                activePath.removeFirst();
            } else {
                activeUnit.moveTowards(
                    next.x(), next.z(), target, distanceLeft
                );
                distanceLeft = 0f;
            }
        }

        if (activePath.isEmpty()) {            
            activeUnit.setWalking(false);
            activeUnit = null;
        }
    }
}