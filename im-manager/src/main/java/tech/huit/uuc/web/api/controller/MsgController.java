package tech.huit.uuc.web.api.controller;

import com.alibaba.fastjson.JSON;
import edu.dbke.socket.cp.ProtocolType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import tech.huit.socket.cp.message.Msg;
import tech.huit.util.FileUtil;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.InvalidTokenException;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.message.MsgService;
import tech.huit.uuc.system.ErrorCode;
import tech.huit.web.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * 聊天服务文件上传下载服务
 * 一、文件上传地址：http://uuc.huit.tech:8888/msg/fileUpload
 * curl -F "token=Z4Dl5LQOR6EKQh6x8jZxZMZm665loDHYrF07rDxkHx7cmEJPkRvXkhNsAOjf57GV" -F "file=@/Users/huit/uuc-pic/test.png" http://localhost:8081/msg/fileUpload
 * curl -F "token=Z4Dl5LQOR6EKQh6x8jZxZMZm665loDHYrF07rDxkHx7cmEJPkRvXkhNsAOjf57GV" -F "file=@/Users/huit/uuc-pic/test.png" http://uuc.huit.tech:8888/msg/fileUpload
 * curl -F "token=Z4Dl5LQOR6EKQh6x8jZxZMZm665loDHYrF07rDxkHx7cmEJPkRvXkhNsAOjf57GV" -F "file=@/Users/huit/uuc-pic/IMG_1990.m4v" http://uuc.huit.tech:8888/msg/fileUpload
 * <p>
 * 请求参数列表：
 * 1、file 要上传的文件
 * 2、token 登录时获取的token :Bo1QmOHNKSAT_rvCYTk4EdNLkCkPHCCusIkq7102rco
 * <p>
 * 返回数据UTF-8编码的Json：{"data": "http://localhost/msg/download/2017-06-23/adb70d6249b146c7995dfd81c0ddbd94.jpg","status":true,"errorCode":"错误编码","errorMsg":"错误信息描述"}
 * 1、data 如果上传成功返回资源的下载地址
 * <p>
 * 二、文件下载地址：http://uuc.huit.tech:8888/msg/download/2017-06-23/bd489af211f24d2186794acbcd90d7e1.jpg?token=yMY3ci3Mo4pqJq1jPVGAnGTbbLZGyFn5kWNEuY1fSko=
 * curl -d "isInvite=true&uids=1,2&groupIds=1,2&msg=XX邀请你加入XX群" "http://uuc.huit.tech:8888/msg/GroupInviteKick"
 * <p>
 * <p>
 * 三、http接口查询历史消息，用于做管理界面，app终端通过tcp拉取历史消息，返回消息中newestOffset表示最新的位置，可以通过这个来取最新的指定条数消息
 * http://uuc.huit.tech:8888/msg/getMsg?id=1&isGroup=true&isSend=false&offset=0&size=10
 * 四、保存内存消息到磁盘
 * http://uuc.huit.tech:8888/msg/save
 */
@Controller
@RequestMapping("/msg")
public class MsgController {
    private static final Logger logger = LoggerFactory.getLogger(MsgController.class);
    private String aesKey = SystemConf.get("auth.aes.key");
    private String fileSavePath = SystemConf.get("msg.file.save.path");
    private String fileDownloadUrl = SystemConf.get("msg.file.download.url");
    private static final String date_format = "yyyy-MM-dd";//按天存放，方便按天删除

    @Autowired
    private MsgService msgService;

    @ResponseBody
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public ResponseStatus upload(HttpServletRequest request, String token) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("file");
        ResponseStatus rs = new ResponseStatus(true);
        UserLoginInfo userInfo = null;
        try {
            userInfo = UserLoginInfo.parseUucToken(token, aesKey);
        } catch (InvalidTokenException e) {
            rs.setStatus(false);
            rs.setErrorCode(ErrorCode.AUTH_TOKEN_ERROR.getCode());
            rs.setErrorMsg(ErrorCode.AUTH_TOKEN_ERROR.getMsg());
            logger.error("msgUploadTokenError->token:{} fileName:{}", token);
            return rs;
        }
        handleFile(file, rs);
        logger.info("msgUpload->userInfo:{} result:{}", userInfo, rs);
        return rs;
    }

    /**
     * 保存上传的文件
     *
     * @param file
     * @param rs
     * @throws IOException
     */
    private void handleFile(CommonsMultipartFile file, ResponseStatus rs) throws IOException {
        String realFileName = file.getOriginalFilename();
        String subFileSavePath = DateFormatUtils.format(new Date(), date_format);
        File dirPath = new File(fileSavePath + "/" + subFileSavePath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();  // 创建文件夹
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = subFileSavePath + "/" + uuid + "." + FileUtil.getExtension(realFileName);
        File uploadFile = new File(fileSavePath + "/" + fileName);
        FileOutputStream fos = new FileOutputStream(uploadFile);
        FileCopyUtils.copy(file.getInputStream(), fos);
        rs.data = fileDownloadUrl + "/" + fileName;
    }


    @RequestMapping("/download/{dir}/{fileName:.+}")
    public ModelAndView download(@PathVariable("dir") String dir, @PathVariable("fileName") String fileName, HttpServletResponse response, String token) throws Exception {
        UserLoginInfo userInfo;
        try {
            userInfo = UserLoginInfo.parseUucToken(token, aesKey);
        } catch (InvalidTokenException e) {
            ResponseStatus rs = new ResponseStatus(false);
            rs.setErrorCode(ErrorCode.AUTH_TOKEN_ERROR.getCode());
            rs.setErrorMsg(ErrorCode.AUTH_TOKEN_ERROR.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getOutputStream().write(JSON.toJSONString(rs).getBytes("UTF-8"));
            logger.error("msgDownloadTokenError->token:{} fileName:{}", token, fileName);
            return null;
        }

        response.setContentType("text/html;charset=utf-8");
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        String downLoadPath = fileSavePath + "/" + dir + "/" + fileName;
        try {
            long fileLength = new File(downLoadPath).length();
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename="
                    + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        logger.info("msgDownload->userInfo:{} fileName:{}", userInfo, fileName);
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/GroupInviteKick", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus groupInviteKick(@RequestParam boolean isInvite, @RequestParam String uids, @RequestParam String groupIds, String msg) throws Exception {
        ResponseStatus rs = new ResponseStatus(true);
        Msg.GroupInviteKickRequest.Builder request = Msg.GroupInviteKickRequest.newBuilder().setIsInvite(isInvite);
        for (String uid : uids.split(",")) {
            request.addUids(Integer.valueOf(uid));
        }
        for (String groupId : groupIds.split(",")) {
            request.addGroupNum(groupId);
        }
        msgService.doTask(null, ProtocolType.Msg_GroupInviteKickRequest, request.build());
        logger.info("GroupInviteKick->isInvite:{},uids:{},groupIds:{},msg:{}", isInvite, uids, groupIds, msg);
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/getMsg", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus getMsg(@RequestParam Integer id, @RequestParam boolean isGroup, boolean isSend, @RequestParam int offset, @RequestParam int size) throws Exception {
        ResponseStatus rs = new ResponseStatus(true);
        rs.setData(msgService.getMsg(id, isGroup, isSend, offset, size));
        logger.info("GroupInviteKick->id:{},isGroup:{},isSend:{},offset:{},size:{},result:{}", id, isGroup, isSend, offset, size, rs);
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus getSave() throws Exception {
        ResponseStatus rs = new ResponseStatus(true);
        rs.setData(msgService.save());
        logger.info("msgSave->result:{}", rs);
        return rs;
    }
}

