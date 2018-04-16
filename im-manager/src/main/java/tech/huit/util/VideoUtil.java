package tech.huit.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VideoUtil {
    private static Logger logger = LoggerFactory.getLogger(VideoUtil.class);
    private static String[] exts = new String[]{"avi", "asf", "divx", "h264", "mp4", "m4v",
            "mpg", "mpeg", "mpe", "mkv", "rm", "rmvb", "rm", "vob", "wmv"};

    public static boolean isVideo(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        fileName = fileName.toLowerCase();
        for (String ext : exts) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static void getImg(String srouce, String target) {
        File file = new File(target);
        if (file.isFile()) {
            return;
        }

        VideoFirstThumbTaker firstThumbTaker = new VideoFirstThumbTaker("ffmpeg");
        try {
            firstThumbTaker.getThumb(srouce, target);
        } catch (Exception e) {
            logger.error("getImgError", e);
        }
    }

    public static void main(String[] args) {
        VideoUtil.getImg("/Users/huit/uuc-pic/IMG_1990.m4v", "/Users/huit/uuc-pic/IMG_1990.jpg");
    }
}
