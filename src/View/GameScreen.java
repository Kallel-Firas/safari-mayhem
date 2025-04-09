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
    MiniMap miniMap;
    Safari safari = new Safari(1, 1, "1/1/2021");

    public GameScreen() {



        setTitle("Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        gameMap = new GameMap();
        miniMap = new MiniMap(gameMap.getTerrainImages(), gameMap);
        gameMap.setMiniMap(miniMap);
        // TODO: remove magic numbers
        gameMap.setBounds(0, 0, 800, 800);
        miniMap.setBounds(600, 0, 200, 200);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        layeredPane.add(gameMap, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(miniMap, JLayeredPane.PALETTE_LAYER);

        add(layeredPane, BorderLayout.CENTER);
        pack();
    }

    public void run() {
        safari.Update();
        gameMap.update(safari.getLandscapes(), safari.getEntities());
        miniMap.update(safari.getLandscapes(),safari.getAnimalList());
        gameMap.repaint();
    }
}
