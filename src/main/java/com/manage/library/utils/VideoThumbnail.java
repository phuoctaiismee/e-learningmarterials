package com.manage.library.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoThumbnail {

    public static BufferedImage getThumbnail(String path, int frame) throws IOException, JCodecException {
        Picture picture = FrameGrab.getFrameFromFile(
                new File(path),  frame);
        BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
        return bufferedImage;
    }

}
