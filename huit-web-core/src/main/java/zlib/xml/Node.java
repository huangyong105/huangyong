/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import zlib.set.ArrayList;
import zlib.set.IntArrayList;

/**
 * 类说明：xml节点类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Node
{

	/* static methods */
	/** 判断指定的类型是否为文本类型 */
	public static final boolean isTextType(int type)
	{
		return type==XmlReader.TEXT||type==XmlReader.CDSECT
			||type==XmlReader.IGNORABLE_WHITESPACE;
	}

	/* fields */
	/** 父节点 */
	Node parent;
	/** 内容列表 */
	ArrayList contentList;
	/** 内容类型列表 */
	IntArrayList typeList;
	/** 源对象 */
	Object source;

	/* properties */
	/** 获得父节点 */
	public Node getParent()
	{
		return parent;
	}
	/** 获得源对象 */
	public Object getSource()
	{
		return source;
	}
	/** 获得源对象 */
	public void setSource(Object source)
	{
		this.source=source;
	}
	/* methods */
	/** 获得内容的数量 */
	public int getContentCount()
	{
		return contentList==null?0:contentList.size();
	}
	/** 获得指定位置的内容 */
	public Object getContent(int index)
	{
		return contentList==null?null:contentList.get(index);
	}
	/** 获得指定位置的内容类型 */
	public int getType(int index)
	{
		return typeList==null?-1:typeList.get(index);
	}
	/** 添加子元素 */
	public void addContent(Element child)
	{
		addContent(getContentCount(),child);
	}
	/** 在指定位置上添加子元素 */
	public void addContent(int index,Element child)
	{
		if(child==null) return;
		if(contentList==null)
		{
			contentList=new ArrayList();
			typeList=new IntArrayList();
		}
		child.parent=this;
		contentList.add(child,index);
		typeList.add(XmlReader.ELEMENT,index);
	}
	/** 在指定位置上设置子元素 */
	public boolean setContent(int index,Element child)
	{
		if(index<0&&child==null) return false;
		if(contentList==null) return false;
		if(contentList.size()<=index) return false;
		child.parent=this;
		Object obj=contentList.set(child,index);
		typeList.set(XmlReader.ELEMENT,index);
		if(obj instanceof Node) ((Node)obj).parent=null;
		return true;
	}
	/** 添加指定类型的内容 */
	public void addContent(int type,String str)
	{
		addContent(getContentCount(),type,str);
	}
	/** 在指定位置上添加指定类型的内容 */
	public void addContent(int index,int type,String str)
	{
		if(str==null) str="";
		if(contentList==null)
		{
			contentList=new ArrayList();
			typeList=new IntArrayList();
		}
		contentList.add(str,index);
		typeList.add(type,index);
	}
	/** 在指定位置上设置指定类型的内容 */
	public boolean setContent(int index,int type,String str)
	{
		if(index<0) return false;
		if(contentList==null) return false;
		if(contentList.size()<=index) return false;
		if(str==null) str="";
		Object child=contentList.set(str,index);
		typeList.set(type,index);
		if(child instanceof Node) ((Node)child).parent=null;
		return true;
	}
	/** 移除指定位置上的内容 */
	public Object removeContent(int index)
	{
		typeList.removeIndex(index);
		Object child=contentList.remove(index);
		if(child instanceof Node) ((Node)child).parent=null;
		return child;
	}
	/** 清理内容方法 */
	public void clearContents()
	{
		if(contentList==null) return;
		Object child;
		for(int i=0,n=contentList.size();i<n;i++)
		{
			child=contentList.get(i);
			if(child instanceof Node) ((Node)child).parent=null;
		}
		contentList.clear();
		typeList.clear();
	}
	/** 索引指定名字的子元素 */
	public int indexElement(String name)
	{
		return indexElement(null,name,0,true);
	}
	/** 索引指定名称空间和名字的子元素 */
	public int indexElement(String namespace,String name)
	{
		return indexElement(namespace,name,0,true);
	}
	/** 从指定位置开始，向后索引指定名字的子元素 */
	public int indexElement(String name,int index)
	{
		return indexElement(null,name,index,true);
	}
	/** 从指定位置开始，向后索引指定名称空间和名字的子元素 */
	public int indexElement(String namespace,String name,int index)
	{
		return indexElement(namespace,name,index,true);
	}
	/**
	 * 从指定位置开始，向后索引指定名称空间和名字的子元素，
	 * 参数ignoreCase表示名字是否忽视大小写
	 */
	public int indexElement(String namespace,String name,int index,
		boolean ignoreCase)
	{
		if(contentList==null) return -1;
		Element e;
		if(ignoreCase)
		{
			for(int i=index,n=contentList.size();i<n;i++)
			{
				if(typeList.get(i)!=XmlReader.ELEMENT) continue;
				e=(Element)(contentList.get(i));
				if((name!=null&&!name.equalsIgnoreCase(e.getName())))
					continue;
				if(namespace!=null&&!namespace.equals(e.getNamespace()))
					continue;
				return i;
			}
		}
		else
		{
			for(int i=index,len=contentList.size();i<len;i++)
			{
				if(typeList.get(i)!=XmlReader.ELEMENT) continue;
				e=(Element)(contentList.get(i));
				if((name!=null&&!name.equals(e.getName()))) continue;
				if(namespace!=null&&!namespace.equals(e.getNamespace()))
					continue;
				return i;
			}
		}
		return -1;
	}
	/** 获得第一个子元素 */
	public Element getFirstElement()
	{
		for(int i=0,n=getContentCount();i<n;i++)
		{
			if(getType(i)!=XmlReader.ELEMENT) continue;
			return (Element)getContent(i);
		}
		return null;
	}
	/** 获得指定元素名称的第一个子元素 */
	public Element getFirstElement(String name)
	{
		if(name==null) return null;
		Element e;
		for(int i=0,n=getContentCount();i<n;i++)
		{
			if(getType(i)!=XmlReader.ELEMENT) continue;
			e=(Element)getContent(i);
			if(name.equals(e.getName())) return e;
		}
		return null;
	}
	/** 获得第一段文字 */
	public String getFirstText()
	{
		for(int i=0,n=getContentCount();i<n;i++)
		{
			if(getType(i)==XmlReader.IGNORABLE_WHITESPACE) return "";
			if(getType(i)==XmlReader.TEXT) return (String)getContent(i);
		}
		return null;
	}
	/**
	 * 创建指定名称空间和名字的元素，
	 * 子类如果需要新类型的元素（Document,Element）需要进行重载
	 */
	public Element createElement(String namespace,String name)
	{
		Element e=new Element();
		if(namespace!=null) e.namespace=namespace;
		e.name=name;
		return e;
	}
	/** 读取方法 */
	public void read(XmlReader reader)
	{
		readContent(reader);
	}
	/** 读取内容方法 */
	public void readContent(XmlReader reader)
	{
		int type;
		Element child;
		while(true)
		{
			type=reader.getTokenType();
			switch(type)
			{
				case XmlReader.ELEMENT:
					child=createElement(reader.getNamespace(),reader
						.getName());
					addContent(child);
					child.read(reader);
					break;
				case XmlReader.DOCUMENT_END:
				case XmlReader.ELEMENT_END:
					return;
				default:
					if(reader.getText()!=null)
					{
						if(type==XmlReader.ENTITY_REF) type=XmlReader.TEXT;
						addContent(type,reader.getText());
					}
					else if(type==XmlReader.ENTITY_REF
						&&reader.getName()!=null)
					{
						addContent(XmlReader.ENTITY_REF,reader.getName());
					}
					reader.nextToken();
			}
		}
	}
	/** 写入方法 */
	public void write(XmlWriter writer)
	{
		writeContent(writer);
		writer.flush();
	}
	/** 写入内容方法 */
	public void writeContent(XmlWriter writer)
	{
		if(contentList==null) return;
		int type;
		Object child;
		for(int i=0,len=contentList.size();i<len;i++)
		{
			type=typeList.get(i);
			child=contentList.get(i);
			switch(type)
			{
				case XmlReader.ELEMENT:
					((Element)child).write(writer);
					break;
				case XmlReader.TEXT:
					writer.text((String)child);
					break;
				case XmlReader.IGNORABLE_WHITESPACE:
					writer.ignorableWhitespace((String)child);
					break;
				case XmlReader.CDSECT:
					writer.cdsect((String)child);
					break;
				case XmlReader.COMMENT:
					writer.comment((String)child);
					break;
				case XmlReader.ENTITY_REF:
					writer.entityRef((String)child);
					break;
				case XmlReader.PROCESSING_INSTRUCTION:
					writer.processingInstruction((String)child);
					break;
				case XmlReader.DOCDECL:
					writer.docdecl((String)child);
					break;
			}
		}
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[count="+getContentCount()+"]";
	}

}