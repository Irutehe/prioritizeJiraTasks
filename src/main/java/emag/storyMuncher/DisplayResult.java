package emag.storyMuncher;

import javax.swing.*;
import java.awt.*;

public class DisplayResult {

    private JPanel panel = new JPanel(new BorderLayout());

    DisplayResult setTable(JTable table) {
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return this;
    }

    DisplayResult setSkippedText(JTextArea skippedTextArea) {
        panel.add(new JScrollPane(skippedTextArea), BorderLayout.SOUTH);
        return this;
    }

    void show() {
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
