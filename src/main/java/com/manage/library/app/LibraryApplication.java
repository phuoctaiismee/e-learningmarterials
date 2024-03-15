package com.manage.library.app;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.manage.library.ui.splashscreen.SplashScreenUI;
import com.manage.library.utils.USBUtils;
import javax.swing.JOptionPane;

public class LibraryApplication extends javax.swing.JFrame {

    public LibraryApplication() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatIntelliJLaf.registerCustomDefaultsSource("styles");
        FlatIntelliJLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            if (USBUtils.isUsbKeyConnected()) {
                SplashScreenUI splash = new SplashScreenUI(new javax.swing.JFrame(), true);
                splash.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy USB hoặc file cấu hình!");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
