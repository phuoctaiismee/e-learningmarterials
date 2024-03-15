package com.manage.library.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.interfaces.CardListener;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.services.CardRepository;
import com.manage.library.utils.FileUtils;
import com.manage.library.utils.StringUtils;
import com.manage.library.utils.XImage;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author PC
 */
public class NestedFolder extends javax.swing.JFrame implements CardListener {

    private ResourceDAO resourceDAO = new ResourceDAO();
    private List<Resource> listFiles = new ArrayList<>();
    private Subject subject;
    private Topic topic;
    private Resource material;
    private Resource folder;

    public NestedFolder() {

    }

    public NestedFolder(List<Resource> list, Subject subject, Topic topic, Resource material, Resource folder) throws Exception {
        this.listFiles = list;
        this.subject = subject;
        this.topic = topic;
        this.material = material;
        this.folder = folder;

        initComponents();
        this.setLocationRelativeTo(null);
        this.setIconImage(XImage.read("icons/folder.png").getImage());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        scrollPaneWin111.setViewportView(mainNested);
        scrollPaneWin111.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainNested.setLayout(new MigLayout("insets 20, gap 10, wrap 4", "[left]", "[top]"));

        mainNested.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");

        if (!listFiles.isEmpty()) {
            renderCards();
        } else {
            notFound();
        }

    }

    /* 
     * TODO: Hàm khởi tạo danh sách các Card học liệu
     * - Khởi tạo và fill dữ liệu của từng học liệu lên Card
     * RETURN: Trả về một danh sách Card (Component)
     * 
     */
    public List<Card> generateCard() throws Exception {
        List<Card> fsp = new ArrayList<>();
        for (int i = 0; i < listFiles.size(); i++) {
            Resource file = listFiles.get(i);
            Card card = new Card();
            card.setFile(file);
            card.fillData();
            card.addDeletionListener(this);
            fsp.add(card);
        }
        return fsp;
    }

