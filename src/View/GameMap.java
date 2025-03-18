package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.util.*;

import Model.*;

public class GameMap extends JPanel {
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
    private List<Landscape> terrain;
    private List<Object> entities;

    private Map<Object, BufferedImage> terrainImages = new HashMap<>();
    private Map<Object, BufferedImage> entityImages = new HashMap<>();
    // load the safari map
    public GameMap(List<Landscape> terrain, List<Objects> animals) {
        try {
            terrainImages.put(Water.class, ImageIO.read(new File("resources/water.png")));
            // do like the line above for the following lines
            terrainImages.put(Dirt.class, ImageIO.read(new File("resources/dirt.png")));
            terrainImages.put(Tree.class, ImageIO.read(new File("resources/tree.png")));
            terrainImages.put(Grass.class, ImageIO.read(new File("resources/grass.png")));
            terrainImages.put(Bush.class, ImageIO.read(new File("resources/bush.png")));
            // load the images for the entities into the entityImages map
            entityImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
            entityImages.put(BabySheep.class, ImageIO.read(new File("resources/baby_sheep.png")));
            entityImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            entityImages.put(BabyCheetah.class, ImageIO.read(new File("resources/baby_cheetah.png")));
            entityImages.put(Poacher.class, ImageIO.read(new File("resources/poacher.png")));
            entityImages.put(Ranger.class, ImageIO.read(new File("resources/ranger.png")));
            entityImages.put(Jeep.class, ImageIO.read(new File("resources/jeep.png")));

        } catch (IOException e) {
            System.out.println("Error loading image");
        }
        setPreferredSize(new Dimension(50*16, 50*16));
        this.terrain = terrain;
        this.animals = animals;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                g.drawImage(terrainImages.get(terrain.get(x + 50 * y).getClass()), x*16, y*16, null);
            }
        }
        g.drawImage(grassImage, 0, 0, null);
        g.drawImage(waterImage, 16, 0, null);
        g.drawImage(treeImage, 16*16, 16*15, null);
        g.drawImage(cheetahImage, 16*32, 16*32, null);
        g.drawImage(babyCheetahImage, 16*16, 16*16, null);
        g.drawImage(babySheepImage, 16*16, 16*17, null);
    }
}
