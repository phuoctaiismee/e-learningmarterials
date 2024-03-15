package com.manage.library.ui.message;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import com.manage.library.ui.glasspanepopup.GlassPanePopup;

/**
 *
 * @author RAVEN
 */
public class Message extends javax.swing.JPanel {

    public static final int GLASS_SUCCESS = 1;
    public static final int GLASS_ERROR = 0;
    public static final int GLASS_WARNING = 2;
    public static final boolean GLASS_HAS_CANCEL = true;
    public static final boolean GLASS_HASNOT_CANCEL = false;

    public Message() {
        initComponents();
        setOpaque(false);
        txtContent.setBackground(new Color(0, 0, 0, 0));
        txtContent.setSelectionColor(new Color(48, 170, 63, 200));
        txtContent.setOpaque(false);
    }

    public Message(String title, String message, int type, boolean hasCancel) {
        initComponents();
        setOpaque(false);
        txtContent.setBackground(new Color(0, 0, 0, 0));
        txtContent.setSelectionColor(new Color(48, 170, 63, 200));
        txtContent.setOpaque(false);
        txtTitle.setText(title);
        txtContent.setText(message);
        Color clor = type == 1 ? new java.awt.Color(0, 102, 102) : type == 0 ? new java.awt.Color(222, 0, 0, 255) : new java.awt.Color(245, 183, 29, 255);
        cmdOK.setBackground(clor);
        if (hasCancel) {
            cmdCancel.setVisible(true);
        } else {
            cmdCancel.setVisible(false);
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        g2.dispose();
        super.paintComponent(grphcs);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtTitle = new javax.swing.JLabel();
        txtContent = new javax.swing.JTextPane();
        cmdOK = new com.manage.library.ui.message.Button();
        cmdCancel = new com.manage.library.ui.message.Button();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));

        txtTitle.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(80, 80, 80));
        txtTitle.setText("Your Message Title Dialog Custom");

        txtContent.setEditable(false);
        txtContent.setForeground(new java.awt.Color(133, 133, 133));
        txtContent.setText("This is part of a series of short tutorials about specific elements, components, or interactions. We’ll cover the UX, the UI, and the construction inside of Sketch. Plus, there’s a freebie for you at the end!");

        cmdOK.setBackground(new java.awt.Color(0, 102, 102));
        cmdOK.setForeground(new java.awt.Color(255, 255, 255));
        cmdOK.setText("OK");
        cmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOKActionPerformed(evt);
            }
        });

        cmdCancel.setBackground(new java.awt.Color(221, 221, 221));
        cmdCancel.setText("Cancel");
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTitle)
                        .addGap(0, 261, Short.MAX_VALUE))
                    .addComponent(txtContent, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCancelActionPerformed
        GlassPanePopup.closePopupLast();
    }//GEN-LAST:event_cmdCancelActionPerformed

    private void cmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOKActionPerformed
        GlassPanePopup.closePopupLast();
    }//GEN-LAST:event_cmdOKActionPerformed

    public void eventOK(ActionListener event) {
        cmdOK.addActionListener(event);
    }

    public void eventCancel(ActionListener event) {
        cmdCancel.addActionListener(event);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.manage.library.ui.message.Button cmdCancel;
    private com.manage.library.ui.message.Button cmdOK;
    private javax.swing.JTextPane txtContent;
    private javax.swing.JLabel txtTitle;
    // End of variables declaration//GEN-END:variables
}
