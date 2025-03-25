package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import Model.*;

public class MiniMap extends JPanel {
    private List<List<Landscape>> terrain;
    private Map<Object, BufferedImage> terrainImages;
    private final int miniMapResolution = 4; // Size of each tile in the minimap
    private boolean isOnRight = true;
    private GameMap gameMap;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;

    public MiniMap(Map<Object, BufferedImage> terrainImages, GameMap gameMap) {
        this.terrainImages = terrainImages;
        this.gameMap = gameMap;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (terrain == null) {
            return;
        }
        for (int x = 0; x < terrain.size(); x++) {
            for (int y = 0; y < terrain.get(x).size(); y++) {
                Landscape currentTile = terrain.get(x).get(y);
                BufferedImage image = terrainImages.get(currentTile.getClass());
                g.drawImage(image, x * miniMapResolution, y * miniMapResolution, miniMapResolution, miniMapResolution, null);
            }
        }
        g.setColor(Color.RED);
        g.drawRect(viewportX * miniMapResolution, viewportY * miniMapResolution, viewportWidth * miniMapResolution -1, viewportHeight * miniMapResolution -1);
    }

    public void update(List<List<Landscape>> terrain) {
        this.terrain = terrain;
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