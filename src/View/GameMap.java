package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public GameMap() {
        try {
            waterImage = ImageIO.read(new File("resources/water.png"));
            dirtImage = ImageIO.read(new File("resources/dirt.png"));
            treeImage = ImageIO.read(new File("resources/tree.png"));
            grassImage = ImageIO.read(new File("resources/grass.png"));
            bushImage = ImageIO.read(new File("resources/bush.png"));
            sheepImage = ImageIO.read(new File("resources/sheep.png"));
            babySheepImage = ImageIO.read(new File("resources/baby_sheep.png"));
            cheetahImage = ImageIO.read(new File("resources/cheetah.png"));
            babyCheetahImage = ImageIO.read(new File("resources/baby_cheetah.png"));
        } catch (IOException e) {
            System.out.println("Error loading image");
        }
        setPreferredSize(new Dimension(50*16, 50*16));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                g.drawImage(dirtImage, x*16, y*16, null);
            }
        }*/
        g.drawImage(grassImage, 0, 0, null);
        g.drawImage(waterImage, 16, 0, null);
        g.drawImage(treeImage, 16*16, 16*15, null);
        g.drawImage(cheetahImage, 16*32, 16*32, null);
        g.drawImage(babyCheetahImage, 16*16, 16*16, null);
        g.drawImage(babySheepImage, 16*16, 16*17, null);
    }
}
