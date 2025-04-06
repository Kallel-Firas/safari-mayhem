package View;

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

    private final int textureResolution = 32;

    private int viewportX = 0;
    private int viewportY = 0;
    private final int viewportWidth = 25; // Display 25 tiles horizontally
    private final int viewportHeight = 25;

    private MiniMap miniMap;

    public void setMiniMap(MiniMap miniMap) {
        this.miniMap = miniMap;
    }

    public GameMap() {
        try {
            terrainImages.put(Water.class, ImageIO.read(new File("resources/water.png")));
            // do like the line above for the following lines
            terrainImages.put(Dirt.class, ImageIO.read(new File("resources/dirt.png")));
            terrainImages.put(Tree.class, ImageIO.read(new File("resources/tree.png")));
            terrainImages.put(Grass.class, ImageIO.read(new File("resources/grass.png")));
            terrainImages.put(Bush.class, ImageIO.read(new File("resources/bush.png")));
            // load the images for the entities into the entityImages map
            entityImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
            //entityImages.put(BabySheep.class, ImageIO.read(new File("resources/baby_sheep.png")));
            entityImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            //entityImages.put(BabyCheetah.class, ImageIO.read(new File("resources/baby_cheetah.png")));
            entityImages.put(Elephant.class, ImageIO.read(new File("resources/elephant.png")));
            //entityImages.put(BabyElephant.class, ImageIO.read(new File("resources/baby_elephant.png")));
            entityImages.put(Lion.class, ImageIO.read(new File("resources/lion.png")));
            //entityImages.put(BabyLion.class, ImageIO.read(new File("resources/baby_lion.png")));

            //entityImages.put(Poacher.class, ImageIO.read(new File("resources/poacher.png")));
            //entityImages.put(Ranger.class, ImageIO.read(new File("resources/ranger.png")));
            //entityImages.put(Jeep.class, ImageIO.read(new File("resources/jeep.png")));

        } catch (IOException e) {
            System.out.println("Error loading image");
        }
        setPreferredSize(new Dimension(50*textureResolution/2, 50*textureResolution/2));
        addMouseWheelListener(this);
    }

    public void update(List<List<Landscape>> terrain, List<Entity> entities) {
        this.terrain = terrain;
        this.entities = entities;
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (terrain == null) {
//            return;
//        }
//        for (int x = 0; x < terrain.size(); x++) {
//            for (int y = 0; y < terrain.size(); y++) {
//                Landscape currentTile = terrain.get(x).get(y);
//                BufferedImage image = terrainImages.get(currentTile.getClass());
//                g.drawImage(image, x*textureResolution, y*textureResolution, null);
//            }
//        }
//        // draw entities
//        for (Object entity : entities) {
//            BufferedImage image = entityImages.get(entity.getClass());
//            if (entity instanceof Animal animal) {
//                g.drawImage(image, animal.getCurrentX()*textureResolution, animal.getCurrentY()*textureResolution, null);
//            }
//        }
////        g.drawImage(entityImages.get(Cheetah.class), 0, 0, null);
////        g.drawImage(waterImage, 16, 0, null);
////        g.drawImage(treeImage, 16*16, 16*15, null);
////        g.drawImage(cheetahImage, 16*32, 16*32, null);
////        g.drawImage(babyCheetahImage, 16*16, 16*16, null);
////        g.drawImage(babySheepImage, 16*16, 16*17, null);
//    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) {
            return;
        }
        for (int x = viewportX; x < viewportX + viewportWidth && x < terrain.size(); x++) {
            for (int y = viewportY; y < viewportY + viewportHeight && y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image = terrainImages.get(currentTile.getClass());
                g.drawImage(image, (x - viewportX) * textureResolution, (y - viewportY) * textureResolution, null);
            }
        }
        for (Entity entity : entities) {
            BufferedImage image = entityImages.get(entity.getClass());
            int entityX = entity.getCurrentX();
            int entityY = entity.getCurrentY();
            if (entityX >= viewportX && entityX < viewportX + viewportWidth && entityY >= viewportY && entityY < viewportY + viewportHeight) {
                g.drawImage(image, (entityX - viewportX) * textureResolution, (entityY - viewportY) * textureResolution, null);
            }
        }
        for (Entity entity : entities) {
            if (entity instanceof Vegetation) {
                Vegetation vegetation = (Vegetation) entity;
                BufferedImage image = terrainImages.get(vegetation.getClass());
                g.drawImage(image, (vegetation.getCurrentX() - viewportX) * textureResolution, (vegetation.getCurrentY() - viewportY) * textureResolution, null);
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

}
