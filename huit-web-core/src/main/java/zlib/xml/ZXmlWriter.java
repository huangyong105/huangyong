/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 类说明：xml读入器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ZXmlWriter implements XmlWriter
{

	/* fields */
	/** 输出字符流 */
	private Writer writer;

	/** 字符编码格式 */
	private String encoding;
	/** 是否为unicode编码 */
	private boolean unicode;

	/** 是否在元素属性内 */
	private boolean pending;
	/** 前缀是否补齐 */
	private int auto;
	/** 当前的元素嵌套深度 */
	private int depth;

	/** 元素嵌套堆栈 */
	private String[] elementStack=new String[32];
	// nsp/prefix/name
	private int[] nspCounts=new int[4];
	/** 名称空间嵌套堆栈 */
	private String[] nspStack=new String[8];
	// prefix/nsp; both empty are ""
	private boolean[] indent=new boolean[4];

	/* methods */
	/** 写入字符串方法 */
	private void write(String s)
	{
		try
		{
			writer.write(s);
		}
		catch(IOException e)
		{
		}
	}
	/** 写入字符方法 */
	private void write(char c)
	{
		try
		{
			writer.write(c);
		}
		catch(IOException e)
		{
		}
	}
	/** 检查是否需要扩展临时堆栈 */
	private final void check(boolean close)
	{
		if(!pending) return;
		depth++;
		pending=false;
		if(indent.length<=depth)
		{
			boolean[] hlp=new boolean[depth+4];
			System.arraycopy(indent,0,hlp,0,depth);
			indent=hlp;
		}
		indent[depth]=indent[depth-1];

		for(int i=nspCounts[depth-1];i<nspCounts[depth];i++)
		{
			write(' ');
			write("xmlns");
			if(!"".equals(nspStack[i*2]))
			{
				write(':');
				write(nspStack[i*2]);
			}
			else if("".equals(getNamespace())&&!"".equals(nspStack[i*2+1]))
				throw new IllegalStateException(
					"Cannot set default namespace for elements in no namespace");
			write("=\"");
			writeEscaped(nspStack[i*2+1],'"');
			write('"');
		}
		if(nspCounts.length<=depth+1)
		{
			int[] temp=new int[depth+8];
			System.arraycopy(nspCounts,0,temp,0,depth+1);
			nspCounts=temp;
		}
		nspCounts[depth+1]=nspCounts[depth];
		if(close)
			write(" />");
		else
			write('>');
	}
	/** 将指定的字符串经过转码处理后写入 */
	private final void writeEscaped(String s,int quot)
	{
		char c;
		for(int i=0,len=s.length();i<len;i++)
		{
			c=s.charAt(i);
			switch(c)
			{
				case '\n':
				case '\r':
				case '\t':
					if(quot==-1)
						write(c);
					else
						write("&#"+((int)c)+';');
					break;
				case '&':
					write("&amp;");
					break;
				case '>':
					write("&gt;");
					break;
				case '<':
					write("&lt;");
					break;
				case '"':
				case '\'':
					if(c==quot)
					{
						write(c=='"'?"&quot;":"&apos;");
						break;
					}
				default:
					if(c>=' '&&c!='@'&&(c<127||unicode))
						write(c);
					else
						write("&#"+((int)c)+";");
			}
		}
	}

	/** 判断指定特性是否存在 */
	public boolean getFeature(String name)
	{
		return false;
	}
	/** 设置指定特性是否存在 */
	public void setFeature(String name,boolean value)
	{
		throw new RuntimeException("Unsupported Feature");
	}
	/** 获得指定的属性 */
	public Object getProperty(String name)
	{
		throw new RuntimeException("Unsupported property");
	}
	/** 设置指定的属性 */
	public void setProperty(String name,Object value)
	{
		throw new RuntimeException("Unsupported Property:"+value);
	}
	/** 设置输出字符流 */
	public void setOutput(Writer writer)
	{
		this.writer=writer;
		nspCounts[0]=2;
		nspCounts[1]=2;
		nspStack[0]="";
		nspStack[1]="";
		nspStack[2]="xml";
		nspStack[3]="http://www.w3.org/XML/1998/namespace";
		encoding=System.getProperty("file.encoding");
		unicode=!"ISO-8859-1".equalsIgnoreCase(encoding);
		pending=false;
		auto=0;
		depth=0;
	}
	/** 设置输出字节流和字符编码格式 */
	public void setOutput(OutputStream os,String encoding)
	{
		if(os==null) throw new IllegalArgumentException();
		if(encoding==null) encoding=System.getProperty("file.encoding");
		OutputStreamWriter writer;
		try
		{
			writer=new OutputStreamWriter(os,encoding);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException();
		}
		setOutput(writer);
		this.encoding=encoding;
		unicode=!"ISO-8859-1".equalsIgnoreCase(encoding);
	}

	/** 获得当前的元素嵌套深度 */
	public int getDepth()
	{
		return pending?depth+1:depth;
	}
	/** 获得当前的元素名称空间 */
	public String getNamespace()
	{
		return getDepth()==0?null:elementStack[getDepth()*3-3];
	}
	/** 获得当前的元素名称 */
	public String getName()
	{
		return getDepth()==0?null:elementStack[getDepth()*3-1];
	}
	/** 获得当前的名称空间前缀，参数generatePrefix为是否生成相应前缀 */
	public String getPrefix(String namespace,boolean create)
	{
		return getPrefix(namespace,false,create);
	}
	/** 获得当前的名称空间前缀，参数generatePrefix为是否生成相应前缀 */
	private final String getPrefix(String namespace,boolean includeDefault,
		boolean create)
	{
		for(int i=nspCounts[depth+1]*2-2;i>=0;i-=2)
		{
			if(nspStack[i+1].equals(namespace)
				&&(includeDefault||!nspStack[i].equals("")))
			{
				String cand=nspStack[i];
				for(int j=i+2;j<nspCounts[depth+1]*2;j++)
				{
					if(nspStack[j].equals(cand))
					{
						cand=null;
						break;
					}
				}
				if(cand!=null) return cand;
			}
		}
		if(!create) return null;

		String prefix;
		if("".equals(namespace))
			prefix="";
		else
		{
			do
			{
				prefix="n"+(auto++);
				for(int i=nspCounts[depth+1]*2-2;i>=0;i-=2)
				{
					if(prefix.equals(nspStack[i]))
					{
						prefix=null;
						break;
					}
				}
			}
			while(prefix==null);
		}

		boolean p=pending;
		pending=false;
		setPrefix(prefix,namespace);
		pending=p;
		return prefix;
	}
	/** 设置当前的名称空间前缀 */
	public void setPrefix(String prefix,String namespace)
	{
		check(false);
		if(prefix==null) prefix="";
		if(namespace==null) namespace="";
		String defined=getPrefix(namespace,true,false);
		// boil out if already defined
		if(prefix.equals(defined)) return;

		int pos=(nspCounts[depth+1]++)<<1;
		if(nspStack.length<pos+1)
		{
			String[] hlp=new String[nspStack.length+16];
			System.arraycopy(nspStack,0,hlp,0,pos);
			nspStack=hlp;
		}
		nspStack[pos++]=prefix;
		nspStack[pos]=namespace;
	}

	/** 文档写入开始 */
	public void startDocument(String encoding,Boolean standalone)
	{
		write("<?xml version='1.0' ");
		if(encoding!=null)
		{
			unicode=!"ISO-8859-1".equalsIgnoreCase(encoding);
			write("encoding='");
			write(encoding);
			write("' ");
		}
		if(standalone!=null)
		{
			write("standalone='");
			write(standalone.booleanValue()?"yes":"no");
			write("' ");
		}
		write("?>");
	}
	/** 写入文档类型声明（<!DOCTYPE >） */
	public void docdecl(String dd)
	{
		write("<!DOCTYPE");
		write(dd);
		write(">");
	}
	/** 元素写入开始 */
	public void startElement(String namespace,String name)
	{
		check(false);
		if(indent[depth])
		{
			write("\r\n");
			for(int i=0;i<depth;i++)
				write("  ");
		}
		int esp=depth*3;
		if(elementStack.length<esp+3)
		{
			String[] hlp=new String[elementStack.length+12];
			System.arraycopy(elementStack,0,hlp,0,esp);
			elementStack=hlp;
		}
		String prefix=(namespace==null)?"":getPrefix(namespace,true,true);
		if("".equals(namespace))
		{
			for(int i=nspCounts[depth];i<nspCounts[depth+1];i++)
			{
				if("".equals(nspStack[i*2])&&!"".equals(nspStack[i*2+1]))
					throw new IllegalStateException(
						"Cannot set default namespace for elements in no namespace");
			}
		}

		elementStack[esp++]=namespace;
		elementStack[esp++]=prefix;
		elementStack[esp]=name;

		write('<');
		if(!"".equals(prefix))
		{
			write(prefix);
			write(':');
		}
		write(name);
		pending=true;
	}
	/** 写入属性 */
	public void attribute(String namespace,String name,String value)
	{
		if(!pending)
			throw new IllegalStateException("illegal position for attribute");
		if(namespace==null) namespace="";
		String prefix="".equals(namespace)?""
			:getPrefix(namespace,false,true);
		write(' ');
		if(!"".equals(prefix))
		{
			write(prefix);
			write(':');
		}
		write(name);
		write('=');
		if(value!=null)
		{
			char q=value.indexOf('"')==-1?'"':'\'';
			write(q);
			writeEscaped(value,q);
			write(q);
		}
		else
			write("");
	}
	/** 写入CDATA段，纯字符数据（<![CDATA[ ]]>） */
	public void cdsect(String data)
	{
		check(false);
		write("<![CDATA[");
		write(data);
		write("]]>");
	}
	/** 写入注释（<!-- -->） */
	public void comment(String comment)
	{
		check(false);
		write("<!--");
		write(comment);
		write("-->");
	}
	/** 写入实体引用，会被字符数据取代的置标（& ;） */
	public void entityRef(String name)
	{
		check(false);
		write('&');
		write(name);
		write(';');
	}
	/** 写入空格 */
	public void ignorableWhitespace(String whitespace)
	{
		text(whitespace);
	}
	/** 写入处理指令（<? ?>） */
	public void processingInstruction(String pi)
	{
		check(false);
		write("<?");
		write(pi);
		write("?>");
	}
	/** 写入文本 */
	public void text(String text)
	{
		check(false);
		indent[depth]=false;
		writeEscaped(text,-1);
	}
	/** 写入文本 */
	public void text(char[] text,int start,int len)
	{
		text(new String(text,start,len));
	}
	/** 元素写入结束 */
	public void endElement(String namespace,String name)
	{
		if(!pending) depth--;
		if((namespace==null&&elementStack[depth*3]!=null)
			||(namespace!=null&&!namespace.equals(elementStack[depth*3]))
			||!elementStack[depth*3+2].equals(name))
			throw new IllegalArgumentException("</{"+namespace+"}"+name
				+"> does not match start");
		if(pending)
		{
			check(true);
			depth--;
		}
		else
		{
			if(indent[depth+1])
			{
				write("\r\n");
				for(int i=0;i<depth;i++)
					write("  ");
			}
			write("</");
			String prefix=elementStack[depth*3+1];
			if(!"".equals(prefix))
			{
				write(prefix);
				write(':');
			}
			write(name);
			write('>');
		}
		nspCounts[depth+1]=nspCounts[depth];
	}
	/** 文档写入结束 */
	public void endDocument()
	{
		while(depth>0)
			endElement(elementStack[depth*3-3],elementStack[depth*3-1]);
		flush();
	}
	/** 刷新方法 */
	public void flush()
	{
		check(false);
		try
		{
			writer.flush();
		}
		catch(IOException e)
		{
		}
	}

}