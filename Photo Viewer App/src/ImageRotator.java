import java.awt.*;
import java.awt.image.BufferedImage;
public class ImageRotator {
    private BufferedImage image;
    public ImageRotator(BufferedImage image) {
        this.image = image;
    }
    public BufferedImage rotate(int angle) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), w / 2, h / 2);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotatedImage;
    }
}
