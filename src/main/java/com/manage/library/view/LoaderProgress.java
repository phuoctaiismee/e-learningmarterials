package com.manage.library.view;

import java.awt.Color;

/**
 *
 * @author PC
 */
public class LoaderProgress extends javax.swing.JDialog {

    public LoaderProgress(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setBackground(new Color(0, 0, 0, 0));
    }

    private void doTask(String taskName, int progress) throws Exception {
        lblStatus.setText("<html><div style='text-align: center; background-color: none; color: white; "
                + "padding: 5px;'>" + taskName + "<br> Vui lòng chờ đợi...</div></html>");
        Thread.sleep(1000);
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doTask("Đang thêm học liệu...", 10);
                    doTask("Đang thêm học liệu", 20);
                    doTask("Đang thêm học liệu...", 30);
                    doTask("Đang thêm học liệu", 40);
                    doTask("Đang thêm học liệu...", 50);
                    doTask("Đang thêm học liệu", 60);
                    doTask("Đang thêm học liệu...", 70);
                    doTask("Đang thêm học liệu", 80);
                    doTask("Đang thêm học liệu...", 90);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void complete() throws Exception {
        doTask("Hoàn thành ...", 100);
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRoundUI1 = new com.manage.library.ui.panelrounded.PanelRoundUI();
        lblStatus = new javax.swing.JLabel();
        panelRoundUI2 = new com.manage.library.ui.panelrounded.PanelRoundUI();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);

        panelRoundUI1.setBackground(new java.awt.Color(51, 51, 51));
        panelRoundUI1.setRoundBottomLeft(100);
        panelRoundUI1.setRoundBottomRight(100);
        panelRoundUI1.setRoundTopLeft(100);
        panelRoundUI1.setRoundTopRight(100);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(255, 255, 255));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Đang giải nén học liệu,....");

        panelRoundUI2.setBackground(new java.awt.Color(255, 255, 255));
        panelRoundUI2.setPreferredSize(new java.awt.Dimension(130, 130));
        panelRoundUI2.setRoundBottomLeft(150);
        panelRoundUI2.setRoundBottomRight(150);
        panelRoundUI2.setRoundTopLeft(150);
        panelRoundUI2.setRoundTopRight(150);
        panelRoundUI2.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logos/icons/Hourglass.gif"))); // NOI18N
        panelRoundUI2.add(jLabel1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout panelRoundUI1Layout = new javax.swing.GroupLayout(panelRoundUI1);
        panelRoundUI1.setLayout(panelRoundUI1Layout);
        panelRoundUI1Layout.setHorizontalGroup(
            panelRoundUI1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRoundUI1Layout.createSequentialGroup()
                .addGroup(panelRoundUI1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRoundUI1Layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(panelRoundUI2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRoundUI1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(114, Short.MAX_VALUE))
        );
        panelRoundUI1Layout.setVerticalGroup(
            panelRoundUI1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRoundUI1Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(panelRoundUI2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(92, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRoundUI1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRoundUI1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoaderProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoaderProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoaderProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoaderProgress.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoaderProgress dialog = new LoaderProgress(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblStatus;
    private com.manage.library.ui.panelrounded.PanelRoundUI panelRoundUI1;
    private com.manage.library.ui.panelrounded.PanelRoundUI panelRoundUI2;
    // End of variables declaration//GEN-END:variables
}
