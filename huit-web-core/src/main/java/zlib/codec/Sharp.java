/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.codec;

import zlib.text.CharBuffer;

/**
 * 类说明：自定义#号编码规则，用#做为命令字符进行编码。
 * 主要用于编码文本中的换行符，和sql语句或脚本语言中的转义符和单引号，
 * 也可自定义转换关系。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Sharp
{

	/* static fields */
	/** 命令字符 */
	public static final char SHARP='#';
	/** 默认的忽略字符 */
	public static final char[] IGNORE_CHARS={'\r'};
	/** 默认的替换字符1 */
	public static final char[] REPLACE_CHARS1={'\n','n'};
	/** 默认的替换字符2 */
	public static final char[] REPLACE_CHARS2={'\n','n','\\','b','\'','q',';','s'};

	/* fields */
	/** 忽略字符数组 */
	char[] ignoreChars;
	/** 替换字符数组 */
	char[] replaceChars;

	/* constructors */
	/** 公开构造方法 */
	public Sharp()
	{
		this(IGNORE_CHARS,REPLACE_CHARS1);
	}
	/** 构造指定忽略字符数组和替换字符数组的Sharp编解码算法 */
	public Sharp(char[] ignoreChars,char[] replaceChars)
	{
		this.ignoreChars=ignoreChars;
		this.replaceChars=replaceChars;
	}
	/* properties */
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
	/* methods */
	/** 编码方法 */
	public String encode(String str)
	{
		if(str==null) return "";
		int n=str.length();
		if(n<=0) return str;
		CharBuffer cb=new CharBuffer(n+n/8);
		boolean b=encode(str,cb);
		return b?cb.getString():str;
	}
	/** 编码方法，返回是否编码 */
	public boolean encode(String str,CharBuffer cb)
	{
		if(str==null) return false;
		int n=str.length();
		if(n<=0) return false;
		boolean coding=false;
		char[] ignores=ignoreChars;
		char[] replaces=replaceChars;
		char c;
		for(int i=0,j=0,n1=ignores.length,n2=replaces.length-1;i<n;i++)
		{
			c=str.charAt(i);
			for(j=0;j<n1;j++)
			{
				if(c!=ignores[j]) continue;
				coding=true;
				break;
			}
			if(j<n1) continue;
			for(j=0;j<n2;j+=2)
			{
				if(c!=replaces[j]) continue;
				cb.append(SHARP);
				c=replaces[j+1];
				coding=true;
				break;
			}
			cb.append(c);
		}
		return coding;
	}
	/** 解码方法，返回是否解码 */
	public String decode(String str)
	{
		if(str==null) return "";
		int n=str.length();
		if(n<=0) return str;
		CharBuffer cb=new CharBuffer(n);
		boolean b=decode(str,cb);
		return b?cb.getString():str;
	}
	/** 解码方法 */
	public boolean decode(String str,CharBuffer cb)
	{
		if(str==null) return false;
		int n=str.length();
		if(n<=0) return false;
		boolean coding=false;
		char[] replaces=replaceChars;
		char c1,c2;
		for(int i=0,j=0,m=replaces.length;i<n;i++)
		{
			c1=str.charAt(i);
			if(c1!=SHARP)
			{
				cb.append(c1);
				continue;
			}
			coding=true;
			i++;
			if(i>=n) break;
			c2=str.charAt(i);
			for(j=1;j<m;j+=2)
			{
				if(c2!=replaces[j]) continue;
				cb.append(replaces[j-1]);
				break;
			}
		}
		return coding;
	}

}