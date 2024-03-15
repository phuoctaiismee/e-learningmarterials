package com.manage.library.view;

import com.manage.library.config.CryptionFileAndFolder;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.ResourceTypeDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.model.Resource;
import com.manage.library.model.ResourceType;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.ui.glasspanepopup.GlassPanePopup;
import com.manage.library.ui.message.Message;
import com.manage.library.utils.USBUtils;
import com.manage.library.utils.XImage;
import com.manage.library.utils.ZipperUtilsRoot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author PHUOCTAI
 */
public final class MainFrame extends javax.swing.JFrame {

//    private static String jsonRoot = GenerateFileSystem.convertFolderToJson();
    List<Subject> listSubject = new ArrayList<>();
    List<Resource> listFiles = new ArrayList<>();

    SubjectDAO subjectDAO = new SubjectDAO();
    TopicDAO topicDAO = new TopicDAO();
    ResourceDAO fileDAO = new ResourceDAO();

    List<String> listTopic;

    List<ResourceType> listType = new ArrayList<>();
    ResourceTypeDAO typeDAO = new ResourceTypeDAO();

    public MainFrame() throws Exception {
        initComponents();
        GlassPanePopup.install(this);
        this.setIconImage(XImage.getAppIcon());
        initialValue();
        this.setLocationRelativeTo(null);
        this.renderCards();
        scrollPaneWin112.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneWin113.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private void initialValue() throws Exception {
        initTypes();
        initSubjects();
    }

    private void initTypes() {
        listType = typeDAO.select();

        List<Item> fileItem = listType.stream()
                .filter(resourceType -> !"Thư mục".equalsIgnoreCase(resourceType.getName()))
                .map(resourceType -> new Item(resourceType.getName(), XImage.read("icons/education.png")))
                .collect(Collectors.toList());

        fileItem.forEach(listTypeFile::addItem);

        if (!fileItem.isEmpty()) {
            listTypeFile.setSelectedIndex(0); // Mặc định select
        }
    }

    private void initSubjects() {
        DefaultComboBoxModel<String> cboModel = new DefaultComboBoxModel<>();
        cboModel.removeAllElements();

        listSubject = subjectDAO.select();

        // Thêm tên của mỗi đối tượng Subject vào cboModel
        if (!listSubject.isEmpty()) {
            listSubject.forEach(item -> cboModel.addElement(item.getName()));

            cboMonhoc.setModel(cboModel);
            cboMonhoc.setSelectedIndex(0);
        }
    }

    private void changeTopic() {
        List<Item> fileItem = new ArrayList<>();
        String selectedSubject = cboMonhoc.getSelectedItem().toString();

        DefaultListModel listModel = (DefaultListModel) listTopicRender.getModel();
        listModel.removeAllElements();

        listSubject.forEach(subject -> {
            if (subject.getName().equals(selectedSubject)) {
                subject.getTopics()
                        .stream()
                        .filter(top -> !top.getName().equalsIgnoreCase("Sách tham khảo"))
                        .forEach(topic -> {
                            fileItem.add(new Item(topic.getName(), XImage.read("icons/education.png")));
                        });
            }
        });

        fileItem.forEach(item -> listModel.addElement(item));
        listTopicRender.setModel(listModel);
        listTopicRender.setSelectedIndex(0);
    }

    private void handleSelectedTopic(String name) throws Exception {
        listFiles.clear();
        String selectedSubject = cboMonhoc.getSelectedItem().toString();
        Subject sub = subjectDAO.selectName(selectedSubject);
        Topic topicSelected = topicDAO.selectName(name, sub.getId());
        if (topicSelected != null) {

            final int index = listTypeFile.getSelectedIndex();
            final int type;

            if (index != -1) {
                Item item = listTypeFile.getItem(index);

                switch (item.getText()) {
                    case "Thư mục":
                        type = 1;
                        break;
                    case "Tranh, ảnh":
                        type = 2;
                        break;
                    case "Video":
                        type = 3;
                        break;
                    case "Tài liệu":
                        type = 4;
                        topicSelected = handleSelectedDocument(sub);
                        break;
                    default:
                        type = 0;
                        break;
                }

                //Lấy thư mục tổng (VIDEO, TRANH ẢNH, TÀI LIỆU) gốc
                if (topicSelected != null) {
                    Resource rsou = fileDAO.selectNameandTopicId(item.getText(), topicSelected.getId());
                    if (rsou != null) {
                        topicSelected.getResources().stream()
                                .filter(rs -> rs.getTypeId() == type || rs.getParentId() == rsou.getId())
                                .forEach(listFiles::add);
                    } else {
                        topicSelected.getResources().stream()
                                .filter(rs -> rs.getTypeId() == type)
                                .forEach(listFiles::add);
                    }
                }
            }
        }

        renderCards();
        if (listFiles.isEmpty() || listFiles.size() <= 0) {
            pnlHome.removeAll();
            pnlHome.revalidate();
            pnlHome.repaint();
            JLabel lbl = new JLabel("KHÔNG TÌM THẤY HỌC LIỆU NÀO");
            lbl.setIcon(XImage.read("no-results.png"));
            lbl.setFont(new Font("Opens sans", Font.BOLD, 24));
            lbl.setHorizontalAlignment((int) CENTER_ALIGNMENT);
            lbl.setSize(pnlHome.getSize());
            pnlHome.add(lbl);
        } else {
            renderCards();
        }
    }

    private Topic handleSelectedDocument(Subject subject) {
        Topic topicSelected = topicDAO.selectName("Sách tham khảo", subject.getId());
        listTopicRender.clearSelection();
        listTopicRender.setSelectedIndex(-1);
        return topicSelected;
    }
    private JPanel callPanel;

    // HÀM GỌI PANEL (NAVIGATION)
    private void showPanel(JPanel pnl) {
        callPanel = pnl;
        pnlRoot.removeAll();
        pnlRoot.add(callPanel);
        pnlRoot.repaint();
        pnlRoot.validate();
    }

    // HÀM KHỞI TẠO MỘT DANH SÁCH CARD
    public List<Card> generateCard() throws Exception {
        List<Card> fsp = new ArrayList<Card>();
        for (int i = 0; i < listFiles.size(); i++) {
            Resource file = listFiles.get(i);
            Card card = new Card();
            card.setBackground(MainFrame.this.getBackground());
            card.setForeground(MainFrame.this.getForeground());
            card.setFile(file);
            card.fillData();
            fsp.add(card);
        }
        return fsp;
    }

    public void renderCards() throws Exception {
        List<Card> listPanel = generateCard();
        pnlHome.removeAll();
        pnlHome.revalidate();
        pnlHome.repaint();
        pnlHome.setSize(pnlRoot.getSize());

        pnlHome.setBackground(new Color(242, 242, 242));

        int fixedColumns = 5; // Số cột cố định trên mỗi hàng
        int padding = 10; // Padding của mỗi card
        int totalPadding = ((fixedColumns - 1) * padding) + 15;
        int xPadding = padding;
        int yPadding = padding;

        int availableWidth = pnlHome.getWidth();

        int cardWidth = (availableWidth - totalPadding) / fixedColumns;
        int cardHeight = 220;
        int xShift = cardWidth + padding;
        int yShift = cardHeight + padding;

//        System.out.println("HOME: " + availableWidth);
//        System.out.println("CARD: " + cardWidth);
        int indexComponent = 0;

        for (int y = yPadding; indexComponent < listPanel.size(); y += yShift) {
            for (int x = xPadding; x < pnlHome.getWidth() && indexComponent < listPanel.size(); x += xShift) {
                listPanel.get(indexComponent).setBounds(x, y, cardWidth, cardHeight);
                pnlHome.add(listPanel.get(indexComponent));
                indexComponent++;
            }
        }

        int pnlHeight = (int) Math.ceil((double) indexComponent / fixedColumns) * yShift + yPadding;
        pnlHome.setPreferredSize(new Dimension(availableWidth, pnlHeight));

        if (listPanel == null || listPanel.size() <= 0) {
            pnlHome.removeAll();
            pnlHome.revalidate();
            pnlHome.repaint();
            JLabel lbl = new JLabel("KHÔNG TÌM THẤY HỌC LIỆU NÀO");
            lbl.setIcon(XImage.read("no-results.png"));
            lbl.setFont(new Font("Opens sans", Font.BOLD, 24));
            lbl.setHorizontalAlignment((int) CENTER_ALIGNMENT);
            lbl.setSize(pnlHome.getSize());
            pnlHome.add(lbl);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlNav = new javax.swing.JPanel();
        scrollPaneWin113 = new com.manage.library.ui.scrollbar.win11.ScrollPaneWin11();
        listTypeFile = new com.manage.library.view.ListTopic<>();
        pnlRoot = new javax.swing.JPanel();
        scrollPaneWin111 = new com.manage.library.ui.scrollbar.win11.ScrollPaneWin11();
        pnlHome = new javax.swing.JPanel();
        pnlArticle = new javax.swing.JPanel();
        pnlSubject = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboMonhoc = new javax.swing.JComboBox<>();
        pnlImport = new javax.swing.JPanel();
        lblImport = new javax.swing.JLabel();
        pnlTopics = new javax.swing.JPanel();
        scrollPaneWin112 = new com.manage.library.ui.scrollbar.win11.ScrollPaneWin11();
        listTopicRender = new com.manage.library.view.ListTopic<>();
        pnlFooter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GDVN Bộ học liệu điện tử");
        setPreferredSize(new java.awt.Dimension(1370, 700));

        pnlNav.setBackground(new java.awt.Color(0, 102, 102));
        pnlNav.setForeground(new java.awt.Color(255, 255, 255));
        pnlNav.setPreferredSize(new java.awt.Dimension(120, 454));
        pnlNav.setLayout(new java.awt.BorderLayout());

        scrollPaneWin113.setBorder(null);

        listTypeFile.setBackground(new java.awt.Color(0, 102, 102));
        listTypeFile.setBorder(null);
        listTypeFile.setForeground(new java.awt.Color(255, 255, 255));
        listTypeFile.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listTypeFile.setSelectedColor(new java.awt.Color(127, 157, 146));
        listTypeFile.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listTypeFileValueChanged(evt);
            }
        });
        scrollPaneWin113.setViewportView(listTypeFile);

