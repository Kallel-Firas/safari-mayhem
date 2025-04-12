package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Model.*;
import javax.imageio.ImageIO;

public class MiniMap extends JPanel {
    private List<List<Landscape>> terrain;
    private List<Animal> entities;
    private Map<Object, BufferedImage> terrainImages;
    private final int miniMapResolution = 4; // Size of each tile in the minimap
    private boolean isOnRight = true;
    private final GameMap gameMap;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;
    private Map<Class<? extends Animal>, BufferedImage> animalImages = new HashMap<>();
    private Map<Class<? extends Vegetation>, BufferedImage> vegetationImages = new HashMap<>();
    private Map<Class<? extends Entity>, BufferedImage> entityImages = new HashMap<>();

    public MiniMap(Map<Object, BufferedImage> terrainImages, GameMap gameMap) {
        this.terrainImages = terrainImages;
        this.gameMap = gameMap;
        loadAnimalImages();
        loadVegetationImages();
        loadEntityImages();
        setPreferredSize(new Dimension(50 * miniMapResolution, 50 * miniMapResolution));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    togglePosition();
                }
                else if (SwingUtilities.isLeftMouseButton(e)) {
                    updateGameMapViewport(e.getX(), e.getY());
                }
            }
        });
    }

    private void loadAnimalImages() {
        try {
            animalImages.put(Elephant.class, ImageIO.read(new File("resources/elephant.png")));
            animalImages.put(Lion.class, ImageIO.read(new File("resources/lion.png")));
            animalImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            animalImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
        } catch (IOException e) {
            System.out.println("Error loading animal images: " + e.getMessage());
        }
    }

    private void loadVegetationImages() {
        try {
            vegetationImages.put(Tree.class, ImageIO.read(new File("resources/tree.png")));
            vegetationImages.put(Bush.class, ImageIO.read(new File("resources/bush.png")));
            vegetationImages.put(Grass.class, ImageIO.read(new File("resources/grass.png")));
        } catch (IOException e) {
            System.out.println("Error loading vegetation images: " + e.getMessage());
        }
    }

    private void loadEntityImages() {
        try {
            entityImages.put(Poacher.class, ImageIO.read(new File("resources/poacher.png")));
            entityImages.put(Ranger.class, ImageIO.read(new File("resources/ranger.png")));
        } catch (IOException e) {
            System.out.println("Error loading entity images: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) return;

        // Draw terrain first
        for (int x = 0; x < terrain.size(); x++) {
            for (int y = 0; y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image = terrainImages.get(currentTile.getClass());
                g.drawImage(image, x * miniMapResolution, y * miniMapResolution,
                        miniMapResolution, miniMapResolution, null);
            }
        }

        // Draw vegetation
        for (Vegetation vegetation : gameMap.getSafari().getVegetationList()) {
            BufferedImage img = vegetationImages.get(vegetation.getClass());
            if (img != null) {
                g.drawImage(img, vegetation.getCurrentX() * miniMapResolution,
                        vegetation.getCurrentY() * miniMapResolution,
                        miniMapResolution, miniMapResolution, null);
            }
        }

        // Draw animals
        for (Animal animal : gameMap.getSafari().getAnimalList()) {
            BufferedImage img = animalImages.get(animal.getClass());
            if (img != null) {
                g.drawImage(img, animal.getCurrentX() * miniMapResolution,
                        animal.getCurrentY() * miniMapResolution,
                        miniMapResolution, miniMapResolution, null);
            }
        }

        // Draw rangers
        for (Ranger ranger : gameMap.getSafari().getRangers()) {
            BufferedImage img = entityImages.get(Ranger.class);
            if (img != null) {
                g.drawImage(img, ranger.getCurrentX() * miniMapResolution,
                        ranger.getCurrentY() * miniMapResolution,
                        miniMapResolution, miniMapResolution, null);
            }
        }

        // Draw poachers
        for (Poacher poacher : gameMap.getSafari().getPoachers()) {
            BufferedImage img = entityImages.get(Poacher.class);
            if (img != null) {
                g.drawImage(img, poacher.getCurrentX() * miniMapResolution,
                        poacher.getCurrentY() * miniMapResolution,
                        miniMapResolution, miniMapResolution, null);
            }
        }

        // Draw viewport rectangle
        g.setColor(Color.RED);
        g.drawRect(viewportX * miniMapResolution, viewportY * miniMapResolution,
                viewportWidth * miniMapResolution - 1, viewportHeight * miniMapResolution - 1);
    }

    public void update(List<List<Landscape>> terrain, List<Animal> entities) {
        this.terrain = terrain;
        this.entities = entities;
        repaint();
    }

    private void togglePosition() {
        if (isOnRight) {
            setBounds(0, 0, getWidth(), getHeight());
        } else {
            setBounds(600, 0, getWidth(), getHeight());
        }
        isOnRight = !isOnRight;
        getParent().repaint();
    }

    private void updateGameMapViewport(int x, int y) {
        int newViewportX = x / miniMapResolution;
        int newViewportY = y / miniMapResolution;
        gameMap.setViewport(newViewportX, newViewportY);
        updateViewport(gameMap.getViewportX(), gameMap.getViewportY(), 25, 25);
    }

    public void updateViewport(int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        repaint();
    }
    
}