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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class GameScreen extends JFrame {
    GameMap gameMap;
    MiniMap miniMap;
    Safari safari;
    private JLabel balanceLabel;
    private JLabel timeLabel;
    private JLabel touristLabel;
    private JButton shopButton;
    private JButton saveButton;
    private JDialog shopDialog;
    private JLayeredPane layeredPane;
    private int balance = 1000; // Starting balance
    private String selectedItem = null;
    private String selectedItemType = null;

    private Timer timer1;
    private Timer timer2;
    private Map<Class<? extends Animal>, BufferedImage> animalImages = new HashMap<>();

    public GameScreen(String gameName, Safari s,boolean fromLoadGame) {
        if (fromLoadGame) {
            this.safari = s;
        } else {
            safari = new Safari(1, 1, gameName);
        }
        setTitle("Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load animal images once
        loadAnimalImages();

        // Create layered pane for managing components
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));
        setContentPane(layeredPane);

        // Setup game map (bottom layer)
        gameMap = new GameMap(this.safari);
        gameMap.setBounds(0, 0, 800, 800);
        gameMap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Calculate tile coordinates considering viewport offset
                int tileX = (e.getX() / 32) + gameMap.getViewportX();
                int tileY = (e.getY() / 32) + gameMap.getViewportY();

                if (selectedItem != null) {
                    placeItem(tileX, tileY);
                } else {
                    // If no item is selected, check if we clicked on a road
                    Landscape clickedTile = safari.getLandscapes().get(tileX).get(tileY);
                    if (clickedTile instanceof Road) {
                        Road road = (Road) clickedTile;
                        String imageKey = road.getImageKey();
                        String roadType = "";
                        switch (imageKey) {
                            case "road1": roadType = "Straight Road"; break;
                            case "road2": roadType = "Corner Road"; break;
                            case "road3": roadType = "T-Junction Road"; break;
                            case "road4": roadType = "Crossroad"; break;
                            case "road5": roadType = "End Road"; break;
                            case "road6": roadType = "Side Road"; break;
                            default: roadType = "Straight Road";
                        }

                        if (e.getButton() == MouseEvent.BUTTON1) {  // Left click - sell the road
                            // Remove the road
                            safari.setLandscape(tileX, tileY, new Dirt());
                            // Refund the road cost
                            balance += 250; // Road cost is 250
                            balanceLabel.setText("Balance: $" + balance);
                            // Reset the road network completion status since we removed a road
                            safari.setLastRoadNetworkComplete(false);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {  // Right click - copy the road
                            if (balance >= 250) {  // Check if player can afford the road
                                // Deduct the cost
                                balance -= 250;
                                balanceLabel.setText("Balance: $" + balance);
                                // Set the road type as selected item
                                selectedItem = roadType;
                                selectedItemType = "road";
                                
                                // Set custom cursor with the road image
                                try {
                                    String imagePath = "resources/" + imageKey + (imageKey.equals("road1") || imageKey.equals("road4") ? ".jpg" : "withoutback.png");
                                    BufferedImage cursorImg = ImageIO.read(new File(imagePath));
                                    cursorImg = resize(cursorImg, 32, 32);
                                    Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                                            cursorImg, new Point(16, 16), "custom cursor");
                                    setCursor(customCursor);
                                } catch (IOException ex) {
                                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                                }
                            } else {
                                JOptionPane.showMessageDialog(GameScreen.this,
                                    "Not enough money to copy this road!",
                                    "Insufficient Funds",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        
                        // Update the display
                        gameMap.repaint();
                        miniMap.repaint();
                    }
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

        // Add tourist count display
        touristLabel = new JLabel("Tourists: 0", SwingConstants.LEFT);
        touristLabel.setFont(new Font("Arial", Font.BOLD, 16));
        touristLabel.setBounds(450, 80, 150, 20);
        touristLabel.setForeground(Color.WHITE);
        layeredPane.add(touristLabel, JLayeredPane.POPUP_LAYER);

        // Add shop button
        shopButton = new JButton("Shop");
        shopButton.setFont(new Font("Arial", Font.BOLD, 14));
        shopButton.setBounds(450, 110, 120, 30);
        layeredPane.add(shopButton, JLayeredPane.POPUP_LAYER);

        // Create shop dialog
        createShopDialog();

        // Add shop button action listener
        shopButton.addActionListener(e -> shopDialog.setVisible(true));

        // Add shop button
        saveButton = new JButton("Save+Exit");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBounds(450, 150, 120, 30);
        layeredPane.add(saveButton, JLayeredPane.POPUP_LAYER);
        saveButton.addActionListener(e -> {
            // Save game logic here
            // For example, you can call a method to save the game state
            timer1.stop();
            timer2.stop();
            safari.saveGame();
            JOptionPane.showMessageDialog(this, "Game saved successfully!");
            // Exit the game to the main menu
            this.dispose();
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setLocationRelativeTo(null);
            welcomeScreen.setVisible(true);
        });

//        add(layeredPane, BorderLayout.CENTER);
        pack();
    }

    private void loadAnimalImages() {
        try {
            animalImages.put(Elephant.class, ImageIO.read(new File("resources/elephant.png")));
            animalImages.put(Lion.class, ImageIO.read(new File("resources/lion.png")));
            animalImages.put(Cheetah.class, ImageIO.read(new File("resources/cheetah.png")));
            animalImages.put(Sheep.class, ImageIO.read(new File("resources/sheep.png")));
        } catch (IOException e) {
            System.out.println("Error loading animal images: " + e.getMessage());
        }
    }

    private void placeItem(int x, int y) {
        if (selectedItem == null || selectedItemType == null) return;

        System.out.println("Attempting to place " + selectedItemType + ": " + selectedItem + " at (" + x + "," + y + ")");

        // First check if coordinates are valid
        if (x < 0 || x >= safari.getLandscapes().size() ||
            y < 0 || y >= safari.getLandscapes().get(0).size()) {
            showPlacementError("Invalid coordinates");
            return;
        }

        // Check if the position is already occupied by an animal
        boolean isOccupiedByAnimal = safari.getAnimalList().stream()
                .anyMatch(animal -> animal.getCurrentX() == x && animal.getCurrentY() == y);

        // Check if the position has vegetation (tree or bush)
        boolean hasVegetation = safari.getEntities().stream()
                .filter(entity -> entity instanceof Vegetation)
                .anyMatch(vegetation ->
                    vegetation.getCurrentX() == x &&
                    vegetation.getCurrentY() == y &&
                    (vegetation instanceof Tree || vegetation instanceof Bush));

        // Check if the position is water
        boolean isWater = safari.getLandscapes().get(x).get(y) instanceof Water;

        // Check if the position is dirt (for road placement)
        boolean isDirt = safari.getLandscapes().get(x).get(y) instanceof Dirt;

        // Handle jeep placement
        if (selectedItemType.equals("jeep")) {
            // Check if the clicked position is a road
            if (!(safari.getLandscapes().get(x).get(y) instanceof Road)) {
                refundPurchase();
                showPlacementError("Jeeps can only be placed on roads!");
                return;
            }

            // Check if there are any roads on the map
            boolean hasRoads = safari.getLandscapes().stream()
                    .flatMap(row -> row.stream())
                    .anyMatch(tile -> tile instanceof Road);

            if (!hasRoads) {
                refundPurchase();
                showPlacementError("Cannot place jeep! There are no roads on the map.");
                return;
            }

            // Check if the road network is complete
            if (!safari.isRoadNetworkComplete()) {
                refundPurchase();
                showPlacementError("Cannot place jeep! The road network must be complete (connected entrance to exit).");
                return;
            }

            // Create and add the jeep
            Jeep jeep = new Jeep(4, 500); // 4 capacity, 500 rental price
            jeep.setCurrentX(x);
            jeep.setCurrentY(y);
            
            // Set the initial route to stay on the current road
            List<int[]> initialRoute = new ArrayList<>();
            initialRoute.add(new int[]{x, y});
            jeep.setCurrentRoute(initialRoute);
            jeep.setRouteIndex(0);
            jeep.setMoving(true);  // Set moving to true so the jeep will start moving
            
            safari.addJeep(jeep);
            System.out.println("Jeep placed successfully on road at (" + x + ", " + y + ")");

            selectedItem = null;
            selectedItemType = null;
            setCursor(Cursor.getDefaultCursor());
            gameMap.repaint();
            miniMap.repaint();
            return;
        }

        // Handle road placement
        if (selectedItemType.equals("road")) {
            // Check if this is the first road placement or if we're starting from a white box
            boolean isFirstRoad = safari.getLandscapes().stream()
                .flatMap(row -> row.stream())
                .noneMatch(tile -> tile instanceof Road);

            boolean isStartingFromWhiteBox = gameMap.isValidRoadStartPoint(x, y);

            if (!isFirstRoad && !isStartingFromWhiteBox) {
                // For roads not starting from a white box, must be adjacent to an existing road
                boolean isAdjacentToRoad = false;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < safari.getLandscapes().size() &&
                            ny >= 0 && ny < safari.getLandscapes().get(0).size()) {
                            if (safari.getLandscapes().get(nx).get(ny) instanceof Road) {
                                isAdjacentToRoad = true;
                                break;
                            }
                        }
                    }
                    if (isAdjacentToRoad) break;
                }

                if (!isAdjacentToRoad) {
                    refundPurchase();
                    showPlacementError("Roads must be connected to existing roads or start from a white box!");
                    return;
                }
            }

            if (!isDirt) {
                refundPurchase();
                showPlacementError("Roads can only be placed on dirt terrain!");
                return;
            }

            if (isOccupiedByAnimal || hasVegetation) {
                String errorMessage = "Cannot place road here!\n";
                if (isOccupiedByAnimal) {
                    errorMessage += "Position is occupied by an animal.";
                } else if (hasVegetation) {
                    errorMessage += "Position has vegetation (tree/bush).";
                }
                refundPurchase();
                showPlacementError(errorMessage);
                return;
            }

            // Place the road
            String imageKey;
            switch (selectedItem) {
                case "Straight Road": imageKey = "road1"; break;
                case "Corner Road": imageKey = "road2"; break;
                case "T-Junction Road": imageKey = "road3"; break;
                case "Crossroad": imageKey = "road4"; break;
                case "End Road": imageKey = "road5"; break;
                case "Side Road": imageKey = "road6"; break;
                default: imageKey = "road1";
            }

            // Check if this is an entrance or exit based on the valid start points
            boolean isEntrance = gameMap.isValidRoadStartPoint(x, y);
            boolean isExit = gameMap.isValidRoadStartPoint(x, y);
            // Note: A point can be both an entrance and an exit if it's the same point.
            // The connectivity check handles finding *a* path, so this is okay.

            Road road = new Road(x, y, isEntrance, isExit);
            road.setImageKey(imageKey);
            safari.setLandscape(x, y, road);
            System.out.println("Road placed successfully at (" + x + ", " + y + ") Entrance: " + isEntrance + ", Exit: " + isExit);

            // Check if this specific road network is complete
            boolean wasComplete = safari.isRoadNetworkComplete();
            if (wasComplete && !safari.wasLastRoadNetworkComplete()) {
                JOptionPane.showMessageDialog(this, "Road network complete! You can now purchase jeeps.", "Road Complete", JOptionPane.INFORMATION_MESSAGE);
                safari.setLastRoadNetworkComplete(true);
            } else if (!wasComplete) {
                safari.setLastRoadNetworkComplete(false);
            }

            selectedItem = null;
            selectedItemType = null;
            setCursor(Cursor.getDefaultCursor());
            gameMap.repaint();
            miniMap.repaint();
            return;
        }

        // Handle ranger placement
        if (selectedItemType.equals("ranger")) {
            if (isOccupiedByAnimal || hasVegetation || isWater) {
                String errorMessage = "Cannot place ranger here!\n";
                if (isOccupiedByAnimal) {
                    errorMessage += "Position is occupied by an animal.";
                } else if (hasVegetation) {
                    errorMessage += "Position has vegetation (tree/bush).";
                } else if (isWater) {
                    errorMessage += "Cannot place ranger on water.";
                }

                refundPurchase();
                showPlacementError(errorMessage);
                return;
            }

            Ranger ranger = new Ranger(50); // Daily salary of 50
            ranger.setCurrentX(x);
            ranger.setCurrentY(y);
            safari.addRanger(ranger);
            System.out.println("Ranger placed successfully");

            // Reset selection and cursor immediately after placing ranger
            selectedItem = null;
            selectedItemType = null;
            setCursor(Cursor.getDefaultCursor());
            gameMap.repaint();
            miniMap.repaint();
            return;
        }

        // Handle location chip placement
        if (selectedItemType.equals("chip")) {
            // Find the animal at this position
            Animal targetAnimal = safari.getAnimalList().stream()
                .filter(animal -> animal.getCurrentX() == x && animal.getCurrentY() == y)
                .findFirst()
                .orElse(null);

            if (targetAnimal == null) {
                showPlacementError("No animal found at this location!");
                return;
            }

            // Mark the animal as having a location chip
            targetAnimal.setHasLocationChip(true);
            System.out.println("Location chip placed on animal at (" + x + "," + y + ")");

            // Reset selection and cursor
            selectedItem = null;
            selectedItemType = null;
            setCursor(Cursor.getDefaultCursor());
            gameMap.repaint();
            miniMap.repaint();
            return;
        }

        // Existing placement logic for other items
        try {
            switch (selectedItemType) {
                case "animal":
                    switch (selectedItem) {
                        case "Cheetah":
                            safari.addAnimal(new Cheetah(safari.getNextAnimalId(), "Cheetah", false, x, y));
                            break;
                        case "Lion":
                            safari.addAnimal(new Lion(safari.getNextAnimalId(), "Lion", false, x, y));
                            break;
                        case "Elephant":
                            safari.addAnimal(new Elephant(safari.getNextAnimalId(), "Elephant", false, x, y));
                            break;
                        case "Sheep":
                            safari.addAnimal(new Sheep(safari.getNextAnimalId(), "Sheep", false, x, y));
                            break;
                    }
                    System.out.println("Animal placed successfully");
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
                            safari.setLandscape(x, y, new Water());
                            break;
                        case "Dirt":
                            safari.setLandscape(x, y, new Dirt());
                            break;
                    }
                    System.out.println("Plant/landscape placed successfully");
                    break;
            }

            // Reset selection and update display
            selectedItem = null;
            selectedItemType = null;
            setCursor(Cursor.getDefaultCursor());
            gameMap.repaint();
            miniMap.repaint();

        } catch (Exception e) {
            System.out.println("Error placing item: " + e.getMessage());
            e.printStackTrace();
            refundPurchase();
            showPlacementError("Error placing item: " + e.getMessage());
        }
    }

    private void refundPurchase() {
        int refundAmount = 0;
        switch (selectedItem) {
            case "Cheetah": refundAmount = 500; break;
            case "Lion": refundAmount = 600; break;
            case "Elephant": refundAmount = 800; break;
            case "Sheep": refundAmount = 200; break;
            case "Tree": refundAmount = 100; break;
            case "Bush": refundAmount = 75; break;
            case "Grass": refundAmount = 50; break;
            case "Water": refundAmount = 200; break;
            case "Dirt": refundAmount = 20; break;
            case "Road": refundAmount = 250; break;
            case "Location Chip": refundAmount = 300; break;
        }
        balance += refundAmount;
        balanceLabel.setText("Balance: $" + balance);
    }

    private void showPlacementError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Invalid Placement",
                JOptionPane.ERROR_MESSAGE);

        // Reset selection
        selectedItem = null;
        selectedItemType = null;
        setCursor(Cursor.getDefaultCursor());
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
        addShopItem(panel, "resources/ranger.png", "Ranger", 500, "ranger");
        addShopItem(panel, "resources/location_chip.png", "Location Chip", 300, "chip");

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

        // Add different types of roads with their prices
        addShopItem(panel, "resources/road1.jpg", "Straight Road", 250, "road");
        addShopItem(panel, "resources/road2withoutback.png", "Corner Road", 250, "road");
        addShopItem(panel, "resources/road3withoutback.png", "T-Junction Road", 250, "road");
        addShopItem(panel, "resources/road4.jpg", "Crossroad", 250, "road");
        addShopItem(panel, "resources/road5withoutback.png", "End Road", 250, "road");
        addShopItem(panel, "resources/road6withoutback.png", "Side Road", 250, "road");
        
        // Add jeep to the roads tab
        addShopItem(panel, "resources/jeepstraight.png", "Jeep", 2000, "jeep");

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
                // Check for roads if buying a jeep
                if (type.equals("jeep")) {
                    if (!safari.isRoadNetworkComplete()) {
                        JOptionPane.showMessageDialog(shopDialog,
                                "Cannot buy jeep! The road network must be complete (connected entrance to exit) before purchasing a jeep.",
                                "Incomplete Road Network",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

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
            System.out.println("Error loading image: " + e.getMessage());
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
        timer1 = new Timer(1000, e -> {
            // put game updates here
            safari.Update();
            gameMap.update(safari.getLandscapes(), safari.getEntities());
            miniMap.update(safari.getLandscapes(), safari.getAnimalList());
            updateTime();
            updateGameState();
        });
        timer2 = new Timer(1000/60, e -> {
            // put render updates here
            gameMap.repaint();
            repaint();
            miniMap.repaint();
        });
        timer1.start();
        timer2.start();
    }

    private void updateTime() {
        String currentTime = timeLabel.getText();
        String[] parts = currentTime.split(", ");
        String[] dayPart = parts[0].split(" ");
        String[] timePart = parts[1].split(":");

        int day = Integer.parseInt(dayPart[1]);
        int hour = Integer.parseInt(timePart[0]);

//        hour++;
//        if (hour >= 24) {
//            hour = 0;
//            day++;
//        }

        timeLabel.setText(String.format("Day %d, %02d:00", day, hour));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        drawAnimals(g);
    }

    private void drawAnimals(Graphics g) {
        for (Animal animal : safari.getAnimalList()) {
            int x = animal.getCurrentX() * 32;
            int y = animal.getCurrentY() * 32;

        }
    }

    private void updateGameState() {
        // Increment time
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
            // Add daily capital at the start of each new day
            balance += 50000;
            balanceLabel.setText("Balance: $" + balance);

            // Pay rangers their daily salary
            for (Ranger ranger : safari.getRangers()) {
                balance -= ranger.getSalary();
                balanceLabel.setText("Balance: $" + balance);
            }
        }

        // Update tourist count based on number of animals (1 tourist per 15 animals)
        int touristCount = safari.getAnimalList().size() / 15;
        touristLabel.setText("Tourists: " + touristCount);

        // Update the game map with current hour for day/night cycle
        gameMap.setCurrentHour(hour);

        // Update rangers and handle poacher interactions
        for (Ranger ranger : safari.getRangers()) {
            ranger.update(safari);
            if (ranger.hasRepelledPoacher()) {
                balance += 50; // Bonus for repelling poacher
                balanceLabel.setText("Balance: $" + balance);
            }
        }

        // Spawn one poacher every 8 hours
        if (hour % 8 == 0) {
            spawnPoacher();
        }

        // Update poachers and remove those that have been caught or have been present for too long
        Iterator<Poacher> iterator = safari.getPoachers().iterator();
        while (iterator.hasNext()) {
            Poacher poacher = iterator.next();
            poacher.update(safari);
            // Only remove poachers if they're escaping or have been present for exactly 18 hours
            if (poacher.isEscaping() || poacher.getTimePresent() == 18) {
                iterator.remove();
                gameMap.repaint();
                miniMap.repaint();
            }
        }
        
        // Update jeeps
        for (Jeep jeep : safari.getJeeps()) {
            jeep.update(safari);
        }

        timeLabel.setText(String.format("Day %d, %02d:00", day, hour));
    }


    private void spawnPoacher() {
        Random random = new Random();
        boolean validPosition = false;
        int attempts = 0;

        // Try to find a valid position (max 10 attempts)
        while (!validPosition && attempts < 10) {
            final int currentX = random.nextInt(safari.getLandscapes().size());
            final int currentY = random.nextInt(safari.getLandscapes().get(0).size());

            // Check if position is valid (not water, not occupied by animals, not occupied by vegetation)
            if (!(safari.getLandscapes().get(currentX).get(currentY) instanceof Water) &&
                !safari.getAnimalList().stream().anyMatch(a -> a.getCurrentX() == currentX && a.getCurrentY() == currentY) &&
                !safari.getVegetationList().stream().anyMatch(v -> v.getCurrentX() == currentX && v.getCurrentY() == currentY)) {

                // Check if there are any rangers nearby (within 5 tiles)
                boolean rangerNearby = safari.getRangers().stream()
                    .anyMatch(r -> Math.abs(r.getCurrentX() - currentX) <= 5 && Math.abs(r.getCurrentY() - currentY) <= 5);

                if (!rangerNearby) {
                    Poacher poacher = new Poacher(currentX, currentY);
                    safari.addPoacher(poacher);
                    validPosition = true;
                }
            }
            attempts++;
        }
    }

    public Safari getSafari() {
        return safari;
    }
}
