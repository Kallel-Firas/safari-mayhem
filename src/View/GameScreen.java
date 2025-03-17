package View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameScreen extends JFrame {
    public GameScreen() {
        setTitle("Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(50*16, 50*16));
        setLayout(new BorderLayout());
        GameMap gameMap = new GameMap();
        add(gameMap, BorderLayout.CENTER);
        pack();
    }
}
