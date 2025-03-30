import java.awt.*;
import java.awt.image.BufferedImage;
public class ImageZoomer {
    private BufferedImage image;
    private double zoomFactor;
    public ImageZoomer(BufferedImage image) {
        this.image = image;
        this.zoomFactor = 1.0; 
    }
    public Image zoomIn(int width, int height) {
        zoomFactor *= 1.2; 
        return scaleImage(image, width, height);
    }
    public Image zoomOut(int width, int height) {
        zoomFactor /= 1.2; 
        return scaleImage(image, width, height);
    }
    private Image scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth = (int) (targetWidth * zoomFactor);
        int newHeight = (int) (newWidth / aspectRatio);
        return originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}
