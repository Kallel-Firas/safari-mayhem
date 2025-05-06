package View;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class NewGameScreen extends JFrame {
    private Image backgroundImage;

    public NewGameScreen() {
        setTitle("New Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        try {
            backgroundImage = ImageIO.read(new File("resources/safariback.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Game Name", SwingConstants.CENTER);
        styleLabel(nameLabel);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField gameNameField = createStyledTextField();
        gameNameField.setMaximumSize(new Dimension(200, 30));
        gameNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel difficultyLabel = new JLabel("Difficulty", SwingConstants.CENTER);
        styleLabel(difficultyLabel);
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> difficultyComboBox = createStyledComboBox();
        difficultyComboBox.setMaximumSize(new Dimension(200, 30));
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startGameButton = createStyledButton("Start Game");
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setMaximumSize(new Dimension(200, 35));

        JButton backButton = createStyledButton("Main Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 35));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(nameLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(gameNameField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(difficultyLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(difficultyComboBox);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(startGameButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(backButton);
        mainPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(mainPanel);

        add(backgroundPanel, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setLocationRelativeTo(null);
            welcomeScreen.setVisible(true);
            this.dispose();
        });

        startGameButton.addActionListener(e -> {
            String gameName = gameNameField.getText();
            if (gameName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a game name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // if the game name exists (chech if there is a file in saves with the same name) then show a message
            File file = new File("./saves/" + gameName + ".save");
            if (file.exists()) {
                JOptionPane.showMessageDialog(this, "Game name already exists. Please choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            GameScreen gameScreen = new GameScreen(gameNameField.getText(), null, false);
            gameScreen.setLocationRelativeTo(null);
            gameScreen.setVisible(true);
            this.dispose();
            gameScreen.run();
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBackground(new Color(51, 51, 51));
        textField.setForeground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 76, 76), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(new String[] {"Easy", "Medium", "Hard"});
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(new Color(51, 51, 51));
        comboBox.setForeground(Color.WHITE);
        ((JComponent) comboBox.getRenderer()).setOpaque(true);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(76, 76, 76), 2));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 76, 76), 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(76, 76, 76));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        return button;
    }
}

