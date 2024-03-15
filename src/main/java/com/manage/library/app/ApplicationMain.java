package com.manage.library.app;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.manage.library.config.Authentication;
import com.manage.library.ui.drawer.Drawer;
import com.manage.library.ui.drawer.DrawerController;
import com.manage.library.ui.drawer.DrawerItem;
import com.manage.library.ui.drawer.EventDrawer;
import com.manage.library.ui.drawer.header.Header;
import com.manage.library.utils.XImage;
import com.manage.library.view.LibraryPanel;
import com.manage.library.view.ManagePanel;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author PC
 */
public final class ApplicationMain extends javax.swing.JFrame {

    private final IndexApp index = new IndexApp(this);
    private ManagePanel manage;
    private LibraryPanel library;

    public DrawerController drawer;

    public ApplicationMain() {

        initComponents();
        setIconImage(XImage.getAppIcon());
        setLocationRelativeTo(null);
        reloadPage();
        initDrawer((ApplicationMain) this);
        this.setContentPane(index);
    }

    public void reloadPage() {
        manage = new ManagePanel(this);
        library = new LibraryPanel(this);
    }

    public void initDrawer(ApplicationMain main) {
        drawer = Drawer.newDrawer(main)
                .enableScroll(true)
                .enableScrollUI(false)
                .header(new Header())
                .space(5)
                .addChild(new DrawerItem("Kho học liệu").icon(XImage.read("icons/librarys.png")).build())
                .addChild(new DrawerItem("Quản lý học liệu").icon(XImage.read("icons/books-stack.png")).build())
                .addFooter(new DrawerItem("Thoát chương trình").icon(XImage.read("icons/exit.png")).iconTextGap(4).build())
                .event(new EventDrawer() {
                    @Override
                    public void selected(int index, DrawerItem item) {

                        switch (index) {
                            case 0 -> {
                                setContentPane(library);
                                drawer.hide();
                            }
                            case 1 -> {
                                boolean passwordEntered = false;
                                while (!passwordEntered) {
                                    JPanel panel = new JPanel(new MigLayout("fillx, insets 5", "[center]", "[center]"));
                                    JLabel label = new JLabel("Vui lòng xác nhận mật khẩu:");
                                    label.putClientProperty(FlatClientProperties.STYLE,
                                            "font: $small.bold.font");
                                    JPasswordField pass = new JPasswordField();
                                    panel.add(label, "wrap, w 100%");
                                    panel.add(pass, "w 100%");
                                    pass.requestFocus();
                                    int option = JOptionPane.showConfirmDialog(null, panel, "CONFIRM!!!",
                                            JOptionPane.YES_NO_OPTION
                                    );
                                    if (option == JOptionPane.YES_OPTION) {
                                        char[] password = pass.getPassword();
                                        if (password == null || password.length <= 0) {
                                            pass.setFocusable(true);
                                        } else {

                                            Authentication auth = new Authentication();
                                            if (auth.isAuthenticationConfirm(new String(password))) {
                                                System.out.println("Dô đây");
                                                passwordEntered = true;
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (passwordEntered) {
                                    setContentPane(manage);
                                    drawer.hide();
                                }

                            }
                            case 2 -> {
                                int rs = JOptionPane.showConfirmDialog(new JFrame(), "Bạn có thực sự muốn thoát chương trình?", "Thoát chương trình?", JOptionPane.YES_NO_OPTION);
                                if (rs == JOptionPane.YES_OPTION) {
                                    System.exit(0);
                                }
                            }
                            default ->
                                JOptionPane.showMessageDialog(new JFrame(), "Chức năng đang phát triển!!!");
                        }
                    }

                })
                .build();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuDrawer = new javax.swing.JMenu();
        mnuAbout = new javax.swing.JMenu();
        mnuExit = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(" Inc. GD Việt Nam");

        jPanel1.setPreferredSize(new java.awt.Dimension(1248, 700));

        jButton3.setText("jButton3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(637, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(536, 536, 536))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(719, 719, 719)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mnuDrawer.setText("Menu");
        mnuDrawer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuDrawerMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuDrawer);

        mnuAbout.setText("About");
        mnuAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuAboutMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuAbout);

        mnuExit.setText("Exit");
        mnuExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuExitMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuExit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closingWindow() throws HeadlessException {
        int rs = JOptionPane.showConfirmDialog(new JFrame(), "Bạn có thực sự muốn thoát chương trình?", "Thoát chương trình?", JOptionPane.YES_NO_OPTION);
        if (rs == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void mnuDrawerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuDrawerMouseClicked
        reloadPage();
        if (drawer != null) {
            if (drawer.isShow()) {
                drawer.hide();
                mnuDrawer.setSelected(false);
            } else {
                drawer.show();
                mnuDrawer.setSelected(true);
            }
        }
    }//GEN-LAST:event_mnuDrawerMouseClicked

    private void mnuExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuExitMouseClicked
        closingWindow();
    }//GEN-LAST:event_mnuExitMouseClicked

    private void mnuAboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuAboutMouseClicked

    }//GEN-LAST:event_mnuAboutMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatIntelliJLaf.registerCustomDefaultsSource("styles");
        FlatIntelliJLaf.setup();
//        FlatDarculaLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApplicationMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenu mnuAbout;
    private javax.swing.JMenu mnuDrawer;
    private javax.swing.JMenu mnuExit;
    // End of variables declaration//GEN-END:variables
}
