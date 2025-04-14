package View;

import Model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.util.*;

import Model.*;

public class GameMap extends JPanel implements MouseWheelListener {
    // also load images: tree, grass, bush
    private BufferedImage treeImage;
    private BufferedImage grassImage;
    private BufferedImage bushImage;
    private BufferedImage dirtImage;
    // load water image
    private BufferedImage waterImage;
    // load sheep image
    private BufferedImage sheepImage;
    private BufferedImage babySheepImage;
    // load cheetah and baby cheetah images
    private BufferedImage cheetahImage;
    private BufferedImage babyCheetahImage;
    private List<List<Landscape>> terrain;
    private List<Entity> entities;

    private Map<Object, BufferedImage> terrainImages = new HashMap<>();
    private Map<Object, BufferedImage> entityImages = new HashMap<>();
    private Map<String, BufferedImage> roadImages = new HashMap<>();

    private final int textureResolution = 32;

    private int viewportX = 0;
    private int viewportY = 0;
    private final int viewportWidth = 25; // Display 25 tiles horizontally
    private final int viewportHeight = 25;

    private MiniMap miniMap;

    private Safari safari;

    private BufferedImage poacherImage;
    private BufferedImage rangerImage;

    private Set<Point> roadStartPoints; // Store valid road start points

    public GameMap(Safari safari) {
        this.safari = safari;
        initializeRoadStartPoints(); // Initialize the road start points
        try {
            // Load terrain images
            terrainImages.put(Water.class, ImageIO.read(new File("resources/water.png")));
            terrainImages.put(Dirt.class, ImageIO.read(new File("resources/dirt.png")));
            terrainImages.put(Tree.class, ImageIO.read(new File("resources/tree.png")));
            terrainImages.put(Grass.class, ImageIO.read(new File("resources/grass.png")));
            terrainImages.put(Bush.class, ImageIO.read(new File("resources/bush.png")));
            terrainImages.put(Road.class, ImageIO.read(new File("resources/road1.jpg"))); // Default road image

            // Load road variant images
            roadImages.put("road1", ImageIO.read(new File("resources/road1.jpg")));
            roadImages.put("road2", ImageIO.read(new File("resources/road2withoutback.png")));
            roadImages.put("road3", ImageIO.read(new File("resources/road3withoutback.png")));
            roadImages.put("road4", ImageIO.read(new File("resources/road4.jpg")));
            roadImages.put("road5", ImageIO.read(new File("resources/road5withoutback.png")));
            roadImages.put("road6", ImageIO.read(new File("resources/road6withoutback.png")));

            //roadImages.get("road6").setRGB();
            // Load entity images
            entityImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
            entityImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            entityImages.put(Elephant.class, ImageIO.read(new File("resources/elephant.png")));
            entityImages.put(Lion.class, ImageIO.read(new File("resources/lion.png")));
            entityImages.put(Poacher.class, ImageIO.read(new File("resources/poacher.png")));
            entityImages.put(Ranger.class, ImageIO.read(new File("resources/ranger.png")));

        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(50 * textureResolution / 2, 50 * textureResolution / 2));
        addMouseWheelListener(this);
    }

    private void initializeRoadStartPoints() {
        roadStartPoints = new HashSet<>();

        // Get the map size
        int mapWidth = 50;  // Assuming 50x50 map, adjust if different
        int mapHeight = 50;

        // Add entry points on each side
        // Left side (3 points)
        roadStartPoints.add(new Point(0, mapHeight/4));
        roadStartPoints.add(new Point(0, mapHeight/2));
        roadStartPoints.add(new Point(0, 3*mapHeight/4));

        // Right side (3 points)
        roadStartPoints.add(new Point(mapWidth-1, mapHeight/4));
        roadStartPoints.add(new Point(mapWidth-1, mapHeight/2));
        roadStartPoints.add(new Point(mapWidth-1, 3*mapHeight/4));

        // Top side (3 points)
        roadStartPoints.add(new Point(mapWidth/4, 0));
        roadStartPoints.add(new Point(mapWidth/2, 0));
        roadStartPoints.add(new Point(3*mapWidth/4, 0));

        // Bottom side (3 points)
        roadStartPoints.add(new Point(mapWidth/4, mapHeight-1));
        roadStartPoints.add(new Point(mapWidth/2, mapHeight-1));
        roadStartPoints.add(new Point(3*mapWidth/4, mapHeight-1));
    }

    public void update(List<List<Landscape>> terrain, List<Entity> entities) {
        this.terrain = terrain;
        this.entities = entities;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) {
            return;
        }

