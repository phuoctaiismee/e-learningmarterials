package com.manage.library.ui.viewer.image;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.services.ImageCache;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.swing.*;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.painter.MattePainter;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageView extends JFrame {

    private JXImageView imageView;
    private JPanel controlPanel;
    private JButton btnPlus;
    private JButton btnMinus;
    private JButton btnRoot;
    private JButton btnFull;
    private JLabel lblStatus;
    private double scaleRoot;
    private final BufferedImage image;
    private static final Dimension dimension = new Dimension(1248, 800);
//    private static final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    public ImageView(String path) throws IOException {
        initComponents();
        setTitle(path.substring(path.lastIndexOf(File.separator) + 1));
        image = ImageCache.getImage(path);
//        image = loadImage(path);
        imageView.setImage(image);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        if (image.getHeight() > getHeight()) {
            double scaleFactor = (double) (getHeight() - 100) / image.getHeight();
            scaleRoot = scaleFactor;
            imageView.setScale(scaleRoot);
        } else {
            scaleRoot = imageView.getScale();
        }
        statusChange(imageView.getScale());

    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(dimension);
        setLocationRelativeTo(null);

        imageView = new JXImageView();
        imageView.setBackgroundPainter(new MattePainter(new Color(26, 33, 43)));

        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(26, 33, 43));
        btnPlus = new JButton(new FlatSVGIcon("logos/icons/icon/image/zoom_in.svg"));
        btnPlus.setBackground(new Color(26, 33, 43));
        btnPlus.setBorder(null);
        btnPlus.setToolTipText("Phóng to");

        lblStatus = new JLabel();
        lblStatus.setForeground(Color.white);

        btnMinus = new JButton(new FlatSVGIcon("logos/icons/icon/image/zoom_out.svg"));
        btnMinus.setBackground(new Color(26, 33, 43));
        btnMinus.setBorder(null);
        btnMinus.setToolTipText("Thu nhỏ");

        btnRoot = new JButton(new FlatSVGIcon("logos/icons/icon/image/origin_page.svg"));
        btnRoot.setBackground(new Color(26, 33, 43));
        btnRoot.setBorder(null);
        btnRoot.setToolTipText("Kích thước gốc");

        btnFull = new JButton(new FlatSVGIcon("logos/icons/icon/image/full_screen.svg"));
        btnFull.setBackground(new Color(26, 33, 43));
        btnFull.setBorder(null);
        btnFull.setToolTipText("Toàn màn hình");

        controlPanel.add(btnMinus);
        controlPanel.add(lblStatus);
        controlPanel.add(btnPlus);
        controlPanel.add(btnRoot);
        controlPanel.add(btnFull);

        add(controlPanel, BorderLayout.SOUTH);
        getContentPane().add(imageView);

        imageView.addMouseWheelListener((MouseWheelEvent e) -> {
            // Lấy giá trị của sự kiện cuộn bánh xe chuột
            int notches = e.getWheelRotation();

            // Kiểm tra xem giá trị cuộn lên hoặc xuống
            if (notches < 0) {
                // Nếu cuộn lên, tăng tỷ lệ thu phóng
                imageView.setScale(imageView.getScale() + 0.01);
            } else {
                // Nếu cuộn xuống, giảm tỷ lệ thu phóng
                imageView.setScale(imageView.getScale() - 0.01);
            }

            // Giới hạn tỷ lệ thu phóng trong khoảng từ scaleRoot đến 500
            double scale = imageView.getScale();
            if (scale <= scaleRoot) {
                scale = scaleRoot;
            } else {
                double maxPercentScale = 5000; // Giới hạn tối đa là 5000%
                double maxScale = maxPercentScale / 100 * scaleRoot; // Tính tỷ lệ tương ứng với 5000%
                if (scale > maxScale) {
                    scale = maxScale;
                }
            }

            statusChange(scale);
            imageView.setScale(scale);
        });

        btnPlus.addMouseListener(new MouseAdapter() {

            private boolean holding = false;

            @Override
            public void mousePressed(MouseEvent e) {
                holding = true;

                new Thread(() -> {
                    while (holding) {
                        double scale = imageView.getScale();
                        if (scale < 500) { // Kiểm tra nếu tỷ lệ thu phóng chưa đạt giới hạn tối đa
                            scale += 0.1; // Tăng tỷ lệ thu phóng lên 0.1
                            imageView.setScale(scale); // Thiết lập tỷ lệ thu phóng mới
                            statusChange(scale); // Cập nhật trạng thái
                        }
                        try {
                            Thread.sleep(100); // Chờ một khoảng thời gian
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                holding = false;
            }

        });

        btnMinus.addMouseListener(new MouseAdapter() {

            private boolean holding = false;

            @Override
            public void mousePressed(MouseEvent e) {
                holding = true;
                new Thread(() -> {
                    while (holding) {
                        double scale = imageView.getScale();
                        if (scale - 0.01 > scaleRoot) { // Kiểm tra nếu tỷ lệ thu phóng chưa đạt giới hạn tối đa
                            scale -= 0.1; // Giảm tỷ lệ thu phóng đi 0.1
                            imageView.setScale(scale); // Thiết lập tỷ lệ thu phóng mới
                            statusChange(scale); // Cập nhật trạng thái
                        }
                        try {
                            Thread.sleep(100); // Chờ một khoảng thời gian
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                holding = false;
            }

        });

        btnRoot.addActionListener((e) -> {
            imageView.setScale(scaleRoot);
            statusChange(scaleRoot);
        });

        btnFull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    // Nếu cửa sổ đang ở chế độ toàn màn hình, chuyển về chế độ bình thường
                    imageView.setImage(image);
                    imageView.setScale(scaleRoot);
                    setExtendedState(JFrame.NORMAL);
                    setSize(dimension);
                    dispose();
                    setUndecorated(false);
                    setLocationRelativeTo(null);
                    setVisible(true);

                } else {
                    // Nếu cửa sổ đang ở chế độ bình thường, chuyển sang chế độ toàn màn hình
                    Double old = imageView.getScale();
                    imageView.setImage(image);
                    imageView.setScale(old);
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    dispose();
                    setUndecorated(true);
                    setVisible(true);
                }
                changeScale();
            }

        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Kiểm tra nếu cửa sổ đang ở chế độ toàn màn hình
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    btnFull.setIcon(new FlatSVGIcon("logos/icons/icon/image/normal_screen.svg"));
                    btnFull.setToolTipText("Cửa sổ mặc định");
                } else {
                    btnFull.setIcon(new FlatSVGIcon("logos/icons/icon/image/full_screen.svg"));
                    btnFull.setToolTipText("Toàn màn hình");
                }
                changeScale();

            }
        });
    }

    private void changeScale() {
        if (image.getHeight() > getHeight()) {
            double scaleFactor = (double) (getHeight() - 100) / image.getHeight();
            scaleRoot = scaleFactor;
            imageView.setScale(scaleRoot);
        } else {
            scaleRoot = imageView.getScale();
        }
        // Tính toán vị trí mới cho imageView
        int x = (this.getWidth() / 2);
        int y = ((this.getHeight() - 100) / 2);
        // Tạo đối tượng Point2D mới với các giá trị x và y
        Point2D location = new Point2D.Double(x, y);

        // Đặt vị trí mới cho imageView
        imageView.setImageLocation(location);
        statusChange(imageView.getScale());
    }

    private void statusChange(Double scale) {
        double percentScale;
        if (scale <= scaleRoot) {
            percentScale = 100; // Nếu tỷ lệ thu phóng nhỏ hơn hoặc bằng scaleRoot, đặt phần trăm là 100%
        } else {
            percentScale = (scale / scaleRoot) * 100; // Tính phần trăm dựa trên scaleRoot
        }
        lblStatus.setText(String.format("%.0f%%", percentScale)); // Hiển thị tỷ lệ thu phóng là phần trăm
        // Kiểm tra xem nút Minus có bị vô hiệu hóa không
        btnMinus.setEnabled(scale > scaleRoot);
        btnPlus.setEnabled(scale < 500);
    }

    private BufferedImage loadImage(String path) throws IOException {
        File file = new File(path);
        return ImageIO.read(file);
    }

    public static BufferedImage readFragment(InputStream stream, Rectangle rect)
            throws IOException {
        BufferedImage image;
        try (ImageInputStream imageStream = ImageIO.createImageInputStream(stream)) {
            ImageReader reader = ImageIO.getImageReaders(imageStream).next();
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(rect);
            reader.setInput(imageStream, true, true);
            image = reader.read(0, param);
            reader.dispose();
        }

        return image;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ImageView viewer = new ImageView("C:/Users/rifud/Downloads/gop.jpg");
                viewer.setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

}
