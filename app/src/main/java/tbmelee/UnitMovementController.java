package tbmelee;

import com.jme3.math.Vector3f;

public class UnitMovementController {
    private final GameMap gameMap;

    private GameEntity pendingUnit;
    private int pendingTileX;
    private int pendingTileZ;

    public UnitMovementController(GameMap gameMap) {
        this.gameMap = gameMap;
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
            return;
        }

        Vector3f destination = gameMap.tileToWorld(tileX, tileZ);
        destination.x += 0.5f;
        destination.z += 0.5f;

        unit.moveTo(tileX, tileZ, destination);
        cancelPendingOrder();
    }

    public void cancelPendingOrder() {
        pendingUnit = null;
    }
}