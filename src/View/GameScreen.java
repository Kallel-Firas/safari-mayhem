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
    private Timer gameLogicTimer;
    private Timer renderTimer;
    private Map<Class<? extends Animal>, BufferedImage> animalImages = new HashMap<>();

    public GameScreen() {
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
                if (selectedItem != null) {
                    // Calculate tile coordinates considering viewport offset
                    int tileX = (e.getX() / 32) + gameMap.getViewportX();
                    int tileY = (e.getY() / 32) + gameMap.getViewportY();
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

        // Initialize game logic timer for normal time progression
        gameLogicTimer = new Timer(1000, e -> updateGameState());
        gameLogicTimer.start();

        // Initialize render timer for smoother animations
        renderTimer = new Timer(1000 / 60, e -> repaint());
        renderTimer.start();

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

        // Handle road placement
        if (selectedItemType.equals("road")) {
            // Check if this is the first road placement
            boolean isFirstRoad = safari.getLandscapes().stream()
                .flatMap(row -> row.stream())
                .noneMatch(tile -> tile instanceof Road);

            if (isFirstRoad) {
                // For first road, must start from a white box
                if (!gameMap.isValidRoadStartPoint(x, y)) {
                    refundPurchase();
                    showPlacementError("Roads must start from one of the white boxes on the edges!");
                    return;
                }
            } else {
                // For subsequent roads, must be adjacent to an existing road
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
                    showPlacementError("Roads must be connected to existing roads!");
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

            // Check if this is an entrance or exit based on position
            boolean isEntrance = x == 0 || y == 0; // Left or top edge
            boolean isExit = x == safari.getLandscapes().size() - 1 || y == safari.getLandscapes().get(0).size() - 1; // Right or bottom edge

            Road road = new Road(x, y, isEntrance, isExit);
            road.setImageKey(imageKey);
            safari.setLandscape(x, y, road);
            System.out.println("Road placed successfully");

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
        Timer timer1 = new Timer(1000, e -> {
            // put game updates here
            safari.Update();
            gameMap.update(safari.getLandscapes(), safari.getEntities());
            miniMap.update(safari.getLandscapes(), safari.getAnimalList());
            updateTime();
        });
        Timer timer2 = new Timer(1000/60, e -> {
            // put render updates here
            gameMap.repaint();
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

        hour++;
        if (hour >= 24) {
            hour = 0;
            day++;
        }

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

//        hour++;
        if (hour >= 24) {
            hour = 0;
            day++;
            // Add daily capital at the start of each new day
            balance += 500;
            balanceLabel.setText("Balance: $" + balance);

            // Pay rangers their daily salary
            for (Ranger ranger : safari.getRangers()) {
                balance -= ranger.getSalary();
                balanceLabel.setText("Balance: $" + balance);
            }
        }

        // Update rangers and handle poacher interactions
        for (Ranger ranger : safari.getRangers()) {
            ranger.update(safari);
            if (ranger.hasRepelledPoacher()) {
                balance += 50; // Bonus for repelling poacher
                balanceLabel.setText("Balance: $" + balance);
            }
        }

        // Spawn poacher every 8 hours
        if (hour % 8 == 0) {
            spawnPoachers();
        }

        // Update poachers and remove those that have been caught or have been present for too long
        Iterator<Poacher> iterator = safari.getPoachers().iterator();
        while (iterator.hasNext()) {
            Poacher poacher = iterator.next();
            poacher.update(safari);
            if (poacher.isEscaping() || poacher.getTimePresent() >= 6) {
                iterator.remove();
                gameMap.repaint();
                miniMap.repaint();
            }
        }

        timeLabel.setText(String.format("Day %d, %02d:00", day, hour));
    }

    private void spawnPoachers() {
        Random random = new Random();
        int numPoachers = random.nextInt(3) + 1; // 1-3 poachers

        for (int i = 0; i < numPoachers; i++) {
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
    }

    public Safari getSafari() {
        return safari;
    }
}
