import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class ProfilePictureViewer extends JFrame {

    public ProfilePictureViewer(Image image, int x, int y, int size, String text) {
        // Scale the image to fit the window
        Image scaledImage = scaleImage(image, size, size);

        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setHorizontalAlignment(JLabel.CENTER);

        JLabel textLabel = new JLabel(text);
        textLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textLabel, BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);

        add(panel);
        setTitle("Profile Picture Viewer");
        setSize(size, size + 20); // Increased height to accommodate text
        setLocation(x, y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Handle any cleanup or additional actions when the window is closed
                // For example, you might want to enable updates in the main TextingInterface
                // by setting flag to true.
            }
        });
    }

    private Image scaleImage(Image image, int width, int height) {
        // Create a buffered image with the desired size
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get the graphics context of the buffered image
        Graphics2D g2d = scaledImage.createGraphics();

        // Draw the original image to the buffered image with scaling
        g2d.drawImage(image, 0, 0, width, height, null);

        // Dispose of the graphics context
        g2d.dispose();

        return scaledImage;
    }
}
