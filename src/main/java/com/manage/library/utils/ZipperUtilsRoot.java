package com.manage.library.utils;

import com.manage.library.dao.ResourceDAO;
import com.manage.library.dao.SubjectDAO;
import com.manage.library.dao.TopicDAO;
import com.manage.library.model.Resource;
import com.manage.library.model.Subject;
import com.manage.library.model.Topic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;

public class ZipperUtilsRoot {

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final TopicDAO topicDAO = new TopicDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();

    private static final Map<String, String[]> fileTypeMap = new HashMap<>();

    static {
        fileTypeMap.put("Tranh, ảnh", new String[]{"png", "jpg", "jpeg", "gif"});
        fileTypeMap.put("Video", new String[]{"mp4", "avi", "mkv"});
        fileTypeMap.put("Tài liệu", new String[]{"pdf", "docx", "txt", "pptx", "csv", "xlsx"});
    }

    public void unzip(String zipFilePath) throws URISyntaxException, IOException {

//        InputStream resource = ZipperUtilsRoot.class.getClassLoader().getResource("subject").openStream();
//
//        if (resource == null) {
//            System.out.println("NOT FOUND SUBJECTS");
//        }
//        System.out.println("Dô đây");

        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            try (ZipInputStream zis = new ZipInputStream(fis)) {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileName = ze.getName();
                    String[] components = fileName.split("/");
                    
                    /*
                    * NOTE: Cấu trúc file zip từ 3 cấp trở lên
                    * 1 - Môn học
                    * 2 - Chủ đề
                    * 3 - Thư mục loại học liệu (Tranh ảnh, Video, Sách) -> Có thể trực tiếp là file
                    * 4 - File hoặc Groups File
                    * 5 - Subfile (nếu có GroupFiles)
                    *
                    *
                    * *** LƯU Ý: File luôn là vị trí cuối cùng
                    * VD:
                    *     Môn học -> Chủ đề -> image.jpg, video.mp4, sach.pdf (3 cấp)
                    *   or
                    *     Môn học -> Chủ đề -> Tranh, ảnh -> anh.png, anh2.png,... (4 cấp)
                    *   or
                    *     Môn học -> Chủ đề -> Tranh, ảnh -> Nhóm ảnh -> anh1.png, anh2.png (5 cấp)
                    */
                    if (components.length < 3 || components.length > 5) {
                        return;
                    }
                    switch (components.length) {
                        case 3:
                            System.out.println("____________________________________");
                            System.out.println("CASE 3");
                            String subjectName3 = components[0];
                            String topicName3 = components[1];
                            String file3 = components[2];
                            
                            int subRs = insertSubject(subjectName3);
                            // Khi học liệu đã tồn tại
                            if (subRs != -1) {
                                int topRs = insertTopic(topicName3, subRs);
                                // Khi topic đã tồn tại
                                if (topRs != -1) {
                                    String name = FilenameUtils.getName(fileName).replace(".encrypted", "");
                                    String fileExtension = FilenameUtils.getExtension(name).replace(".encrypted", "");
                                    String url = subjectName3 + "/" + topicName3 + "/" + file3;
                                    int type = checkFileType(fileExtension);
                                    int resRs = insertResource(name, type, topRs, url);
                                } else {
                                    //Khi topic vừa thêm vào
                                    Topic topAddNew = this.topicDAO.selectName(topicName3, subRs);
                                    if (topAddNew != null) {
                                        String name = FilenameUtils.getName(fileName).replace(".encrypted", "");
                                        String fileExtension = FilenameUtils.getExtension(name).replace(".encrypted", "");
                                        String url = subjectName3 + "/" + topicName3 + "/" + file3;
                                        int type = checkFileType(fileExtension);
                                        int resRs = insertResource(name, type, topAddNew.getId(), url);
                                    }
                                }
                            } else {
                                //Khi học liệu vừa được thêm vào
                                // Truy vấn nó lên
                                Subject subAdd = subjectDAO.selectName(subjectName3);
                                if (subAdd != null) {
                                    int topRs = insertTopic(topicName3, subAdd.getId());
                                    // Khi topic đã tồn tại
                                    if (topRs != -1) {
                                        String name = FilenameUtils.getName(fileName).replace(".encrypted", "");
                                        String fileExtension = FilenameUtils.getExtension(name).replace(".encrypted", "");
                                        String url = subjectName3 + "/" + topicName3 + "/" + file3;
                                        int type = checkFileType(fileExtension);
                                        insertResource(name, type, topRs, url);
                                    } else {
                                        //Khi topic vừa thêm vào
                                        Topic topAddNew = this.topicDAO.selectName(topicName3, subAdd.getId());
                                        if (topAddNew != null) {
                                            String name = FilenameUtils.getName(fileName).replace(".encrypted", "");
                                            String fileExtension = FilenameUtils.getExtension(name).replace(".encrypted", "");
                                            String url = subjectName3 + "/" + topicName3 + "/" + file3;
                                            int type = checkFileType(fileExtension);
                                            insertResource(name, type, topAddNew.getId(), url);
                                        }
                                    }
                                }
                            }
                            break;
                        case 4:
                            String subjectName4 = components[0];
                            String topicName4 = components[1];
                            String materialType4 = components[2];
                            String file4 = components[3];
                            
                            System.out.println("____________________________________");
                            System.out.println("CASE 4");
                            System.out.println("Subject: " + subjectName4);
                            System.out.println("Topic: " + topicName4);
                            System.out.println("Material : " + materialType4);
                            System.out.println("File : " + file4);
                            
                            int subRs4 = insertSubject(subjectName4);
                            // Khi học liệu đã tồn tại
                            if (subRs4 != -1) {
                                int topRs = insertTopic(topicName4, subRs4);
                                // Khi topic đã tồn tại
                                if (topRs != -1) {
                                    int res4 = insertResource(materialType4, 1, topRs, "");
                                    if (res4 != -1) {
                                        String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                        int type = checkFileType(FilenameUtils.getExtension(name));
                                        String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                        insertResourceSubFolder(name, type, res4, url);
                                    } else {
                                        Resource resChk4 = resourceDAO.selectNameandTopicId(materialType4, topRs);
                                        if (resChk4 != null) {
                                            String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                            int type = checkFileType(FilenameUtils.getExtension(name));
                                            String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                            insertResourceSubFolder(name, type, resChk4.getId(), url);
                                        }
                                    }
                                } else {
                                    //Khi topic vừa thêm vào
                                    Topic topAddNew = topicDAO.selectName(topicName4, subRs4);
                                    if (topAddNew != null) {
                                        int resRs = insertResource(materialType4, 1, topAddNew.getId(), "");
                                        //Khi thư mục loại file đã tồn tại
                                        if (resRs != -1) {
                                            String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                            int type = checkFileType(FilenameUtils.getExtension(name));
                                            String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                            insertResourceSubFolder(name, type, resRs, url);
                                        } else {
                                            //Khi thư mục loại file vừa được thêm                                        
                                            // Lấy đối tượng đó lên để kiểm tra
                                            Resource materialType = resourceDAO.selectNameandTopicId(materialType4, topAddNew.getId());
                                            if (materialType != null) {
                                                String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;                                          
                                                insertResourceSubFolder(name, type, materialType.getId(), url);
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Khi học liệu vừa được thêm vào
                                // Truy vấn nó lên
                                Subject subAdd = subjectDAO.selectName(subjectName4);
                                if (subAdd != null) {
                                    int topRs = insertTopic(topicName4, subAdd.getId());
                                    // Khi topic đã tồn tại
                                    if (topRs != -1) {
                                        int res4 = insertResource(materialType4, 1, topRs, "");
                                        if (res4 != -1) {
                                            String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                            int type = checkFileType(FilenameUtils.getExtension(name));
                                            String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                            insertResourceSubFolder(name, type, res4, url);
                                        } else {
                                            Resource resChk4 = resourceDAO.selectNameandTopicId(FilenameUtils.getName(file4), topRs);
                                            if (resChk4 != null) {
                                                String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                                insertResourceSubFolder(name, type, resChk4.getId(), url);
                                            }
                                        }
                                    } else {
                                        //Khi topic vừa thêm vào
                                        Topic topAddNew = topicDAO.selectName(topicName4, subAdd.getId());
                                        if (topAddNew != null) {
                                            int resRs = insertResource(materialType4, 1, topAddNew.getId(), "");
                                            //Khi thư mục loại file đã tồn tại
                                            if (resRs != -1) {
                                                String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                                insertResourceSubFolder(name, type, resRs, url);
                                            } else {
                                                //Khi thư mục loại file vừa được thêm
                                                // Lấy đối tượng đó lên để kiểm tra
                                                Resource materialType = resourceDAO.selectNameandTopicId(materialType4, topAddNew.getId());
                                                if (materialType != null) {
                                                    String name = FilenameUtils.getName(file4).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName4 + "/" + topicName4 + "/" + materialType4 + "/" + file4;
                                                    insertResourceSubFolder(name, type, materialType.getId(), url);
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case 5:
                            String subjectName5 = components[0];
                            String topicName5 = components[1];
                            String materialType5 = components[2];
                            String groupFiles5 = components[3];
                            String file5 = components[4];
                            
                            System.out.println("____________________________________");
                            System.out.println("CASE 5");
                            System.out.println("Subject: " + subjectName5);
                            System.out.println("Topic: " + topicName5);
                            System.out.println("Material : " + materialType5);
                            System.out.println("Groups file : " + groupFiles5);
                            System.out.println("File : " + file5);
                            
                            int subRs5 = insertSubject(subjectName5);
                            // Khi học liệu đã tồn tại
                            if (subRs5 != -1) {
                                int topRs = insertTopic(topicName5, subRs5);
                                // Khi topic đã tồn tại
                                if (topRs != -1) {
                                    int res5 = insertResource(materialType5, 1, topRs, "");
                                    // Khi thư mục loại file tồn tại
                                    if (res5 != -1) {
                                        int resFolder5 = insertResourceSubFolder(groupFiles5, 1, res5, "");
                                        // Khi thư mục nhóm file tồn tại
                                        if (resFolder5 != -1) {
                                            String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                            int type = checkFileType(FilenameUtils.getExtension(name));
                                            String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                            insertResourceSubFolder(name, type, resFolder5, url);
                                        } else {
                                            Resource resChk5 = resourceDAO.selectNameandParentId(groupFiles5, res5);
                                            if (resChk5 != null) {
                                                String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                insertResourceSubFolder(name, type, resChk5.getId(), url);
                                            }
                                        }
                                    } else {
                                        Resource resChk5 = resourceDAO.selectNameandTopicId(materialType5, topRs);
                                        if (resChk5 != null) {
                                            int resFolder5 = insertResourceSubFolder(groupFiles5, 1, resChk5.getId(), "");
                                            // Khi thư mục nhóm file tồn tại
                                            if (resFolder5 != -1) {
                                                String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                insertResourceSubFolder(name, type, resFolder5, url);
                                            } else {
                                                Resource resSubChk5 = resourceDAO.selectNameandParentId(groupFiles5, resChk5.getId());
                                                if (resSubChk5 != null) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resSubChk5.getId(), url);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //Khi topic vừa thêm vào
                                    Topic topAddNew = topicDAO.selectName(topicName5, subRs5);
                                    if (topAddNew != null) {
                                        int res5 = insertResource(materialType5, 1, topAddNew.getId(), "");
                                        // Khi thư mục loại file tồn tại
                                        if (res5 != -1) {
                                            int resFolder5 = insertResourceSubFolder(groupFiles5, 1, res5, "");
                                            // Khi thư mục nhóm file tồn tại
                                            if (resFolder5 != -1) {
                                                String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                insertResourceSubFolder(name, type, resFolder5, url);
                                            } else {
                                                Resource resChk5 = resourceDAO.selectNameandParentId(groupFiles5, res5);
                                                if (resChk5 != null) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resChk5.getId(), url);
                                                }
                                            }
                                        } else {
                                            Resource resChk5 = resourceDAO.selectNameandTopicId(materialType5, topAddNew.getId());
                                            if (resChk5 != null) {
                                                int resFolder5 = insertResourceSubFolder(groupFiles5, 1, resChk5.getId(), "");
                                                // Khi thư mục nhóm file tồn tại
                                                if (resFolder5 != -1) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resFolder5, url);
                                                } else {
                                                    Resource resSubChk5 = resourceDAO.selectNameandParentId(groupFiles5, resChk5.getId());
                                                    if (resSubChk5 != null) {
                                                        String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                        int type = checkFileType(FilenameUtils.getExtension(name));
                                                        String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                        insertResourceSubFolder(name, type, resSubChk5.getId(), url);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Khi học liệu vừa được thêm vào
                                // Truy vấn nó lên
                                Subject subAdd = subjectDAO.selectName(subjectName5);
                                if (subAdd != null) {
                                    int topRs = insertTopic(topicName5, subAdd.getId());
                                    // Khi topic đã tồn tại
                                    if (topRs != -1) {
                                        int res5 = insertResource(materialType5, 1, topRs, "");
                                        // Khi thư mục loại file tồn tại
                                        if (res5 != -1) {
                                            int resFolder5 = insertResourceSubFolder(groupFiles5, 1, res5, "");
                                            // Khi thư mục nhóm file tồn tại
                                            if (resFolder5 != -1) {
                                                String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                int type = checkFileType(FilenameUtils.getExtension(name));
                                                String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                insertResourceSubFolder(name, type, resFolder5, url);
                                            } else {
                                                Resource resChk5 = resourceDAO.selectNameandParentId(groupFiles5, res5);
                                                if (resChk5 != null) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resChk5.getId(), url);
                                                }
                                            }
                                        } else {
                                            Resource resChk5 = resourceDAO.selectNameandTopicId(materialType5, topRs);
                                            if (resChk5 != null) {
                                                int resFolder5 = insertResourceSubFolder(groupFiles5, 1, resChk5.getId(), "");
                                                // Khi thư mục nhóm file tồn tại
                                                if (resFolder5 != -1) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resFolder5, url);
                                                } else {
                                                    Resource resSubChk5 = resourceDAO.selectNameandParentId(groupFiles5, resChk5.getId());
                                                    if (resSubChk5 != null) {
                                                        String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                        int type = checkFileType(FilenameUtils.getExtension(name));
                                                        String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                        insertResourceSubFolder(name, type, resSubChk5.getId(), url);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        //Khi topic vừa thêm vào
                                        Topic topAddNew = topicDAO.selectName(topicName5, subAdd.getId());
                                        if (topAddNew != null) {
                                            int res5 = insertResource(materialType5, 1, topAddNew.getId(), "");
                                            // Khi thư mục loại file tồn tại
                                            if (res5 != -1) {
                                                int resFolder5 = insertResourceSubFolder(groupFiles5, 1, res5, "");
                                                // Khi thư mục nhóm file tồn tại
                                                if (resFolder5 != -1) {
                                                    String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                    int type = checkFileType(FilenameUtils.getExtension(name));
                                                    String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                    insertResourceSubFolder(name, type, resFolder5, url);
                                                } else {
                                                    Resource resChk5 = resourceDAO.selectNameandParentId(groupFiles5, res5);
                                                    if (resChk5 != null) {
                                                        String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                        int type = checkFileType(FilenameUtils.getExtension(name));
                                                        String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                        insertResourceSubFolder(name, type, resChk5.getId(), url);
                                                    }
                                                }
                                            } else {
                                                Resource resChk5 = resourceDAO.selectNameandTopicId(materialType5, topAddNew.getId());
                                                if (resChk5 != null) {
                                                    int resFolder5 = insertResourceSubFolder(groupFiles5, 1, resChk5.getId(), "");
                                                    // Khi thư mục nhóm file tồn tại
                                                    if (resFolder5 != -1) {
                                                        String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                        int type = checkFileType(FilenameUtils.getExtension(name));
                                                        String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                        insertResourceSubFolder(name, type, resFolder5, url);
                                                    } else {
                                                        Resource resSubChk5 = resourceDAO.selectNameandParentId(groupFiles5, resChk5.getId());
                                                        if (resSubChk5 != null) {
                                                            String name = FilenameUtils.getName(file5).replace(".encrypted", "");
                                                            int type = checkFileType(FilenameUtils.getExtension(name));
                                                            String url = subjectName5 + "/" + topicName5 + "/" + materialType5 + "/" + groupFiles5 + "/" + file5;
                                                            insertResourceSubFolder(name, type, resSubChk5.getId(), url);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            break;
                        default:
                            JOptionPane.showMessageDialog(new JFrame(), "Học liệu sai định dạng! \n Vui lòng liên hệ nhà cung cấp để mua học liệu chuẩn!!");
                            return;
                    }
                    
                    File newFile = new File("subject" + File.separator + fileName);
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    System.out.println("Unzipping to " + newFile.getAbsolutePath());
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    zis.closeEntry();
                    ze = zis.getNextEntry();
                }
                zis.closeEntry();
            }
            fis.close();          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int insertSubject(String name) {
        Subject sub = new Subject(name);
        Subject subChecking = subjectDAO.selectName(name);
        if (subChecking == null) {
            subjectDAO.insert(sub);
            return -1;
        } else {
            System.out.println("Học liệu này đã tồn tại!!");
            return subChecking.getId();
        }
    }

    public int insertTopic(String name, int idSubject) {
        Topic top = new Topic(name, idSubject);
        Topic topChecking = topicDAO.selectName(name, idSubject);
        if (topChecking == null) {
            topicDAO.insert(top);
            return -1;
        } else {
            System.out.println("Chủ đề này đã tồn tại!!");
            return topChecking.getId();
        }
    }

    /*
    TODO: Hàm này dùng để thêm một thư mục hoặc file KHÔNG có sub folder
     */
    public int insertResource(String name, int type, int topic, String url) {
        Resource resource = new Resource(name, url, type, topic);
        Resource resChecking = resourceDAO.selectNameandTopicId(name, topic);
        if (resChecking == null) {
            resourceDAO.insert(resource);
            return -1;
        } else {
            System.out.println("Resource này đã tồn tại!!");
            return resChecking.getId();

        }
    }

    /*
    TODO: Hàm này dùng để thêm một thư mục hoặc file CÓ sub folder
     */
    public int insertResourceSubFolder(String name, int type, int parent, String url) {
        Resource resource = new Resource(name, type, parent, url);
        Resource resChecking = resourceDAO.selectNameandParentId(name, parent);
        if (resChecking == null) {
            resourceDAO.insert(resource);
            return -1;
        } else {
            System.out.println("Resource này đã tồn tại!!");
            return resChecking.getId();

        }
    }

    public static Integer checkFileType(String fileExtension) {
        String lowercaseExtension = fileExtension.toLowerCase();

        return fileTypeMap.entrySet()
                .stream()
                .filter(entry -> Arrays.asList(entry.getValue()).contains(lowercaseExtension))
                .map(new Function<Map.Entry<String, String[]>, Integer>() {
                    @Override
                    public Integer apply(Map.Entry<String, String[]> entry) {
                        return switch (entry.getKey()) {
                            case "Tranh, ảnh" ->
                                2;
                            case "Video" ->
                                3;
                            case "Tài liệu" ->
                                4;
                            default ->
                                0;
                        };
                    }
                })
                .findFirst()
                .orElse(0);
    }

}