        // Get current time from the timeLabel in GameScreen
        boolean isNightTime = false;
        if (getParent() instanceof JLayeredPane && getParent().getParent() instanceof GameScreen) {
            GameScreen gameScreen = (GameScreen) getParent().getParent();
            String timeText = gameScreen.getTimeLabel().getText();
            if (timeText != null) {
                String[] parts = timeText.split(", ");
                if (parts.length > 1) {
                    String[] timeParts = parts[1].split(":");
                    if (timeParts.length > 0) {
                        int hour = Integer.parseInt(timeParts[0]);
                        // Night time between 18:00 and 6:00
                        isNightTime = (hour >= 18 || hour < 6);
                    }
                }
            }
        }

        // Draw terrain
        for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
            for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image;

                // First draw the dirt texture if it's a road
                if (currentTile instanceof Road) {
                    image = terrainImages.get(Dirt.class);
                    if (image != null) {
                        g.drawImage(image, (x - viewportX) * textureResolution,
                                (y - viewportY) * textureResolution,
                                textureResolution, textureResolution, null);
                    }

                    // Then draw the road image on top
                    Road road = (Road) currentTile;
                    image = roadImages.get(road.getImageKey());
                    if (image == null) {
                        image = roadImages.get("road1");
                    }
                } else {
                    image = terrainImages.get(currentTile.getClass());
                }

                if (image != null) {
                    g.drawImage(image, (x - viewportX) * textureResolution,
                            (y - viewportY) * textureResolution,
                            textureResolution, textureResolution, null);
                }

