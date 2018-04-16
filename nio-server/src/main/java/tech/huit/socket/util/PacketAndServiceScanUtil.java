package tech.huit.socket.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.dbke.socket.cp.Packet;
import tech.huit.socket.nio.server.NioServer;
import tech.huit.socket.nio.service.BaseService;

/**
 * 包扫描工具
 * @author huitang
 */
public class PacketAndServiceScanUtil {
	private static Logger logger = LoggerFactory.getLogger(PacketAndServiceScanUtil.class);

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		// 自定义过滤规则
		List<String> classFilters = new ArrayList<String>();
		ClassScanner handler = new ClassScanner(true, true, classFilters);
		Set<Class<?>> calssList;
		classFilters.add("*Service");
		classFilters.add("*Packet");
		calssList = handler.getPackageAllClasses("edu", true);
		System.out.println("class:" + calssList.size() + ":" + calssList + "\nscan use time:"
				+ (System.currentTimeMillis() - begin));
	}

	/**
	 * 注册可解析的包和处理包的服务,服务以*Service，包以*Packet命名
	 */
	public final static void registerPacketAndService(Map<Short,Object> packetMap,
			Map<Short, BaseService> packetToService, Map<Class<?>, BaseService> classToServiceMap, NioServer server,
			String classScanBasePath) {
		List<String> classFilters = new ArrayList<String>();
		ClassScanner handler = new ClassScanner(true, true, classFilters);
		Set<Class<?>> calssList = new LinkedHashSet<Class<?>>();
		classFilters.add("*Service");
		classFilters.add("*Packet");
		String[] path = classScanBasePath.split(",");
		for (String dir : path) {
			calssList.addAll(handler.getPackageAllClasses(dir, true));
		}
		Map<Class<?>, Object> scanClassMap = new HashMap<Class<?>, Object>();
		for (Class<?> clazz : calssList) {
			try {
				Object obj = clazz.getConstructor(new Class[] {}).newInstance(new Object[] {});
				scanClassMap.put(clazz, obj);
			} catch (Exception ignore) {
			}
		}

		for (Class<?> clazz : scanClassMap.keySet()) {
			String clazzName = clazz.getName();
			Object obj = scanClassMap.get(clazz);
			if (!clazzName.replace("edu.dbke.socket.cp.", "").contains(".")) {//排除cp下的基类包
				continue;
			} else if (obj instanceof Packet<?>) {
				registerPacket(packetMap, clazz, obj);
			} else if (obj instanceof BaseService) {
				registerService(packetToService, classToServiceMap, server, scanClassMap, clazz, obj, packetMap);
			}
		}

		logger.info("register Packet count:" + packetMap.size() + ":" + packetMap.toString());
		logger.info("register Serivce count:" + classToServiceMap.size() + ":" + classToServiceMap.toString());
		logger.info("mapped packet with Service count " + packetToService.size() + ":" + packetToService.toString());

		/*if (packetToService.size() < packetMap.size()) {
			Iterator<Short> it = packetMap.keySet().iterator();
			while (it.hasNext()) {
				Short protocl = it.next();
				if (!packetToService.containsKey(protocl)) {
					logger.warn("unhandled packet:" + getClassName(packetMap.get(protocl)) + ":type=" + protocl);
				}
			}
		}*/
	}

	private static void registerService(Map<Short, BaseService> packetToServiceMap,
			Map<Class<?>, BaseService> classToServiceMap, NioServer server, Map<Class<?>, Object> objMap,
			Class<?> clazz, Object obj, Map<Short, Object> packetMap) {
		BaseService baseService = (BaseService) obj;
		baseService.setServer(server);
		classToServiceMap.put(clazz, baseService);
		Set<Class<?>> packetClasses = baseService.getDisposePacket();
		if (null != packetClasses) {
			for (Class<?> packetClass : packetClasses) {//注册要处理的协议包
				Packet<?> packet = (Packet<?>) objMap.get(packetClass);
				if (null == packet) {
					logger.error("register Service: " + getClassName(baseService) + " with packet "
							+ getClassName(packetClass) + " error!,not a valid packet ");
				} else if (packetToServiceMap.containsKey(packet.type)) {
					logger.error("register Service: " + getClassName(baseService) + " with packet "
							+ getClassName(packet) + " error!,the packet has registered by "
							+ getClassName(packetToServiceMap.get(packet.type)));
				} else {
					packetToServiceMap.put(packet.type, baseService);
				}
			}
		}
		Set<Short> types = baseService.getDisposeType();
		if (null != types) {
			for (short type : types) {//注册要处理协议类型
				if (packetToServiceMap.containsKey(type)) {
					logger.error("register Service: " + getClassName(baseService) + " with type " + type
							+ " error!,the type has registered by " + getClassName(packetToServiceMap.get(type)));
				} else {
					packetToServiceMap.put(type, baseService);
				}
			}
		}
	}

	/**
	 * 添加协议到类的映射
	 * @param packetMap
	 * @param clazz
	 * @param obj
	 */
	private static void registerPacket(Map<Short, Object> packetMap, Object clazz, Object obj) {
		Packet<?> packet = (Packet<?>) obj;
		if (packet.type == -1) {//多种协议混合使用的协议包不做自动映射
			return;
		}
		if (packetMap.containsKey(packet.type)) {
			logger.error("register Packet:" + clazz + " error! " + packet.type + " has registered with"
					+ packetMap.get(packet.type));
		} else {
			packetMap.put(packet.type, clazz);
		}
	}

	private static String getClassName(Object obj) {
		String name = obj.toString();
		int index = name.indexOf('@');
		if (index > 0) {
			name = name.substring(0, index);
		}
		return name;
	}
}
