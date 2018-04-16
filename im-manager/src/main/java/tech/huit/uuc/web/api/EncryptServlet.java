package tech.huit.uuc.web.api;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.huit.conf.SystemConf;
import tech.huit.util.encrypt.RSAUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据加密服务，Base64不是URL安全的，请使用Post方式提交数据
 * 测试URL地址：http://uuc.huit.tech:8088/uuc-manager/encrypt/encode
 * <p>
 * 请求参数列表：
 * 1、resource_type 要加密的资源类型（必选）：[monitor_streaming:视频监控流媒体、call_streaming:通话流媒体]
 * 2、resource_id 要加密的资源id（必选）：如监控摄像头id、会议视频通道号
 * 3、authorize 授权code和有效期（必选）：使用RSA公钥（authorize_code=testAuthorizeCode&deadline=1494814358)加密。参见3-1、3-2
 * 3-1、authorize->authorize_code 授权code（必选）：1、业务系统使用授权指纹。2、SIP终端使用临时指纹[登录成功后分配]
 * 3-2、authorize->deadline 请求有效期（可选）：Unix时间戳，防止第三方截取数据用于重复生成加密信息，如果设定了值返回数据会带上deadline=xxx属性
 * 4、is_encrypt 反回数据是否需要加密（可选）:true/false，默认false
 * 5、is_only_visit_once 反回的数据是否添加只能访问一次的标识（可选）:true/false,默认false，如果设定为true返数据将附加上only_once=true属性
 * <p>
 * 返回数据：
 * UTF-8编码的Json数据格式：{"status":"0:成功/1:失败","data":"数据","error_code":"错误编码","error_msg":"错误信息描述"}
 * 流媒体服务加密数据：rtmp://221.236.245.41:1935/ssl/S+6/CA9kToAXeuXFIT+NojDGt8sCAggPS/Nzsas8FrHQWmCjWtkEbdehQaQTLfSz2yM/NRc/CAktaGumdnXNxyfWLvOT+HrQjlK8sGW7xNRQXcJjYhyuHReCVxrFGwg2NEEH+OrEvO+w06ZPq1qWtmj+rig5K3xbDGeS4IBOY+k=
 * 加密数据解密后内容：rtmp://221.236.245.41:1935/ssl/resource_id（资源标识）&deadline=1494814358（数据有效期）&only_once=true（是否只能使用一次）
 * <p>
 */
