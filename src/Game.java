import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man");
            GamePanel panel = new GamePanel();

            try {
                BufferedImage icon = ImageIO.read(
                        Game.class.getResourceAsStream("/pacman-art/pacman-right/1.png")
                );
                frame.setIconImage(icon);
            } catch(Exception e) {
                System.out.println("İkon yüklenemedi!");
            }

            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}