    /* 
     * TODO: Hàm xử lí việc render danh sách Card từ trên hiển thị trên giao diện
     *
     */
    public void renderCards() throws Exception {
        List<Card> listPanel = generateCard();
        mainNested.removeAll();
        mainNested.revalidate();
        mainNested.repaint();
        for (Card card : listPanel) {
            mainNested.add(card, "h 230, hmax 250, w 164,  wmax 200");

        }

        //Nút thêm mới học liệu
        JPanel pnlAdd = new JPanel(new MigLayout("fill, insets 20", "[center]", "[center]"));
        pnlAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlAdd.setBackground(Color.white);
        pnlAdd.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20;");

        JPanel mainAdd = new JPanel(new MigLayout("fill, insets 20", "[center]", "[center]"));
        mainAdd.setOpaque(false);

        JLabel btnAdd = new JLabel(new FlatSVGIcon("logos/icons/icon/add.svg"));
        JLabel lblAdd = new JLabel("Thêm mới");
        lblAdd.putClientProperty(FlatClientProperties.STYLE,
                "font: $tree.font;");
        btnAdd.setToolTipText("Thêm mới");

        mainAdd.add(btnAdd, "wrap");
        mainAdd.add(lblAdd);

        pnlAdd.add(mainAdd);
        mainNested.add(pnlAdd, "h 230, hmax 250, w 164,  wmax 200");

        pnlAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseListener listener;
        listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                String type = material.getName();
                String name = FileUtils.insertDataNested(type, subject.getName(), topic.getName(), material.getName(), folder.getName());
                if (name != null) {
                    Resource resNew = resourceDAO.selectNameandParentId(name, folder.getId());
                    if (resNew != null) {
                        listFiles.add(resNew);
                        try {
                            renderCards();
                            if (type.equalsIgnoreCase("Video")) {
                                JOptionPane.showMessageDialog(null, "Video đã được thêm thành công");
                            } else {
                                JOptionPane.showMessageDialog(null, "Hình ảnh đã được thêm thành công");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };

        pnlAdd.addMouseListener(listener);
        mainAdd.addMouseListener(listener);
        btnAdd.addMouseListener(listener);
        lblAdd.addMouseListener(listener);

        if (listPanel == null || listPanel.size() <= 0) {
            notFound();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPaneWin111 = new com.manage.library.ui.scrollbar.win11.ScrollPaneWin11();
        mainNested = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(740, 600));
        setResizable(false);

        scrollPaneWin111.setBorder(null);

        javax.swing.GroupLayout mainNestedLayout = new javax.swing.GroupLayout(mainNested);
        mainNested.setLayout(mainNestedLayout);
        mainNestedLayout.setHorizontalGroup(
            mainNestedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        mainNestedLayout.setVerticalGroup(
            mainNestedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        scrollPaneWin111.setViewportView(mainNested);

        getContentPane().add(scrollPaneWin111, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new NestedFolder().setVisible(true);
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainNested;
    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scrollPaneWin111;
    // End of variables declaration//GEN-END:variables
@Override
    public void onDeleteCard(Integer id) {
        try {
            Resource reDel = resourceDAO.selectID(id);
            if (reDel != null) {
                // Xóa tệp từ hệ thống tệp
                String filePath = "subject" + File.separator + reDel.getUrl().replace("/", File.separator);
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);

                String fileThumbnail = StringUtils.addThumbnail(filePath);
                Path pathThumbnail = Paths.get(fileThumbnail);
                Files.deleteIfExists(pathThumbnail);

                //Xóa trong Database
                resourceDAO.delete(id);
                listFiles.removeIf(file -> file.getId() == (id));
                if (!listFiles.isEmpty()) {
                    renderCards();
                } else {
                    notFound();
                }
                JOptionPane.showMessageDialog(this, "Đã xóa " + reDel.getName() + " ra khỏi học liệu");
            } else {
                System.out.println("NULLL");
            }
        } catch (Exception ex) {
            Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onUpdateCard(Integer id, String name) {
        try {
            // Tìm tệp cần thay đổi thông qua ID
            Resource resource = resourceDAO.selectID(id);
            if (resource != null) {
                // Xác định đường dẫn đến tệp
                String filePath = "subject" + File.separator + resource.getUrl().replace("/", File.separator);
                Path path = Paths.get(filePath);

                String fileThumbnail = StringUtils.addThumbnail(filePath);
                Path pathThumbnail = Paths.get(fileThumbnail);

                // Lấy phần mở rộng của tên file gốc
                String extension = FilenameUtils.getExtension(resource.getName());

                // Tạo tên mới với phần tên và phần mở rộng giữ nguyên, đuôi là .encrypted
                String newName = name + "." + extension;
                String newThumbname = name + "_thumbnail.jpg.encrypted";

                String url = resource.getUrl().substring(0, resource.getUrl().lastIndexOf("/") + 1);
                url += newName + ".encrypted";
                System.out.println("URL: " + url);
                // Thực hiện thay đổi tên tệp
                Files.move(path, path.resolveSibling(newName + ".encrypted"));
                Files.move(pathThumbnail, pathThumbnail.resolveSibling(newThumbname));
                // Cập nhật tên tệp trong cơ sở dữ liệu
                resource.setName(newName); // Cập nhật tên mới cho tệp
                resource.setUrl(url);
                resourceDAO.update(resource);

//                // Cập nhật danh sách tệp và giao diện
                for (Resource file : listFiles) {
                    if (file.getId() == id) {
                        file.setName(newName);
                        file.setUrl(url);
                        break;
                    }
                }
                CardRepository.getInstance().updateFiles(listFiles);
                renderCards();
                if (listFiles.isEmpty()) {
                    notFound();
                }

                JOptionPane.showMessageDialog(this, "Đã thay đổi tên tệp thành công");
            } else {
                System.out.println("Không tìm thấy tệp có ID: " + id);
            }
        } catch (IOException ex) {
            Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void notFound() {
        if (listFiles.isEmpty() || listFiles.size() <= 0) {
            mainNested.removeAll();
            mainNested.revalidate();
            mainNested.repaint();

            JPanel notiPanel = new JPanel(new MigLayout("fill, insets 100", "[center]", "[center]"));

            JLabel tit = new JLabel("Không tìm thấy học liệu!!!");
            tit.setFont(UIManager.getFont("h5.font"));
            notiPanel.add(tit, "wrap");

            notiPanel.putClientProperty(FlatClientProperties.STYLE, ""
                    + "[light]background:darken(@background,0%);"
                    + "[dark]background:lighten(@background,0%);"
                    + "arc: 20");
            JLabel lbl = new JLabel();
            lbl.setIcon(XImage.read("not-found.png"));
            notiPanel.add(lbl, "wrap");

            JLabel des = new JLabel("<html><div style='text-align: center;'>" + "Thư mục này chưa có học liệu" + ".  <br> Vui lòng tham khảo những chủ đề khác !!!" + "</div></html>");
            des.setFont(UIManager.getFont("medium.font"));
            des.setForeground(UIManager.getColor("text.secondary.color"));

            notiPanel.add(des);

            
            
            
            
            
            mainNested.add(notiPanel, "dock center");
        }
    }
}