@WebServlet(value = "/encrypt/*", loadOnStartup = 1)
public class EncryptServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EncryptServlet.class);
    public static final String ERROR_MSG = "error_msg";

    private enum ResourceType {
        monitor_streaming("视频监控流媒体"), call_streaming("通话流媒体");

        private String value;

        ResourceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        //是否包含枚举项
        public static boolean contains(String name) {
            if (null == name) {
                return false;
            }
            //所有的枚举值
            ResourceType[] season = values();
            //遍历查找
            for (ResourceType s : season) {
                if (s.name().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }


    @Override
    public void destroy() {
    }

    @Override
    public void init() throws ServletException {
//        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 授权信息
     */
    private static Map<String, String> authorizes = new ConcurrentHashMap<>();
    private static PrivateKey privateKey = null;

    static {
        authorizes.put("testAuthorizeCode", "SIP_01");
        privateKey = RSAUtil.restorePrivateKey(SystemConf.getFromBase64("rsa.private.key"));
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String requestUrl = request.getRequestURL().toString();
        String fnType = requestUrl.substring(requestUrl.lastIndexOf('/') + 1);
        Map result = null;
        if ("encode".equals(fnType)) {
            result = encode(request);
        } else {
            logger.error("encrypt-urlError->" + requestUrl);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getOutputStream().write(JSON.toJSONString(result).getBytes("UTF-8"));
    }

    private Map encode(HttpServletRequest request) {
        String resource_type = request.getParameter("resource_type");
        String resource_id = request.getParameter("resource_id");
        String authorize = request.getParameter("authorize");
        String is_encrypt = request.getParameter("is_encrypt");
        String is_only_visit_once = request.getParameter("is_only_visit_once");
        logger.debug("encode request param->resource_type:{} resource_id:{} authorize:{} ", resource_type, resource_id, authorize);

        Map result = new HashMap();
        result.put("status", 1);
        if (null == resource_type) {
            result.put(ERROR_MSG, "resource_type不能为空");
            return result;
        } else {
            if (!ResourceType.contains(resource_type)) {
                result.put(ERROR_MSG, "resource_type数据[" + resource_type + "]值不是有效值");
                return result;
            }
        }
        if (null == resource_id) {
            result.put(ERROR_MSG, "resource_id不能为空");
            return result;
        }
        if (null == authorize) {
            result.put(ERROR_MSG, "authorize不能为空");
            return result;
        }
        String authorizeDecode = RSAUtil.decodeFromBase64(privateKey, authorize);//authorize_code=testAuthorizeCode&deadline=2017-05-15 22:22:22
        if (null == authorizeDecode) {
            result.put(ERROR_MSG, "authorize数据不能解密");
            return result;
        }
        String authorizeCode = null;//授权code
        Integer deadline = null;//数据有效截止时间
        String[] authorizeInfos = authorizeDecode.split("&");
        for (String keyValueMap : authorizeInfos) {
            String[] keyValue = keyValueMap.split("=");
            if (keyValue.length != 2) {
                result.put(ERROR_MSG, "authorize数据[" + keyValueMap + "]格式有误");
                return result;
            }
            if ("authorize_code".equals(keyValue[0])) {
                authorizeCode = keyValue[1];
            } else if ("deadline".equals(keyValue[0])) {
                try {
                    deadline = Integer.valueOf(keyValue[1]);
                } catch (Exception e) {
                }
                if (null == deadline) {
                    result.put(ERROR_MSG, "authorize数据[" + keyValue[1] + "]deadline不是整数");
                    return result;
                }
                if (System.currentTimeMillis() / 1000 > deadline) {
                    result.put(ERROR_MSG, "authorize数据[" + deadline + "]deadline已经过期");
                    return result;
                }
            } else {
                result.put(ERROR_MSG, "authorize数据[" + keyValueMap + "]不能识别的参数");
                return result;
            }
        }
        if (null == authorizeCode) {
            result.put(ERROR_MSG, "authorize数据有误，找不到authorizeCode参数");
            return result;
        }

        String userInfo = authorizes.get(authorizeCode);
        if (null == userInfo) {
            result.put(ERROR_MSG, "authorize数据有误，authorizeCode[" + authorizeCode + "]找不到授权信息");
            return result;
        }
        logger.debug("encode request param decode->authorize:{} authorizeDecode:{} userInfo:{}", authorize, authorizeDecode, userInfo);

        result.put("status", 0);

        ResourceType requestTyep = ResourceType.valueOf(resource_type);
        String data = null;
        if (ResourceType.monitor_streaming.equals(requestTyep)) {
            data = buildMonitorStreaming(is_encrypt, is_only_visit_once, deadline);
        } else if (ResourceType.call_streaming.equals(requestTyep)) {
            data = buildMonitorStreaming(is_encrypt, is_only_visit_once, deadline);
        }

        logger.debug("encode clear data->resource_id:{} data:{}", resource_id, data);
        result.put("data", data);
        return result;
    }

    private String buildMonitorStreaming(String is_encrypt, String is_only_visit_once, Integer deadline) {
        String data;
        StringBuffer dataBuf = new StringBuffer("rtmp://221.236.245.41:1935/");//TODO 从数据库中加载
        StringBuffer encodeData = new StringBuffer("resource_id");
        if ("true".equals(is_encrypt)) {
            dataBuf.append("ssl/");//用于标识数据是否加密
            if (null != deadline) {
                encodeData.append("&deadline=" + deadline);
            }
            if ("true".equals(is_only_visit_once)) {
                encodeData.append("&only_once=true");//需要客户端自己加缓存实现只能调用次的逻辑，内存使用内存会存在重启失效的问题
            }
            logger.debug("rsaEncodeData:" + encodeData);
            dataBuf.append(RSAUtil.encodeToBase64(privateKey, encodeData.toString()));
        } else {
            dataBuf.append(encodeData);
        }
        data = dataBuf.toString();
        return data;
    }
}