                // Draw white boxes at road start points
                if (roadStartPoints.contains(new Point(x, y))) {
                    g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
                    g.fillRect((x - viewportX) * textureResolution,
                            (y - viewportY) * textureResolution,
                            textureResolution, textureResolution);
                    g.setColor(Color.BLACK);
                    g.drawRect((x - viewportX) * textureResolution,
                            (y - viewportY) * textureResolution,
                            textureResolution, textureResolution);
                }
            }
        }

        // Draw vegetation
        for (Vegetation vegetation : safari.getVegetationList()) {
            BufferedImage image = terrainImages.get(vegetation.getClass());
            int vegX = vegetation.getCurrentX();
            int vegY = vegetation.getCurrentY();
            if (vegX >= viewportX && vegX < viewportX + viewportWidth &&
                    vegY >= viewportY && vegY < viewportY + viewportHeight) {
                g.drawImage(image, (vegX - viewportX) * textureResolution,
                        (vegY - viewportY) * textureResolution, null);
            }
        }

        // Draw entities (animals, poachers, rangers)
        for (Entity entity : entities) {
            // Skip animals at night unless they meet visibility criteria
            if (isNightTime && entity instanceof Animal) {
                Animal animal = (Animal) entity;
                if (!isAnimalVisibleAtNight(animal)) {
                    continue;
                }
            }

            BufferedImage image = entityImages.get(entity.getClass());
            int entityX = entity.getCurrentX();
            int entityY = entity.getCurrentY();
            if (entityX >= viewportX && entityX < viewportX + viewportWidth &&
                    entityY >= viewportY && entityY < viewportY + viewportHeight) {
                g.drawImage(image, (entityX - viewportX) * textureResolution,
                        (entityY - viewportY) * textureResolution, null);
            }
        }

        // Apply night effect if it's night time
        if (isNightTime) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Create a semi-transparent dark overlay for the entire map
            g2d.setColor(new Color(0, 0, 30, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Set composite to clear areas that should be visible
            g2d.setComposite(AlphaComposite.Clear);

            // Make areas around visible entities visible (create light circles)
            for (Entity entity : entities) {
                if (entity instanceof Ranger ||
                        (entity instanceof Animal && ((Animal)entity).hasLocationChip())) {

                    int entityX = entity.getCurrentX();
                    int entityY = entity.getCurrentY();

                    if (entityX >= viewportX && entityX < viewportX + viewportWidth &&
                            entityY >= viewportY && entityY < viewportY + viewportHeight) {

                        int screenX = (entityX - viewportX) * textureResolution + textureResolution/2;
                        int screenY = (entityY - viewportY) * textureResolution + textureResolution/2;
                        int radius = textureResolution * 3; // Visibility radius

                        g2d.fillOval(screenX - radius, screenY - radius, radius * 2, radius * 2);
                    }
                }
            }

            // Make roads and water visible at night
            for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
                for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                    Landscape tile = terrain.get(x).get(y);

                    // Roads and water are visible at night
                    if (tile instanceof Road || tile instanceof Water) {
                        int screenX = (x - viewportX) * textureResolution;
                        int screenY = (y - viewportY) * textureResolution;
                        g2d.fillRect(screenX, screenY, textureResolution, textureResolution);
                    }
                }
            }

            // Make vegetation visible at night
            for (Vegetation vegetation : safari.getVegetationList()) {
                int vegX = vegetation.getCurrentX();
                int vegY = vegetation.getCurrentY();

                if (vegX >= viewportX && vegX < viewportX + viewportWidth &&
                        vegY >= viewportY && vegY < viewportY + viewportHeight) {

                    int screenX = (vegX - viewportX) * textureResolution;
                    int screenY = (vegY - viewportY) * textureResolution;
                    g2d.fillRect(screenX, screenY, textureResolution, textureResolution);
                }
            }

            g2d.dispose();
        }
    }
    // Helper method to determine if an animal is visible at night
    private boolean isAnimalVisibleAtNight(Animal animal) {
        // Check if animal has location chip (to be implemented)
        if (animal.hasLocationChip()) {
            return true;
        }

        // Check if animal is near a ranger (within 3 tiles)
        boolean nearRanger = safari.getRangers().stream()
                .anyMatch(ranger ->
                        Math.abs(ranger.getCurrentX() - animal.getCurrentX()) <= 3 &&
                                Math.abs(ranger.getCurrentY() - animal.getCurrentY()) <= 3);

        // TODO: Check if animal is near tourists when implemented

        return nearRanger;
    }
    /*
    @Override

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) {
            return;
        }
        //if hour >18 || < 6:
        // Draw terrain
        for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
            for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image;

                // First draw the dirt texture if it's a road
                if (currentTile instanceof Road) {
                    image = terrainImages.get(Dirt.class);
                    if (image != null) {
                        g.drawImage(image, (x - viewportX) * textureResolution,
                                  (y - viewportY) * textureResolution,
                                  textureResolution, textureResolution, null);
                    }

                    // Then draw the road image on top
                    Road road = (Road) currentTile;
                    image = roadImages.get(road.getImageKey());
                    if (image == null) {
                        image = roadImages.get("road1");
                    }
                } else {
                    image = terrainImages.get(currentTile.getClass());
                }

                if (image != null) {
                    g.drawImage(image, (x - viewportX) * textureResolution,
                              (y - viewportY) * textureResolution,
                              textureResolution, textureResolution, null);
                }

                // Draw white boxes at road start points
                if (roadStartPoints.contains(new Point(x, y))) {
                    g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
                    g.fillRect((x - viewportX) * textureResolution,
                             (y - viewportY) * textureResolution,
                             textureResolution, textureResolution);
                    g.setColor(Color.BLACK);
                    g.drawRect((x - viewportX) * textureResolution,
                             (y - viewportY) * textureResolution,
                             textureResolution, textureResolution);
                }
            }
        }

        // Draw entities (animals, poachers, rangers)
        for (Entity entity : entities) {
            BufferedImage image = entityImages.get(entity.getClass());
            int entityX = entity.getCurrentX();
            int entityY = entity.getCurrentY();
            if (entityX >= viewportX && entityX < viewportX + viewportWidth &&
                entityY >= viewportY && entityY < viewportY + viewportHeight) {
                g.drawImage(image, (entityX - viewportX) * textureResolution,
                          (entityY - viewportY) * textureResolution, null);
            }
        }

        // Draw vegetation
        for (Vegetation vegetation : safari.getVegetationList()) {
            BufferedImage image = terrainImages.get(vegetation.getClass());
            int vegX = vegetation.getCurrentX();
            int vegY = vegetation.getCurrentY();
            if (vegX >= viewportX && vegX < viewportX + viewportWidth &&
                vegY >= viewportY && vegY < viewportY + viewportHeight) {
                g.drawImage(image, (vegX - viewportX) * textureResolution,
                          (vegY - viewportY) * textureResolution, null);
            }
        }
    }

     */

    public Map<Object, BufferedImage> getTerrainImages() {
        return terrainImages;
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (e.isShiftDown()) {
            viewportX = Math.max(0, Math.min(terrain.size() - viewportWidth, viewportX + notches));
        } else {
            viewportY = Math.max(0, Math.min(terrain.get(0).size() - viewportHeight, viewportY + notches));
        }
        if (miniMap != null) {
            miniMap.updateViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        }
        repaint();
    }

    public int getViewportX() {
        return viewportX;
    }

    public int getViewportY() {
        return viewportY;
    }

    public void setViewport(int x, int y) {
        viewportX = Math.max(0, Math.min(terrain.size() - viewportWidth, x));
        viewportY = Math.max(0, Math.min(terrain.get(0).size() - viewportHeight, y));
        if (miniMap != null) {
            miniMap.updateViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        }
        repaint();
    }

    public Safari getSafari() {
        return safari;
    }

    public void setMiniMap(MiniMap miniMap) {
        this.miniMap = miniMap;
    }

    public boolean isValidRoadStartPoint(int x, int y) {
        return roadStartPoints.contains(new Point(x, y));
    }
}
