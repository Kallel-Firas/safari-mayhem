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
    private boolean isNightTime = false;
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

        // Get night time status from the game screen
        if (getParent() instanceof JLayeredPane && getParent().getParent() instanceof GameScreen) {
            GameScreen gameScreen = (GameScreen) getParent().getParent();
            String timeText = gameScreen.getTimeLabel().getText();
            if (timeText != null) {
                String[] parts = timeText.split(", ");
                if (parts.length > 1) {
                    String[] timeParts = parts[1].split(":");
                    if (timeParts.length > 0) {
                        int hour = Integer.parseInt(timeParts[0]);
                        isNightTime = (hour >= 18 || hour < 6);
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) {
            return;
        }

        // Draw terrain
        for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
            for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image = terrainImages.get(currentTile.getClass());
                if (image != null) {
                    g.drawImage(image, (x - viewportX) * textureResolution,
                            (y - viewportY) * textureResolution,
                            textureResolution, textureResolution, null);
                }
            }
        }

        // Draw road start points
        //g.setColor(Color.WHITE);
        for (Point point : roadStartPoints) {
            int screenX = (point.x - viewportX) * textureResolution;
            int screenY = (point.y - viewportY) * textureResolution;

            if (screenX >= 0 && screenX < getWidth() && screenY >= 0 && screenY < getHeight()) {
                g.fillRect(screenX, screenY, textureResolution, textureResolution);
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
                        (vegY - viewportY) * textureResolution, textureResolution, textureResolution, null);
            }
        }

        // Draw entities
        for (Entity entity : entities) {
            // Skip animals at night unless they should be visible
            if (isNightTime && entity instanceof Animal && !isAnimalVisibleAtNight((Animal)entity)) {
                continue;
            }

            BufferedImage image = entityImages.get(entity.getClass());
            int entityX = entity.getCurrentX();
            int entityY = entity.getCurrentY();
            if (entityX >= viewportX && entityX < viewportX + viewportWidth &&
                    entityY >= viewportY && entityY < viewportY + viewportHeight) {
                g.drawImage(image, (entityX - viewportX) * textureResolution,
                        (entityY - viewportY) * textureResolution, textureResolution, textureResolution, null);
            }
        }

        // Apply night effect
        if (isNightTime) {
            applyNightOverlay(g);
        }
    }

    // In GameMap.java, update the applyNightOverlay method
    private void applyNightOverlay(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Dark overlay for night time
        g2d.setColor(new Color(0, 0, 30, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Create visibility areas using DstOut composite (removes darkness)
        g2d.setComposite(AlphaComposite.DstOut);

        // Make water and roads completely visible (no night effect)
        for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
            for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                Landscape tile = terrain.get(x).get(y);
                if (tile instanceof Water || tile instanceof Road) {
                    int screenX = (x - viewportX) * textureResolution;
                    int screenY = (y - viewportY) * textureResolution;

                    // Make the tile completely visible (no night effect)
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
                    g2d.fillRect(screenX, screenY, textureResolution, textureResolution);
                }
            }
        }

        // Make vegetation completely visible (no night effect)
        for (Vegetation vegetation : safari.getVegetationList()) {
            int vegX = vegetation.getCurrentX();
            int vegY = vegetation.getCurrentY();

            if (isInViewport(vegX, vegY)) {
                int screenX = (vegX - viewportX) * textureResolution;
                int screenY = (vegY - viewportY) * textureResolution;

                // Make the vegetation completely visible (no night effect)
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));
                g2d.fillRect(screenX, screenY, textureResolution, textureResolution);
            }
        }

        // Make areas around rangers and tracked animals visible
        for (Entity entity : entities) {
            if (entity instanceof Ranger || (entity instanceof Animal && ((Animal) entity).hasLocationChip())) {
                int entityX = entity.getCurrentX();
                int entityY = entity.getCurrentY();
                if (isInViewport(entityX, entityY)) {
                    int screenX = (entityX - viewportX) * textureResolution + textureResolution / 2;
                    int screenY = (entityY - viewportY) * textureResolution + textureResolution / 2;

                    int radius = textureResolution * 3;

                    // Create gradient visibility effect
                    for (int r = radius; r > 0; r -= textureResolution/4) {
                        float alpha = 1.0f - (float)(radius - r) / radius;
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, alpha));
                        g2d.fillOval(screenX - r, screenY - r, r * 2, r * 2);
                    }
                }
            }
        }

        g2d.dispose();
    }
    // Helper method to check if coordinates are in viewport
    private boolean isInViewport(int x, int y) {
        return x >= viewportX && x < viewportX + viewportWidth &&
                y >= viewportY && y < viewportY + viewportHeight;
    }

    // Helper method to determine if animal is visible at night
    private boolean isAnimalVisibleAtNight(Animal animal) {
        // Animals with location chips are always visible
        if (animal.hasLocationChip()) {
            return true;
        }

        // Check if animal is near a ranger (within 3 tiles)
        for (Ranger ranger : safari.getRangers()) {
            if (Math.abs(ranger.getCurrentX() - animal.getCurrentX()) <= 3 &&
                    Math.abs(ranger.getCurrentY() - animal.getCurrentY()) <= 3) {
                return true;
            }
        }

        // Check if animal is near water, plants, or roads
        int animalX = animal.getCurrentX();
        int animalY = animal.getCurrentY();
        
        // Check surrounding 3x3 area
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                int checkX = animalX + dx;
                int checkY = animalY + dy;
                
                if (checkX >= 0 && checkX < terrain.size() && 
                    checkY >= 0 && checkY < terrain.get(checkX).size()) {
                    
                    Landscape tile = terrain.get(checkX).get(checkY);
                    // Check for water or road
                    if (tile instanceof Water || tile instanceof Road) {
                        return true;
                    }
                    
                    // Check for vegetation
                    for (Vegetation vegetation : safari.getVegetationList()) {
                        if (vegetation.getCurrentX() == checkX && vegetation.getCurrentY() == checkY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
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

    public void setNightTime(boolean isNightTime) {
        this.isNightTime = isNightTime;
        repaint(); // Force repaint when night status changes
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
