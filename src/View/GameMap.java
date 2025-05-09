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
    private Map<String, BufferedImage> jeepImages = new HashMap<>();

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
    private int currentHour = 0; // Track current hour for day/night cycle

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

            // Load entity images
            entityImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
            entityImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            entityImages.put(Elephant.class, ImageIO.read(new File("resources/elephant.png")));
            entityImages.put(Lion.class, ImageIO.read(new File("resources/lion.png")));
            entityImages.put(Poacher.class, ImageIO.read(new File("resources/poacher.png")));
            entityImages.put(Ranger.class, ImageIO.read(new File("resources/ranger.png")));

            // Load and resize all jeep images
            loadAndResizeJeepImage(jeepImages, "jeepstraight", "resources/jeepstraight.png");
            loadAndResizeJeepImage(jeepImages, "jeepforward", "resources/jeepforward.png");
            loadAndResizeJeepImage(jeepImages, "jeepleft", "resources/jeepleft.png");
            loadAndResizeJeepImage(jeepImages, "jeepright", "resources/jeepright.png");

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

    public void setCurrentHour(int hour) {
        this.currentHour = hour;
    }

    private boolean isNightTime() {
        // Night time is from 20:00 to 02:00 (6 hours)
        return currentHour >= 20 || currentHour < 2;
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
            // Special handling for jeeps
            if (entity instanceof Jeep) {
                Jeep jeep = (Jeep) entity;
                int jeepX = jeep.getCurrentX();
                int jeepY = jeep.getCurrentY();

                if (jeepX >= viewportX && jeepX < viewportX + viewportWidth &&
                        jeepY >= viewportY && jeepY < viewportY + viewportHeight) {
                    // Get the appropriate jeep image based on direction
                    BufferedImage jeepImage = jeepImages.get(jeep.getCurrentImageKey());
                    if (jeepImage != null) {
                        // Use visual position for smooth animation
                        int visualX = jeep.getVisualX();
                        int visualY = jeep.getVisualY();

                        // Convert to viewport coordinates
                        int screenX = visualX - (viewportX * textureResolution);
                        int screenY = visualY - (viewportY * textureResolution);

                        g.drawImage(jeepImage, screenX, screenY, textureResolution, textureResolution, null);
                    }
                }
            } else {
                // Regular entity drawing
                BufferedImage image = entityImages.get(entity.getClass());
                int entityX = entity.getCurrentX();
                int entityY = entity.getCurrentY();

                // Check if entity is in viewport
                if (entityX >= viewportX && entityX < viewportX + viewportWidth &&
                        entityY >= viewportY && entityY < viewportY + viewportHeight) {

                    // During nighttime, check visibility for animals
                    if (isNightTime() && entity instanceof Animal) {
                        boolean isVisible = false;

                        // Check if animal has a location chip
                        if (((Animal)entity).hasLocationChip()) {
                            isVisible = true;
                        }

                        // Check proximity to rangers
                        if (!isVisible) {
                            for (Ranger ranger : safari.getRangers()) {
                                int dx = ranger.getCurrentX() - entityX;
                                int dy = ranger.getCurrentY() - entityY;
                                double distance = Math.sqrt(dx * dx + dy * dy);
                                if (distance <= 3) { // Visible within 3 tiles of rangers
                                    isVisible = true;
                                    break;
                                }
                            }
                        }

                        // Check proximity to jeeps (tourists)
                        if (!isVisible) {
                            for (Jeep jeep : safari.getJeeps()) {
                                int dx = jeep.getCurrentX() - entityX;
                                int dy = jeep.getCurrentY() - entityY;
                                double distance = Math.sqrt(dx * dx + dy * dy);
                                if (distance <= 3) { // Visible within 3 tiles of jeeps
                                    isVisible = true;
                                    break;
                                }
                            }
                        }

                        // If not visible at night, skip drawing
                        if (!isVisible) {
                            continue;
                        }
                    }

                    // Draw the entity
                    g.drawImage(image, entity.getVisualX() - viewportX * textureResolution,
                            entity.getVisualY() - viewportY * textureResolution,
                            textureResolution, textureResolution, null);
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

        // Add night time overlay if it's night
        if (isNightTime()) {
            // Create a semi-transparent dark overlay
            g.setColor(new Color(0, 0, 0, 150)); // Dark overlay with 150 alpha (out of 255)
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw lighting effects around roads, trees, and water
            for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
                for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                    Landscape currentTile = terrain.get(x).get(y);

                    // Check if this tile needs lighting
                    if (currentTile instanceof Road || currentTile instanceof Water) {
                        // Create a radial gradient for the light effect
                        int centerX = (x - viewportX) * textureResolution + textureResolution/2;
                        int centerY = (y - viewportY) * textureResolution + textureResolution/2;
                        int radius = textureResolution * 2; // Light radius is 2 tiles

                        // Create a radial gradient for the light
                        for (int r = radius; r > 0; r--) {
                            float alpha = (float)(radius - r) / radius;
                            alpha = alpha * 0.08f; // Reduced maximum brightness to 8%
                            g.setColor(new Color(1f, 1f, 1f, alpha));
                            g.fillOval(centerX - r, centerY - r, r * 2, r * 2);
                        }
                    }
                }
            }

            // Draw lighting effects for trees and bushes
            for (Vegetation vegetation : safari.getVegetationList()) {
                if (vegetation instanceof Tree || vegetation instanceof Bush) {
                    int vegX = vegetation.getCurrentX();
                    int vegY = vegetation.getCurrentY();
                    if (vegX >= viewportX && vegX < viewportX + viewportWidth &&
                            vegY >= viewportY && vegY < viewportY + viewportHeight) {
                        // Create a radial gradient for the light effect
                        int centerX = (vegX - viewportX) * textureResolution + textureResolution/2;
                        int centerY = (vegY - viewportY) * textureResolution + textureResolution/2;
                        int radius = textureResolution * 2; // Light radius is 2 tiles

                        // Create a radial gradient for the light
                        for (int r = radius; r > 0; r--) {
                            float alpha = (float)(radius - r) / radius;
                            alpha = alpha * 0.03f; // Reduced maximum brightness to 8%
                            g.setColor(new Color(1f, 1f, 1f, alpha));
                            g.fillOval(centerX - r, centerY - r, r * 2, r * 2);
                        }
                    }
                }
            }

            // Redraw the lit objects (especially roads) to make them visible
            for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
                for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                    Landscape tile = terrain.get(x).get(y);

                    if (tile instanceof Road) {
                        Road road = (Road) tile;
                        BufferedImage image = roadImages.getOrDefault(road.getImageKey(), roadImages.get("road1"));
                        if (image != null) {
                            g.drawImage(image, (x - viewportX) * textureResolution,
                                    (y - viewportY) * textureResolution,
                                    textureResolution, textureResolution, null);
                        }
                    } else if (tile instanceof Water) {
                        BufferedImage image = terrainImages.get(tile.getClass());
                        if (image != null) {
                            g.drawImage(image, (x - viewportX) * textureResolution,
                                    (y - viewportY) * textureResolution,
                                    null);
                        }
                    }
                }
            }

            // Redraw trees and bushes to make them visible
            for (Vegetation vegetation : safari.getVegetationList()) {
                if (vegetation instanceof Tree || vegetation instanceof Bush) {
                    int vegX = vegetation.getCurrentX();
                    int vegY = vegetation.getCurrentY();
                    if (vegX >= viewportX && vegX < viewportX + viewportWidth &&
                            vegY >= viewportY && vegY < viewportY + viewportHeight) {
                        BufferedImage image = terrainImages.get(vegetation.getClass());
                        if (image != null) {
                            g.drawImage(image, (vegX - viewportX) * textureResolution,
                                    (vegY - viewportY) * textureResolution,
                                    null);
                        }
                    }
                }
            }
        }
        // Redraw jeeps after night overlay (like roads/trees)
        if (isNightTime()) {
            for (Jeep jeep : safari.getJeeps()) {
                int x = jeep.getCurrentX();
                int y = jeep.getCurrentY();

                if (x >= viewportX && x < viewportX + viewportWidth &&
                        y >= viewportY && y < viewportY + viewportHeight) {

                    BufferedImage jeepImage = jeepImages.get(jeep.getCurrentImageKey());
                    if (jeepImage != null) {
                        int screenX = jeep.getVisualX() - viewportX * textureResolution;
                        int screenY = jeep.getVisualY() - viewportY * textureResolution;
                        g.drawImage(jeepImage, screenX, screenY, textureResolution, textureResolution, null);
                    }
                }
            }
        }

    }

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

    private void loadAndResizeJeepImage(Map<String, BufferedImage> jeepImages, String key, String filePath) throws IOException {
        BufferedImage jeepImage = ImageIO.read(new File(filePath));
        Image scaledJeep = jeepImage.getScaledInstance(textureResolution, textureResolution, Image.SCALE_SMOOTH);
        BufferedImage resizedJeep = new BufferedImage(textureResolution, textureResolution, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedJeep.createGraphics();
        g2d.drawImage(scaledJeep, 0, 0, null);
        g2d.dispose();
        jeepImages.put(key, resizedJeep);
    }



}
