package com.manage.library.app;

import com.formdev.flatlaf.FlatClientProperties;
import com.manage.library.config.Authentication;
import com.manage.library.utils.XImage;
import com.manage.library.view.LibraryPanel;
import com.manage.library.view.ManagePanel;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.miginfocom.swing.MigLayout;

public class IndexApp extends javax.swing.JPanel {

    private ApplicationMain main;

    public IndexApp(JFrame frame) {
        this.main = (ApplicationMain) frame;
        initComponents();
        setLayout(new MigLayout("fillx, insets 20 200", "[center]", "[center]"));

        /*
         *   _________CONFIGURATION HEADER AND MAIN CONTENT_____________
         */
        // TITLE SECTION
        JLabel title = new JLabel("Hệ thống học liệu điện tử.");
        title.setFont(UIManager.getFont("h3.font"));
        title.setForeground(UIManager.getColor("text.primary.color"));
        add(title, "wrap");

        // DESCRIPTION
        JTextPane description = new JTextPane();
        description.setEditable(false);
        description.setFocusable(false);
        StyledDocument doc = description.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        description.setText("Chào mừng bạn đã đến với học liệu điện tử của GD Việt Nam.\n Chúc bạn có một trải nghiệm vui vẻ!!");
        description.setFont(UIManager.getFont("medium.font"));
        description.setForeground(UIManager.getColor("text.secondary.color"));
        add(description, "wrap");

        // HERO SECTION
        JPanel contentPanel = new JPanel(new MigLayout("wrap 2, fillx"));

        // Left Panel
        JPanel leftPanel = new JPanel(new MigLayout("fillx, gapy 10"));
//        leftPanel.setBackground(UIManager.getColor("text.secondary.color"));

        // Left Panel - Title
        JLabel leftPanelContent = new JLabel("E-learning Materials");
        leftPanelContent.setFont(UIManager.getFont("h2.font"));
        leftPanel.add(leftPanelContent, "wrap, span");

        // Left Panel - Desc
        JTextPane leftContentDesc = new JTextPane();
        leftContentDesc.setEditable(false);
        leftContentDesc.setFocusable(false);
        StyledDocument docLeftContent = leftContentDesc.getStyledDocument();
        SimpleAttributeSet centerLeftContent = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerLeftContent, StyleConstants.ALIGN_LEFT);
        docLeftContent.setParagraphAttributes(0, docLeftContent.getLength(), centerLeftContent, false);
        leftContentDesc.setText("Được xây dựng dựa theo Thông tư 37, 38, 39/2021/TT-BGDĐT ban hành ngày 30 tháng 12 năm 2021 về danh mục thiết bị dạy học tối thiểu cấp Tiểu học, THCS, THPT.");
        leftContentDesc.setFont(UIManager.getFont("medium.font"));
        leftContentDesc.setBackground(leftPanel.getBackground());
        leftContentDesc.setForeground(UIManager.getColor("text.secondary.color"));
        leftPanel.add(leftContentDesc, "span");

        // Left Panel - Check List
        List<String> listCheck = Arrays.asList(
//                "<html><b>Quản lý học liệu</b> – Xây dựng các khóa học liệu, linh động, tiện lợi.</html>",
                "<html><b>Kho học liệu</b> – Học liệu đa dạng gồm nhiều ảnh minh họa, video và tài liệu tham khảo.</html>",
                "<html><b>Tiện lợi và nhanh chóng</b> – Không phức tạp hóa vấn về cài đặt, sử dụng ngay khi kết nối USB.</html>");
        listCheck.forEach(action -> {
            JLabel checkListItem = new JLabel(action, XImage.read("icons/checklist.png"), JLabel.LEADING);
            checkListItem.setVerticalTextPosition(JLabel.TOP);
            checkListItem.setFont(UIManager.getFont("small.font"));
            leftPanel.add(checkListItem, "wrap");
        });

        //Left Panel - Button
        JPanel buttonPanel = new JPanel(new MigLayout());
        buttonPanel.setBackground(leftPanel.getBackground());
        JButton btnLibrary = new JButton("Kho học liệu");
        JButton btnManageLib = new JButton("Quản lý học liệu");
        btnLibrary.setBackground(UIManager.getColor("button.primary.color"));
        btnManageLib.setBackground(UIManager.getColor("button.white.color"));
        btnLibrary.setForeground(Color.white);
        btnManageLib.setForeground(Color.black);
        btnLibrary.addActionListener(action -> {
            setContentPage(new LibraryPanel(main));
        });
        btnManageLib.addActionListener(action -> {
            boolean passwordEntered = false;
            while (!passwordEntered) {
                JPanel panel = new JPanel(new MigLayout("fillx, insets 5", "[center]", "[center]"));
                JLabel label = new JLabel("Vui lòng xác nhận mật khẩu:");
                label.putClientProperty(FlatClientProperties.STYLE,
                        "font: $small.bold.font");
                JPasswordField pass = new JPasswordField();
                pass.requestFocus();
                panel.add(label, "wrap, w 100%");
                panel.add(pass, "w 100%");
                int option = JOptionPane.showConfirmDialog(this, panel, "CONFIRM!!!",
                        JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    char[] password = pass.getPassword();
                    if (password == null || password.length <= 0) {
                        pass.setFocusable(true);
                    } else {

                        Authentication auth = new Authentication();
                        if (auth.isAuthenticationConfirm(new String(password))) {
                            setContentPage(new ManagePanel(main));
                            passwordEntered = true;
                        }
                    }
                } else {
                    break;
                }
            }

        });

        buttonPanel.add(btnLibrary, "width 100, height 35");
        buttonPanel.add(btnManageLib, "align left, width 100, height 35");
        leftPanel.add(buttonPanel);

        // Right Panel
        JPanel rightPanel = new JPanel(new MigLayout("fillx"));
        rightPanel.setBackground(leftPanel.getBackground());
        // Right Panel - Hero
        JLabel rightPanelContent = new JLabel(XImage.read("hero.png"));
        rightPanel.add(rightPanelContent);

        // ADD TO PANEL MAIN CONTENT
        contentPanel.add(leftPanel, "width 100%, grow");
        contentPanel.add(rightPanel, "width 100%, grow");

        add(contentPanel, "span");

    }

    public void setContentPage(JPanel newContentPanel) {
        main.setContentPane(newContentPanel);
        main.revalidate();
        main.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
