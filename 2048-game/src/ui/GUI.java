package ui;

import javax.swing.*;
import Constants.Constants;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private int HEIGHT = Constants.HEIGHT;
    private int WIDTH = Constants.WIDTH;

    public GUI() {
        frame = new JFrame("2048 Game");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        frame.setSize(HEIGHT, WIDTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void addComponent(JComponent component) {
        panel.add(component);
        panel.revalidate();
        panel.repaint();
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        JButton button = new JButton("Click Me!");
        gui.addComponent(button);
    }
}