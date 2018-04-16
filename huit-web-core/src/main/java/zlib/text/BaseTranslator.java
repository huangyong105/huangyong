/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.text;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：文字转换器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class BaseTranslator extends Translator
{

	/* fields */
	/** 文字转换表 */
	Map textMap;

	/* constructors */
	/** 构造指定父文字转换器的文字转换器 */
	public BaseTranslator(Translator parent)
	{
		super(parent);
		textMap=new HashMap();
	}
	/* properties */
	/** 获得指定的转换文字 */
	public String getText(String str)
	{
		return (String)(textMap.get(str));
	}
	/** 添加指定的转换文字 */
	public void addText(String str,String text)
	{
		textMap.put(str,text);
	}
	/** 移除指定的转换文字 */
	public String removeText(String str)
	{
		return (String)(textMap.remove(str));
	}
	/** 移除全部转换文字 */
	public void clearTexts()
	{
		textMap.clear();
	}

}