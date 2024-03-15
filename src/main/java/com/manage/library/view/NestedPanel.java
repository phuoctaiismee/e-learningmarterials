/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.manage.library.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.interfaces.CardListener;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.ui.scrollbar.win11.ScrollPaneWin11;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author PC
 */
public class NestedPanel extends javax.swing.JPanel implements CardListener{

    private ResourceDAO resourceDAO = new ResourceDAO();
    private List<Resource> listFiles = new ArrayList<>();
    private Subject subject;
    private Topic topic;
    private Resource material;
    private Resource folder;

    public NestedPanel() {
        initComponents();
    }

    public NestedPanel(List<Resource> list, Subject subject, Topic topic, Resource material, Resource folder) throws Exception {
        this.listFiles = list;
        this.subject = subject;
        this.topic = topic;
        this.material = material;
        this.folder = folder;

        initComponents();
        scroll = new ScrollPaneWin11();
        JPanel mainNested = new JPanel();
        scroll.setViewportView(mainNested);
        mainNested.setLayout(new MigLayout("insets 20, gap 10, wrap 4", "[left]", "[top]"));

        mainNested.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");
        
        this.add(scroll, "dock center");
        JButton btn = new JButton("Back");
        this.add(btn, "dock north");

//        if (!listFiles.isEmpty()) {
//            renderCards();
//        } else {
//            notFound();
//        }

    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scroll;

    @Override
    public void onDeleteCard(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onUpdateCard(Integer id, String newName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