        pnlNav.add(scrollPaneWin113, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlNav, java.awt.BorderLayout.EAST);

        pnlRoot.setForeground(new java.awt.Color(204, 204, 204));
        pnlRoot.setLayout(new java.awt.CardLayout());

        scrollPaneWin111.setBorder(null);

        javax.swing.GroupLayout pnlHomeLayout = new javax.swing.GroupLayout(pnlHome);
        pnlHome.setLayout(pnlHomeLayout);
        pnlHomeLayout.setHorizontalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1019, Short.MAX_VALUE)
        );
        pnlHomeLayout.setVerticalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 725, Short.MAX_VALUE)
        );

        scrollPaneWin111.setViewportView(pnlHome);

        pnlRoot.add(scrollPaneWin111, "card3");

        getContentPane().add(pnlRoot, java.awt.BorderLayout.CENTER);

        pnlArticle.setBackground(new java.awt.Color(255, 255, 255));
        pnlArticle.setPreferredSize(new java.awt.Dimension(250, 661));
        pnlArticle.setLayout(new java.awt.BorderLayout());

        pnlSubject.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Bộ môn:");
        jLabel1.setToolTipText("");

        cboMonhoc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboMonhoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cboMonhoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonhocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSubjectLayout = new javax.swing.GroupLayout(pnlSubject);
        pnlSubject.setLayout(pnlSubjectLayout);
        pnlSubjectLayout.setHorizontalGroup(
            pnlSubjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubjectLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlSubjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboMonhoc, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        pnlSubjectLayout.setVerticalGroup(
            pnlSubjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSubjectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboMonhoc, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pnlArticle.add(pnlSubject, java.awt.BorderLayout.PAGE_START);

        pnlImport.setBackground(new java.awt.Color(0, 102, 102));
        pnlImport.setPreferredSize(new java.awt.Dimension(250, 50));

        lblImport.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblImport.setForeground(new java.awt.Color(255, 255, 255));
        lblImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logos/icons/import.png"))); // NOI18N
        lblImport.setText("Thêm học liệu");
        lblImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblImport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImportMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlImportLayout = new javax.swing.GroupLayout(pnlImport);
        pnlImport.setLayout(pnlImportLayout);
        pnlImportLayout.setHorizontalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(lblImport)
                .addContainerGap(72, Short.MAX_VALUE))
        );
        pnlImportLayout.setVerticalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlImportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImport, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlArticle.add(pnlImport, java.awt.BorderLayout.PAGE_END);

        pnlTopics.setLayout(new java.awt.BorderLayout());

        scrollPaneWin112.setBorder(null);

        listTopicRender.setBackground(new java.awt.Color(0, 102, 102));
        listTopicRender.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        listTopicRender.setForeground(new java.awt.Color(255, 255, 255));
        listTopicRender.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listTopicRender.setSelectedColor(new java.awt.Color(0, 153, 153));
        listTopicRender.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listTopicRenderValueChanged(evt);
            }
        });
        scrollPaneWin112.setViewportView(listTopicRender);

        pnlTopics.add(scrollPaneWin112, java.awt.BorderLayout.LINE_START);

        pnlArticle.add(pnlTopics, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlArticle, java.awt.BorderLayout.WEST);

        pnlFooter.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(153, 153, 153)));
        pnlFooter.setPreferredSize(new java.awt.Dimension(1170, 30));
        pnlFooter.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Công ty cổ phần công nghệ GD Việt Nam.");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlFooter.add(jLabel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlFooter, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboMonhocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonhocActionPerformed
        if (USBUtils.isUsbKeyConnected()) {
            if (!listSubject.isEmpty()) {
                changeTopic();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối usb để tiếp tục sử dụng");
            System.exit(0);
        }
    }//GEN-LAST:event_cboMonhocActionPerformed

    private void listTopicRenderValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listTopicRenderValueChanged
        if (USBUtils.isUsbKeyConnected()) {
//            if (evt.getValueIsAdjusting()) {
            int index = listTopicRender.getSelectedIndex();
            if (index != -1) {
                Item item = listTopicRender.getItem(index);
                try {
                    handleSelectedTopic(item.getText());
                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối usb để tiếp tục sử dụng");
            System.exit(0);
        }
    }//GEN-LAST:event_listTopicRenderValueChanged

    private void listTypeFileValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listTypeFileValueChanged
        if (USBUtils.isUsbKeyConnected()) {
            int index = listTopicRender.getSelectedIndex();
            if (index != -1) {
                Item item = listTopicRender.getItem(index);
                try {
                    handleSelectedTopic(item.getText());
                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                listTopicRender.setSelectedIndex(0);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng kết nối usb để tiếp tục sử dụng");
            System.exit(0);
        }
    }//GEN-LAST:event_listTypeFileValueChanged

    private void lblImportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImportMouseClicked
        FileDialog dialog = new FileDialog((Frame) null, "Vui lòng chọn học liệu cần thêm");
        dialog.setMode(FileDialog.LOAD);
        dialog.setMultipleMode(false);
        dialog.setFile("*.zip");
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);
        ZipperUtilsRoot zipper = new ZipperUtilsRoot();
        final String file = dialog.getFile();
        final File[] directory = dialog.getFiles();

        dialog.dispose();

        if (null != file && !file.isEmpty()) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private LoaderProgress loader;

                @Override
                protected Void doInBackground() throws Exception {
                    try {

                        SwingUtilities.invokeLater(() -> {
                            loader = new LoaderProgress(MainFrame.this, true);
                            loader.start();
                            loader.setVisible(true);
                        });
                        System.out.println("File: " + directory[0].getAbsolutePath());
                        zipper.unzip(directory[0].getAbsolutePath());

                    } catch (URISyntaxException | IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        SwingUtilities.invokeLater(() -> {
                            if (loader != null) {
                                try {
//                                    onEncryptionComplete();
                                    loader.complete();
                                } catch (Exception ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        });
                    }

                    return null;
                }

                @Override
                protected void done() {
                    initSubjects();
                    Message obj = new Message("Hoàn thành", "Học liệu đã được thêm vào thư viện của bạn!!!", Message.GLASS_SUCCESS, false);
                    obj.eventOK((ActionEvent ae) -> {
                        System.out.println("Click OK");
                        GlassPanePopup.closePopupLast();
                    });
                    GlassPanePopup.showPopup(obj);
                }
            };

            worker.execute();
        }

    }//GEN-LAST:event_lblImportMouseClicked

    private void onEncryptionComplete() {
        try {
            CryptionFileAndFolder cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
            CryptionFileAndFolder.encryptFolder("subject", cf.secretkey, cf.FIXED_IV);

            initSubjects();
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainFrame().setVisible(true);

                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboMonhoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblImport;
    private com.manage.library.view.ListTopic<String> listTopicRender;
    private com.manage.library.view.ListTopic<String> listTypeFile;
    private javax.swing.JPanel pnlArticle;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlImport;
    private javax.swing.JPanel pnlNav;
    private javax.swing.JPanel pnlRoot;
    private javax.swing.JPanel pnlSubject;
    private javax.swing.JPanel pnlTopics;
    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scrollPaneWin111;
    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scrollPaneWin112;
    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scrollPaneWin113;
    // End of variables declaration//GEN-END:variables
}
