package model.util;

import model.board.BoardView;

import java.util.ArrayDeque;

/**
 * Utility class providing static methods for pathfinding on a board.
 * <p>
 * In particular, this class offers methods to check if a path of empty tiles exists
 * between two coordinates using breadth-first search (BFS).
 *
 * @author ubpst
 */
public final class PathFinder {
    private static final int[][] DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private PathFinder() {
        // utility class
    }

    /**
     * Checks if there is a path of empty tiles between start and goal.
     *
     * @param boardView the board to check
     * @param start     starting coordinate
     * @param goal      target coordinate
     * @return true if a path exists, false otherwise
     */
    public static boolean existsPathBFS(BoardView boardView, Vector2D start, Vector2D goal) {
        if (!boardView.isInside(start) || !boardView.isInside(goal)) {
            return false;
        }

        boolean[][] visited = new boolean[boardView.height()][boardView.width()];
        ArrayDeque<Vector2D> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.vertical()][start.horizontal()] = true;

        while (!queue.isEmpty()) {
            Vector2D current = queue.poll();
            if (current.equals(goal)) {
                return true;
            }

            for (int[] direction : DIRECTIONS) {
                Vector2D next = current.sum(new Vector2D(direction[0], direction[1]));
                if (boardView.isInside(next)
                        && !visited[next.vertical()][next.horizontal()]
                        && boardView.tileAt(next).isEmptyTile()) {
                    visited[next.vertical()][next.horizontal()] = true;
                    queue.add(next);
                }
            }
        }

        return false;
    }
}
