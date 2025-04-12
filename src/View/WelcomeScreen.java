package View;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        // set the preferred size of the welcome screen
        setPreferredSize(new Dimension(50 * 16, 50 * 16));
        // set the layout of the welcome screen
        setLayout(new GridLayout(3, 1));
        // create a new game button
        JButton newGameButton = new JButton("New Game");
        // create a load game button
        JButton loadGameButton = new JButton("Load Game");
        // create an exit button
        JButton exitButton = new JButton("Exit");
        // add the buttons to the welcome screen
        add(newGameButton);
        add(loadGameButton);
        add(exitButton);
        // set the title of the welcome screen
        setTitle("Model.Safari Mayhem");
        // set the default close operation of the welcome screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // pack the welcome screen
        pack();
        // set the location of the welcome screen to the center of the screen
        setLocationRelativeTo(null);
        // make the welcome screen visible
        setVisible(true);
        // add an action listener to the exit button
        exitButton.addActionListener(e -> System.exit(0));
        // add an action listener to the new game button
        newGameButton.addActionListener(e -> {
            // create a new game screen
            NewGameScreen newGameScreen = new NewGameScreen();
            // set the location of the new game screen to the center of the screen
            newGameScreen.setLocationRelativeTo(null);
            // make the new game screen visible
            newGameScreen.setVisible(true);
            // dispose the welcome screen
            this.dispose();
        });
    }
}
