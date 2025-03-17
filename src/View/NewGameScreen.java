package View;

import javax.swing.*;
import java.awt.*;

public class NewGameScreen extends JFrame {

    public NewGameScreen() {
        setTitle("New Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(50*16, 50*16));

        setLayout(new GridLayout(4, 1));

        JTextField gameNameField = new JTextField();
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[] {"Easy", "Medium", "Hard"});
        JButton startGameButton = new JButton("Start Game");
        JButton backButton = new JButton("Main Menu");

        add(gameNameField);
        add(difficultyComboBox);
        add(startGameButton);
        add(backButton);

        backButton.addActionListener(e -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setLocationRelativeTo(null);
            welcomeScreen.setVisible(true);
            this.dispose();
        });

        startGameButton.addActionListener(e -> {
            GameScreen gameScreen = new GameScreen();
            gameScreen.setLocationRelativeTo(null);
            gameScreen.setVisible(true);
            this.dispose();
        });

        pack();
    }
}
