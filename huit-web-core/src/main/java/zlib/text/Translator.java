/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.text;

/**
 * 类说明：文字转换接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Translator
{

	/* static fields */
	/** 变量1,2,3,4的默认标识符 */
	public static final String VARIABLE1="%$1%",VARIABLE2="%$2%",
					VARIABLE3="%$3%",VARIABLE4="%$4%";
	/* static fields */
	/** 当前的文字转换器 */
	protected static Translator translator=new Translator(null);

	/* static methods */
	/** 获得当前的文字转换器 */
	public static Translator getInstance()
	{
		return translator;
	}
	/** 文字转换方法 */
	public static String trans(String str)
	{
		return translator.translate(str);
	}
	/** 文字转换方法 */
	public static String trans(String str,String value)
	{
		return translator.translate(str,value);
	}
	/** 文字转换方法 */
	public static String trans(String str,String v1,String v2)
	{
		return translator.translate(str,v1,v2);
	}
	/** 文字转换方法 */
	public static String trans(String str,String v1,String v2,String v3)
	{
		return translator.translate(str,v1,v2,v3);
	}
	/** 文字转换方法 */
	public static String trans(String str,String v1,String v2,String v3,
		String v4)
	{
		return translator.translate(str,v1,v2,v4);
	}

	/* fields */
	/** 父文字转换器 */
	Translator parent;
	/** 变量1的标识符 */
	String variable1;
	/** 变量2的标识符 */
	String variable2;
	/** 变量3的标识符 */
	String variable3;
	/** 变量4的标识符 */
	String variable4;

	/* constructors */
	/** 构造指定父文字转换器的文字转换器 */
	public Translator(Translator parent)
	{
		this.parent=parent;
	}
	/* properties */
	/** 获得父文字转换器 */
	public Translator getParent()
	{
		return parent;
	}
	/** 获得变量1的标识符 */
	public String getVariable1()
	{
		return variable1;
	}
	/** 设置变量1的标识符 */
	public void setVariable1(String str)
	{
		variable1=str;
	}
	/** 获得变量2的标识符 */
	public String getVariable2()
	{
		return variable2;
	}
	/** 设置变量2的标识符 */
	public void setVariable2(String str)
	{
		variable2=str;
	}
	/** 获得变量3的标识符 */
	public String getVariable3()
	{
		return variable3;
	}
	/** 设置变量3的标识符 */
	public void setVariable3(String str)
	{
		variable3=str;
	}
	/** 获得变量4的标识符 */
	public String getVariable4()
	{
		return variable4;
	}
	/** 设置变量4的标识符 */
	public void setVariable4(String str)
	{
		variable4=str;
	}
	/** 获得指定的转换文字 */
	public String getText(String str)
	{
		return null;
	}
	/** 添加指定的转换文字 */
	public void addText(String str,String text)
	{
	}
	/** 移除指定的转换文字 */
	public String removeText(String str)
	{
		return null;
	}
	/* methods */
	/** 将自身配置为当前的文字转换器 */
	public void configure()
	{
		translator=this;
	}
	/** 文字转换方法 */
	public String translateText(String str)
	{
		String text=getText(str);
		if(text!=null) return text;
		return (parent!=null)?parent.translateText(str):null;
	}
	/** 文字转换方法 */
	public String translate(String str)
	{
		String text=translateText(str);
		return (text!=null)?text:str;
	}
	/** 文字转换方法 */
	public String translate(String str,String value)
	{
		String text=translateText(str);
		if(text==null) return str;
		return TextKit.replaceAll(text,variable1,value);
	}
	/** 文字转换方法 */
	public String translate(String str,String v1,String v2)
	{
		String text=translateText(str);
		if(text==null) return str;
		text=TextKit.replaceAll(text,variable1,v1);
		return TextKit.replaceAll(text,variable2,v2);
	}
	/** 文字转换方法 */
	public String translate(String str,String v1,String v2,String v3)
	{
		String text=translateText(str);
		if(text==null) return str;
		text=TextKit.replaceAll(text,variable1,v1);
		text=TextKit.replaceAll(text,variable2,v2);
		return TextKit.replaceAll(text,variable3,v3);
	}
	/** 文字转换方法 */
	public String translate(String str,String v1,String v2,String v3,
		String v4)
	{
		String text=translateText(str);
		if(text==null) return str;
		text=TextKit.replaceAll(text,variable1,v1);
		text=TextKit.replaceAll(text,variable2,v2);
		text=TextKit.replaceAll(text,variable3,v3);
		return TextKit.replaceAll(text,variable4,v4);
	}

}