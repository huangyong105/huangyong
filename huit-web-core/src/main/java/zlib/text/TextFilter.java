/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.text;

/**
 * 类说明：文字过滤类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class TextFilter
{

	/* static fields */
	/** 默认的限制长度，1兆 */
	public static final int LIMIT_ENGTH=0xfffff;

	/* fields */
	/** 限制长度 */
	int limitLength=LIMIT_ENGTH;
	/** 忽略字符数组 */
	char[] ignoreChars={};
	/** 替换字符数组 */
	char[] replaceChars={};
	/** 忽略文字数组 */
	String[] ignoreTexts={};
	/** 替换文字数组 */
	String[] replaceTexts={};

	/* properties */
	/** 获得限制长度 */
	public int getLimitLength()
	{
		return limitLength;
	}
	/** 设置限制长度 */
	public void setLimitLength(int length)
	{
		if(length<0) length=0;
		limitLength=length;
	}
	/** 获得忽略字符数组 */
	public char[] getIgnoreChars()
	{
		return ignoreChars;
	}
	/** 设置忽略字符数组 */
	public void setIgnoreChars(char[] chars)
	{
		if(chars==null) chars=new char[0];
		ignoreChars=chars;
	}
	/** 获得忽略文字数组 */
	public String[] getIgnoreTexts()
	{
		return ignoreTexts;
	}
	/** 设置忽略文字数组 */
	public void setIgnoreTexts(String[] strings)
	{
		if(strings==null) strings=new String[0];
		ignoreTexts=strings;
	}
	/** 获得替换字符数组 */
	public char[] getReplaceChars()
	{
		return replaceChars;
	}
	/** 设置替换字符数组 */
	public void setReplaceChars(char[] chars)
	{
		if(chars==null) chars=new char[0];
		replaceChars=chars;
	}
	/** 获得替换文字数组 */
	public String[] getReplaceTexts()
	{
		return replaceTexts;
	}
	/** 设置替换文字数组 */
	public void setReplaceTexts(String[] strings)
	{
		if(strings==null) strings=new String[0];
		replaceTexts=strings;
	}
	/* methods */
	/** 过滤指定的字符串，忽略或替换其中的字符和文字 */
	public String filter(String str)
	{
		return filter(str,false,new CharBuffer());
	}
	/** 过滤指定的字符串，忽略或替换其中的字符和文字，caseless为忽略大小写 */
	public String filter(String str,boolean caseless)
	{
		return filter(str,caseless,new CharBuffer());
	}
	/** 过滤指定的字符串，忽略或替换其中的字符和文字，caseless为忽略大小写 */
	public String filter(String str,boolean caseless,CharBuffer cb)
	{
		if(str==null||str.length()<1||str.length()>limitLength) return "";
		char[] ignores=ignoreChars,replaces=replaceChars;
		int n=str.length();
		cb.clear();
		int i=0,n1=ignores.length,n2=replaces.length-1;
		int j;
		char c;
		while(i<n)
		{
			c=str.charAt(i++);
			for(j=0;j<n1&&c!=ignores[j];j++)
				;
			if(j<n1) continue;
			for(j=0;j<n2;j+=2)
			{
				if(c!=replaces[j]) continue;
				c=replaces[j+1];
				break;
			}
			cb.append(c);
		}
		if(cb.length()==0) return "";
		str=cb.getString();
		String[] strs=ignoreTexts;
		for(i=0,n=strs.length;i<n;i++)
			str=TextKit.replaceAll(str,strs[i],"",caseless,cb);
		strs=replaceTexts;
		for(i=0,n=strs.length-1;i<n;i+=2)
			str=TextKit.replaceAll(str,strs[i],strs[i+1],caseless,cb);
		return str;
	}

}