import javax.swing.*;
import java.awt.*;

/**
 * User: vasiliy
 */
public class ProgressBar {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setBounds(0, 0, 300, 300);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBounds(0, 0, 300, 300);
        panel.setMinimumSize(new Dimension(300, 300));
        panel.setSize(300, 300);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(0, 0, 300, 300);
        progressBar.setMinimumSize(new Dimension(300, 300));
        progressBar.setPreferredSize(new Dimension(300, 300));
        progressBar.setMaximum(10);
        progressBar.setValue(5);
        progressBar.setBorderPainted(true);
        progressBar.setSize(300, 300);
        panel.add(progressBar);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
