package tech.huit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件类型工具类
 *
 * @author huitang
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(File.class);
    private static final int BUFFER_SIZE = 1024 * 1024;

    // image
    public static final String JPEG = "image/jpeg";
    public static final String PNG = "image/png";
    public static final String GIF = "image/gif";
    public static final String BMP = "image/bmp";
    // video
    public static final String SWF = "application/x-shockwave-flash";
    public static final String FLV = "video/x-flv";
    public static final String MOV = "video/quicktime";
    public static final String WMV = "video/x-ms-wmv";
    public static final String MP3 = "audio/x-mpeg";
    public static final String MP4 = "audio/mp4";
    public static final String RMVB = "application/vnd.rn-realmedia-vbr";// 目前不能播发
    // doc
    public static final String XLS = "application/vnd.ms-excel";
    public static final String XLSX = "application/vnd.ms-excel";
    public static final String PPT = "application/mspowerpoint";
    public static final String PPTX = "application/mspowerpoint";
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/msword";
    public static final String PDF = "application/pdf";
    // stream
    public static final String ZIP = "application/zip";
    public static final String DEFAULT = "application/octet-stream";

    /**
     * 得到文件扩展名
     *
     * @param fileName 文件名
     * @return
     */
    public static String getExtension(String fileName) {
        String extension = null;
        if (null != fileName) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return extension;
    }

    /**
     * 得到文件扩展名
     *
     * @param fileName 文件名
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = null;
        if (null != fileName) {
            name = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return name;
    }

    /**
     * 得到文件扩展名
     *
     * @return
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * 从扩展名得到文件类型，未定义后缀的返回null
     *
     * @param fileName
     * @return
     */
    public static String getContentTypeFromFileName(String fileName) {
        return getContentTypeFromExtension(getExtension(fileName));
    }

    /**
     * 从扩展名得到文件类型，未定义后缀的返回二进制
     *
     * @param extension
     * @return
     */
    public static String getContentTypeFromExtension(String extension) {
        String contentType = DEFAULT;
        // image
        if ("jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)) {
            contentType = FileUtil.JPEG;
        } else if ("png".equalsIgnoreCase(extension)) {
            contentType = FileUtil.PNG;
        } else if ("gif".equalsIgnoreCase(extension)) {
            contentType = FileUtil.GIF;
        } else if ("bmp".equalsIgnoreCase(extension)) {
            contentType = FileUtil.BMP;
        } else if ("mp3".equalsIgnoreCase(extension)) {// video
            contentType = FileUtil.MP3;
        } else if ("mp4".equalsIgnoreCase(extension)) {
            contentType = FileUtil.MP4;
        } else if ("flv".equalsIgnoreCase(extension)) {
            contentType = FileUtil.FLV;
        } else if ("swf".equalsIgnoreCase(extension)) {
            contentType = FileUtil.SWF;
        } else if ("mov".equalsIgnoreCase(extension)) {
            contentType = FileUtil.MOV;
        } else if ("wmv".equalsIgnoreCase(extension)) {
            contentType = FileUtil.WMV;
        } else if ("rmvb".equalsIgnoreCase(extension)) {
            contentType = FileUtil.RMVB;
        } else if ("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)) {// doc
            contentType = FileUtil.DOC;
        } else if ("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)) {
            contentType = FileUtil.XLS;
        } else if ("ppt".equalsIgnoreCase(extension) || "pptx".equalsIgnoreCase(extension)) {
            contentType = FileUtil.PPT;
        } else if ("pdf".equalsIgnoreCase(extension)) {
            contentType = FileUtil.PDF;
        }
        return contentType;
    }

    /**
     * 是否可以在线浏览
     *
     * @param extension
     * @return
     */
    public static boolean viewAble(String extension) {
        boolean viewAble = false;
        if (null != extension) {
            String ext = extension.substring(extension.lastIndexOf(".") + 1).toLowerCase();
            if ("jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "gif".equals(ext) || "bmp".equals(ext)
                    || "flv".equals(ext) || "wmv".equals(ext) || "mp3".equals(ext) || "mp4".equals(ext)
                    || "swf".equals(ext)) {
                viewAble = true;
            }
        }
        return viewAble;
    }

    /**
     * 得到文件大小的字符串表示
     *
     * @param length
     * @return
     */
    public static String getFileSize(long length) {
        String size = null;
        if (length / 1073741824 > 0) {
            size = getSize(length, 1073741824.0f, "GB");
        } else if (length / 1048576 > 0) {
            size = getSize(length, 1048576.0f, "MB");
        } else if (length / 1024 > 0) {
            size = getSize(length, 1024.0f, "KB");
        } else if (length / 1024 == 0) {
            size = String.valueOf(length) + "bytes";
        }
        return size;
    }

    private static String getSize(long length, float div, String unit) {
        String size;
        String result = String.valueOf(length / div);
        int index = result.lastIndexOf('.');
        if (index == 3) {
            size = result.substring(0, 3) + unit;
        } else if (result.length() > 4) {
            size = result.substring(0, 4) + unit;
        } else {
            size = result + unit;
        }
        return size;
    }

    /**
     * 得到文件大小的字符串表示
     *
     * @param file
     * @return
     */
    public static String getFileSize(File file) {
        if (null != file) {
            return getFileSize(file.length());
        } else {
            return "";
        }
    }

    /**
     * 文件拷贝
     *
     * @param targetFile
     */
    public static void copy(File srcFile, File targetFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(srcFile));
            out = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 判断文件是否文档类
     */
    public static boolean isDoc(String contentType) {
        boolean result = false;
        if (PDF.equals(contentType) || PPT.equals(contentType) || DOC.equals(contentType) || XLS.equals(contentType)) {
            result = true;
        }
        return result;
    }

    /**
     * 判断文件是否图片
     */
    public static boolean isImage(String contentType) {
        boolean result = false;
        if (JPEG.equals(contentType) || PNG.equals(contentType) || GIF.equals(contentType) || BMP.equals(contentType)) {
            result = true;
        }
        return result;
    }

    /**
     * 判断文件是否视频类
     */
    public static boolean isVideo(String contentType) {
        boolean result = false;
        if (SWF.equals(contentType) || FLV.equals(contentType) || MOV.equals(contentType) || WMV.equals(contentType)
                || RMVB.equals(contentType) || MP3.equals(contentType) || MP4.equals(contentType)) {
            result = true;
        }
        return result;
    }

    /**
     * 判断文件是否二进制文件
     */
    public static boolean isStream(String contentType) {
        boolean result = false;
        if (!isImage(contentType) || !isDoc(contentType) || !isVideo(contentType)) {
            result = true;
        }
        return result;
    }
}
