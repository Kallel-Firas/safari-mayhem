package View;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

        // Add a list of saved games (for demonstration purposes)
        String[] savedGames = getSavedGames().toArray(new String[0]);
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
            // Logic to go back to the main menu
            // For example, you can create a new instance of the WelcomeScreen class
            new WelcomeScreen().setVisible(true);
            dispose(); // Close the load game screen
        });
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(10));

        // Add an action listener to the load button
        loadButton.addActionListener(e -> {
            String selectedGame = gameList.getSelectedValue();
            if (selectedGame != null) {
                // Logic to load the selected game
                // For example, you can call a method to load the game state
                JOptionPane.showMessageDialog(this, "Loading game: " + selectedGame);
                // Load the game logic here
            } else {
                JOptionPane.showMessageDialog(this, "Please select a game to load.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add the panel to the frame
        add(panel);
    }


    private ArrayList<String> getSavedGames() {
        ArrayList<String> savedGamesList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        Path filePath = Paths.get("saves.json");
        try {
            // Read the JSON file content as a String
            String content = Files.readString(filePath);

            // Parse the JSON content to a JSONObject
            JSONObject jsonObject = (JSONObject) parser.parse(content);

            // Iterate through the keys (game names) in the JSONObject
            for (Object key : jsonObject.keySet()) {
                savedGamesList.add((String) key);
            }
        } catch (IOException e) {
            // Handle file reading errors
//            JOptionPane.showMessageDialog(this, "Error reading saves.json file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        } catch (ParseException e) {
            // Handle JSON parsing errors
            JOptionPane.showMessageDialog(this, "Error parsing JSON file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Convert the list of game names to an array and return it
        return savedGamesList;
    }
}
