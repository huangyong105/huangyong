package tech.huit.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by huit on 2017/7/11.
 */
public class ServletRequestGetIp {
    public static String getIpAddr(HttpServletRequest request) {
        if (null == request) {
            return "";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        return clearIp(ip);
    }

    /**
     * 用于处理代理，移动4G等特殊IP
     *
     * @param ip
     * @return
     */
    private static String clearIp(String ip) {
        try {
            String[] ips = ip.split(",");
            for (int i = ips.length - 1; i >= 0; i--) {
                String curIp = ips[i].trim();
                if (curIp.matches("\\b([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\b")) {
                    return curIp;
                }
            }
        } catch (Exception error) {
            //any to do
        }
        return ip;
    }
}
