/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：文件工厂，可以通过变量替换来动态改变文件地址
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class FileFactory
{

	/* static fields */
	/** 默认的变量前缀 */
	public static final char VARIABLE_PREFIX='[';
	/** 默认的变量后缀 */
	public static final char VARIABLE_SUFFIX=']';

	/** 当前的文件工厂 */
	protected static FileFactory factory;

	/* static methods */
	/** 获得当前的文件工厂 */
	public static FileFactory getFactory()
	{
		if(factory==null) factory=new JFileFactory();
		return factory;
	}
	/** 创建指定源地址的文件 */
	public static File getFile(String src)
	{
		if(src==null||src.length()==0) return null;
		if(factory==null) factory=new JFileFactory();
		String realSrc=factory.replaceVariable(src);
		if(realSrc==null) realSrc=src;
		return factory.getInstance(realSrc);
	}

	/* fields */
	/** 变量前缀 */
	char variablePrefix=VARIABLE_PREFIX;
	/** 变量后缀 */
	char variableSuffix=VARIABLE_SUFFIX;

	/* properties */
	/** 获得变量前缀 */
	public char getVariablePrefix()
	{
		return variablePrefix;
	}
	/** 设置变量前缀 */
	public void setVariablePrefix(char prefix)
	{
		variablePrefix=prefix;
	}
	/** 获得变量后缀 */
	public char getVariableSuffix()
	{
		return variableSuffix;
	}
	/** 设置变量后缀 */
	public void setVariableSuffix(char suffix)
	{
		variableSuffix=suffix;
	}
	/* methods */
	/** 替换变量，如果系统参数中有变量，但没有相应的变量替换则返回null */
	public String replaceVariable(String str)
	{
		// 使用变量解析对象源地址
		int i=str.indexOf(variablePrefix);
		if(i>=0)
		{
			int j=str.indexOf(variableSuffix);
			if(j<0) return str;
			String key=str.substring(i,j+1);
			String value=System.getProperty(key);
			if(value==null) return null;
			i=str.indexOf(key);
			if(i<0) return str;
			return str.substring(0,i)+value+str.substring(i+key.length());
		}
		return str;
	}
	/** 获得指定源地址的文件实例 */
	public abstract File getInstance(String src);

}