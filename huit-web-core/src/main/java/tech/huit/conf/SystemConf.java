package tech.huit.conf;

import tech.huit.util.encrypt.UrlSafeBase64;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * @author huit
 * @department 成都-产品运营-商务智能-java
 * @description 系统配置文件，支持配置文件动态修改(需要实现SystemConfModifyListener接口，并手动注册监听)
 * @date 2014年10月23日 下午2:54:41
 */

public class SystemConf implements Runnable {
    private static Map<String, String> confMap = new HashMap<String, String>();
    public static boolean isWindos = "windows".equals(System.getProperties().get("sun.desktop"));
    public static String confFileDir;// 配置文件加载目录
    private static String filePath;
    private static Thread monitorThread;//配置文件修改监控线程
    private static boolean isStopMonitor;//是否停止监控

    static {
        filePath = Thread.currentThread().getContextClassLoader().getResource("conf.properties").getPath()
                .replaceAll("%20", " ");//放到common模块里拿不到配置
        int index = filePath.indexOf("jar!");
        if (index > 0) {//jar文件
            filePath = filePath.substring(5, filePath.lastIndexOf('/', index)) + "/conf.properties";
        }
        if (isWindos) {
            filePath = filePath.substring(1);//Paths.get(confFileDir)如果是windos下面不去掉斜杠会导致异常
        }
        confFileDir = filePath.substring(0, filePath.lastIndexOf('/') + 1);
        listeners = new Vector<SystemConfModifyListener>();
        loadProperties();
        startMonitor();
    }

    /**
     * @description
     * @author huit
     * @date 2014年12月8日 下午3:53:37
     */
    public static void startMonitor() {
        if (null == monitorThread || !monitorThread.isAlive()) {
            monitorThread = new Thread(new SystemConf(), "conf.properties modify monitor");
            monitorThread.setDaemon(true);
            monitorThread.start();
        }
    }

    /**
     * @description
     * @author huit
     * @date 2014年12月8日 下午3:53:37
     */
    public static void stopMonitor() {
        isStopMonitor = true;
    }

    private static Vector<SystemConfModifyListener> listeners;

    /**
     * @description 加载配置文件
     * @author huit
     * @date 2014年10月23日 上午10:58:58
     */
    private static void loadProperties() {
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);// 属性文件输入流
            prop.load(fis);// 将属性文件流装载到Properties对象中
            fis.close();// 关闭流
            confMap.clear();
            Enumeration<?> enumeration = prop.propertyNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                confMap.put(name, prop.getProperty(name).trim());
            }
            for (SystemConfModifyListener listener : listeners) {
                listener.onModify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据操作系统类型得到相应的配置值
     *
     * @param key
     * @return
     */
    public static String getBySystem(String key) {
        if (isWindos) {
            key += "Windows";
        } else {
            key += "Linux";
        }
        return confMap.get(key);
    }

    public static String get(String key) {
        return confMap.get(key);
    }

    /**
     * 获取路径，如果最后面一没有"/"则自动加上
     *
     * @param key
     * @return
     */
    public static String getPath(String key) {
        String path = confMap.get(key);
        if (null != path && path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> class_) {
        if (class_.equals(Integer.class)) {
            return (T) Integer.valueOf(confMap.get(key));
        } else if (class_.equals(Long.class)) {
            return (T) Long.valueOf(confMap.get(key));
        } else if (class_.equals(Boolean.class)) {
            return (T) Boolean.valueOf(confMap.get(key));
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        System.out.println(get("voipSipConfTransport"));
        System.out.println("query use time:" + (System.currentTimeMillis() - begin));
        System.in.read();
    }

    /**
     * description 配置文件改变监听线程
     *
     * @see Runnable#run()
     */
    @Override
    public void run() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Paths.get(confFileDir).register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            long lastModify = 0;
            while (!isStopMonitor) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if ("ENTRY_MODIFY".equals(event.kind().name())
                            && "conf.properties".equals(event.context().toString())) {
                        if (System.currentTimeMillis() - lastModify > 2000) {//一次修改可能连续触发两次修改事件
                            System.out.println("conf.properties changed!");
                            loadProperties();
                            lastModify = System.currentTimeMillis();
                        }
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
            watchService.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param listener
     * @description 注册修改监听
     * @author huit
     * @date 2014年10月23日 下午2:57:32
     */
    public static void addListerner(SystemConfModifyListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener
     * @description 删除监听
     * @author huit
     * @date 2014年10月23日 下午2:57:38
     */
    public static void removeListerner(SystemConfModifyListener listener) {
        listeners.remove(listener);
    }

    public static Map<String, String> getConfMap() {
        return confMap;
    }

    public static byte[] getFromBase64(String key) {
        return UrlSafeBase64.decode(key);
    }
}
