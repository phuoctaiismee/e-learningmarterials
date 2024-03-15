package com.manage.library.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.config.ApplicationConstant;
import com.manage.library.config.CryptionFileAndFolder;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.interfaces.CardListener;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.ui.viewer.doc.PDFViewer;
import com.manage.library.ui.viewer.image.ImageView;
import com.manage.library.ui.viewer.video.VideoPlayer;
import com.manage.library.utils.StringUtils;
import com.manage.library.utils.USBUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author PHUOCTAI
 */
public final class Card extends javax.swing.JPanel {

    Resource file;
    ResourceDAO resourceDAO = new ResourceDAO();
    TopicDAO topicDAO = new TopicDAO();
    SubjectDAO subjectDAO = new SubjectDAO();
    private CardListener cardListener;

    private ImageView imageView;
    private PDFViewer pdfViewer;
    private VideoPlayer videoPlayer;

    public Card() {
        initComponents();
        this.setBackground(Color.white);
        pictureBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlInfoCard.setBackground(this.getBackground());
        tplName.setBackground(this.getBackground());
        tplName.setEditable(false);
        tplName.setFocusable(false);
        this.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20;"
                + "border: 12,12,12,12");
        pnlOption.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20;");
        pnlOption.setBackground(this.getBackground());
        btnOption.setBackground(this.getBackground());
        btnOption.setSize(50, 50);
        btnOption.setBorder(null);
        btnOption.setText(null);
        btnOption.setIcon(new FlatSVGIcon("logos/icons/icon/dots.svg"));

    }

    @Override
    public void setForeground(Color fg) {
        if (tplName != null) {
            tplName.setForeground(fg);
        }
    }

    /*
    type == 1 -> Thư mục
    type == 2 -> Hình ảnh
    type == 3 -> Video
    type == 4 -> Tài liệu
     */
    public void fillData() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        CryptionFileAndFolder cf = new CryptionFileAndFolder(ApplicationConstant.PathConfig.CONFIG_PROPERTIES, "folder.serect.key");

        File decryptedFile = null;

        if (file.getTypeId() != 1) {
            decryptedFile = CryptionFileAndFolder.decryptToFile(StringUtils.addThumbnail(file.getUrl()), cf.secretkey, cf.FIXED_IV);
        }

