package tech.huit.uuc.web.api.controller;

import tech.huit.util.ImgUtil;
import tech.huit.util.ServletUtils;
import tech.huit.conf.SystemConf;
import tech.huit.util.VideoUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 图片、视频预览功能
 * <p>
 * url映射规则：msg/download/2017-06-23/1.jpg的下载地址对应的预览地址为：pic/2017-06-23/1-200x200.jpg
 * 中划线后使用200x200设定图片大小
 * <p>
 * http://localhost:8081/pic/2017-12-09/d36cbeed756b407fa1021956f0856e10.png 原图（默认图片会进行1280*1280压缩）
 * http://localhost:8081/pic/2017-12-09/d36cbeed756b407fa1021956f0856e10-400x400.png?crop 图片裁剪为指定大小
 * http://localhost:8081/pic/2017-12-09/d36cbeed756b407fa1021956f0856e10-400x400.png 进行等比缩放
 * http://uuc.huit.tech:8888/pic/2017-12-10/377e8c222a134684907f3ffe6e7a7573-400x400.m4v 视频图片预览(操作系统需要安装ffmpeg)
 *
 * @author huit
 */
@WebServlet(urlPatterns = {"/pic/*"})
public class PicServlet extends HttpServlet {
    private static final String CROP = "crop";
    public static final String IMAGE_JPEG = "image/jpeg";
    private static final long serialVersionUID = 1L;
    private String PIC_BASE_PATH = SystemConf.get("msg.file.save.path");
    private String URL_TRIM = SystemConf.get("PIC_WEB_PATH");// 从url中过滤的二目录
    int URL_TRIM_LENGTH = "/pic".length();

    public PicServlet() {
        super();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (URL_TRIM_LENGTH > 0) {
            uri = uri.substring(URL_TRIM_LENGTH);
        }
        String savePath = PIC_BASE_PATH + uri;
        if (VideoUtil.isVideo(savePath)) {
            savePath = savePath + ".jpg";
        }
        File file = new File(savePath);
        if (!file.isFile()) {
            int beginIndex = uri.lastIndexOf('-');// 约定"-"后为图片分辨率
            if (beginIndex > 0) {
                int endIndex = uri.indexOf('.');
                if (-1 == endIndex) {
                    endIndex = uri.length();
                }
                String picRatioInfo = uri.substring(beginIndex + 1, endIndex);
                String[] picRatio = picRatioInfo.split("x");
                int toWidth = Integer.valueOf(picRatio[0]), toHeight = 0;
                if (picRatio.length == 1) {
                    toHeight = toWidth;
                } else if (picRatio.length == 2) {
                    toHeight = Integer.valueOf(picRatio[1]);
                } else {
                    // error
                }
                String srcImgPath = PIC_BASE_PATH + uri.substring(0, beginIndex) + uri.substring(endIndex);

                if (VideoUtil.isVideo(srcImgPath)) {
                    String targetImg = PIC_BASE_PATH + uri.substring(0, beginIndex) + ".jpg";
                    VideoUtil.getImg(srcImgPath, targetImg);
                    srcImgPath = targetImg;
                }

                String crop = request.getParameter(CROP);
                if (null != crop) {
                    ImgUtil.resizeImg(srcImgPath, savePath, toWidth, toHeight, true, false);
                } else {
                    ImgUtil.resizeImg(srcImgPath, savePath, toWidth, toHeight);
                }
            }

        }

        if (!file.isFile()) {//如果文件还是不存在，设置默认值
            int index = uri.indexOf('/');
            if (index > 0) {
                String defaultName = uri.substring(0, index);
                file = new File(SystemConf.confFileDir + "/pic_default/" + defaultName + ".jpg");
            }
        }

        ServletUtils.setHttpFile(file, response, IMAGE_JPEG);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void init() throws ServletException {
        if (null != URL_TRIM && URL_TRIM.length() > 0) {
            URL_TRIM_LENGTH = URL_TRIM.length() + 2;
        }
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (URL_TRIM_LENGTH > 0) {
            uri = uri.substring(URL_TRIM_LENGTH);
        }
        String savePath = PIC_BASE_PATH + uri;
        File file = new File(savePath);
        if (file.isFile()) {
            return file.lastModified();
        } else {
            return -1;
        }
    }
}
