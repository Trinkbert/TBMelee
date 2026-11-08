package tbmelee;

import com.jme3.math.Vector3f;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class UnitMovementController {
    private final Node previewNode;
    private final Material previewMaterial;
    private final GameMap gameMap;
    private final GridPathfinder pathfinder;

    private static final float MOVE_SPEED = 3f;
    private final Deque<Tile> activePath = new ArrayDeque<>();
    private GameEntity activeUnit;
    private final List<GameEntity> units;
    private final List<GameEntity> enemies;

    private final List<Tile> pendingPath = new ArrayList<>();
    private GameEntity pendingUnit;
    private int pendingTileX;
    private int pendingTileZ;

    public UnitMovementController(GameMap gameMap, Node rootNode, AssetManager assetManager, List<GameEntity> units, List<GameEntity> enemies) {
        this.gameMap = gameMap;
        this.pathfinder = new GridPathfinder(gameMap);
        this.units = units;
        this.enemies = enemies;

        previewNode = new Node("MovePathPreview");
        rootNode.attachChild(previewNode);

        previewMaterial = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );
        previewMaterial.setColor("Color", ColorRGBA.Yellow);
    }

    public void handleOrderClick(GameEntity unit, int tileX, int tileZ) {
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
        Tile start = new Tile(unit.getTileX(), unit.getTileZ());
        Tile destination = new Tile(destinationX, destinationZ);

        return pathfinder.findPath(start, destination, buildBlockedTiles(unit)).tiles();
    }

    private void addBlockedTiles(Set<Tile> blockedTiles, List<GameEntity> entities, GameEntity movingUnit) {
        for (GameEntity entity : entities) {
            if (entity != movingUnit) {
                blockedTiles.add(new Tile(entity.getTileX(), entity.getTileZ()));
            }
        }
    }

    private Set<Tile> buildBlockedTiles(GameEntity unit) {
        Set<Tile> blockedTiles = new HashSet<>();
        addBlockedTiles(blockedTiles, units, unit);
        addBlockedTiles(blockedTiles, enemies, unit);
        return blockedTiles;
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

    public Tile findAttackDestination(GameEntity unit, GameEntity target) {
        if (unit == null || target == null) {
            return null;
        }

        Tile start = new Tile(unit.getTileX(), unit.getTileZ());
        Set<Tile> blockedTiles = buildBlockedTiles(unit);
        Tile bestDestination = null;
        float bestCost = Float.POSITIVE_INFINITY;
        int reach = unit.getWeaponReach();

        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                if (dx == 0 && dz == 0
                        || Math.max(Math.abs(dx), Math.abs(dz)) > reach) {
                    continue;
                }

                Tile candidate = new Tile(
                    target.getTileX() + dx,
                    target.getTileZ() + dz
                );

                if (!gameMap.isInsideMap(candidate.x(), candidate.z())
                        || blockedTiles.contains(candidate)) {
                    continue;
                }

                GridPathfinder.PathResult result =
                    pathfinder.findPath(start, candidate, blockedTiles);

                if (!result.reachesDestination()) {
                    continue;
                }

                float cost = pathCost(start, result.tiles());
                if (cost < bestCost) {
                    bestCost = cost;
                    bestDestination = candidate;
                }
            }
        }

        return bestDestination;
    }

    private float pathCost(Tile start, List<Tile> path) {
        float cost = 0f;
        Tile previous = start;

        for (Tile tile : path) {
            int dx = Math.abs(tile.x() - previous.x());
            int dz = Math.abs(tile.z() - previous.z());
            cost += dx != 0 && dz != 0 ? (float) Math.sqrt(2) : 1f;
            previous = tile;
        }

        return cost;
    }

    public GameEntity update(float tpf) {
        if (activeUnit == null || activePath.isEmpty()) {
            return null;
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
            GameEntity completedUnit = activeUnit;
            activeUnit = null;
            return completedUnit;
        }
        return null;
    }

}