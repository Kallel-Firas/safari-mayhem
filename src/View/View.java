// make the view class that draws the game by drawing a 50x50 grid of "dirt.png" images

package View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class View extends JPanel {
    private BufferedImage dirtImage;

    public View() {
        try {
            dirtImage = ImageIO.read(new File("resources/dirt.png"));
        } catch (IOException e) {
            System.out.println("Error loading image");
        }
        // Set the preferred size of the panel
        setPreferredSize(new Dimension(50*16, 50*16));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                g.drawImage(dirtImage, x*16, y*16, null);
            }
        }
    }
}
