package View;

import Model.Safari;
import Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class GameScreen extends JFrame {
    GameMap gameMap;
    MiniMap miniMap;
    Safari safari = new Safari(1, 1, "1/1/2021");
    private JLabel balanceLabel;
    private JLabel timeLabel;
    private JButton shopButton;
    private JDialog shopDialog;
    private JLayeredPane layeredPane;
    private int balance = 1000; // Starting balance
    private String selectedItem = null;
    private String selectedItemType = null;

    public GameScreen() {
        setTitle("Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create layered pane for managing components
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));
        setContentPane(layeredPane);

        // Setup game map (bottom layer)
        gameMap = new GameMap();
        gameMap.setBounds(0, 0, 800, 800);
        gameMap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedItem != null) {
                    int tileX = e.getX() / 32; // Assuming tile size is 32
                    int tileY = e.getY() / 32;
                    placeItem(tileX, tileY);
                }
            }
        });
        layeredPane.add(gameMap, JLayeredPane.DEFAULT_LAYER);

        // Setup minimap (top layer)
        miniMap = new MiniMap(gameMap.getTerrainImages(), gameMap);
        miniMap.setBounds(600, 0, 200, 200);
        gameMap.setMiniMap(miniMap);
        layeredPane.add(miniMap, JLayeredPane.PALETTE_LAYER);

        // Add balance display
        balanceLabel = new JLabel("Balance: $" + balance, SwingConstants.LEFT);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setBounds(450, 20, 150, 20);
        balanceLabel.setForeground(Color.WHITE);
        layeredPane.add(balanceLabel, JLayeredPane.POPUP_LAYER);

        // Add time display
        timeLabel = new JLabel("Day 1, 15:00", SwingConstants.LEFT);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setBounds(450, 50, 150, 20);
        timeLabel.setForeground(Color.WHITE);
        layeredPane.add(timeLabel, JLayeredPane.POPUP_LAYER);

        // Add shop button
        shopButton = new JButton("Shop");
        shopButton.setFont(new Font("Arial", Font.BOLD, 14));
        shopButton.setBounds(450, 80, 120, 30);
        layeredPane.add(shopButton, JLayeredPane.POPUP_LAYER);

        // Create shop dialog
        createShopDialog();

        // Add shop button action listener
        shopButton.addActionListener(e -> shopDialog.setVisible(true));

        pack();
    }

    private void placeItem(int x, int y) {
        if (selectedItem == null) return;

        switch (selectedItemType) {
            case "animal":
                switch (selectedItem) {
                    case "Cheetah":
                        safari.addAnimal(new Cheetah(0, "Cheetah", false, x, y));
                        break;
                    case "Lion":
                        safari.addAnimal(new Lion(0, "Lion", false, x, y));
                        break;
                    case "Elephant":
                        safari.addAnimal(new Elephant(0, "Elephant", false, x, y));
                        break;
                    case "Sheep":
                        safari.addAnimal(new Sheep(0, "Sheep", false, x, y));
                        break;
                }
                break;
            case "plant":
                switch (selectedItem) {
                    case "Grass":
                        safari.addVegetation(new Grass(x, y));
                        break;
                    case "Tree":
                        safari.addVegetation(new Tree(x, y));
                        break;
                    case "Bush":
                        safari.addVegetation(new Bush(x, y));
                        break;
                    case "Water":
                        safari.setLandscape(x, y, new Water(x, y, 1));
                        break;
                    case "Dirt":
                        safari.setLandscape(x, y, new Dirt(x, y));
                        break;
                }
                break;
        }

        // Reset selection and update display
        selectedItem = null;
        selectedItemType = null;
        setCursor(Cursor.getDefaultCursor());
        gameMap.repaint();
    }

    private void createShopDialog() {
        shopDialog = new JDialog(this, "Safari Shop", true);
        shopDialog.setSize(600, 500);
        shopDialog.setLocationRelativeTo(this);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create animal shop panel
        JScrollPane animalPanel = createAnimalShopPanel();
        tabbedPane.addTab("Animals", animalPanel);

        // Create plant shop panel
        JScrollPane plantPanel = createPlantShopPanel();
        tabbedPane.addTab("Plants", plantPanel);

        // Create road shop panel
        JScrollPane roadPanel = createRoadShopPanel();
        tabbedPane.addTab("Roads", roadPanel);

        shopDialog.add(tabbedPane);
    }

    private JScrollPane createAnimalShopPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add animals with their prices
        addShopItem(panel, "resources/cheetah.png", "Cheetah", 500, "animal");
        addShopItem(panel, "resources/lion.png", "Lion", 600, "animal");
        addShopItem(panel, "resources/elephant.png", "Elephant", 800, "animal");
        addShopItem(panel, "resources/sheep.png", "Sheep", 200, "animal");

        return new JScrollPane(panel);
    }

    private JScrollPane createPlantShopPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add plants with their prices
        addShopItem(panel, "resources/grass.png", "Grass", 50, "plant");
        addShopItem(panel, "resources/tree.png", "Tree", 100, "plant");
        addShopItem(panel, "resources/bush.png", "Bush", 75, "plant");
        addShopItem(panel, "resources/water.png", "Water", 200, "plant");
        addShopItem(panel, "resources/dirt.png", "Dirt", 20, "plant");

        return new JScrollPane(panel);
    }

    private JScrollPane createRoadShopPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add placeholder for roads
        JLabel comingSoon = new JLabel("Roads Coming Soon!");
        comingSoon.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(comingSoon);

        return new JScrollPane(panel);
    }

    private void addShopItem(JPanel panel, String imagePath, String name, int price, String type) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try {
            // Load and scale image
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Create name and price labels
            JLabel nameLabel = new JLabel(name);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel priceLabel = new JLabel("$" + price);
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Create buy button
            JButton buyButton = new JButton("Buy");
            buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add buy button action
            buyButton.addActionListener(e -> {
                if (balance >= price) {
                    balance -= price;
                    balanceLabel.setText("Balance: $" + balance);
                    selectedItem = name;
                    selectedItemType = type;
                    shopDialog.setVisible(false);

                    // Change cursor to indicate placement mode
                    try {
                        BufferedImage cursorImg = ImageIO.read(new File(imagePath));
                        cursorImg = resize(cursorImg, 32, 32);
                        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                                cursorImg, new Point(16, 16), "custom cursor");
                        setCursor(customCursor);
                    } catch (IOException ex) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    }
                } else {
                    JOptionPane.showMessageDialog(shopDialog,
                            "Not enough money to buy " + name,
                            "Insufficient Funds",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // Add components to item panel
            itemPanel.add(Box.createVerticalStrut(10));
            itemPanel.add(imageLabel);
            itemPanel.add(Box.createVerticalStrut(5));
            itemPanel.add(nameLabel);
            itemPanel.add(Box.createVerticalStrut(5));
            itemPanel.add(priceLabel);
            itemPanel.add(Box.createVerticalStrut(5));
            itemPanel.add(buyButton);
            itemPanel.add(Box.createVerticalStrut(10));

            panel.add(itemPanel);
        } catch (IOException e) {
            System.out.println("Error loading image: " + imagePath);
            JLabel errorLabel = new JLabel("Image not found: " + name);
            panel.add(errorLabel);
        }
    }

    private BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resized;
    }

    public void run() {
        safari.Update();
        gameMap.update(safari.getLandscapes(), safari.getEntities());
        miniMap.update(safari.getLandscapes(), safari.getAnimalList());
        updateTime();
        gameMap.repaint();
    }

    private void updateTime() {
        String currentTime = timeLabel.getText();
        String[] parts = currentTime.split(", ");
        String[] dayPart = parts[0].split(" ");
        String[] timePart = parts[1].split(":");

        int day = Integer.parseInt(dayPart[1]);
        int hour = Integer.parseInt(timePart[0]);

        hour++;
        if (hour >= 24) {
            hour = 0;
            day++;
        }

        timeLabel.setText(String.format("Day %d, %02d:00", day, hour));
    }
}
