package tech.huit.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.huit.json.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpKit.class);
/*	public static Long getLongParameter(ServletRequest request, String name) {
		try {
			Long l = ServletRequestUtils.getLongParameter(request, name);
			return l;
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}
	public static Long getLongParameter(ServletRequest request, String name, long defaultVal) {
		return ServletRequestUtils.getLongParameter(request, name, defaultVal);
	}*/
/*
	public static String getStringParameter(ServletRequest request, String name) {
		try {
			String val = ServletRequestUtils.getStringParameter(request, name);
			return val == null ? null : val.trim();
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}*/

	public static String getStringParameter_ISO8859(ServletRequest request, String name) {
		try {
			String val = request.getParameter(name);
			if (val != null && val.length() > 0) {
				val = new String(val.getBytes("ISO8859_1"), "UTF-8");
				return val;
			}
		} catch (Exception e) {

		}
		return null;
	}

/*
	public static byte getByteParameter(ServletRequest request, String name, byte defaultValue) {
		try {
			Integer val = ServletRequestUtils.getIntParameter(request, name);
			if (val == null)
				return defaultValue;
			return val.byteValue();
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Byte getByteParameter(ServletRequest request, String name) {
		try {
			Integer val = ServletRequestUtils.getIntParameter(request, name);
			if (val != null)
				return val.byteValue();
			return null;
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Integer getIntParameter(ServletRequest request, String name) {
		try {
			return ServletRequestUtils.getIntParameter(request, name);
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getIntParameter(ServletRequest request, String name, int defaultValue) {
		return ServletRequestUtils.getIntParameter(request, name, defaultValue);
	}

	public static BigDecimal getBigDecimalParameter(ServletRequest request, String name) {
		try {
			String str = ServletRequestUtils.getStringParameter(request, name);
			if (str == null)
				return null;
			return new BigDecimal(str.trim());
		} catch (ServletRequestBindingException e) {
			throw new RuntimeException(e);
		}
	}
*/

	public static String getRequestIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ipaddress = "";
		if (request.getHeader("x-forwarded-for") == null) {
			ipaddress = request.getRemoteAddr();
		} else {
			ipaddress = request.getHeader("x-forwarded-for");
		}
		if (ipaddress != null && ipaddress.length() > 0) {
			String[] s = ipaddress.split(",");
			if (s.length == 1) {
				ipaddress = s[0].trim();
			} else if (s.length > 1) {
				ipaddress = s[s.length - 2].trim();
			}
		}
		return ipaddress;
	}

	public static Map<String, String> getAllStringParameter(ServletRequest request) {
		final Map<String, String> param = new HashMap<String, String>();
		final Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			final String name = e.nextElement().toString();
			final String value = request.getParameter(name);
			param.put(name, value);
		}
		return param;
	}



	// ----------------

	public static void out(HttpServletResponse response, Object data) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(data);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void outJson(HttpServletResponse response, Object data) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(Json.getJson().toJson(data));
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void out(HttpServletResponse response, String data) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			out.print(data);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 描述:获取 post 请求内容
	 * <pre>
	 * 举例：
	 * </pre>
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public  String getRequestPostStr(HttpServletRequest request)
		throws IOException {
		if ("POST".equals(request.getMethod())) {
			try {
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null)
					responseStrBuilder.append(inputStr);
				return responseStrBuilder.toString();
			} catch (Exception e) {
				logger.info(Logger.class + ",getRequestPostStr Exception:[" + e.getMessage() + "]", e);
			}
		}
		return "";
	}


	/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

	public static String getPostBody(HttpServletRequest request) throws IOException {
		if ("POST".equals(request.getMethod())) {
			final StringBuilder sb = new StringBuilder();

			BufferedReader bufferedReader = null;
			String content = "";

			try {
				//InputStream inputStream = request.getInputStream();
				//inputStream.available();
				//if (inputStream != null) {
				bufferedReader = request.getReader(); //new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead;
				while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
					sb.append(charBuffer, 0, bytesRead);
				}
				//} else {
				//        sb.append("");
				//}

			} catch (IOException ex) {
				throw ex;
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException ex) {
						throw ex;
					}
				}
			}
			String ret = sb.toString();
			return ret;
		}
		return null;
	}

}
