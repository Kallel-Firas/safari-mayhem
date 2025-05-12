package Model;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;

public class Safari implements Serializable {
    private List<Herd> herdList = new ArrayList<>();
    private final int difficultyLevel;   //all of these attributes are in the game section
    private List<Jeep> jeeps = new ArrayList<>();
    private List<Poacher> poachers = new ArrayList<>();
    private List<Ranger> rangers = new ArrayList<>();
    private List<Vegetation> vegetationList = new ArrayList<>();
    private List<List<Landscape>> landscapes = new ArrayList<>();
    private int nextAnimalId = 1;
    private boolean lastRoadNetworkComplete = false;
    private String gameName;

    public List<List<Landscape>> getLandscapes() {// added this getter
        return landscapes;
    }

    public Safari(int difficultyLevel, int speedMode, String gameName) {
        this.gameName = gameName;
        this.difficultyLevel = difficultyLevel;
        //this.speedMode = speedMode;
        //this.startingDate = startingDate;
        // Fields are already initialized with the declarations
        // populate landscape
        this.landscapes.clear();
        for (int i = 0; i < 50; i++) {
            List<Landscape> column = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                column.add(new Dirt());
            }
            landscapes.add(column);
        }
        // Add some water to the landscape
        if (difficultyLevel != 3) {
            for (int i = 0; i < 10; i++) {
                int x = (int) (Math.random() * 50);
                int y = (int) (Math.random() * 50);
                // Add a pond, not just one tile.
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (x + j >= 0 && x + j < 50 && y + k >= 0 && y + k < 50) {
                            landscapes.get(x + j).set(y + k, new Water());
                        }
                    }
                }
            }
        }
        // Add some Animals to the landscape
        Herd<Elephant> elephantHerd = new Herd<>(landscapes, this);
        elephantHerd.generatePopulation("Elephant");
        Herd<Lion> lionHerd = new Herd<>(landscapes, this);
        lionHerd.generatePopulation("Lion");
        Herd<Cheetah> cheetahHerd = new Herd<>(landscapes, this);
        cheetahHerd.generatePopulation("Cheetah");
        Herd<Sheep> sheepHerd = new Herd<>(landscapes, this);
        sheepHerd.generatePopulation("Sheep");
        herdList.add(elephantHerd);
        herdList.add(lionHerd);
        herdList.add(cheetahHerd);
        herdList.add(sheepHerd);

        // Add some vegetation to the landscape
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * 50);
            int y = (int) (Math.random() * 50);
            double randomValue = Math.random();
            if (randomValue < 0.3) {
                vegetationList.add(new Tree(x, y));
            } else if (randomValue < 0.7) {
                vegetationList.add(new Bush(x, y));
            } else {
                vegetationList.add(new Grass(x, y));
            }
        }
    }


    public List<Animal> getAnimalList() {
        List<Animal> animals = new ArrayList<>();
        for (Herd herd : herdList) {
            animals.addAll(herd.getAnimalList());
        }
        return animals;
    }

    public void addJeep(Jeep jeep) {
        jeeps.add(jeep);
    }

    public void addPoacher(Poacher poacher) {
        poachers.add(poacher);
    }

    public void addRanger(Ranger ranger) {
        rangers.add(ranger);
    }

    // add a public method to get all the entities (Animals, Poachers, Rangers, Jeeps)
    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        for (Herd herd : herdList) {
            entities.addAll(herd.getAnimalList());
        }
        entities.addAll(vegetationList);
        entities.addAll(poachers);
        entities.addAll(rangers);
        entities.addAll(jeeps);
        return entities;
    }

    public void Update() {
        List<Herd> deadHerds = new ArrayList<>();
        for (Herd herd : herdList) {
            if (herd.isExtinct()){
                deadHerds.add(herd);
                continue;
            }
            herd.updateLandscape(landscapes);
            herd.updateEntities(getEntities());
            herd.update();
        }
        for (Herd herd : deadHerds) {
            herdList.remove(herd);
        }
        
        // Update jeeps to make them move
        updateJeeps();
    }

    public void removeEntityAt(int x, int y, Class <? extends Entity> entityClass) {
        if (Animal.class.isAssignableFrom(entityClass)) {
            for (Herd herd : herdList){
                for (Object animal : herd.getAnimalList()) {
                    if (((Animal)animal).getCurrentX() == x && ((Animal)animal).getCurrentY() == y) {
                        herd.removeAnimal((Animal) animal);
                        return;
                    }
                }
            }
        }
        else {
            for (int i = 0; i < vegetationList.size(); i++) {
                if (vegetationList.get(i).getCurrentX() == x && vegetationList.get(i).getCurrentY() == y) {
                    vegetationList.remove(i);
                    return;
                }
            }
        }
    }

    public List<Ranger> getRangers() {
        return rangers;
    }

    public List<Poacher> getPoachers() {
        return poachers;
    }

    public List<Vegetation> getVegetationList() {
        return vegetationList;
    }

    public List<Herd> getHerdList() {
        return herdList;
    }

    public void removePoacher(Poacher poacher) {
        poachers.remove(poacher);
    }

    public void setLandscape(int x, int y, Landscape landscape) {
        landscapes.get(x).set(y, landscape);
    }

    public int getNextAnimalId() {
        return nextAnimalId++;
    }

    public void addAnimal(Animal animal) {
        // Find the appropriate herd for this animal type
        for (Herd herd : herdList) {
            if (herd.getAnimalList().isEmpty() ||
                    herd.getAnimalList().get(0).getClass() == animal.getClass()) {
                herd.getAnimalList().add(animal);
                return;
            }
        }
        // If no matching herd found, create a new one
        Herd newHerd = new Herd<>(landscapes, this);
        newHerd.getAnimalList().add(animal);
        herdList.add(newHerd);
    }

    public void addVegetation(Vegetation vegetation) {
        vegetationList.add(vegetation);
    }

    public boolean isRoadNetworkComplete() {
        List<Road> startEndPoints = new ArrayList<>();

        // Find all roads located on a white box (valid start/end points)
        for (List<Landscape> row : landscapes) {
            for (Landscape tile : row) {
                if (tile instanceof Road) {
                    Road road = (Road) tile;
                    // Check if this road tile is marked as an entrance/exit
                    // (which should correspond to being on a white box based on placement logic)
                    if (road.isEntrance() || road.isExit()) { 
                        startEndPoints.add(road);
                    }
                }
            }
        }

        // Need at least two distinct start/end points for a complete network
        if (startEndPoints.size() < 2) {
            return false;
        }
        
        // Check for duplicates - we need at least two *unique* locations
        Set<Point> uniqueLocations = new HashSet<>();
        for(Road r : startEndPoints) {
            uniqueLocations.add(new Point(r.getCurrentX(), r.getCurrentY()));
        }
        if (uniqueLocations.size() < 2) {
            return false; // Only one unique white box location is part of the road network
        }

        // Check if ANY pair of distinct start/end points are connected
        for (int i = 0; i < startEndPoints.size(); i++) {
            for (int j = i + 1; j < startEndPoints.size(); j++) {
                Road start = startEndPoints.get(i);
                Road end = startEndPoints.get(j);
                
                // Ensure we are checking two different locations
                if (start.getCurrentX() != end.getCurrentX() || start.getCurrentY() != end.getCurrentY()) {
                    if (areRoadsConnected(start, end)) {
                        return true; // Found a valid connected path between two distinct points
                    }
                }
            }
        }

        // No connected path found between any pair of distinct start/end points
        return false;
    }

    private boolean areRoadsConnected(Road start, Road end) {
        int n = landscapes.size();
        int m = landscapes.get(0).size();
        boolean[][] visited = new boolean[n][m];
        
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{start.getCurrentX(), start.getCurrentY()});
        visited[start.getCurrentX()][start.getCurrentY()] = true;
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];
            
            if (x == end.getCurrentX() && y == end.getCurrentY()) {
                return true;
            }
            
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                
                if (newX >= 0 && newX < n && newY >= 0 && newY < m && 
                    !visited[newX][newY] && landscapes.get(newX).get(newY) instanceof Road) {
                    queue.add(new int[]{newX, newY});
                    visited[newX][newY] = true;
                }
            }
        }
        
        return false;
    }

    public List<Jeep> getJeeps() {
        return jeeps;
    }
    
    // Update all jeeps
    public void updateJeeps() {
        for (Jeep jeep : jeeps) {
            jeep.update(this);

        }
    }

    public boolean wasLastRoadNetworkComplete() {
        return lastRoadNetworkComplete;
    }

    public void setLastRoadNetworkComplete(boolean complete) {
        this.lastRoadNetworkComplete = complete;
    }


    public void saveGame() {
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("saves/" + gameName + ".save"))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }
}