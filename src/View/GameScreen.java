package View;

import Model.Dirt;
import Model.Land;
import Model.Landscape;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends JFrame {
    public GameScreen() {

        List<Landscape> terrain = new ArrayList<>();
        for (int i = 0; i < 50*50; i++) {
            terrain.add(new Dirt());
        }

        setTitle("Model.Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(50*16, 50*16));
        setLayout(new BorderLayout());
        GameMap gameMap = new GameMap(terrain, new ArrayList<>());
        add(gameMap, BorderLayout.CENTER);
        pack();
    }
}
