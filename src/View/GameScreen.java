package View;

import Model.Dirt;
import Model.Land;
import Model.Landscape;
import Model.Safari;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class GameScreen extends JFrame {
    GameMap gameMap;
    Safari safari = new Safari(1, 1, "1/1/2021");
    public GameScreen() {

        List<Landscape> terrain = new ArrayList<>();
        for (int i = 0; i < 50*50; i++) {
            terrain.add(new Dirt());
        }

        setTitle("Model.Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(50*16, 50*16));
        setLayout(new BorderLayout());
        gameMap = new GameMap(terrain, new ArrayList<>());
        add(gameMap, BorderLayout.CENTER);
        pack();
    }

    public void run() {
        safari.Update();
        gameMap.update(safari.getLandscapes(), safari.getEntities());
        gameMap.repaint();
    }
}
