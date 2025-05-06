package View;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Model.Safari;
import org.json.simple.*;
import org.json.simple.parser.*;

public class LoadGameScreen extends JFrame {
    public LoadGameScreen() {
        setTitle("Load Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create a panel for the load game screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add a label
        JLabel label = new JLabel("Select a saved game to load:");
        label.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(label);

        // Add a list of saved games
        String[] savedGames = getSavedGames();
        JList<String> gameList = new JList<>(savedGames);
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(gameList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        panel.add(scrollPane);

        // Add a button to load the selected game
        JButton loadButton = new JButton("Load Game");
        loadButton.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(loadButton);

        // Add a button to go back to the main menu
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(10));

        // Add an action listener to the load button
        loadButton.addActionListener(e -> {
            String selectedGame = gameList.getSelectedValue();
            if (selectedGame != null) {
                Safari s = loadGame(selectedGame);

                assert s != null;
                GameScreen gameScreen = new GameScreen("a", s, true);
                gameScreen.setLocationRelativeTo(null);
                gameScreen.setVisible(true);
                this.dispose();
                gameScreen.run();                // Load the game logic here
            } else {
                JOptionPane.showMessageDialog(this, "Please select a game to load.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add the panel to the frame
        add(panel);
    }


    public static Safari loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("./saves/" + filename))) {
            return (Safari) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }
    private String[] getSavedGames() {
        // look for all files in the current directory that end with .save
        ArrayList<String> savedGames = new ArrayList<>();
        try {
            Files.list(Paths.get("./saves"))
                    .filter(path -> path.toString().endsWith(".save"))
                    .forEach(path -> savedGames.add(path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedGames.toArray( new String[0]);
    }
}