//        Icon ico = FileSystemView.getFileSystemView().getSystemIcon(decryptedFile.getAbsoluteFile());
        switch (file.getTypeId()) {

            case 1 -> {
                pictureBox.setImage(new FlatSVGIcon("logos/icons/icon/thumnail/folder.svg"));
                btnOption.setVisible(false);

            }
            case 2 -> {
//                BufferedImage image = ThumbnailGenerate.createThumbnail(decryptedFile.getAbsoluteFile(), 128, 128);
//                Icon icon = new ImageIcon(image);
                pictureBox.setImage(new ImageIcon(decryptedFile.getAbsolutePath()));

            }
            case 3 -> {
//                pictureBox.setImage(new FlatSVGIcon("logos/icons/icon/thumnail/video.svg"));
                if (decryptedFile != null) {
//                    BufferedImage thumb = VideoThumbnail.getThumbnail(decryptedFile.getAbsolutePath(), 410);
//                    if (thumb != null) {
//                        pictureBox.setImage(new ImageIcon(thumb));
//                    }
                    pictureBox.setImage(new ImageIcon(decryptedFile.getAbsolutePath()));
                }
            }
            case 4 -> {

//                pictureBox.setImage(new FlatSVGIcon("logos/icons/icon/thumnail/book.svg"));
                if (decryptedFile != null) {
//                    BufferedImage thumbnail = generateThumbnail(decryptedFile.getAbsolutePath(), 500, 500);
//                    if (thumbnail != null) {
//                        pictureBox.setImage(new ImageIcon(thumbnail));
//                    } else {
//                        System.out.println("Failed to generate thumbnail.");
//                    }
                    pictureBox.setImage(new ImageIcon(decryptedFile.getAbsolutePath()));
                }
            }
            default ->
                pictureBox.setImage(new FlatSVGIcon("logos/icons/icon/thumnail/not.svg"));
        }

        String fileName = FilenameUtils.getBaseName(file.getName());
        tplName.setText(fileName);
        this.setToolTipText(fileName);
        if (decryptedFile != null) {
            decryptedFile.deleteOnExit();
        }

    }

    public void setFile(Resource file) {

        this.file = file;
    }

    public void openDesktopSupport(String path) throws IOException {
        File fileOpen = new File(path);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(fileOpen);
        } else {
            JOptionPane.showMessageDialog(this, "Máy tính của bạn không hỗ trợ loại file này!!");
        }
    }

    public void openImage(String path) {
        try {
            if (imageView != null && imageView.isVisible()) {
                imageView.toFront();
            } else {
                imageView = new ImageView(path);
                imageView.setVisible(true);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public void openVideo(String path) {
        try {
            if (videoPlayer != null && videoPlayer.isVisible()) {
                videoPlayer.toFront();
            } else {
                videoPlayer = new VideoPlayer(path);
                videoPlayer.playVideo();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void openPDF(String path) {
        try {
            if (pdfViewer != null && pdfViewer.isVisible()) {
                pdfViewer.toFront();
            } else {
                pdfViewer = new PDFViewer();
                pdfViewer.loadPDF(path);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void openFolder(Resource res) throws Exception {
        System.out.println("Open folder");
        //Lấy cha
        Resource reParent = resourceDAO.selectID(res.getParentId());
        //Lấy topic
        Topic topic = topicDAO.selectID(reParent.getTopicId());
        //Lấy chủ đề
        Subject subject = subjectDAO.selectID(topic.getSubjectId());

        System.out.println("Parent: " + reParent.toString());
        System.out.println("Topic: " + topic.toString());
        System.out.println("Subject: " + subject.toString());

        List<Resource> nestedFolder = resourceDAO.selectParentId(res.getId());
        NestedFolder nestedUI = new NestedFolder(nestedFolder, subject, topic, reParent, file);
        nestedUI.setTitle(file.getName());
        nestedUI.setVisible(true);
    }

    public void handleClickCard() {
        try {
            int typeId = file.getTypeId();
            System.out.println("URL: " + file.getUrl());
            CryptionFileAndFolder cf = new CryptionFileAndFolder(ApplicationConstant.PathConfig.CONFIG_PROPERTIES, "folder.serect.key");

            File decryptedFile = null;

            if (typeId != 1) {
                decryptedFile = CryptionFileAndFolder.decryptToFile(file.getUrl(), cf.secretkey, cf.FIXED_IV);
            }

            switch (typeId) {
                case 1 ->
                    openFolder(file);
                case 2 ->
                    openImage(decryptedFile.getAbsolutePath());
                case 3 ->
                    openVideo(decryptedFile.getAbsolutePath());
                case 4 ->
                    openPDF(decryptedFile.getAbsolutePath());
                default ->
                    JOptionPane.showMessageDialog(null, "Unknown type");
            }
            if (decryptedFile != null) {
                decryptedFile.deleteOnExit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error handling file: " + e.getMessage());
        }
    }

    public void addDeletionListener(CardListener listener) {
        this.cardListener = listener;
    }

    public void deleteCard() {
        if (cardListener != null) {
            cardListener.onDeleteCard(file.getId());
        }
    }

    public void updateCard(String name) {
        if (cardListener != null) {
            cardListener.onUpdateCard(file.getId(), name);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlInfoCard = new javax.swing.JPanel();
        tplName = new javax.swing.JTextArea();
        pictureBox = new com.manage.library.ui.boximage.PictureBox();
        pnlOption = new javax.swing.JPanel();
        btnOption = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        pnlInfoCard.setPreferredSize(new java.awt.Dimension(100, 70));
        pnlInfoCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        pnlInfoCard.setLayout(new java.awt.BorderLayout());

        tplName.setEditable(false);
        tplName.setColumns(20);
        tplName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tplName.setLineWrap(true);
        tplName.setRows(5);
        tplName.setText("hdhshsh\nhahahs");
        tplName.setWrapStyleWord(true);
        tplName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        pnlInfoCard.add(tplName, java.awt.BorderLayout.CENTER);

        add(pnlInfoCard, java.awt.BorderLayout.PAGE_END);

        pictureBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        add(pictureBox, java.awt.BorderLayout.CENTER);

        pnlOption.setPreferredSize(new java.awt.Dimension(100, 40));
        pnlOption.setLayout(new java.awt.BorderLayout());

        btnOption.setText("...");
        btnOption.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOption.setPreferredSize(new java.awt.Dimension(40, 40));
        btnOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOptionActionPerformed(evt);
            }
        });
        pnlOption.add(btnOption, java.awt.BorderLayout.EAST);

        add(pnlOption, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (USBUtils.isUsbKeyConnected()) {
                if (evt.getClickCount() >= 1) {
                    try {
                        this.handleClickCard();
                    } catch (Exception ex) {
                        Logger.getLogger(Card.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy USB hoặc file cấu hình!");
                System.exit(0);
            }
        }
    }//GEN-LAST:event_formMouseClicked

    private void btnOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptionActionPerformed
        JPopupMenu menu = new JPopupMenu();
//        menu.putClientProperty(FlatClientProperties.STYLE,
//                "font: small.bold.font");
        JMenuItem mnuEdit = new JMenuItem("Đổi tên", new FlatSVGIcon("logos/icons/icon/edit.svg"));
        JMenuItem mnuDelete = new JMenuItem("Xóa", new FlatSVGIcon("logos/icons/icon/delete.svg"));

        menu.add(mnuEdit);
        menu.add(mnuDelete);

        //EVENT
        mnuEdit.addActionListener((e) -> {
            boolean txtNameEntered = false;
            while (!txtNameEntered) {
                JPanel panel = new JPanel(new MigLayout("fillx, insets 5", "[center]", "[center]"));
                JLabel label = new JLabel("Nhập tên cần đổi:");
                label.putClientProperty(FlatClientProperties.STYLE,
                        "font: $small.bold.font");
                JTextField txtName = new JTextField(FilenameUtils.getBaseName(file.getName()));
                txtName.addFocusListener(new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent pE) {
                    }

                    @Override
                    public void focusGained(final FocusEvent pE) {
                        txtName.selectAll();
                    }
                });
                txtName.selectAll();
                panel.add(label, "wrap, w 100%");
                panel.add(txtName, "w 100%");
                String[] options = new String[]{"OK", "Hủy"};
                int option = JOptionPane.showOptionDialog(null, panel, "RENAME!!!",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[1]);
                if (option == JOptionPane.YES_OPTION) {
                    if (!txtName.getText().isEmpty() && !txtName.getText().isBlank()) {
                        updateCard(txtName.getText());
                        txtNameEntered = true;
                    }
                } else {
                    break;
                }
            }

        });

        mnuDelete.addActionListener((e) -> {
            int cf = JOptionPane.showConfirmDialog(this, "Xác nhận xóa học liệu ", "DELETE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (cf == JOptionPane.YES_OPTION) {
                deleteCard();
            }
        });

        menu.show(btnOption, 0, btnOption.getHeight());
    }//GEN-LAST:event_btnOptionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOption;
    private com.manage.library.ui.boximage.PictureBox pictureBox;
    private javax.swing.JPanel pnlInfoCard;
    private javax.swing.JPanel pnlOption;
    private javax.swing.JTextArea tplName;
    // End of variables declaration//GEN-END:variables
}
