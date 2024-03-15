import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageReader {

    public static void main(String[] args) {
        String imagePath = "C:\\Users\\rifud\\Downloads\\g.jpg"; // Đường dẫn đến tệp hình ảnh lớn

        try {
            // Tải hình ảnh từ tệp
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Giảm độ phân giải của hình ảnh nếu cần
            BufferedImage resizedImage = resizeImage(originalImage, 800, 1200);

            // Hiển thị hình ảnh (ví dụ: sử dụng Swing)
            displayImage(resizedImage);

        } catch (IOException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // Hàm giảm độ phân giải của hình ảnh
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.SCALE_SMOOTH);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    // Hàm hiển thị hình ảnh (ví dụ: sử dụng Swing)
    private static void displayImage(BufferedImage image) {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        javax.swing.JLabel label = new javax.swing.JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }
}
