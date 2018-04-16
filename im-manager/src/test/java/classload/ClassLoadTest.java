package classload;

/**
 * Created by huit on 2017/6/29.
 */
public class ClassLoadTest {
    public static void main(String[] args) {
//调用class加载器
        ClassLoader cl = ClassLoadTest.class.getClassLoader();
        System.out.println(cl);
//调用上一层Class加载器
        ClassLoader clParent = cl.getParent();
        System.out.println(clParent);
//调用根部Class加载器
        ClassLoader clRoot = clParent.getParent();//null
        System.out.println(clRoot);

//        BootstrapLoader ： sun.boot.class.path
//        ExtClassLoader: java.ext.dirs
//        AppClassLoader: java.class.path

        System.out.println(System.getProperty("sun.boot.class.path"));
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println(System.getProperty("java.class.path"));
    }
}
