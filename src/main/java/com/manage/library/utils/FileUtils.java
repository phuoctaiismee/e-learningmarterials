package com.manage.library.utils;

import com.manage.library.config.ApplicationConstant;
import com.manage.library.config.CryptionFileAndFolder;
import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import com.manage.library.view.ManagePanel;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jcodec.api.JCodecException;

public class FileUtils {

    private static JFileChooser chooser = null;
    private final static SubjectDAO subjectDAO = new SubjectDAO();
    private final static TopicDAO topicDAO = new TopicDAO();
    private final static ResourceDAO resourceDAO = new ResourceDAO();

    /*
     * TODO: Hàm xử lí thêm dữ liệu (File Resource) (Không có nhóm file)
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> Material -> <(Resource)> )
     * PARAM:
     *  @type: Kiểu file sẽ được thêm vào (Hình ảnh || Video)
     *  @subjectName: tên của môn học
     *  @topicName: tên của chủ đề
     * HANDLE:
     *   - Dựa vào type lọc ra những file có đuôi là loại tài liệu đó
     *   - Chọn file
     *   - Lấy subject dựa vào subjectName
     *   - Lấy topic dựa vào topicName
     *   - Tìm ra material dựa trên topic và name
     *   - Thêm file vào thư mục 
     *   - Reload data
     *   - Show notification
     */
    public static String insertData(String type, String subjectName, String topicName) {
        File myFilename;
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        FileFilter videoFilter = new FileNameExtensionFilter("Video files", "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm");
        chooser = new JFileChooser();
        if (type.equalsIgnoreCase("Video")) {
            chooser.setFileFilter(videoFilter);
        } else {
            chooser.setFileFilter(imageFilter);
        }
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();
            Subject subject = subjectDAO.selectName(subjectName);
            if (subject != null) {
                Topic top = subject.getTopics()
                        .stream()
                        .filter(sub -> sub.getName().equalsIgnoreCase(topicName))
                        .findFirst()
                        .orElse(null);

                if (top != null) {
                    Resource material;
                    if (type.equalsIgnoreCase("Video")) {
                        material = resourceDAO.selectNameandTopicId("Video", top.getId());
                    } else {
                        material = resourceDAO.selectNameandTopicId("Tranh, ảnh", top.getId());
                    }
                    if (material != null) {
                        //THÊM FILE VÀO THƯ MỤC 
                        String path = "subject" + File.separator + subjectName + File.separator + topicName + File.separator + material.getName();
                        System.out.println("path: " + path);
                        File directory = new File(path);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        File destinationFile = new File(directory, myFilename.getName());
                        CryptionFileAndFolder cf;

                        try {
                            Files.copy(myFilename.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            if (type.equalsIgnoreCase("Video")) {
                                try {
                                    BufferedImage thumbnail = VideoThumbnail.getThumbnail(destinationFile.getAbsolutePath(), 410);
                                    String thumbnailFileName = myFilename.getName().substring(0, myFilename.getName().lastIndexOf('.')) + "_thumbnail.jpg";
                                    String thumbnailPath = path + File.separator + thumbnailFileName;
                                    // Save thumbnail to the same directory
                                    File thumbnailFile = new File(thumbnailPath);
                                    ImageIO.write(thumbnail, "jpg", thumbnailFile);
                                } catch (IOException | JCodecException ex) {
                                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                try {
                                    BufferedImage thumbnail = ThumbnailGenerator.createThumbnail(destinationFile.getAbsolutePath(), 300, 300);
                                    String thumbnailFileName = myFilename.getName().substring(0, myFilename.getName().lastIndexOf('.')) + "_thumbnail.jpg";
                                    String thumbnailPath = path + File.separator + thumbnailFileName;
                                    // Save thumbnail to the same directory
                                    File thumbnailFile = new File(thumbnailPath);
                                    ImageIO.write(thumbnail, "jpg", thumbnailFile);
                                } catch (IOException ex) {
                                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
                            CryptionFileAndFolder.encryptFolder(directory.getAbsolutePath(), cf.secretkey, cf.FIXED_IV);
                        } catch (IOException e) {
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvalidKeySpecException ex) {
                            Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //CHECK EXISTED
                        if (resourceDAO.selectNameandParentId(myFilename.getName(), material.getId()) != null) {
                            JOptionPane.showMessageDialog(null, "File này đã tồn tại trong học liệu!!\nVui lòng kiểm tra lại");
                            return null;
                        }

                        //INSERT DATABASE
                        Resource newRes = new Resource();
                        String url = subjectName + "/" + topicName + "/" + material.getName() + "/" + myFilename.getName() + ".encrypted";
                        newRes.setName(myFilename.getName());
                        if (type.equalsIgnoreCase("Video")) {
                            newRes.setTypeId(ApplicationConstant.ResourceType.VIDEO);
                        } else {
                            newRes.setTypeId(ApplicationConstant.ResourceType.IMAGE);
                        }
                        newRes.setParentId(material.getId());
                        newRes.setUrl(url);

                        resourceDAO.insert(newRes);

//                        new Thread(() -> {
//                            reloadData();
//                        }).start();
                        return myFilename.getName();
                    } else {
                        //Nếu folder material chưa tồn tại tạo folder dùng đệ quy để gọi lại hàm
                        Resource materialNew = new Resource(type, null, ApplicationConstant.ResourceType.FOLDER, top.getId());
                        resourceDAO.insert(materialNew);

                        Resource materialAddNew = resourceDAO.selectNameandTopicId(materialNew.getName(), top.getId());
                        if (materialAddNew != null) {
                            String path = "subject" + File.separator + subjectName + File.separator + topicName + File.separator + materialAddNew.getName();
                            System.out.println("path: " + path);
                            File directory = new File(path);
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }
                            File destinationFile = new File(directory, myFilename.getName());
                            CryptionFileAndFolder cf;

                            try {
                                Files.copy(myFilename.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
                                CryptionFileAndFolder.encryptFolder(directory.getAbsolutePath(), cf.secretkey, cf.FIXED_IV);
                            } catch (IOException e) {
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvalidKeySpecException ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //CHECK EXISTED
                            if (resourceDAO.selectNameandParentId(myFilename.getName(), materialAddNew.getId()) != null) {
                                JOptionPane.showMessageDialog(null, "File này đã tồn tại trong học liệu!!\nVui lòng kiểm tra lại");
                                return null;
                            }

                            //INSERT DATABASE
                            Resource newRes = new Resource();
                            String url = subjectName + "/" + topicName + "/" + materialAddNew.getName() + "/" + myFilename.getName() + ".encrypted";
                            newRes.setName(myFilename.getName());
                            if (type.equalsIgnoreCase("Video")) {
                                newRes.setTypeId(ApplicationConstant.ResourceType.VIDEO);
                            } else {
                                newRes.setTypeId(ApplicationConstant.ResourceType.IMAGE);
                            }
                            newRes.setParentId(materialAddNew.getId());
                            newRes.setUrl(url);

                            resourceDAO.insert(newRes);

//                        new Thread(() -> {
//                            reloadData();
//                        }).start();                          
                            return myFilename.getName();
                        }
                    }
                }

            }
        }
        return null;
    }

    /*
     * TODO: Hàm xử lí thêm dữ liệu (File Resource) (Có nhóm file)
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> Material -> Parent Resource -> <(Resource)> )
     * PARAM:
     *  @type: Kiểu file sẽ được thêm vào (Hình ảnh || Video)
     *  @subjectName: tên của môn học
     *  @topicName: tên của chủ đề
     *  @materialName: tên của thư mục loại file (Tranh, ảnh || Video)
     *  @parentName: tên của thư mục Group File
     * HANDLE:
     *   - Dựa vào type lọc ra những file có đuôi là loại tài liệu đó
     *   - Chọn file
     *   - Lấy subject dựa vào subjectName
     *   - Lấy topic dựa vào topicName
     *   - Lấy material dựa vào materialName
     *   - Tìm ra parent dựa trên parentName và materialId
     *   - Thêm file vào thư mục 
     *   - Reload data
     *   - Show notification
     */
    public static String insertDataNested(String type, String subjectName, String topicName, String materialName, String parentName) {
        File myFilename;
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        FileFilter videoFilter = new FileNameExtensionFilter("Video files", "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm");
        chooser = new JFileChooser();
        if (type.equalsIgnoreCase("Video")) {
            chooser.setFileFilter(videoFilter);
        } else {
            chooser.setFileFilter(imageFilter);
        }
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();
            Subject subject = subjectDAO.selectName(subjectName);
            if (subject != null) {
                Topic top = subject.getTopics()
                        .stream()
                        .filter(sub -> sub.getName().equalsIgnoreCase(topicName))
                        .findFirst()
                        .orElse(null);

                if (top != null) {
                    Resource material = top.getResources()
                            .stream()
                            .filter(re -> re.getName().equalsIgnoreCase(materialName))
                            .findFirst()
                            .orElse(null);
                    if (material != null) {
                        Resource parentFolder = resourceDAO.selectNameandParentId(parentName, material.getId());
                        if (parentFolder != null) {
                            //THÊM FILE VÀO THƯ MỤC HÌNH ẢNH
                            String path = "subject" + File.separator + subjectName + File.separator + topicName + File.separator + material.getName() + File.separator + parentFolder.getName();
                            System.out.println("path: " + path);
                            File directory = new File(path);
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }
                            File destinationFile = new File(directory, myFilename.getName());
                            CryptionFileAndFolder cf;

                            try {
                                Files.copy(myFilename.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                if (type.equalsIgnoreCase("Video")) {
                                    try {
                                        BufferedImage thumbnail = VideoThumbnail.getThumbnail(destinationFile.getAbsolutePath(), 410);
                                        String thumbnailFileName = myFilename.getName().substring(0, myFilename.getName().lastIndexOf('.')) + "_thumbnail.jpg";
                                        String thumbnailPath = path + File.separator + thumbnailFileName;
                                        // Save thumbnail to the same directory
                                        File thumbnailFile = new File(thumbnailPath);
                                        ImageIO.write(thumbnail, "jpg", thumbnailFile);
                                    } catch (IOException | JCodecException ex) {
                                        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    try {
                                        BufferedImage thumbnail = ThumbnailGenerator.createThumbnail(destinationFile.getAbsolutePath(), 300, 300);
                                        String thumbnailFileName = myFilename.getName().substring(0, myFilename.getName().lastIndexOf('.')) + "_thumbnail.jpg";
                                        String thumbnailPath = path + File.separator + thumbnailFileName;
                                        // Save thumbnail to the same directory
                                        File thumbnailFile = new File(thumbnailPath);
                                        ImageIO.write(thumbnail, "jpg", thumbnailFile);
                                    } catch (IOException ex) {
                                        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
                                CryptionFileAndFolder.encryptFolder(directory.getAbsolutePath(), cf.secretkey, cf.FIXED_IV);
                            } catch (IOException e) {
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvalidKeySpecException ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(ManagePanel.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //CHECK EXISTED
                            if (resourceDAO.selectNameandParentId(myFilename.getName(), parentFolder.getId()) != null) {
                                JOptionPane.showMessageDialog(null, "File này đã tồn tại trong học liệu!!\nVui lòng kiểm tra lại");
                                return null;
                            }

                            //INSERT DATABASE
                            Resource newRes = new Resource();
                            String url = subjectName + "/" + topicName + "/" + materialName + "/" + parentName + "/" + myFilename.getName() + ".encrypted";
                            newRes.setName(myFilename.getName());
                            if (type.equalsIgnoreCase("Video")) {
                                newRes.setTypeId(ApplicationConstant.ResourceType.VIDEO);
                            } else {
                                newRes.setTypeId(ApplicationConstant.ResourceType.IMAGE);
                            }
                            newRes.setParentId(parentFolder.getId());
                            newRes.setUrl(url);

                            resourceDAO.insert(newRes);
//                            new Thread(() -> {
//                                reloadData();
//                            }).start();                          
                            return myFilename.getName();
                        }
                    }
                }

            }
        }
        return null;
    }


    /*
     * TODO: Hàm xử lí thêm dữ liệu (File Resource) (Có nhóm file)
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> Material -> Parent Resource -> <(Resource)> )
     * PARAM:
     *  @type: Kiểu file sẽ được thêm vào (Hình ảnh || Video)
     *  @subjectName: tên của môn học
     *  @topicName: tên của chủ đề
     *  @materialName: tên của thư mục loại file (Tranh, ảnh || Video)
     *  @parentName: tên của thư mục Group File
     * HANDLE:
     *   - Dựa vào type lọc ra những file có đuôi là loại tài liệu đó
     *   - Chọn file
     *   - Lấy subject dựa vào subjectName
     *   - Lấy topic dựa vào topicName
     *   - Lấy material dựa vào materialName
     *   - Tìm ra parent dựa trên parentName và materialId
     *   - Thêm file vào thư mục 
     *   - Reload data
     *   - Show notification
     */
    public static String insertBook(String subjectName) {
        File myFilename;
        FileFilter docFilter = new FileNameExtensionFilter("PDF DOC files", "pdf", "pptx", "doc", "docx");
        chooser = new JFileChooser();

        chooser.setFileFilter(docFilter);

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();
            Subject subject = subjectDAO.selectName(subjectName);
            if (subject != null) {
                Topic top = subject.getTopics()
                        .stream()
                        .filter(sub -> sub.getName().equalsIgnoreCase("Sách tham khảo"))
                        .findFirst()
                        .orElse(null);

                if (top != null) {

                    //THÊM FILE VÀO THƯ MỤC
                    String path = "subject" + File.separator + subjectName + File.separator + top.getName();
                    System.out.println("path: " + path);
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File destinationFile = new File(directory, myFilename.getName());
                    CryptionFileAndFolder cf;

                    try {
                        Files.copy(myFilename.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        try {
                            BufferedImage thumbnail = PDFThumbnail.generateThumbnail(destinationFile.getAbsolutePath(), 300, 300);
                            String thumbnailFileName = myFilename.getName().substring(0, myFilename.getName().lastIndexOf('.')) + "_thumbnail.jpg";
                            String thumbnailPath = path + File.separator + thumbnailFileName;
                            // Save thumbnail to the same directory
                            File thumbnailFile = new File(thumbnailPath);
                            ImageIO.write(thumbnail, "jpg", thumbnailFile);
                        } catch (IOException ex) {
                            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
                        CryptionFileAndFolder.encryptFolder(directory.getAbsolutePath(), cf.secretkey, cf.FIXED_IV);

                    } catch (IOException e) {
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (Exception ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    //INSERT DATABASE
                    if (resourceDAO.selectNameandTopicId(myFilename.getName(), top.getId()) == null) {
                        Resource newRes = new Resource();
                        String url = subjectName + "/" + top.getName() + "/" + myFilename.getName() + ".encrypted";
                        newRes.setName(myFilename.getName());

                        newRes.setTypeId(ApplicationConstant.ResourceType.DOC);

                        newRes.setTopicId(top.getId());
                        newRes.setUrl(url);

                        resourceDAO.insert(newRes);
//                            new Thread(() -> {
//                                reloadData();
//                            }).start();

                        return myFilename.getName();
                    } else {
                        JOptionPane.showMessageDialog(null, "Sách này đã tồn tại!\nHãy đổi tên hoặc thêm sách khác!");
                        return null;
                    }

                } else {
                    //THÊM FILE VÀO THƯ MỤC
                    String path = "subject" + File.separator + subjectName + File.separator + "Sách tham khảo";
                    System.out.println("path: " + path);
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File destinationFile = new File(directory, myFilename.getName());
                    CryptionFileAndFolder cf;

                    try {
                        Files.copy(myFilename.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        cf = new CryptionFileAndFolder("config.properties", "folder.serect.key");
                        CryptionFileAndFolder.encryptFolder(directory.getAbsolutePath(), cf.secretkey, cf.FIXED_IV);

                    } catch (IOException e) {
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (Exception ex) {
                        Logger.getLogger(ManagePanel.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    //INSERT DATABASE
                    Topic bookRes = new Topic();
                    bookRes.setName("Sách tham khảo");
                    bookRes.setSubjectId(subject.getId());
                    topicDAO.insert(bookRes);

                    Topic topNew = topicDAO.selectName(bookRes.getName(), subject.getId());
                    if (topNew != null) {
                        if (resourceDAO.selectNameandTopicId(myFilename.getName(), topNew.getId()) == null) {
                            Resource newRes = new Resource();
                            String url = subjectName + "/" + topNew.getName() + "/" + myFilename.getName() + ".encrypted";
                            newRes.setName(myFilename.getName());

                            newRes.setTypeId(ApplicationConstant.ResourceType.DOC);

                            newRes.setTopicId(topNew.getId());
                            newRes.setUrl(url);

                            resourceDAO.insert(newRes);

                            return "New." + myFilename.getName();
                        } else {
                            JOptionPane.showMessageDialog(null, "Sách này đã tồn tại!\nHãy đổi tên hoặc thêm sách khác!");
                            return null;
                        }
                    }

                }

            }
        }
        return null;
    }

    public static boolean deleteFile(String pathName) {
        String[] subName = pathName.split("/");
        boolean res = false;
        switch (subName.length) {
            case 3 -> { //Xử lí cho xóa sách
                res = deleteFileAtPath(subName[0], subName[1], subName[2]);
            }
            case 4 -> {
                res = deleteFileAtPath(subName[0], subName[1], subName[2], subName[3]);
            }
            case 5 -> {
                res = deleteFileAtPath(subName[0], subName[1], subName[2], subName[3], subName[4]);
            }
            default -> {
            }
        }
        return res;
    }

    /*
     * TODO: Hàm xử lí xóa dữ liệu (File Resource) - Xóa sách
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> <(Resource)> )
     * PARAM:
     *  @subject: tên của môn học
     *  @topic: tên của chủ đề
     *  @file: tên của file
     * HANDLE:
     *   - Lấy subject dựa vào subject
     *   - Lấy topic dựa vào topic
     *   - Tìm ra file dựa trên topic và file
     *   - Show confirm dialog
     *     + Nếu Yes -> Xóa data trong csdl + xóa file trên thư mục học liệu -> Show message
     *     + Nếu No -> Nothing
     */
    private static boolean deleteFileAtPath(String subject, String topic, String file) {
        Subject sub = subjectDAO.selectName(subject);
        if (sub != null) {
            Topic top = topicDAO.selectName(topic, sub.getId());
            if (top != null) {
                Resource res = resourceDAO.selectNameandTopicId(file, top.getId());
                if (res != null) {
                    try {
                        int rs = JOptionPane.showConfirmDialog(null, "Vui lòng xác nhận trước khi thao tác\n(Học liệu đã xóa không thể hoàn tác!!!)", "DELETE", JOptionPane.YES_NO_OPTION);
                        if (rs == JOptionPane.YES_OPTION) {
                            // Xóa tệp từ hệ thống tệp
                            String filePath = "subject" + File.separator + subject + File.separator + topic + File.separator + file + ".encrypted";
                            Path path = Paths.get(filePath);
                            Files.deleteIfExists(path);

                            // Cập nhật cơ sở dữ liệu và tải lại dữ liệu
                            resourceDAO.delete(res.getId());
//                                new Thread(() -> reloadData()).start();
                            JOptionPane.showMessageDialog(null, "Đã xóa " + file + " ra khỏi kho học liệu!");
                            return true;
                        }
                    } catch (HeadlessException | IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                }
            }
        }
        return false;
    }

    /*
     * TODO: Hàm xử lí xóa dữ liệu (File Resource) (Không có nhóm file)
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> Material -> <(Resource)> )
     * PARAM:
     *  @subject: tên của môn học
     *  @topic: tên của chủ đề
     *  @material: tên của thư mục loại file (Tranh, ảnh || Video)
     *  @file: tên của file
     * HANDLE:
     *   - Lấy subject dựa vào subject
     *   - Lấy topic dựa vào topic
     *   - Lấy material dựa vào material
     *   - Tìm ra file dựa trên material và file
     *   - Show confirm dialog
     *     + Nếu Yes -> Xóa data trong csdl + xóa file trên thư mục học liệu -> Show message
     *     + Nếu No -> Nothing
     */
    private static boolean deleteFileAtPath(String subject, String topic, String material, String file) {
        Subject sub = subjectDAO.selectName(subject);
        if (sub != null) {
            Topic top = topicDAO.selectName(topic, sub.getId());
            if (top != null) {
                Resource res = resourceDAO.selectNameandTopicId(material, top.getId());
                if (res != null) {
                    Resource resDel = resourceDAO.selectNameandParentId(file, res.getId());
                    if (resDel != null) {
                        try {
                            int rs = JOptionPane.showConfirmDialog(null, "Vui lòng xác nhận trước khi thao tác\n(Học liệu đã xóa không thể hoàn tác!!!)", "DELETE", JOptionPane.YES_NO_OPTION);
                            if (rs == JOptionPane.YES_OPTION) {
                                // Xóa tệp từ hệ thống tệp
                                String filePath = "subject" + File.separator + subject + File.separator + topic + File.separator + material + File.separator + file + ".encrypted";
                                Path path = Paths.get(filePath);
                                Files.deleteIfExists(path);

                                // Cập nhật cơ sở dữ liệu và tải lại dữ liệu
                                resourceDAO.delete(resDel.getId());
//                                new Thread(() -> reloadData()).start();
                                JOptionPane.showMessageDialog(null, "Đã xóa " + file + " ra khỏi kho học liệu!");
                                return true;
                            }
                        } catch (HeadlessException | IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
     * TODO: Hàm xử lí xóa dữ liệu (File Resource) (Không có nhóm file)
     * NOTE: Xử lí cho cấu trúc mặc định (Subject -> Topic -> Material -> SubFolder -> <(Resource)> )
     * PARAM:
     *  @subject: tên của môn học
     *  @topic: tên của chủ đề
     *  @material: tên của thư mục loại file (Tranh, ảnh || Video)
     *  @subFolder: tên của thư mục chứa file
     *  @file: tên của file
     * HANDLE:
     *   - Lấy subject dựa vào subject
     *   - Lấy topic dựa vào topic
     *   - Lấy material dựa vào material
     *   - Lấy subFolder dựa vào subFolder
     *   - Tìm ra file dựa trên material và file
     *   - Show confirm dialog
     *     + Nếu Yes -> Xóa data trong csdl + xóa file trên thư mục học liệu -> Show message
     *     + Nếu No -> Nothing
     */
    private static boolean deleteFileAtPath(String subject, String topic, String material, String subFolder, String file) {
        Subject sub = subjectDAO.selectName(subject);
        if (sub != null) {
            Topic top = topicDAO.selectName(topic, sub.getId());
            if (top != null) {
                Resource res = resourceDAO.selectNameandTopicId(material, top.getId());
                if (res != null) {
                    Resource resFolder = resourceDAO.selectNameandParentId(subFolder, res.getId());
                    if (resFolder != null) {
                        Resource resDel = resourceDAO.selectNameandParentId(file, resFolder.getId());
                        if (resDel != null) {
                            try {
                                int rs = JOptionPane.showConfirmDialog(null, "Vui lòng xác nhận trước khi thao tác\n(Học liệu đã xóa không thể hoàn tác!!!)", "DELETE", JOptionPane.YES_NO_OPTION);
                                if (rs == JOptionPane.YES_OPTION) {
                                    // Xóa tệp từ hệ thống tệp
                                    String filePath = "subject" + File.separator + subject + File.separator + topic + File.separator + material + File.separator + subFolder + File.separator + file + ".encrypted";
                                    Path path = Paths.get(filePath);
                                    Files.deleteIfExists(path);

                                    // Cập nhật cơ sở dữ liệu và tải lại dữ liệu
                                    resourceDAO.delete(resDel.getId());
//                                    new Thread(() -> reloadData()).start();
                                    JOptionPane.showMessageDialog(null, "Đã xóa " + file + " ra khỏi kho học liệu!");
                                    return true;
                                }
                            } catch (HeadlessException | IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
