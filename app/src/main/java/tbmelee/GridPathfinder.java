package tbmelee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GridPathfinder {
    public record PathResult(List<Tile> tiles, boolean reachesDestination) {}

    private record Direction(int dx, int dz, float cost) {}
    private record SearchNode(Tile tile, float cost) {}

    private static final float DIAGONAL_COST = (float) Math.sqrt(2.0);

    private static final Direction[] DIRECTIONS = {
        new Direction(1, 0, 1f),
        new Direction(-1, 0, 1f),
        new Direction(0, 1, 1f),
        new Direction(0, -1, 1f),
        new Direction(1, 1, DIAGONAL_COST),
        new Direction(1, -1, DIAGONAL_COST),
        new Direction(-1, 1, DIAGONAL_COST),
        new Direction(-1, -1, DIAGONAL_COST)
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

        PriorityQueue<SearchNode> queue = new PriorityQueue<>(
            Comparator.comparingDouble(SearchNode::cost)
        );
        Map<Tile, Float> bestCosts = new HashMap<>();
        Map<Tile, Tile> previous = new HashMap<>();
        Set<Tile> visited = new HashSet<>();

        queue.add(new SearchNode(start, 0f));
        bestCosts.put(start, 0f);

        Tile closest = start;

        while (!queue.isEmpty()) {
            SearchNode currentNode = queue.remove();
            Tile current = currentNode.tile();
            float currentCost = currentNode.cost();

            if (currentCost > bestCosts.getOrDefault(current, Float.POSITIVE_INFINITY)) {
                continue;
            }

            if (current.equals(destination)) {
                return new PathResult(reconstructPath(start, current, previous), true);
            }

            visited.add(current);

            if (distanceTo(current, destination) < distanceTo(closest, destination)) {
                closest = current;
            }

            for (Direction direction : DIRECTIONS) {
                Tile neighbor = new Tile(
                    current.x() + direction.dx(),
                    current.z() + direction.dz()
                );

                if (!gameMap.isInsideMap(neighbor.x(), neighbor.z())
                        || blockedTiles.contains(neighbor)) {
                    continue;
                }

                boolean diagonal = direction.dx() != 0 && direction.dz() != 0;
                if (diagonal) {
                    Tile horizontalSide = new Tile(
                        current.x() + direction.dx(),
                        current.z()
                    );
                    Tile verticalSide = new Tile(
                        current.x(),
                        current.z() + direction.dz()
                    );

                    if (blockedTiles.contains(horizontalSide)
                            || blockedTiles.contains(verticalSide)) {
                        continue;
                    }
                }

                float newCost = currentCost + direction.cost();
                if (newCost >= bestCosts.getOrDefault(
                        neighbor, Float.POSITIVE_INFINITY)) {
                    continue;
                }

                bestCosts.put(neighbor, newCost);
                previous.put(neighbor, current);
                queue.add(new SearchNode(neighbor, newCost));
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

        Collections.reverse(path);
        return path;
    }

    private float distanceTo(Tile first, Tile second) {
        int dx = first.x() - second.x();
        int dz = first.z() - second.z();
        return (float) Math.sqrt(dx * dx + dz * dz);
    }
}