package tech.huit.uuc.web.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.huit.kit.HttpUtils;
import tech.huit.util.encrypt.SignUtils;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.entity.auth.User;
import tech.huit.uuc.message.MsgService;
import tech.huit.uuc.service.auth.AppService;
import tech.huit.web.ResponseStatus;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 类描述：　[基类的控制器]<br/>
 * 项目名称：[ProvideForLiveController]<br/>
 * 包名：　　[tech.huit.uuc.web.api.controller]<br/>
 * 创建人：　[黄勇(yong.huang@gmail.com)]<br/>
 * 创建时间：[2018/03/16 ]<br/>
 */
@Controller
@RequestMapping("/provide")
public class ProvideForLiveController {
    private static final Logger logger = LoggerFactory.getLogger(ProvideForLiveController.class);
    @Autowired
    AppService appService;

    @Autowired
    MsgService msgService ;

    @ResponseBody
    @RequestMapping(value = "/loadAppInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseStatus loadAppInfo(HttpServletRequest request, HttpServletResponse response ) {
        ResponseStatus rs = new ResponseStatus();
        List<App> appList = appService.listAll();
        rs.setStatus(true);
        rs.setData(appList);
/*        String json = Json.getJson().toJson(rs);
        logger.info("ResponseStatus:{} ", json);
       // HttpUtils.out(response,json);*/
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/query_im_send_msg", method = {RequestMethod.GET, RequestMethod.POST})
    /**
     * 通知电台发送推送消息
     */
    public  ResponseStatus query_im_send_msg (HttpServletRequest request, HttpServletResponse response ) {
        ResponseStatus  rs = new ResponseStatus () ;
        String  chatType = StringUtils.EMPTY;
        String  content = StringUtils.EMPTY;
        String  RecvID = StringUtils.EMPTY;
        String  SendID = StringUtils.EMPTY;
        String  MsgSeq = StringUtils.EMPTY;
        String  curtime = StringUtils.EMPTY;
        try {
            String appId =  request.getParameter("appkey");
            curtime =  request.getParameter("curtime");
            App app =  appService.selectById(NumberUtils.toInt(appId));
            String sign = SignUtils.checkSum(app.getAesKey()+curtime);
            String checksum = request.getParameter("checksum");
            if (!sign.equals(checksum)) {
                rs.setStatus(false);
                rs.setErrorCode("1");
                rs.setErrorMsg("签名错误");
                return rs;
            }
            JSONObject jsonObject = new JSONObject(HttpUtils.getPostBody(request)) ;
            chatType = jsonObject.optString("ChatType");
            content = jsonObject.optString("Content");
            RecvID = jsonObject.optString("RecvID");
            SendID = jsonObject.optString("SendID");
            MsgSeq = jsonObject.optString("MsgSeq");
            msgService.sendGroupMsg(content,RecvID,NumberUtils.toInt(curtime));
            rs.setStatus(true);
            logger.debug("params:chatType {}  content {} RecvID {} SendID {} MsgSeq{} curtime{}",chatType,content,RecvID,SendID,MsgSeq,curtime);
            return rs ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("推送消息发送异常",e);
            logger.debug("params:chatType {}  content {} RecvID {} SendID {} MsgSeq{} curtime{}",chatType,content,RecvID,SendID,MsgSeq,curtime);
        }
        rs.setStatus(false);
        rs.setErrorCode("2");
        rs.setErrorMsg("消息异常");
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/get_im_user_list_of_room", method = {RequestMethod.GET, RequestMethod.POST})
    public  ResponseStatus get_im_user_list_of_room (HttpServletRequest request, HttpServletResponse response ) {
        String  curtime = StringUtils.EMPTY;
        ResponseStatus  rs = new ResponseStatus () ;
        try {
            String appId =  request.getParameter("appkey");
            curtime =  request.getParameter("curtime");
            App app =  appService.selectById(NumberUtils.toInt(appId));
            String sign = SignUtils.checkSum(app.getAesKey()+curtime);
            String checksum = request.getParameter("checksum");
            if (!sign.equals(checksum)) {
                rs.setStatus(false);
                rs.setErrorCode("1");
                rs.setErrorMsg("签名错误");
                return rs;
            }
            rs.setStatus(true);
            JSONObject jsonObject = new JSONObject(HttpUtils.getPostBody(request)) ;
            String roomid = jsonObject.optString("RoomID") ;
            List<User> userList = msgService.loadGroupUser(roomid) ;
            rs.setData(userList);
            return rs ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("推送消息发送异常",e);
        }
        rs.setStatus(false);
        rs.setErrorCode("2");
        rs.setErrorMsg("消息异常");
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/get_im_user_list_of_room", method = {RequestMethod.GET, RequestMethod.POST})
    public  ResponseStatus add (HttpServletRequest request, HttpServletResponse response ) {
        String  curtime = StringUtils.EMPTY;
        ResponseStatus  rs = new ResponseStatus () ;
        try {
            String appId =  request.getParameter("appkey");
            curtime =  request.getParameter("curtime");
            App app =  appService.selectById(NumberUtils.toInt(appId));
            String sign = SignUtils.checkSum(app.getAesKey()+curtime);
            String checksum = request.getParameter("checksum");
            if (!sign.equals(checksum)) {
                rs.setStatus(false);
                rs.setErrorCode("1");
                rs.setErrorMsg("签名错误");
                return rs;
            }
            rs.setStatus(true);
            JSONObject jsonObject = new JSONObject(HttpUtils.getPostBody(request)) ;
            String roomid = jsonObject.optString("RoomID") ;
            List<User> userList = msgService.loadGroupUser(roomid) ;
            rs.setData(userList);
            return rs ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("推送消息发送异常",e);
        }
        rs.setStatus(false);
        rs.setErrorCode("2");
        rs.setErrorMsg("消息异常");
        return rs;
    }

}
