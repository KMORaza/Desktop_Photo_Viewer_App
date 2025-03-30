import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
public class PhotoViewer extends JFrame {
    private JLabel imageLabel;
    private BufferedImage image;
    private BufferedImage originalImage;
    private ImageZoomer imageZoomer;
    private ImageRotator imageRotator;
    private ImageCropper imageCropper;
    private int rotationAngle = 0;
    private double currentZoomFactor = 1.0;
    public PhotoViewer() {
        setTitle("Photo Viewer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setOpaque(true);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        JPanel zoomPanel = new JPanel();
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        JButton rotateButton = new JButton("Rotate");
        JButton cropButton = new JButton("Crop");
        JButton saveButton = new JButton("Save");
        JButton printToPDFButton = new JButton("Print to PDF");
        zoomInButton.addActionListener(e -> zoomIn());
        zoomOutButton.addActionListener(e -> zoomOut());
        rotateButton.addActionListener(e -> rotateImage());
        cropButton.addActionListener(e -> cropImage());
        saveButton.addActionListener(e -> saveImage());
        printToPDFButton.addActionListener(e -> printToPDF());
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(rotateButton);
        zoomPanel.add(cropButton);
        zoomPanel.add(saveButton);
        zoomPanel.add(printToPDFButton);
        add(zoomPanel, BorderLayout.SOUTH);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openImage());
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                if (selectedFile != null && selectedFile.exists() && selectedFile.isFile()) {
                    originalImage = ImageIO.read(selectedFile);
                    image = originalImage; // Initialize displayed image
                    imageZoomer = new ImageZoomer(originalImage);
                    imageRotator = new ImageRotator(image);
                    imageCropper = new ImageCropper(image);
                    imageLabel.setIcon(new ImageIcon(image));
                    setTitle("Photo Viewer - " + selectedFile.getName());
                    rotationAngle = 0;
                    currentZoomFactor = 1.0; 
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file path or file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load the image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void zoomIn() {
        if (image != null) {
            currentZoomFactor *= 1.2; // Increase zoom factor
            imageZoomer = new ImageZoomer(originalImage); // Always start from original
            Image zoomedImage = applyZoom(currentZoomFactor);
            image = toBufferedImage(zoomedImage);
            if (rotationAngle != 0) {
                imageRotator = new ImageRotator(image);
                image = imageRotator.rotate(rotationAngle);
            }
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void zoomOut() {
        if (image != null) {
            currentZoomFactor /= 1.2; // Decrease zoom factor
            if (currentZoomFactor < 0.1) currentZoomFactor = 0.1; // Prevent excessive zoom out
            imageZoomer = new ImageZoomer(originalImage); // Always start from original
            Image zoomedImage = applyZoom(currentZoomFactor);
            image = toBufferedImage(zoomedImage);
            if (rotationAngle != 0) {
                imageRotator = new ImageRotator(image);
                image = imageRotator.rotate(rotationAngle);
            }
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private Image applyZoom(double zoomFactor) {
        int newWidth = (int) (originalImage.getWidth() * zoomFactor);
        int newHeight = (int) (originalImage.getHeight() * zoomFactor);
        return originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
    private void rotateImage() {
        if (image != null) {
            rotationAngle = (rotationAngle + 90) % 360;
            imageRotator = new ImageRotator(originalImage);
            BufferedImage rotatedImage = imageRotator.rotate(rotationAngle);
            originalImage = rotatedImage; // Update originalImage with rotation
            imageZoomer = new ImageZoomer(originalImage); // Update zoomer with rotated image
            Image zoomedImage = applyZoom(currentZoomFactor);
            image = toBufferedImage(zoomedImage);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cropImage() {
        if (image != null) {
            String input = JOptionPane.showInputDialog(this, "Enter crop dimensions (x, y, width, height):", "100, 100, 200, 200");
            if (input != null && !input.isEmpty()) {
                String[] dimensions = input.split(",");
                if (dimensions.length == 4) {
                    try {
                        int x = Integer.parseInt(dimensions[0].trim());
                        int y = Integer.parseInt(dimensions[1].trim());
                        int width = Integer.parseInt(dimensions[2].trim());
                        int height = Integer.parseInt(dimensions[3].trim());
                        imageCropper = new ImageCropper(originalImage);
                        BufferedImage croppedImage = imageCropper.crop(x, y, width, height);
                        originalImage = croppedImage; // Update originalImage with crop
                        imageZoomer = new ImageZoomer(originalImage);
                        Image zoomedImage = applyZoom(currentZoomFactor);
                        image = toBufferedImage(zoomedImage);
                        if (rotationAngle != 0) {
                            imageRotator = new ImageRotator(image);
                            image = imageRotator.rotate(rotationAngle);
                        }
                        imageLabel.setIcon(new ImageIcon(image));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid dimensions entered. Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter four values separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
    private void saveImage() {
        if (image != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String fileName = fileToSave.getAbsolutePath();
                if (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")) {
                    fileToSave = new File(fileName + ".png");
                }
                try {
                    ImageIO.write(image, "PNG", fileToSave);
                    JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Failed to save the image", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void printToPDF() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Open an image first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Destination(new File("output.pdf").toURI()));
        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = pageFormat.getPaper();
        double widthInPoints = image.getWidth() * 0.75;
        double heightInPoints = image.getHeight() * 0.75;
        paper.setSize(widthInPoints, heightInPoints);
        paper.setImageableArea(0, 0, widthInPoints, heightInPoints);
        pageFormat.setPaper(paper);
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                
                if (rotationAngle != 0) {
                    g2d.rotate(Math.toRadians(rotationAngle), 
                             pageFormat.getImageableWidth() / 2, 
                             pageFormat.getImageableHeight() / 2);
                }
                g2d.drawImage(image, 0, 0, 
                            (int)pageFormat.getImageableWidth(), 
                            (int)pageFormat.getImageableHeight(), 
                            null);
                return PAGE_EXISTS;
            }
        }, pageFormat);
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save PDF As");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    fileToSave = new File(filePath + ".pdf");
                }
                attributes.add(new Destination(fileToSave.toURI()));
                printerJob.print(attributes);
                JOptionPane.showMessageDialog(this, 
                    "Image successfully saved as PDF!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to create PDF: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new PhotoViewer().setVisible(true));
    }
}