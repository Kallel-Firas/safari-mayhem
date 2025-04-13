package View;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WelcomeScreen extends JFrame {
    private Image backgroundImage;

    public WelcomeScreen() {
        try {
            backgroundImage = ImageIO.read(new File("resources/safariback.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        setTitle("Safari Mayhem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton newGameButton = createStyledButton("New Game");
        JButton loadGameButton = createStyledButton("Load Game");
        JButton exitButton = createStyledButton("Exit");

        Dimension buttonSize = new Dimension(200, 40);
        newGameButton.setPreferredSize(buttonSize);
        loadGameButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        newGameButton.setMaximumSize(buttonSize);
        loadGameButton.setMaximumSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);

        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(loadGameButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        exitButton.addActionListener(e -> System.exit(0));
        newGameButton.addActionListener(e -> {
            NewGameScreen newGameScreen = new NewGameScreen();
            newGameScreen.setLocationRelativeTo(null);
            newGameScreen.setVisible(true);
            this.dispose();
        });

        backgroundPanel.add(buttonPanel);

        add(backgroundPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
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
