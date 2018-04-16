package edu.dbke.socket.cp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.dbke.socket.cp.Desc;
import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;

/**
 * 协议包数据一致性检测工具
 *
 * @author huitang
 */
public class PacketCheckUitl {
    private static final HashMap<Object, Object> map = new HashMap();

    public static final void check() throws Exception {
        Field[] fileds = ProtocolType.class.getFields();
        for (Field field : fileds) {
            Object type = field.get(null);
            Object value = field.getName();
            if (!map.containsKey(type)) {
                map.put(type, value);
            } else {
                System.err.println("重复的协议定义:" + type + ":" + value);
            }
        }
        System.out.println("定义协议：" + map.size() + "种");
    }

    public static final String getCode(String str) throws Exception {
        Field[] fileds = ProtocolType.class.getFields();
        for (Field field : fileds) {
            Desc desc = field.getAnnotation(Desc.class);
            if (field.getName().equals(str)) {
                return desc.key() + "";
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        check();
        Set<Class<? extends Packet<?>>> classSet = PacketScanUtil.getPacketClasses("edu.dbke.socket.cp");
        Iterator<Class<? extends Packet<?>>> it = classSet.iterator();
        HashMap<Short, Class<?>> defaultType = new HashMap<Short, Class<?>>();
        while (it.hasNext()) {
            Class<? extends Packet<?>> c = it.next();
            try {
                if (!c.getName().endsWith("Packet") || "edu.dbke.socket.cp.Packet".equals(c.getName())) {
                    continue;
                }

                Packet<?> packet1 = (Packet<?>) fromClass(c);
                Packet<?> packet2 = (Packet<?>) fromClass(c);
                if (null != packet1) {
                    if (defaultType.containsKey(packet1.type) && packet1.type != -1) {
                        System.err.println(packet1.getClass() + "，默认协议值：" + packet1.type + "已经和"
                                + defaultType.get(packet1.type) + "绑定");
                    } else {
                        defaultType.put(packet1.type, packet1.getClass());
                    }
                    setData(packet1);
                    byte[] b1 = (packet1).writeByteObject();
                    (packet2).readByteObject(b1);
                    byte[] b2 = (packet2).writeByteObject();

                    cmpData(packet1, packet2);

                    if (!cmpByteBuffer(b1, b2)) {
                        System.out.println(c.getName() + ":ByteBuffer比较flase");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean cmpByteBuffer(byte[] b1, byte[] b2) {
        if (b1.length != b2.length)
            return false;
        for (int i = 0; i < b1.length || i < b2.length; ++i) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    public static Object fromClass(Class<?> c) {
        Object objectCopy = null;

        try {
            if (!c.getName().toLowerCase().contains("abstract")) {
                objectCopy = c.getConstructor(new Class[]{}).newInstance(new Object[]{});
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Class " + c.getName() + " can't be constructed");
        }

        return objectCopy;
    }

    public static void cmpData(Object p1, Object p2) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // 获得对象的类型
        Class<?> classType = p1.getClass();
        //		System.out.println("对象的类型是：" + classType.toString());
        classType = p2.getClass();
        // 获得对象的所有属性
        java.lang.reflect.Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) { // 获取数组中对应的属性
            Field field = fields[i];
            try {
            } catch (Exception e) {
                //e.printStackTrace();
            }
            try {
                Object value1 = field.get(p1);//getMethod.invoke(p1, new Object[] {});
                Object value2 = field.get(p2);//getMethod.invoke(p2, new Object[] {});
                boolean b = true;
                if (value1 instanceof byte[]) {
                    byte[] tmp = (byte[]) value1;
                    byte[] tmp2 = (byte[]) value2;
                    for (int j = 0; j < tmp2.length; j++) {
                        if (tmp[j] != tmp2[j]) {
                            b = false;
                            break;
                        }
                    }
                } else {
                    b = (value1.equals(value2));
                }
                if (!b) {
                    System.err.println(p2.getClass() + "\t" + field.getName() + "\terror！");
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 数据设置
     *
     * @param p
     * @throws Exception
     */
    public static void setData(Packet<?> p) throws Exception {
        // 获得对象的类型
        @SuppressWarnings("unchecked")
        Class<? extends Packet<?>> classType = (Class<? extends Packet<?>>) p.getClass();
        // 获得对象的所有属性
        java.lang.reflect.Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) { // 获取数组中对应的属性
            Field field = fields[i];
            try {
                Object o = generateData(fields[i].getType(), i);
                field.set(p, o);
            } catch (Exception e) {
            }
        }
    }

    public static Object generateData(Class<?> type, int i) throws ClassNotFoundException {
        if (type.toString().equals("byte")) {
            return (byte) (Byte.MAX_VALUE * Math.random());
        } else if (type.toString().equals("short")) {
            return (short) (Short.MAX_VALUE * Math.random());
        } else if (type.toString().equals("int")) {
            return Integer.MAX_VALUE * Math.random();
        } else if (type.toString().equals("long")) {
            return Long.MAX_VALUE * Math.random();
        } else if (type.toString().equals("float")) {
            return Float.MAX_VALUE * Math.random();
        } else if (type.toString().equals("double")) {
            return Double.MAX_VALUE * Math.random();
        } else if (type.equals(Class.forName("java.lang.String"))) {
            return Math.random() + "";
        } else if (type.equals(Class.forName("java.util.Date"))) {
            Date data = new Date();
            data.setTime(data.getTime() / 1000 * 1000);
            return data;
        } else if (type.equals(Class.forName("[B"))) {
            return new byte[]{(byte) (Byte.MAX_VALUE * Math.random())};
        } else if (type.toString().equals("boolean")) {
            if (i % 2 == 0) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }
}
