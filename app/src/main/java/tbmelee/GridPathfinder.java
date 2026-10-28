package tbmelee;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GridPathfinder {
    public record PathResult(List<Tile> tiles, boolean reachesDestination) {}

    private static final int[][] DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private final GameMap gameMap;

    public GridPathfinder(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public PathResult findPath(Tile start, Tile destination, Set<Tile> blockedTiles) {
        if (!gameMap.isInsideMap(start.x(), start.z())
                || !gameMap.isInsideMap(destination.x(), destination.z())) {
            return new PathResult(List.of(), false);
        }

        Queue<Tile> queue = new ArrayDeque<>();
        Set<Tile> visited = new HashSet<>();
        Map<Tile, Tile> previous = new HashMap<>();

        queue.add(start);
        visited.add(start);

        Tile closest = start;

        while (!queue.isEmpty()) {
            Tile current = queue.remove();

            if (current.equals(destination)) {
                return new PathResult(reconstructPath(start, current, previous), true);
            }

            if (distanceTo(current, destination) < distanceTo(closest, destination)) {
                closest = current;
            }

            for (int[] direction : DIRECTIONS) {
                Tile neighbor = new Tile(
                    current.x() + direction[0],
                    current.z() + direction[1]
                );

                if (!gameMap.isInsideMap(neighbor.x(), neighbor.z())
                        || visited.contains(neighbor)
                        || blockedTiles.contains(neighbor)) {
                    continue;
                }

                visited.add(neighbor);
                previous.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        return new PathResult(reconstructPath(start, closest, previous), false);
    }

    private List<Tile> reconstructPath(
            Tile start, Tile end, Map<Tile, Tile> previous) {
        List<Tile> path = new ArrayList<>();

        for (Tile tile = end; !tile.equals(start); tile = previous.get(tile)) {
            path.add(tile);
        }

        java.util.Collections.reverse(path);
        return path;
    }

    private int distanceTo(Tile first, Tile second) {
        return Math.abs(first.x() - second.x()) + Math.abs(first.z() - second.z());
    }
}