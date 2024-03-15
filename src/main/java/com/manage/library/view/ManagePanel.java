package com.manage.library.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.app.ApplicationMain;
import com.manage.library.app.IndexApp;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.utils.FileUtils;
import com.manage.library.utils.TreeUtils;
import com.manage.library.utils.ZipperUtils;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author PC
 */
public final class ManagePanel extends javax.swing.JPanel {

    private final ApplicationMain mainFrame;
    private List<Subject> listSubject = new ArrayList<>();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final TopicDAO topicDAO = new TopicDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();

    public ManagePanel(JFrame frame) {
        this.mainFrame = (ApplicationMain) frame;
        mainFrame.initDrawer(mainFrame);
        initComponents();
        initialLayout();
    }

    public void initialLayout() {
        setLayout(new MigLayout("fillx, gapx 10, gapy 20,insets 20", "[center]", "[center]"));

        JPanel titlePanel = new JPanel(new MigLayout("fill"));

        //TITLE 
        JLabel title = new JLabel("Quản lý thư viện học liệu");
        title.setFont(UIManager.getFont("h4.font"));
        title.setForeground(UIManager.getColor("text.primary.color"));
        titlePanel.add(title, "align left, hmax pref+10");

        //BACK BUTTON
        JButton btnBack = new JButton("Back");
        btnBack.setToolTipText("Quay lại trang chủ");
        btnBack.addActionListener(ac -> {
            setContentPage(new IndexApp(mainFrame));
        });
        titlePanel.add(btnBack, "align right");
        add(titlePanel, "span, growx");

        // PANEL CONTENT
        leftPanel = new JPanel(new MigLayout("fillx, insets 10", "[left]", "[top]"));
        rightPanel = new JPanel(new MigLayout("fillx, insets 10", "[center]", "[center]"));

        leftPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");

        rightPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");
        add(leftPanel, "grow, width 80%, h 100%");
        add(rightPanel, "grow, width 20%, h 100%");

//        JLabel lblSubject = new JLabel("Thêm gói học liệu");
//        lblSubject.putClientProperty(FlatClientProperties.STYLE, "font: $h6.font");
//        rightPanel.add(lblSubject, "wrap, pad 0 0 -10 0");
        //BUTTON IMPORT FILE ZIP
        JButton btnImport = new JButton("Thêm học liệu");
        btnImport.setIcon(new FlatSVGIcon("logos/icons/icon/import.svg"));
        btnImport.addActionListener((e) -> {
            FileDialog dialog = new FileDialog((Frame) null, "Vui lòng chọn học liệu cần thêm");
            dialog.setMode(FileDialog.LOAD);
            dialog.setMultipleMode(false);
            dialog.setFile("*.zip");
            dialog.setVisible(true);
            dialog.setLocationRelativeTo(null);
            ZipperUtils zipper = new ZipperUtils();
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
                                loader = new LoaderProgress(mainFrame, true);
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

//                    private void onEncryptionComplete() {
//                        try {
//                            CryptionFileAndFolder cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
//                            CryptionFileAndFolder.encryptFolder("subject", cf.secretkey, cf.FIXED_IV);
//                            JOptionPane.showMessageDialog(null, "Success!!");
//                        } catch (Exception ex) {
//                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
                        reloadData();
                    }
                };

                worker.execute();
            }
        }
        );
        rightPanel.add(btnImport);

        //LEFT PANEL
        root = new DefaultMutableTreeNode();
        // Khởi tạo cây thư mục tượng trưng cho danh sách môn học 
        tree = new JTree(root);
        //Thay đổi icon cho item
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        Icon iconFolder = new FlatSVGIcon("logos/icons/icon/tree/folder.svg");
        Icon iconTopic = new FlatSVGIcon("logos/icons/icon/tree/topic.svg");

        renderer.setLeafIcon(iconTopic);

        renderer.setOpenIcon(iconFolder);

        renderer.setClosedIcon(iconFolder);

        tree.putClientProperty(FlatClientProperties.STYLE,
                "font: $tree.font");

        /*
         * TODO: Thêm sự kiện cho mỗi tree item -> Chuột phải show ra những option
         *       - Thêm file mới
         *       - Xóa file chỉ định
         * NOTE: Dựa trên cấp độ của cây thư mục mà xác định quy tắc thêm hoặc xóa
         *       1 - Root Tree (Thư mục gốc - Không tác động)
         *       2 - Subject (Môn học)
         *       3 - Topic (Chủ đề)
         *       4 - Material (Loại -> Tranh, ảnh, Video)
         *       5 - File || Folder (Có thể là file hoặc thư mục chưa nhiều file)
         *       6 - File (File nếu có thư mục case 5).
         * HANDLE:
         *   Click Case 2 -> Thêm sách tham khảo
         *   Click Case 4 -> Kiểm tra xem là thư mục loại nào(Image || Video) -> để thêm đúng loại
         *   Click Case 5 -> Kiểm tra xemn là File hay Thư mục 
         *                   + Nếu File -> Xử lí xóa
         *                   + Nếu Folder -> Xử lí thêm và xóa
         *   Click Case 6 -> Xử lí xóa file trong thư mục(Nếu có Group File)
         *
         */
        tree.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e
            ) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        Object[] obj = path.getPath();
                        Object lastItem = path.getLastPathComponent();
                        tree.setSelectionPath(path);
                        JPopupMenu menu = new JPopupMenu();
                        menu.putClientProperty(FlatClientProperties.STYLE,
                                "font: $tree.font");

                        switch (obj.length) {
                            case 2 -> {
                                String subName = obj[1].toString();
                                menu.add(new JMenuItem("Thêm sách tham khảo")).addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        String fileName = FileUtils.insertBook(subName);
                                        if (fileName != null) {
                                            if (fileName.startsWith("New")) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                DefaultMutableTreeNode bookNode = new DefaultMutableTreeNode("Sách tham khảo");
                                                selectedNode.add(bookNode);
                                                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                                                String name = fileName.substring(3, fileName.length() - 1);
                                                TreeUtils.addNode(bookNode, name, tree);
                                            } else {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                DefaultMutableTreeNode bookNode = TreeUtils.findNode(selectedNode, "Sách tham khảo");
                                                if (bookNode != null) {
                                                    TreeUtils.addNode(bookNode, fileName, tree);
                                                }
                                            }
                                        }
                                    }
                                });
                            }

                            case 3 -> {
                                String subName = obj[1].toString();
                                String topName = obj[2].toString();
                                if (lastItem.toString().equalsIgnoreCase("Sách tham khảo")) {
                                    menu.add(new JMenuItem("Thêm sách tham khảo")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent addImage) {
                                            String fileName = FileUtils.insertBook(subName);
                                            if (fileName != null) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                TreeUtils.addNode(selectedNode, fileName, tree);
                                                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                                            }
                                        }
                                    });
                                } else {
                                    menu.add(new JMenuItem("Thêm ảnh")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent addImage) {
                                            String fileName = FileUtils.insertData("Tranh, ảnh", subName, topName);
                                            if (fileName != null) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                DefaultMutableTreeNode imgNode = TreeUtils.findNode(selectedNode, "Tranh, ảnh");
                                                if (imgNode == null) {
                                                    imgNode = new DefaultMutableTreeNode("Tranh, ảnh");
                                                }
                                                selectedNode.add(imgNode);
                                                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                                                TreeUtils.addNode(imgNode, fileName, tree);
                                            }
                                        }
                                    });
                                    menu.add(new JMenuItem("Thêm video")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String fileName = FileUtils.insertData("Video", subName, topName);
                                            if (fileName != null) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                DefaultMutableTreeNode vidNode = TreeUtils.findNode(selectedNode, "Video");
                                                if (vidNode == null) {
                                                    vidNode = new DefaultMutableTreeNode("Video");
                                                }
                                                selectedNode.add(vidNode);
                                                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(selectedNode);
                                                TreeUtils.addNode(vidNode, fileName, tree);
                                            }
                                        }

                                    });
                                }

                            }

                            case 4 -> {
                                String subName = obj[1].toString();
                                String topicName = obj[2].toString();
                                if (lastItem.toString().equalsIgnoreCase("Tranh, ảnh")) {
                                    menu.add(new JMenuItem("Thêm ảnh")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent addImage) {
                                            String fileName = FileUtils.insertData("Tranh, ảnh", subName, topicName);
                                            if (fileName != null) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                TreeUtils.addNode(selectedNode, fileName, tree);
                                            }

                                        }
                                    });
                                } else if (lastItem.toString().equalsIgnoreCase("Video")) {
                                    menu.add(new JMenuItem("Thêm video")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String fileName = FileUtils.insertData("Video", subName, topicName);
                                            if (fileName != null) {

                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                TreeUtils.addNode(selectedNode, fileName, tree);
                                            }
                                        }

                                    });
                                } else if (topicName.equalsIgnoreCase("Sách tham khảo")) {
                                    menu.add(new JMenuItem("Xóa")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent eDel) {
                                            String namePath = "";
                                            for (int i = 0; i < obj.length; i++) {
                                                if (!obj[i].toString().isEmpty()) {
                                                    namePath += obj[i].toString() + (i == obj.length - 1 ? "" : "/");
                                                }
                                            }
                                            boolean success = FileUtils.deleteFile(namePath);
                                            if (success) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                TreeUtils.deleteNode(selectedNode, tree);
                                            }
                                        }

                                    });
                                }
                            }
                            case 5 -> {
                                Path pathCheck = Paths.get(lastItem.toString());
                                if (pathCheck.getFileName() != null && lastItem.toString().contains(".")) {
                                    menu.add(new JMenuItem("Xóa")).addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent eDel) {
                                            String namePath = "";
                                            for (int i = 0; i < obj.length; i++) {
                                                if (!obj[i].toString().isEmpty()) {
                                                    namePath += obj[i].toString() + (i == obj.length - 1 ? "" : "/");
                                                }
                                            }
                                            boolean success = FileUtils.deleteFile(namePath);
                                            if (success) {
                                                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                TreeUtils.deleteNode(selectedNode, tree);
                                            }
                                        }

                                    });
                                } else {
                                    TreePath parent = path.getParentPath();
                                    String parentName = parent.getLastPathComponent().toString();
                                    String subName = obj[1].toString();
                                    String topicName = obj[2].toString();
                                    String materialName = obj[3].toString();
                                    String reparentName = obj[4].toString();
                                    if (parentName.equalsIgnoreCase("Tranh, ảnh")) {
                                        menu.add(new JMenuItem("Thêm ảnh")).addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent addImage) {
                                                String fileName = FileUtils.insertDataNested("Tranh, ảnh", subName, topicName, materialName, reparentName);
                                                if (fileName != null) {
                                                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                    TreeUtils.addNode(selectedNode, fileName, tree);
                                                }
                                            }
                                        });
                                    } else if (parentName.equalsIgnoreCase("Video")) {
                                        menu.add(new JMenuItem("Thêm video")).addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String fileName = FileUtils.insertDataNested("Video", subName, topicName, materialName, reparentName);
                                                if (fileName != null) {
                                                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) lastItem;
                                                    TreeUtils.addNode(selectedNode, fileName, tree);
                                                }
                                            }

                                        });
                                    }
                                }

                            }
                            case 6 -> {
                                menu.add(new JMenuItem("Xóa")).addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent eDel) {
                                        String namePath = "";
                                        for (int i = 0; i < obj.length; i++) {
                                            if (!obj[i].toString().isEmpty()) {
                                                namePath += obj[i].toString() + (i == obj.length - 1 ? "" : "/");
                                            }
                                        }
                                        boolean success = FileUtils.deleteFile(namePath);
                                        if (success) {
                                            // Xác định đường dẫn của nút được chọn
                                            TreePath selectedPath = tree.getSelectionPath();
                                            // Lấy nút được chọn
                                            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

                                            // Lấy nút cha của nút được chọn
                                            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();

                                            // Xóa nút được chọn khỏi nút cha
                                            if (parentNode != null) {
                                                parentNode.remove(selectedNode);

                                                // Thông báo cho cây biết rằng có thay đổi cấu trúc
                                                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                                            }
                                        }
                                    }

                                });
                            }

                        }
                        menu.show(tree, pathBounds.x + 100, pathBounds.y + pathBounds.height - 10);
                    }
                }
            }
        }
        );

        tree.addTreeSelectionListener(
                (e) -> {
                    System.out.println(e.getPath());
                }
        );
        reloadData();

        leftPanel.add(
                new JScrollPane(tree), "grow");

    }

    /*
     * TODO: Hàm xử lí reload dữ liệu và fill lên cây thư mục
     */
    public void reloadData() {
        listSubject.clear();
        listSubject = subjectDAO.select();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        root.removeAllChildren();
        listSubject.forEach(sub
                -> {
            DefaultMutableTreeNode subject
                    = new DefaultMutableTreeNode(sub.getName());

            sub.getTopics().forEach(top -> {
                DefaultMutableTreeNode topic
                        = new DefaultMutableTreeNode(top.getName());

                if (top.getResources() != null) {
                    top.getResources().forEach(res -> {
                        DefaultMutableTreeNode material
                                = new DefaultMutableTreeNode(res.getName());

                        List<Resource> resources = resourceDAO.selectParentId(res.getId());
                        if (!resources.isEmpty()) {
                            resources.forEach(resource -> {
                                DefaultMutableTreeNode item
                                        = new DefaultMutableTreeNode(resource.getName());
                                List<Resource> resourcesItems = resourceDAO.selectParentId(resource.getId());
                                if (!resourcesItems.isEmpty()) {
                                    resourcesItems.forEach(resourceItem -> {
                                        DefaultMutableTreeNode itemSub
                                                = new DefaultMutableTreeNode(resourceItem.getName());

                                        item.add(itemSub);
                                    });
                                }
                                material.add(item);
                            });
                        }
                        topic.add(material);
                    });
                }
                subject.add(topic);
            });

            root.add(subject);
        }
        );
        model.reload(root);
    }

    /*
     * TODO: Hàm xử lí thay đổi content của ứng dụng
     */
    public void setContentPage(JPanel newContentPanel) {
        mainFrame.setContentPane(newContentPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private JPanel leftPanel;
    private JPanel rightPanel;
    private JTree tree;
    private DefaultMutableTreeNode root;
    private JFileChooser chooser;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
