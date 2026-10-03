import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

/**
 * Main.java
 * Entry point of the program. Launches the Student Grade Tracker GUI
 * using FlatLaf for a modern, flat look and feel.
 */
public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new GradeTrackerGUI().setVisible(true));
    }
}
