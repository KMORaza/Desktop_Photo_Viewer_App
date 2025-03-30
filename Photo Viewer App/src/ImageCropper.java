import java.awt.*;
import java.awt.image.BufferedImage;
public class ImageCropper {
    private BufferedImage image;
    public ImageCropper(BufferedImage image) {
        this.image = image;
    }
    public BufferedImage crop(int x, int y, int width, int height) {
        if (x < 0 || y < 0 || x + width > image.getWidth() || y + height > image.getHeight()) {
            throw new IllegalArgumentException("Invalid cropping dimensions.");
        }
        return image.getSubimage(x, y, width, height);
    }
}
