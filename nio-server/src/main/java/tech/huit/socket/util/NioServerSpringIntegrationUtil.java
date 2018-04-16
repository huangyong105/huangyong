package tech.huit.socket.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import tech.huit.socket.nio.server.NioServer;
import tech.huit.socket.nio.service.BaseService;

/**
 * Spring 集成工具类
 * @author huitang
 *
 */
public class NioServerSpringIntegrationUtil {
	/**
	 * 使用spring的实例替换
	 * @param nioServer 
	 * @param context
	 */
	public static void addSpringService(NioServer nioServer, ApplicationContext context) {
		//替换包处理服务实例
		Map<Short, BaseService> packetToServiceMap = nioServer.getPacketToSerivceMap();
		Iterator<Entry<Short, BaseService>> it = packetToServiceMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Short, BaseService> entry = it.next();
			try {
				BaseService baseService = context.getBean(entry.getValue().getClass());
				baseService.setServer(nioServer);
				packetToServiceMap.put(entry.getKey(), baseService);
			} catch (NoSuchBeanDefinitionException ignore) {
			}
		}
		//替换类到实例的映射
		Map<Class<?>, BaseService> classToServiceMap = nioServer.getClassToServiceMap();
		Iterator<Entry<Class<?>, BaseService>> test = classToServiceMap.entrySet().iterator();
		while (test.hasNext()) {
			Entry<Class<?>, BaseService> entry = test.next();
			try {
				BaseService baseService = context.getBean(entry.getValue().getClass());
				baseService.setServer(nioServer);
				BaseService oldService = classToServiceMap.get(entry.getKey());
				if (null != oldService) {
					oldService.destroy();
				}
				classToServiceMap.put(entry.getKey(), baseService);
			} catch (NoSuchBeanDefinitionException ignore) {
			}
		}
	}
}
