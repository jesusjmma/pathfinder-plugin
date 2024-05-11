import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class fast_pathfinder {
    // A* pathfinding algorithm
    public static List<Node> findPath(Node start, Node end) {
        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);
        openList.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            openList.remove(current);
            closedList.add(current);
            if (current.equals(end)) {
                return reconstructPath(current);
            }
            for (Node neighbor : current.getNeighbors()) {
                if (closedList.contains(neighbor)) {
                    continue;
                }
                if (!openList.contains(neighbor)) {
                    neighbor.setParent(current);
                    openList.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return 0;
    }

    private static List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}