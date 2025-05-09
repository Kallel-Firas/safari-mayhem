package Model;

import java.util.*;
import java.awt.image.BufferedImage;

public class Jeep extends Entity {
    private int price = 500;
    private int capacity = 4;
    private int rentalPrice;
    private int currentPassengers;
    private boolean isMoving = false;
    private List<int[]> currentRoute = null;
    private int routeIndex = 0;
    private int moveDelay = 0;
    private String currentImageKey = "jeepstraight"; // Default image
    private int moveDelayMax = 5; // Slow down movement for smoother appearance

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setRentalPrice(int rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public void setCurrentPassengers(int currentPassengers) {
        this.currentPassengers = currentPassengers;
    }

    public int getCurrentPassengers() {
        return currentPassengers;
    }

    public int getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRentalPrice() {
        return rentalPrice;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public String getCurrentImageKey() {
        return currentImageKey;
    }

    public Jeep(int capacity, int rentalPrice) {
        this.capacity = capacity;
        this.rentalPrice = rentalPrice;
    }

    public void setCurrentRoute(List<int[]> route) {
        this.currentRoute = route;
    }

    public void setRouteIndex(int index) {
        this.routeIndex = index;
    }

    public void update(Safari safari) {
        if (!isMoving) {
            // Only find a new route if we don't have one or if we're at the end of our current route
            if (currentRoute == null || routeIndex >= currentRoute.size()) {
                findRoute(safari);
                if (currentRoute != null && !currentRoute.isEmpty()) {
                    isMoving = true;  // Start moving once we have a route
                }
            }
            return;
        }

        // Only move every few update cycles (to slow down movement)
        moveDelay++;
        if (moveDelay < moveDelayMax) {
            return;
        }
        moveDelay = 0;

        // Move along the route
        if (currentRoute != null && routeIndex < currentRoute.size()) {
            int[] nextPosition = currentRoute.get(routeIndex);

            // Calculate direction of movement
            int dirX = nextPosition[0] - getCurrentX();
            int dirY = nextPosition[1] - getCurrentY();

            // Update jeep image based on direction
            updateJeepImage(dirX, dirY);

            // Use animate movement instead of direct position setting
            animateMovement(nextPosition[0], nextPosition[1]);

            // Update the logical position (animateMovement handles visual position)
            MoveX(nextPosition[0]);
            MoveY(nextPosition[1]);

            routeIndex++;

            // Check if we've reached the end of the route
            if (routeIndex >= currentRoute.size()) {
                // Reached the end, find a new route
                isMoving = false;
                currentRoute = null;
                routeIndex = 0;
                findRoute(safari);  // Immediately try to find a new route
            }
        } else {
            isMoving = false;
            findRoute(safari);  // Try to find a new route if we don't have one
        }
    }

    private void updateJeepImage(int dirX, int dirY) {
        // Moving right
        if (dirX > 0 && dirY == 0) {
            currentImageKey = "jeepright";
        }
        // Moving left
        else if (dirX < 0 && dirY == 0) {
            currentImageKey = "jeepleft";
        }
        // Moving down
        else if (dirX == 0 && dirY > 0) {
            currentImageKey = "jeepstraight";
        }
        // Moving up
        else if (dirX == 0 && dirY < 0) {
            currentImageKey = "jeepforward";
        }
        // Diagonal movement - use the closest cardinal direction
        else if (Math.abs(dirX) > Math.abs(dirY)) {
            // Mostly horizontal movement
            currentImageKey = (dirX > 0) ? "jeepright" : "jeepleft";
        } else {
            // Mostly vertical movement
            currentImageKey = (dirY > 0) ? "jeepstraight" : "jeepforward";
        }
    }

    private void findRoute(Safari safari) {
        // Find all entrance roads
        List<Road> entrances = new ArrayList<>();
        List<Road> exits = new ArrayList<>();

        // Find all entrance and exit roads
        for (List<Landscape> row : safari.getLandscapes()) {
            for (Landscape tile : row) {
                if (tile instanceof Road) {
                    Road road = (Road) tile;
                    if (road.isEntrance()) {
                        entrances.add(road);
                    }
                    if (road.isExit()) {
                        exits.add(road);
                    }
                }
            }
        }

        // If no entrances or exits, can't create a route
        if (entrances.isEmpty() || exits.isEmpty()) {
            return;
        }

        // Pick a random entrance and exit
        Random random = new Random();
        Road start = entrances.get(random.nextInt(entrances.size()));
        Road end = exits.get(random.nextInt(exits.size()));

        // Find a path between them
        currentRoute = findShortestPath(safari.getLandscapes(),
                new int[]{start.getCurrentX(), start.getCurrentY()},
                new int[]{end.getCurrentX(), end.getCurrentY()});

        if (currentRoute != null && !currentRoute.isEmpty()) {
            // Set the jeep position to the starting point
            setCurrentX(currentRoute.get(0)[0]);
            setCurrentY(currentRoute.get(0)[1]);
            routeIndex = 1;  // Start moving from the second point
            isMoving = true;

            // Set initial direction based on next point
            if (currentRoute.size() > 1) {
                int[] nextPoint = currentRoute.get(1);
                int dirX = nextPoint[0] - getCurrentX();
                int dirY = nextPoint[1] - getCurrentY();
                updateJeepImage(dirX, dirY);
            }
        }
    }

    private List<int[]> findShortestPath(List<List<Landscape>> matrix, int[] start, int[] end) {
        int n = matrix.size();
        int m = matrix.get(0).size();
        boolean[][] visited = new boolean[n][m];
        Map<String, int[]> parentMap = new HashMap<>();

        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        visited[start[0]][start[1]] = true;

        // Directions: up, down, left, right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        boolean found = false;
        while (!queue.isEmpty() && !found) {
            int[] current = queue.poll();
            int row = current[0], col = current[1];

            if (row == end[0] && col == end[1]) {
                found = true;
                break;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < m &&
                        !visited[newRow][newCol] && matrix.get(newRow).get(newCol) instanceof Road) {
                    queue.add(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                    parentMap.put(newRow + "," + newCol, new int[]{row, col});
                }
            }
        }

        if (!found) {
            return null;
        }

        // Reconstruct the path
        List<int[]> path = new ArrayList<>();
        int[] current = end;
        while (current[0] != start[0] || current[1] != start[1]) {
            path.add(current);
            String key = current[0] + "," + current[1];
            current = parentMap.get(key);
            if (current == null) break;  // This shouldn't happen if a path was found
        }
        path.add(start);
        Collections.reverse(path);

        return path;
    }
}
