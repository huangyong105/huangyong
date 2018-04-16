/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.text;


/**
 * 类说明：文字效验器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class TextValidity
{

	/* fields */
	/** 允许通过的字符范围集 */
	char[] charRangeSet;
	/** 非法文字数组 */
	String[] invalidTexts;

	/* properties */
	/** 获得允许通过的字符范围集，两个字符为一组范围 */
	public char[] getCharRangeSet()
	{
		return charRangeSet;
	}
	/** 设置允许通过的字符范围集，两个字符为一组范围 */
	public void setCharRangeSet(char[] rangeSet)
	{
		charRangeSet=rangeSet;
	}
	/** 获得非法文字数组 */
	public String[] getInvalidTexts()
	{
		return invalidTexts;
	}
	/** 设置非法文字数组 */
	public void setInvalidTexts(String[] strings)
	{
		invalidTexts=strings;
	}
	/* methods */
	/** 验证指定的字符串的有效性，返回验证失败的文字 */
	public String valid(String str)
	{
		return valid(str,false);
	}
	/** 验证指定的字符串的有效性，caseless为忽略大小写，返回验证失败的文字 */
	public String valid(String str,boolean caseless)
	{
		if(str==null||str.length()<1) return null;
		char c=TextKit.valid(str,charRangeSet);
		if(c>0) return String.valueOf(c);
		String[] strs=invalidTexts;
		if(strs==null) return null;
		if(caseless)
		{
			str=str.toLowerCase();
			for(int i=0,n=strs.length;i<n;i++)
			{
				if(str.indexOf(strs[i].toLowerCase())>=0) return strs[i];
			}
		}
		else
		{
			for(int i=0,n=strs.length;i<n;i++)
			{
				if(str.indexOf(strs[i])>=0) return strs[i];
			}
		}
		return null;
	}

}