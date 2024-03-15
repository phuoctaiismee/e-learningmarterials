
package com.manage.library.services;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.manage.library.config.ApplicationConstant;
import com.manage.library.config.CryptionFileAndFolder;
import com.manage.library.utils.PDFThumbnail;
import com.manage.library.utils.ThumbnailGenerator;
import com.manage.library.utils.VideoThumbnail;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {
    private static DataLoader instance;
    private Map<String, BufferedImage> cache = new HashMap<>();
    private CryptionFileAndFolder cf;

    private DataLoader() throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.cf = new CryptionFileAndFolder(ApplicationConstant.PathConfig.CONFIG_PROPERTIES, "folder.serect.key");
    }

    public static DataLoader getInstance() {
        if (instance == null) {
            try {
                instance = new DataLoader();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public BufferedImage getThumbnail(String filePath, int typeId) {
        BufferedImage thumbnail = cache.get(filePath);
        if (thumbnail == null) {
            try {
                File decryptedFile = null;
                if (typeId != 1) {
                    decryptedFile = CryptionFileAndFolder.decryptToFile(filePath, cf.secretkey, cf.FIXED_IV);
                }
                switch (typeId) {
                    case 1:
                        thumbnail = (BufferedImage) new FlatSVGIcon("logos/icons/icon/thumnail/folder.svg").getImage();
                        break;
                    case 2:
                        thumbnail = ThumbnailGenerator.createThumbnail(decryptedFile.getAbsolutePath(), 128, 128);
                        break;
                    case 3:
                        thumbnail = VideoThumbnail.getThumbnail(decryptedFile.getAbsolutePath(), 410);
                        break;
                    case 4:
                        thumbnail = PDFThumbnail.generateThumbnail(decryptedFile.getAbsolutePath(), 128, 128);
                        break;
                    default:
                        thumbnail = (BufferedImage) new FlatSVGIcon("logos/icons/icon/thumnail/not.svg").getImage();
                }
                cache.put(filePath, thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return thumbnail;
    }
}

