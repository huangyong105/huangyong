/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import zlib.text.TextKit;

/**
 * 类说明：域功能函数库
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class FieldKit
{

	/* static fields */
	/** 库信息 */
	public static final String toString=FieldKit.class.getName();

	/* static methods */
	/** 创建指定参数的域对象 */
	public static BooleanField create(String name,boolean value)
	{
		BooleanField obj=new BooleanField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static ByteField create(String name,byte value)
	{
		ByteField obj=new ByteField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static ShortField create(String name,short value)
	{
		ShortField obj=new ShortField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static CharField create(String name,char value)
	{
		CharField obj=new CharField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static IntField create(String name,int value)
	{
		IntField obj=new IntField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static LongField create(String name,long value)
	{
		LongField obj=new LongField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static FloatField create(String name,float value)
	{
		FloatField obj=new FloatField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static DoubleField create(String name,double value)
	{
		DoubleField obj=new DoubleField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static StringField create(String name,String value)
	{
		StringField obj=new StringField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 创建指定参数的域对象 */
	public static ByteArrayField create(String name,byte[] value)
	{
		ByteArrayField obj=new ByteArrayField();
		obj.name=name;
		obj.value=value;
		return obj;
	}
	/** 利用反射机制获取类或实例的公共域 */
	public static FieldValue getField(Class c,Object o,String fieldName)
	{
		if(fieldName==null)
			throw new IllegalArgumentException(toString
				+" getField, null fieldName, class="+c.getName()+", object="
				+o);
		Field f;
		try
		{
			f=c.getField(fieldName);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" getField, field not found, class="+c.getName()
				+", fieldName="+fieldName,e);
		}
		try
		{
			return new FieldValue(f.getType(),f.get(o));
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString+" getField, get, class="
				+c.getName()+", object="+o+", fieldName="+fieldName,e);
		}
	}
	/** 利用反射机制获取类或实例域，包括所有已声明的域 */
	public static FieldValue getDeclaredField(Class c,Object o,
		String fieldName)
	{
		if(fieldName==null)
			throw new IllegalArgumentException(toString
				+" getDeclaredField, null fieldName, class="+c.getName()
				+", object="+o);
		Field f=null;
		Class cc=c;
		Field[] fields;
		try
		{
			do
			{
				fields=cc.getDeclaredFields();
				for(int i=fields.length-1;i>=0;i--)
				{
					if(!fieldName.equals(fields[i].getName())) continue;
					f=fields[i];
					break;
				}

			}
			while(f==null&&(cc=cc.getSuperclass())!=null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" getDeclaredField, field exception, class="+c.getName()
				+", fieldName="+fieldName,e);
		}
		if(f==null)
			throw new RuntimeException(toString
				+" getDeclaredField, field not found, class="+c.getName()
				+", fieldName="+fieldName,null);
		try
		{
			f.setAccessible(true);
			return new FieldValue(f.getType(),f.get(o));
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" getDeclaredField, get, class="+c.getName()+", object="+o
				+", fieldName="+fieldName,e);
		}
	}
	/** 利用反射机制设置类或实例的公共域（支持模糊设置） */
	public static void setField(Class c,Object o,String fieldName,
		FieldValue arg)
	{
		if(fieldName==null)
			throw new IllegalArgumentException(toString
				+" setField, null fieldName, class="+c.getName()+", object="
				+o);
		Field f;
		try
		{
			f=c.getField(fieldName);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" setField, field not found, class="+c.getName()
				+", fieldName="+fieldName,e);
		}
		Class type=f.getType();
		if(arg==null)
		{
			if(type.isPrimitive())
				throw new IllegalArgumentException(toString
					+" setField, invalid null arg, class="+c.getName()
					+", object="+o+", fieldName="+fieldName);
		}
		else if(arg.type==null)
		{
			if(String.class==type||type.isPrimitive())
			{
				if(!adaptArgument(type,arg))
					throw new IllegalArgumentException(toString
						+" setField, adapt arg, class="+c.getName()
						+", object="+o+", fieldName="+fieldName);
			}
			else
				throw new IllegalArgumentException(toString
					+" setField, invalid field, class="+c.getName()
					+", object="+o+", fieldName="+fieldName);
		}
		else
		{
			if(!type.isAssignableFrom(arg.type))
				throw new IllegalArgumentException(toString
					+" setField, assignable arg, class="+c.getName()
					+", object="+o+", fieldName="+fieldName);
		}
		try
		{
			f.set(o,(arg!=null)?arg.value:null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString+" setField, set, class="
				+c.getName()+", fieldName="+fieldName,e);
		}
	}
	/** 利用反射机制设置类或实例域（支持模糊设置），包括所有已声明的域 */
	public static void setDeclaredField(Class c,Object o,String fieldName,
		FieldValue arg)
	{
		if(fieldName==null)
			throw new IllegalArgumentException(toString
				+" setDeclaredField, null fieldName, class="+c.getName()
				+", object="+o);
		Field f=null;
		Class cc=c;
		Field[] fields;
		try
		{
			do
			{
				fields=cc.getDeclaredFields();
				for(int i=fields.length-1;i>=0;i--)
				{
					if(!fieldName.equals(fields[i].getName())) continue;
					f=fields[i];
					break;
				}

			}
			while(f==null&&(cc=cc.getSuperclass())!=null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" setDeclaredField, field exception, class="+c.getName()
				+", fieldName="+fieldName,e);
		}
		if(f==null)
			throw new RuntimeException(toString
				+" setDeclaredField, field not found, class="+c.getName()
				+", fieldName="+fieldName,null);
		Class type=f.getType();
		if(arg==null)
		{
			if(type.isPrimitive())
				throw new IllegalArgumentException(toString
					+" setDeclaredField, invalid null arg, class="
					+c.getName()+", object="+o+", fieldName="+fieldName);
		}
		else if(arg.type==null)
		{
			if(String.class==type||type.isPrimitive())
			{
				if(!adaptArgument(type,arg))
					throw new IllegalArgumentException(toString
						+" setDeclaredField, adapt arg, class="+c.getName()
						+", object="+o+", fieldName="+fieldName);
			}
			else
				throw new IllegalArgumentException(toString
					+" setDeclaredField, invalid field, class="+c.getName()
					+", object="+o+", fieldName="+fieldName);
		}
		else
		{
			if(!type.isAssignableFrom(arg.type))
				throw new IllegalArgumentException(toString
					+" setDeclaredField, assignable arg, class="+c.getName()
					+", object="+o+", fieldName="+fieldName);
		}
		try
		{
			f.setAccessible(true);
			f.set(o,(arg!=null)?arg.value:null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" setDeclaredField, set, class="+c.getName()+", fieldName="
				+fieldName,e);
		}
	}
	/** 利用反射机制调用类或实例的公共方法（支持模糊调用） */
	public static FieldValue invoke(Class c,Object o,String methodName,
		FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		Method m=adaptMethod(c,methodName,args);
		if(m==null)
			throw new RuntimeException(toString
				+" invoke, method not found, class="+c.getName()
				+", methodName="+methodName+" args="+n);
		Object[] objs=null;
		if(n>0)
		{
			objs=new Object[n];
			for(int i=0;i<n;i++)
			{
				if(args[i]!=null) objs[i]=args[i].value;
			}
		}
		try
		{
			return new FieldValue(m.getReturnType(),m.invoke(o,objs));
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString+" invoke, invoke, class="
				+c.getName()+", object="+o+", methodName="+methodName
				+" args="+n,e);
		}
	}
	/** 利用反射机制调用类或实例方法（支持模糊调用），包括所有已声明的方法 */
	public static FieldValue invokeDeclared(Class c,Object o,
		String methodName,FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		Method m=adaptDeclaredMethod(c,methodName,args);
		if(m==null)
			throw new RuntimeException(toString
				+" invokeDeclared, method not found, class="+c.getName()
				+", methodName="+methodName+" args="+n);
		Object[] objs=null;
		if(n>0)
		{
			objs=new Object[n];
			for(int i=0;i<n;i++)
			{
				if(args[i]!=null) objs[i]=args[i].value;
			}
		}
		try
		{
			m.setAccessible(true);
			return new FieldValue(m.getReturnType(),m.invoke(o,objs));
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" invokeDeclared, invoke, class="+c.getName()+", object="+o
				+", methodName="+methodName+" args="+n,e);
		}
	}
	/** 适配类或实例的公共方法的模糊参数，返回对应的方法，同时修正了模糊参数 */
	public static Method adaptMethod(Class c,String methodName,
		FieldValue[] args)
	{
		if(methodName==null||methodName.length()<=0) return null;
		int n=(args!=null)?args.length:0;
		Class[] clazz;
		Method[] methods=c.getMethods();
		for(int i=0,m=methods.length,j=0;i<m;i++)
		{
			if(!methodName.equals(methods[i].getName())) continue;
			clazz=methods[i].getParameterTypes();
			if(clazz.length!=n) continue;
			if(n==0) return methods[i];
			for(j=0;j<n;j++)
			{
				if(args[j]==null)
				{
					if(clazz[j].isPrimitive()) break;
					continue;
				}
				if(args[j].type==null)
				{
					if(String.class==clazz[j]||clazz[j].isPrimitive())
						continue;
					break;
				}
				if(!clazz[j].isAssignableFrom(args[j].type)) break;
			}
			if(j<n) continue;
			// 匹配模糊参数
			for(j=0;j<n;j++)
			{
				if(args[j]==null) continue;
				if(args[j].type!=null) continue;
				if(!adaptArgument(clazz[j],args[j])) break;
			}
			if(j<n) return null;
			return methods[i];
		}
		return null;
	}
	/**
	 * 适配类或实例方法的模糊参数， 返回对应的方法， 同时修正了模糊参数，
	 * 包括所有已声明的方法
	 */
	public static Method adaptDeclaredMethod(Class c,String methodName,
		FieldValue[] args)
	{
		if(methodName==null||methodName.length()<=0) return null;
		int n=(args!=null)?args.length:0;
		Method[] methods;
		Class[] clazz;
		Class cc=c;
		while(cc!=null)
		{
			methods=cc.getDeclaredMethods();
			for(int i=0,m=methods.length,j=0;i<m;i++)
			{
				if(!methodName.equals(methods[i].getName())) continue;
				clazz=methods[i].getParameterTypes();
				if(clazz.length!=n) continue;
				if(n==0) return methods[i];
				for(j=0;j<n;j++)
				{
					if(args[j]==null)
					{
						if(clazz[j].isPrimitive()) break;
						continue;
					}
					if(args[j].type==null)
					{
						if(String.class==clazz[j]||clazz[j].isPrimitive())
							continue;
						break;
					}
					if(!clazz[j].isAssignableFrom(args[j].type)) break;
				}
				if(j<n) continue;
				// 匹配模糊参数
				for(j=0;j<n;j++)
				{
					if(args[j]==null) continue;
					if(args[j].type!=null) continue;
					if(!adaptArgument(clazz[j],args[j])) break;
				}
				if(j<n) return null;
				return methods[i];
			}
			cc=cc.getSuperclass();
		}
		return null;
	}
	/** 利用反射机制调用公共的构造方法（支持模糊调用） */
	public static Object construct(Class c,FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		if(n==0)
		{
			try
			{
				return c.newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(toString
					+" construct, newInstance, class="+c.getName(),e);
			}
		}
		Constructor constructor=adaptConstructor(c,args);
		if(constructor==null)
			throw new RuntimeException(toString
				+" construct, constructor not found, class="+c.getName()
				+", args="+n);
		Object[] objs=new Object[n];
		for(int i=0;i<n;i++)
		{
			if(args[i]!=null) objs[i]=args[i].value;
		}
		try
		{
			return constructor.newInstance(objs);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" construct, newInstance args, class="+c.getName()
				+", args="+n,e);
		}
	}
	/** 利用反射机制调用构造方法（支持模糊调用），包括所有已声明的构造方法 */
	public static Object constructDeclared(Class c,FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		Constructor constructor=adaptDeclaredConstructor(c,args);
		if(constructor==null)
			throw new RuntimeException(toString
				+" construct, constructor not found, class="+c.getName()
				+", args="+n);
		Object[] objs=new Object[n];
		for(int i=0;i<n;i++)
		{
			if(args[i]!=null) objs[i]=args[i].value;
		}
		try
		{
			constructor.setAccessible(true);
			return constructor.newInstance(objs);
		}
		catch(Exception e)
		{
			throw new RuntimeException(toString
				+" construct, newInstance args, class="+c.getName()
				+", args="+n,e);
		}
	}
	/** 适配类的公共构造方法的模糊参数，返回对应的构造方法，同时修正了模糊参数 */
	public static Constructor adaptConstructor(Class c,FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		Class[] clazz;
		Constructor[] constructors=c.getConstructors();
		for(int i=0,m=constructors.length,j=0;i<m;i++)
		{
			clazz=constructors[i].getParameterTypes();
			if(clazz.length!=n) continue;
			if(n==0) return constructors[i];
			for(j=0;j<n;j++)
			{
				if(args[j]==null)
				{
					if(clazz[j].isPrimitive()) break;
					continue;
				}
				if(args[j].type==null)
				{
					if(String.class==clazz[j]||clazz[j].isPrimitive())
						continue;
					break;
				}
				if(!clazz[j].isAssignableFrom(args[j].type)) break;
			}
			if(j<n) continue;
			// 匹配模糊参数
			for(j=0;j<n;j++)
			{
				if(args[j]!=null) continue;
				if(args[j].type!=null) continue;
				if(!adaptArgument(clazz[j],args[j])) break;
			}
			if(j<n) continue;
			return constructors[i];
		}
		return null;
	}
	/**
	 * 适配类的构造方法的模糊参数， 返回对应的构造方法， 同时修正了模糊参数，
	 * 包括所有已声明的方法
	 */
	public static Constructor adaptDeclaredConstructor(Class c,
		FieldValue[] args)
	{
		int n=(args!=null)?args.length:0;
		Constructor[] constructors;
		Class[] clazz;
		Class cc=c;
		while(cc!=null)
		{
			constructors=c.getDeclaredConstructors();
			for(int i=0,m=constructors.length,j=0;i<m;i++)
			{
				clazz=constructors[i].getParameterTypes();
				if(clazz.length!=n) continue;
				if(n==0) return constructors[i];
				for(j=0;j<n;j++)
				{
					if(args[j]==null)
					{
						if(clazz[j].isPrimitive()) break;
						continue;
					}
					if(args[j].type==null)
					{
						if(String.class==clazz[j]||clazz[j].isPrimitive())
							continue;
						break;
					}
					if(!clazz[j].isAssignableFrom(args[j].type)) break;
				}
				if(j<n) continue;
				// 匹配模糊参数
				for(j=0;j<n;j++)
				{
					if(args[j]!=null) continue;
					if(args[j].type!=null) continue;
					if(!adaptArgument(clazz[j],args[j])) break;
				}
				if(j<n) continue;
				return constructors[i];
			}
			cc=cc.getSuperclass();
		}
		return null;
	}
	/**
	 * 根据参数类型， 适配模糊参数， 模糊参数只存在于基本类型或字符串中，
	 * 且其值必须为字符串。
	 */
	public static boolean adaptArgument(Class c,FieldValue arg)
	{
		if(c==String.class)
		{
			arg.type=c;
			return true;
		}
		try
		{
			if(c==int.class)
				arg.value=new Integer(TextKit.parseInt((String)arg.value));
			else if(c==long.class)
				arg.value=new Long(TextKit.parseLong((String)arg.value));
			else if(c==boolean.class)
				arg.value=new Boolean(TextKit
					.parseBoolean((String)arg.value));
			else if(c==byte.class)
				arg.value=new Byte((byte)TextKit.parseInt((String)arg.value));
			else if(c==short.class)
				arg.value=new Short((short)TextKit
					.parseInt((String)arg.value));
			else if(c==char.class)
				arg.value=new Character(((String)arg.value).charAt(0));
			else if(c==float.class)
				arg.value=new Float((String)arg.value);
			else if(c==double.class)
				arg.value=new Double((String)arg.value);
			else
				return false;
			arg.type=c;
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/* constructors */
	private FieldKit()
	{
	}

}