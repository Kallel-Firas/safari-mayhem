import View.View;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        // Set up the JFrame to hold the GameView panel
        JFrame frame = new JFrame("2D Top-Down Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        View view = new View();
        frame.add(view); // Add the GameView panel to the frame
        //adjust the frame size to fit the panel
        frame.pack();
        //center the frame on the screen
        frame.setLocationRelativeTo(null);
        //set
        frame.setVisible(true);
    }
}
