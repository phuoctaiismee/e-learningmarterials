package com.manage.library.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.app.ApplicationMain;
import com.manage.library.app.IndexApp;
import com.manage.library.config.ApplicationConstant;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.interfaces.CardListener;
import com.manage.library.model.Resource;
import com.manage.library.model.ResourceType;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.services.CardRepository;
import com.manage.library.ui.scrollbar.win11.ScrollPaneWin11;
import com.manage.library.utils.FileUtils;
import com.manage.library.utils.StringUtils;
import com.manage.library.utils.XImage;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author PC
 */
public final class LibraryPanel extends JPanel implements CardListener {

    private final ApplicationMain mainFrame;

    private List<Resource> listFiles = new ArrayList<>();

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final TopicDAO topicDAO = new TopicDAO();

    private List<Subject> listSubject = new ArrayList<>();

    public LibraryPanel(JFrame main) {
        this.mainFrame = (ApplicationMain) main;
        mainFrame.initDrawer(mainFrame);
//        GlassPanePopup.install(main);
        initComponents();
        initialLayout();
    }

    /* 
     * TODO:Hàm chuyển đổi Main Content (Nội dung chính của màn hình) --> Navigation
     * 
     */
    public void setContentPage(JPanel newContentPanel) {
        mainFrame.setContentPane(newContentPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /* 
     * TODO: Hàm Khỏi tạo và fill Data lên giao diện người dùng
     * 
     */
    public void initialLayout() {
        setLayout(new MigLayout("fill, gapx 10, gapy 20,insets 20", "[center]", "[center]"));

        JPanel titlePanel = new JPanel(new MigLayout("fill"));

        //TITLE 
        //================================================
        JLabel title = new JLabel("Học liệu điện tử");
        title.setFont(UIManager.getFont("h4.font"));
        title.setForeground(UIManager.getColor("text.primary.color"));
        titlePanel.add(title, "align left, hmax pref+10");

        //BACK BUTTON
        //================================================
        btnBack = new JButton("Back");
        btnBack.setToolTipText("Quay lại trang chủ");
        btnBack.addActionListener(ac -> {
            setContentPage(new IndexApp(mainFrame));
        });
        titlePanel.add(btnBack, "align right");
        add(titlePanel, "span, growx");

        //SET UP CONTENT PANEL
        //================================================
        //LeftPanel -> Combobox Subject and List Topics
        leftPanel = new JPanel(new MigLayout("fill, insets 20", "[center]", "[center]"));
        //ScrollPanel 
        scroll = new ScrollPaneWin11();
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //CenterPanel -> Content Resources
        centerPanel = new JPanel(new MigLayout("wrap 4,  gap 10, insets 20", "[left]", "[top]"));
        scroll.setViewportView(centerPanel);
        //RightPanel -> Type Resources
        rightPanel = new JPanel(new MigLayout("fillx, insets 10", "[left]", "[top]"));

        leftPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");
        centerPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");
        rightPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%);"
                + "arc: 20");
        add(leftPanel, "grow, w 25%, wmax 25%, h 100%");
        add(scroll, "grow, w 60%, h 100%");
        add(rightPanel, "grow, w 14%, h 100%");

        //INIT LEFT PANEL
        //================================================
        JLabel lblSubject = new JLabel(" Môn học: ");
        lblSubject.putClientProperty(FlatClientProperties.STYLE, "font: $label.font");
        cboSubject = new JComboBox(); //Khởi tạo combobox môn học và danh sách chủ đề
        JLabel lblTopic = new JLabel(" Chủ đề: ");
        lblTopic.putClientProperty(FlatClientProperties.STYLE, "font: $label.font");
        listTopicRender = new ListTopic<>();
        listTopicRender.setSelectedColor(UIManager.getColor("selected.color.light"));
//        listTopicRender.setForeground(UIManager.getColor("text.white.color"));
        DefaultComboBoxModel<String> cboModel = new DefaultComboBoxModel<>();
        cboModel.removeAllElements();

        listSubject = subjectDAO.select(); //Lấy chủ đề từ server

        if (!listSubject.isEmpty()) { //Kiểm tra và đổ dữ liệu
            cboModel.addElement("Chọn môn học");
            listSubject.forEach(item -> cboModel.addElement(item.getName()));
        } else {
            cboModel.addElement("Thư viện rỗng!");
        }

        cboSubject.setModel(cboModel);
        cboSubject.setSelectedIndex(0);

        //Thêm sự kiện khi combobox thay đổi -> Render lai chủ đề của từng môn học
        cboSubject.addActionListener((e) -> {
            handleChangeTopic();
            if (cboSubject.getSelectedIndex() != 0 && cboSubject.getSelectedIndex() != -1) {
                int indexSelectedTopic = listTopicRender.getSelectedIndex();
                Item item = listTopicRender.getItem(indexSelectedTopic);
                try {
                    handleSelectedTopic(item.getText());
                } catch (Exception ex) {
                    Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Gọi hàm lần đầu để kiểm tra và show thông báo
        handleChangeTopic();
        checkAndShowNoti();
        
        leftPanel.add(lblSubject, "wrap, align left");
        leftPanel.add(cboSubject, "span, w 90%, wmax 90%");
        leftPanel.add(lblTopic, "wrap, align left");
        leftPanel.add(listTopicRender, "dock center");

        //INIT RIGHT PANEL
        //================================================
        JLabel titleType = new JLabel("Loại tài liệu"); //Khởi tạo danh sách các loại tài liệu
        titleType.putClientProperty(FlatClientProperties.STYLE, "font: $label.font");
        rightPanel.add(titleType, "wrap");
        groupType = new ButtonGroup();
        String[] type = {"Hình ảnh", "Video"};

        boxes = new JRadioButton[type.length]; //  Dùng vòng lặp để tạo 2 loại tài liệu hình anh và video
        for (int i = 0; i < boxes.length; i++) {

            boxes[i] = new JRadioButton(type[i]);
            boxes[i].setActionCommand(type[i]);
            groupType.add(boxes[i]);
            rightPanel.add(boxes[i], "wrap");
            boxes[0].setSelected(true);

            //Gán sự kiện vào mỗi radio button
            boxes[i].addActionListener((e) -> {
                if (cboSubject.getSelectedIndex() == 0 || cboSubject.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học trước khi thao tác!");
                    boxes[0].setSelected(true);
                    return;
                }
                if (chkDoc.isSelected()) {
                    chkDoc.setSelected(false);
                    listTopicRender.setSelectedIndex(0);
                }

                int index = listTopicRender.getSelectedIndex();
                if (index != -1) {
                    Item item = listTopicRender.getItem(index);
                    try {
                        handleSelectedTopic(item.getText());
                    } catch (Exception ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }

        //Loai tài liệu riêng biệt --> Sách tham khảo
        JLabel ortherType = new JLabel("Khác");
        ortherType.putClientProperty(FlatClientProperties.STYLE, "font: $label.font");
        rightPanel.add(ortherType, "wrap");
        chkDoc = new JCheckBox("Sách tham khảo");
        chkDoc.addActionListener((e) -> {
            if (cboSubject.getSelectedIndex() == 0 || cboSubject.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học trước khi thao tác!");
                chkDoc.setSelected(false);
                return;
            }
            if (chkDoc.isSelected()) {
                for (JRadioButton rdo : boxes) {
                    rdo.setSelected(false);
                    groupType.clearSelection();
                }

                listFiles.clear();
                String selectedSubject = cboSubject.getSelectedItem().toString();
                Subject sub = subjectDAO.selectName(selectedSubject);
                int typeFile = ApplicationConstant.ResourceType.DOC;
                if (sub != null) {
                    Topic top = handleSelectedDocument(sub);
                    if (top != null) {
                        top.getResources().stream()
                                .filter(rs -> rs.getTypeId() == typeFile)
                                .forEach(listFiles::add);
                    }
                }
                try {
                    renderCards();

                    if (listFiles.isEmpty()) {
                        notFound();
                    } else {
                        renderCards();
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else {
                chkDoc.setSelected(true);
            }
        });
        rightPanel.add(chkDoc);

    }

    private Topic handleSelectedDocument(Subject subject) {
        Topic topicSelected = topicDAO.selectName("Sách tham khảo", subject.getId());
        listTopicRender.clearSelection();
        listTopicRender.setSelectedIndex(-1);
        return topicSelected;
    }

    /*
     * TODO: Phương thức dùng để kiểm tra giao diện lần đẩu được render 
     *      (Kiểm tra người dùng có chọn vào môn học nào hay chưa để hiển thị thông báo chào mừng)
     */
    private void checkAndShowNoti() {
        if (listFiles.isEmpty() || listFiles.size() <= 0) {
            centerPanel.removeAll();
            centerPanel.revalidate();
            centerPanel.repaint();

            JPanel notiPanel = new JPanel(new MigLayout("fill, insets 100", "[center]", "[center]"));

            JLabel tit = new JLabel("Chào mừng bạn đến với kho học liệu");
            tit.setFont(UIManager.getFont("h5.font"));
            notiPanel.add(tit, "wrap");

            notiPanel.putClientProperty(FlatClientProperties.STYLE, ""
                    + "[light]background:darken(@background,0%);"
                    + "[dark]background:lighten(@background,0%);"
                    + "arc: 20");
            JLabel lbl = new JLabel();
            lbl.setIcon(XImage.read("wel.png"));
            notiPanel.add(lbl, "wrap");

            JLabel des = new JLabel("(Vui lòng chọn môn học để bắt đầu !!!)");
            des.setFont(UIManager.getFont("medium.font"));
            des.setForeground(UIManager.getColor("text.secondary.color"));

            notiPanel.add(des);

            centerPanel.add(notiPanel, "dock center");
        }
    }

    /*
     * TODO: Phương thức dùng để kiểm tra chủ đề ABC có loại học liệu XYZ hay không 
     *      (VD: Kiểm tra trong CHỦ ĐỀ 1 có tồn tại thư mục HÌNH ẢNH hoặc File Hình ảnh hay không
     *       - Nếu không có --> Hiển thị thông báo "Không tìm thấy học liệu
     *       - Ngược lại nếu có --> Xử lí hiển thị danh sách học liệu )
     */
    private void notFound() {
        if (listFiles.isEmpty() || listFiles.size() <= 0) {
            centerPanel.removeAll();
            centerPanel.revalidate();
            centerPanel.repaint();

            JPanel notiPanel = new JPanel(new MigLayout("fillx, insets 50", "[center]", "[center]"));

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
            String typeSelected;
            if (chkDoc.isSelected()) {
                typeSelected = chkDoc.getText();
            } else {
                typeSelected = groupType.getSelection().getActionCommand();
            }

            JLabel des = new JLabel("<html><div style='text-align: center;'>" + "Chủ đề này chưa có học liệu " + "<u style='color:red'>" + typeSelected + "</u>" + ".  <br> Vui lòng tham khảo những chủ đề khác " + " <br> <i color='black'><font size=4>hoặc</font></i>" + "</div></html>");
            JButton btnAddNew = new JButton("Thêm file mới", new FlatSVGIcon("logos/icons/icon/addnew.svg"));
            des.setFont(UIManager.getFont("medium.font"));
            des.setForeground(UIManager.getColor("text.secondary.color"));

            btnAddNew.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chkDoc.isSelected()) {
                        handleInsertBook();
                        System.out.println("Dô đây");
                    } else {
                        handleInsertData();
                    }
                }
            });

            notiPanel.add(des, "wrap");
            notiPanel.add(btnAddNew, "wrap");

            centerPanel.add(notiPanel, "dock center");
        }
    }

    /* 
     * TODO: Hàm xử lý sự thay đổi của Combobox Môn học -> Render danh sách chủ đề của môn học đó
     * - Khi người dùng chọn môn môn học trong combobox -> Select danh sách những chủ đề bên trong môn học
     * - Fill danh sách lên list
     * 
     */
    public void handleChangeTopic() {
        List<Item> fileItem = new ArrayList<>();
        String selectedSubject = cboSubject.getSelectedItem().toString();
        DefaultListModel listModel = (DefaultListModel) listTopicRender.getModel();
        listModel.removeAllElements();
        listFiles.clear();

        int indexSub = cboSubject.getSelectedIndex();
        if (indexSub == -1 || indexSub == 0) {
            Item item = new Item("(Vui lòng chọn môn học) !!!", new FlatSVGIcon("logos/icons/icon/error.svg"));
            listModel.addElement(item);
            listTopicRender.setEnabled(false);
            checkAndShowNoti();
        } else {
            listTopicRender.setEnabled(true);
            listSubject.forEach(subject -> {
                if (subject.getName().equalsIgnoreCase(selectedSubject)) {
                    subject.getTopics()
                            .stream()
                            .filter(top -> !top.getName().equalsIgnoreCase("Sách tham khảo"))
                            .forEach(topic -> {
                                fileItem.add(new Item(topic.getName(), new FlatSVGIcon("logos/icons/icon/book.svg")));
                            });
                }
            });

            if (!fileItem.isEmpty() || fileItem.size() < 0) {
                fileItem.forEach(item -> listModel.addElement(item));
                listTopicRender.setSelectedIndex(0);
                listTopicRender.addListSelectionListener((e) -> {
                    if (e.getValueIsAdjusting()) {
                        int index = listTopicRender.getSelectedIndex();
                        if (index != -1) {
                            Item item = listTopicRender.getItem(index);
                            try {
                                handleSelectedTopic(item.getText());
                            } catch (Exception ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "NONEEEE");
            }
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
//        List<Card> listPanel = generateCard();
        CardRepository cardRenderer = CardRepository.getInstance();
        List<Card> listPanel = cardRenderer.renderCards(listFiles);
        centerPanel.removeAll();
        centerPanel.revalidate();
        centerPanel.repaint();
//        scroll.getVerticalScrollBar().setValue(0);
        for (Card card : listPanel) {
            card.addDeletionListener(this);
            centerPanel.add(card, "h 230, hmax 250, w 164,  wmax 200");
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
        centerPanel.add(pnlAdd, "h 230, hmax 250, w 164,  wmax 200");

        pnlAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseListener listener;
        listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (chkDoc.isSelected()) {
                    handleInsertBook();
                } else {
                    handleInsertData();
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
//        scroll.getViewport().setViewPosition(new Point(0,0));
    }

    /* 
     * TODO: Hàm xử lý khi người dùng chọn vào chủ đề
     * - Khi chọn vào chủ đề -> Select ra những học liệu trong chủ đề đó
     * - Kiểm tra cấp độ thư mục (Xem có lồng cấp 4 5 cấp hay không)
     *  + Nếu có -> hiển thị Group thư mục
     *  + Nếu không -> Hiện thị học liệu trực tiếp
     * 
     * - Gọi hàm Render để fill lên giao diện và kiểm tra
     */
    private void handleSelectedTopic(String name) throws Exception {
        if (chkDoc.isSelected()) {
            boxes[0].setSelected(true);
            chkDoc.setSelected(false);
        }
        listFiles.clear();
        String selectedSubject = cboSubject.getSelectedItem().toString();
        Subject sub = subjectDAO.selectName(selectedSubject);
        Topic topicSelected = topicDAO.selectName(name, sub.getId());
        if (topicSelected != null) {
            ResourceType rsType = new ResourceType();
            for (JRadioButton chk : boxes) {
                if (chk.isSelected()) {
                    switch (chk.getText()) {
                        case "Hình ảnh" -> {
                            rsType.setId(2);
                            rsType.setName("Tranh, ảnh");
                        }
                        case "Video" -> {
                            rsType.setId(3);
                            rsType.setName("Video");
                        }
                        default -> {
                        }
                    }
                }
            }

            //Lấy thư mục tổng (VIDEO, TRANH ẢNH, TÀI LIỆU) gốc
            Resource rsou = resourceDAO.selectNameandTopicId(rsType.getName(), topicSelected.getId());
            if (rsou != null) {
                topicSelected.getResources().stream()
                        .filter(rs -> rs.getTypeId() == rsType.getId() || rs.getParentId() == rsou.getId())
                        .forEach(listFiles::add);
            } else {
                topicSelected.getResources().stream()
                        .filter(rs -> rs.getTypeId() == rsType.getId())
                        .forEach(listFiles::add);
            }

        }

        renderCards();
        if (listFiles.isEmpty() || listFiles.size() <= 0) {
            notFound();
        } else {
            renderCards();
        }
    }

    public void handleInsertData() {
        if (cboSubject.getSelectedIndex() != -1 && cboSubject.getSelectedIndex() != 0) {
            String subString = cboSubject.getSelectedItem().toString();
            Subject subject = subjectDAO.selectName(subString);
            if (subject != null) {
                String topString = null;

                int index = listTopicRender.getSelectedIndex();
                if (index != -1) {
                    Item item = listTopicRender.getItem(index);
                    topString = item.getText();
                }

                Topic topic = topicDAO.selectName(topString, subject.getId());
                if (topic != null) {
                    Resource material = null;
                    String type = null;
                    for (JRadioButton chk : boxes) {
                        if (chk.isSelected()) {
                            switch (chk.getText()) {
                                case "Hình ảnh" -> {
                                    type = "Tranh, ảnh";

                                }
                                case "Video" -> {
                                    type = "Video";

                                }
                                default -> {
                                    type = "Tranh, ảnh";
                                }
                            }
                        }
                    }
                    material = resourceDAO.selectNameandTopicId(type, topic.getId());
                    if (material == null) {
                        Resource materialAddNew = new Resource(type, null, ApplicationConstant.ResourceType.FOLDER, topic.getId());
                        resourceDAO.insert(materialAddNew);

                        //Lấy lên lại để lấy ID;
                        Resource materialNew = resourceDAO.selectNameandTopicId(type, topic.getId());
                        if (materialNew != null) {
                            material = materialNew;
                        }
                    }
                    String name = FileUtils.insertData(type, subject.getName(), topic.getName());
                    if (name != null) {
                        Resource resNew = resourceDAO.selectNameandParentId(name, material.getId());
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

            }
        }
    }

    public void handleInsertBook() {
        if (cboSubject.getSelectedIndex() != -1 && cboSubject.getSelectedIndex() != 0) {
            String subString = cboSubject.getSelectedItem().toString();
            Subject subject = subjectDAO.selectName(subString);
            if (subject != null) {
                Topic topBook = topicDAO.selectName("Sách tham khảo", subject.getId());
                if (topBook != null) {
                    String name = FileUtils.insertBook(subject.getName());
                    if (name != null) {
                        Resource resNew = resourceDAO.selectNameandTopicId(name, topBook.getId());
                        if (resNew != null) {
                            listFiles.add(resNew);
                            try {
                                renderCards();

                                JOptionPane.showMessageDialog(null, "Sách đã được thêm thành công");

                            } catch (Exception ex) {
                                Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } else {
                    Topic topNew = new Topic("Sách tham khảo", subject.getId());
                    topicDAO.insert(topNew);

                    //Lấy lên lại
                    Topic topAddNew = topicDAO.selectName("Sách tham khảo", subject.getId());
                    if (topAddNew != null) {
                        String name = FileUtils.insertBook(subject.getName());
                        if (name != null) {
                            Resource resNew = resourceDAO.selectNameandTopicId(name, topAddNew.getId());
                            if (resNew != null) {
                                listFiles.add(resNew);
                                try {
                                    renderCards();

                                    JOptionPane.showMessageDialog(null, "Sách đã được thêm thành công");

                                } catch (Exception ex) {
                                    Logger.getLogger(LibraryPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JButton btnBack;
    private JComboBox cboSubject;
    private com.manage.library.view.ListTopic<String> listTopicRender;
    private JCheckBox chkDoc;
    private ButtonGroup groupType;
    private JRadioButton[] boxes;
    private com.manage.library.ui.scrollbar.win11.ScrollPaneWin11 scroll;

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
                if (listFiles.isEmpty() || listFiles.size() <= 0) {
                    notFound();
                } else {
                    renderCards();
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

}
