import java.util.*;

class ShortestPath {
    // 8 directions (Up, Down, Left, Right, and 4 Diagonals)
    private static final int[] ROW_MOVES = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] COL_MOVES = {0, 0, -1, 1, -1, 1, -1, 1};

    public static List<int[]> findShortestPath(int[][] matrix, int[] start, int[] end) {
        int n = matrix.length;
        boolean[][] visited = new boolean[n][n];
        Map<String, int[]> parentMap = new HashMap<>();

        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0], col = current[1];

            if (row == end[0] && col == end[1]) break;

            for (int i = 0; i < 8; i++) {
                int newRow = row + ROW_MOVES[i];
                int newCol = col + COL_MOVES[i];

                if (isValid(newRow, newCol, n, matrix, visited)) {
                    queue.add(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                    parentMap.put(newRow + "," + newCol, new int[]{row, col});
                }
            }
        }

        return reconstructPath(parentMap, start, end);
    }

    private static boolean isValid(int row, int col, int n, int[][] matrix, boolean[][] visited) {
        return row >= 0 && col >= 0 && row < n && col < n && matrix[row][col] != -1 && !visited[row][col];
    }

    private static List<int[]> reconstructPath(Map<String, int[]> parentMap, int[] start, int[] end) {
        List<int[]> path = new ArrayList<>();
        int[] current = end;

        while (current != null) {
            path.add(current);
            current = parentMap.getOrDefault(current[0] + "," + current[1], null);
        }

        Collections.reverse(path);
        return (path.get(0)[0] == start[0] && path.get(0)[1] == start[1]) ? path : new ArrayList<>();
    }
    /*
    public static void main(String[] args) {
        int[][] matrix = {
                { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                { 0, -1, -1, -1, -1,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0, -1,  0, -1, -1, -1,  0},
                { 0, -1, -1,  0, -1,  0,  0,  0, -1,  0},
                { 0, -1,  0,  0,  0, -1, -1,  0, -1,  0},
                { 0,  0,  0, -1,  0,  0,  0,  0,  0,  0},
                { 0, -1,  0, -1, -1, -1, -1, -1,  0,  0},
                { 0, -1,  0,  0,0,  0,  0, -1, -1,  0},
                { 0, -1, -1, -1, -1, -1,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  0, -1, -1,  0}
        };

        int[] start = {5, 3}; // Start position
        int[] end = {1, 8};   // End position

        List<int[]> path = findShortestPath(matrix, start, end);

        if (!path.isEmpty()) {
            System.out.println("Shortest Path:");
            for (int[] pos : path) {
                System.out.println(Arrays.toString(pos));
            }
        } else {
            System.out.println("No path found.");
        }
    }

     */
}
