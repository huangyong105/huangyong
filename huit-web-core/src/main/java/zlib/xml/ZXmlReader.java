/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import zlib.text.CharBuffer;

/**
 * 类说明：xml读取器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ZXmlReader implements XmlReader
{

	private Object location;
	static final private String UNEXPECTED_EOF="Unexpected EOF";
	static final private String ILLEGAL_TYPE="Wrong event type";
	static final private int LEGACY=999;
	static final private int XML_DECL=998;

	// general

	private String version;
	private Boolean standalone;

	private boolean processNsp;
	private boolean relaxed;
	private HashMap entityMap;
	private int depth;
	private String[] elementStack=new String[16];
	private String[] nspStack=new String[8];
	private int[] nspCounts=new int[4];

	// source

	private Reader reader;
	private String encoding;
	private char[] srcBuf;

	private int srcPos;
	private int srcCount;

	private int line;
	private int column;

	// txtbuffer

	private char[] txtBuf=new char[128];
	private int txtPos;

	// Event-related

	private int type;
	// private String text;
	private boolean isWhitespace;
	private String namespace;
	private String prefix;
	private String name;

	private boolean degenerated;
	private int attributeCount;
	private String[] attributes=new String[16];
	private int stackMismatch=0;
	private String error;

	/**
	 * A separate peek buffer seems simpler
	 * than managing wrap around in the
	 * first level read buffer
	 */

	private int[] peek=new int[2];
	private int peekCount;
	private boolean wasCR;

	private boolean unresolved;
	private boolean token;

	public ZXmlReader()
	{
		srcBuf=new char[Runtime.getRuntime().freeMemory()>=1048576?8192:128];
	}

	private final boolean isProp(String n1,boolean prop,String n2)
	{
		if(!n1.startsWith("http://xmlpull.org/v1/doc/")) return false;
		if(prop) return n1.substring(42).equals(n2);
		return n1.substring(40).equals(n2);
	}

	private final boolean adjustNsp()
	{

		boolean any=false;

		for(int i=0;i<attributeCount<<2;i+=4)// *
		// 4-4;i>=0;i-=4)
		{
			String attrName=attributes[i+2];
			int cut=attrName.indexOf(':');
			String prefix;

			if(cut!=-1)
			{
				prefix=attrName.substring(0,cut);
				attrName=attrName.substring(cut+1);
			}
			else if(attrName.equals("xmlns"))
			{
				prefix=attrName;
				attrName=null;
			}
			else
				continue;

			if(!prefix.equals("xmlns"))
			{
				any=true;
			}
			else
			{
				int j=(nspCounts[depth]++)<<1;
				nspStack=ensureCapacity(nspStack,j+2);
				nspStack[j]=attrName;
				nspStack[j+1]=attributes[i+3];
				if(attrName!=null&&attributes[i+3].equals(""))
					error("illegal empty namespace");
				System.arraycopy(attributes,i+4,attributes,i,
					((--attributeCount)<<2)-i);
				i-=4;
			}
		}

		if(any)
		{
			for(int i=(attributeCount<<2)-4;i>=0;i-=4)
			{
				String attrName=attributes[i+2];
				int cut=attrName.indexOf(':');

				if(cut==0&&!relaxed)
					throw new RuntimeException("illegal attribute name:"
						+attrName+" at "+this);

				else if(cut!=-1)
				{
					String attrPrefix=attrName.substring(0,cut);

					attrName=attrName.substring(cut+1);

					String attrNs=getNamespace(attrPrefix);

					if(attrNs==null&&!relaxed)
						throw new RuntimeException("Undefined Prefix:"
							+attrPrefix+" in "+this);

					attributes[i]=attrNs;
					attributes[i+1]=attrPrefix;
					attributes[i+2]=attrName;

				}
			}
		}

		int cut=name.indexOf(':');

		if(cut==0) error("illegal tag name:"+name);

		if(cut!=-1)
		{
			prefix=name.substring(0,cut);
			name=name.substring(cut+1);
		}

		this.namespace=getNamespace(prefix);

		if(this.namespace==null)
		{
			if(prefix!=null) error("undefined prefix:"+prefix);
			this.namespace="";
		}

		return any;
	}

	private final String[] ensureCapacity(String[] arr,int required)
	{
		if(arr.length>=required) return arr;
		String[] bigger=new String[required+16];
		System.arraycopy(arr,0,bigger,0,arr.length);
		return bigger;
	}

	private final void error(String desc)
	{
		if(relaxed)
		{
			if(error==null) error="ERR:"+desc;
		}
		else
			exception(desc);
	}

	private final void exception(String msg)
	{
		if(msg.length()>100) msg=msg.substring(0,100);
		throw new XmlReaderException(msg+"\n"+"position:"
			+getPositionDescription()+"\n",null,getLineNumber(),
			getColumnNumber());
	}

	/**
	 * common base for next and nextToken.
	 * Clears the state,except from txtPos
	 * and whitespace. Does not set the type
	 * variable
	 */

	private final void nextImpl()
	{
		if(reader==null) exception("No Input specified");
		if(type==ELEMENT_END) depth--;
		while(true)
		{
			attributeCount=-1;
			// degenerated needs to be
			// handled before error
			// because of
			// possible
			// processor
			// expectations(!)

			if(degenerated)
			{
				degenerated=false;
				type=ELEMENT_END;
				return;
			}
			if(error!=null)
			{
				for(int i=0;i<error.length();i++)
					push(error.charAt(i));
				// text=error;
				error=null;
				type=COMMENT;
				return;
			}

			if(relaxed&&(stackMismatch>0||(peek(0)==-1&&depth>0)))
			{
				int sp=(depth-1)<<2;
				type=ELEMENT_END;
				namespace=elementStack[sp];
				prefix=elementStack[sp+1];
				name=elementStack[sp+2];
				if(stackMismatch!=1)
					error="missing end tag /"+name+" inserted";
				if(stackMismatch>0) stackMismatch--;
				return;
			}

			prefix=null;
			name=null;
			namespace=null;
			// text=null;

			type=peekType();
			switch(type)
			{
				case ENTITY_REF:
					pushEntity();
					return;
				case ELEMENT:
					parseStartTag(false);
					return;
				case ELEMENT_END:
					parseEndTag();
					return;
				case DOCUMENT_END:
					return;
				case TEXT:
					pushText('<',!token);
					if(depth==0)
					{
						if(isWhitespace) type=IGNORABLE_WHITESPACE;
						// make
						// exception
						// switchable
						// for
						// instances.chg...!!!!
						// else
						// exception("text
						// '"+getText()+"'
						// not allowed
						// outside
						// root
						// element");
					}
					return;
				default:
					type=parseLegacy(token);
					if(type!=XML_DECL) return;
			}
		}
	}

	private final int parseLegacy(boolean push)
	{

		String req="";
		int term;
		int result;
		int prev=0;

		read();// <
		int c=read();

		if(c=='?')
		{
			if((peek(0)=='x'||peek(0)=='X')&&(peek(1)=='m'||peek(1)=='M'))
			{
				if(push)
				{
					push(peek(0));
					push(peek(1));
				}
				read();
				read();
				if((peek(0)=='l'||peek(0)=='L')&&peek(1)<=' ')
				{
					if(line!=1||column>4)
						error("PI must not start with xml");
					parseStartTag(true);
					if(attributeCount<1||!"version".equals(attributes[2]))
						error("version expected");
					version=attributes[3];
					int pos=1;
					if(pos<attributeCount
						&&"encoding".equals(attributes[2+4]))
					{
						encoding=attributes[3+4];
						pos++;
					}

					if(pos<attributeCount
						&&"standalone".equals(attributes[4*pos+2]))
					{
						String st=attributes[3+4*pos];
						if("yes".equals(st))
							standalone=new Boolean(true);
						else if("no".equals(st))
							standalone=new Boolean(false);
						else
							error("illegal standalone value:"+st);
						pos++;
					}
					if(pos!=attributeCount) error("illegal xmldecl");

					isWhitespace=true;
					txtPos=0;
					return XML_DECL;
				}
			}

			/*
			 * int c0=read(); int c1=read();
			 * int
			 */

			term='?';
			result=PROCESSING_INSTRUCTION;
		}
		else if(c=='!')
		{
			if(peek(0)=='-')
			{
				result=COMMENT;
				req="--";
				term='-';
			}
			else if(peek(0)=='[')
			{
				result=CDSECT;
				req="[CDATA[";
				term=']';
				push=true;
			}
			else
			{
				result=DOCDECL;
				req="DOCTYPE";
				term=-1;
			}
		}
		else
		{
			error("illegal:<"+c);
			return COMMENT;
		}

		for(int i=0;i<req.length();i++)
			read(req.charAt(i));

		if(result==DOCDECL)
			parseDoctype(push);
		else
		{
			while(true)
			{
				c=read();
				if(c==-1)
				{
					error(UNEXPECTED_EOF);
					return COMMENT;
				}

				if(push) push(c);
				if((term=='?'||c==term)&&peek(0)==term&&peek(1)=='>') break;
				prev=c;
			}
			if(term=='-'&&prev=='-')
				error("illegal comment delimiter:--->");
			read();
			read();
			if(push&&term!='?') txtPos--;
		}
		return result;
	}
	/** precondition:&lt! consumed */
	private final void parseDoctype(boolean push)
	{
		int nesting=1;
		boolean quoted=false;
		// read();
		while(true)
		{
			int i=read();
			switch(i)
			{
				case -1:
					error(UNEXPECTED_EOF);
					return;
				case '\'':
					quoted=!quoted;
					break;
				case '<':
					if(!quoted) nesting++;
					break;
				case '>':
					if(!quoted)
					{
						if((--nesting)==0) return;
					}
					break;
			}
			if(push) push(i);
		}
	}

	/* precondition:&lt;/ consumed */
	private final void parseEndTag()
	{
		read();// '<'
		read();// '/'
		name=readName();
		skip();
		read('>');
		int sp=(depth-1)<<2;
		if(depth==0)
		{
			error("element stack empty");
			type=COMMENT;
			return;
		}
		if(!name.equals(elementStack[sp+3]))
		{
			error("expected:/"+elementStack[sp+3]+" read:"+name);
			// become case insensitive
			// in relaxed mode
			int probe=sp;
			while(probe>=0
				&&!name.toLowerCase().equals(
					elementStack[probe+3].toLowerCase()))
			{
				stackMismatch++;
				probe-=4;
			}
			if(probe<0)
			{
				stackMismatch=0;
				// text="unexpected
				// end tag ignored";
				type=COMMENT;
				return;
			}
		}
		namespace=elementStack[sp];
		prefix=elementStack[sp+1];
		name=elementStack[sp+2];
	}

	private final int peekType()
	{
		switch(peek(0))
		{
			case -1:
				return DOCUMENT_END;
			case '&':
				return ENTITY_REF;
			case '<':
				switch(peek(1))
				{
					case '/':
						return ELEMENT_END;
					case '?':
					case '!':
						return LEGACY;
					default:
						return ELEMENT;
				}
			default:
				return TEXT;
		}
	}

	private final String get(int pos)
	{
		return new String(txtBuf,pos,txtPos-pos);
	}

	/*
	 * private final String pop(int pos) {
	 * String result=new
	 * String(txtBuf,pos,txtPos-pos);
	 * txtPos=pos; return result; }
	 */

	private final void push(int c)
	{
		isWhitespace&=c<=' ';
		if(txtPos==txtBuf.length)
		{
			char[] bigger=new char[txtPos*4/3+4];
			System.arraycopy(txtBuf,0,bigger,0,txtPos);
			txtBuf=bigger;
		}
		txtBuf[txtPos++]=(char)c;
	}
	/** Sets name and attributes */
	private final void parseStartTag(boolean xmldecl)
	{
		if(!xmldecl) read();
		name=readName();
		attributeCount=0;
		while(true)
		{
			skip();
			int c=peek(0);
			if(xmldecl)
			{
				if(c=='?')
				{
					read();
					read('>');
					return;
				}
			}
			else
			{
				if(c=='/')
				{
					degenerated=true;
					read();
					skip();
					read('>');
					break;
				}
				if(c=='>'&&!xmldecl)
				{
					read();
					break;
				}
			}

			if(c==-1)
			{
				error(UNEXPECTED_EOF);
				// type=COMMENT;
				return;
			}
			String attrName=readName();
			if(attrName.length()==0)
			{
				error("attr name expected");
				// type=COMMENT;
				break;
			}

			int i=(attributeCount++)<<2;
			attributes=ensureCapacity(attributes,i+4);
			attributes[i++]="";
			attributes[i++]=null;
			attributes[i++]=attrName;
			skip();
			if(peek(0)!='=')
			{
				error("Attr.value missing f. "+attrName);
				attributes[i]="1";
			}
			else
			{
				read('=');
				skip();
				int delimiter=peek(0);
				if(delimiter!='\''&&delimiter!='"')
				{
					error("attr value delimiter missing!");
					delimiter=' ';
				}
				else
					read();
				int p=txtPos;
				pushText(delimiter,true);
				attributes[i]=get(p);
				txtPos=p;
				if(delimiter!=' ') read();// skip
				// endquote
			}
		}

		int sp=depth++<<2;
		elementStack=ensureCapacity(elementStack,sp+4);
		elementStack[sp+3]=name;

		if(depth>=nspCounts.length)
		{
			int[] bigger=new int[depth+4];
			System.arraycopy(nspCounts,0,bigger,0,nspCounts.length);
			nspCounts=bigger;
		}
		nspCounts[depth]=nspCounts[depth-1];

		if(processNsp)
			adjustNsp();
		else
			namespace="";

		elementStack[sp]=namespace;
		elementStack[sp+1]=prefix;
		elementStack[sp+2]=name;
	}

	/**
	 * result:isWhitespace;if the setName
	 * parameter is set, the name of the
	 * entity is stored in "name"
	 */

	private final void pushEntity()
	{

		read();// &

		int pos=txtPos;

		while(true)
		{
			int c=read();
			if(c==';') break;
			if(c<128&&(c<'0'||c>'9')&&(c<'a'||c>'z')&&(c<'A'||c>'Z')&&c!='_'
				&&c!='-'&&c!='#')
			{
				error("unterminated entity ref");
				// ;ends
				// with:"+(char)c);
				if(c!=-1) push(c);
				return;
			}

			push(c);
		}

		String code=get(pos);
		txtPos=pos;
		if(token&&type==ENTITY_REF) name=code;

		if(code.charAt(0)=='#')
		{
			int c=(code.charAt(1)=='x'?Integer
				.parseInt(code.substring(2),16):Integer.parseInt(code
				.substring(1)));
			push(c);
			return;
		}

		String result=(String)entityMap.get(code);

		unresolved=result==null;

		if(unresolved)
		{
			if(!token) error("unresolved:&"+code+";");
		}
		else
		{
			for(int i=0;i<result.length();i++)
				push(result.charAt(i));
		}
	}

	/**
	 * types: '<':parse to any token(for
	 * nextToken()) '"':parse to quote '
	 * ':parse to whitespace or '>'
	 */

	private final void pushText(int delimiter,boolean resolveEntities)
	{

		int next=peek(0);
		int cbrCount=0;

		while(next!=-1&&next!=delimiter)
		{ // covers eof,'<','"'

			if(delimiter==' ') if(next<=' '||next=='>') break;

			if(next=='&')
			{
				if(!resolveEntities) break;

				pushEntity();
			}
			else if(next=='\n'&&type==ELEMENT)
			{
				read();
				push(' ');
			}
			else
				push(read());

			if(next=='>'&&cbrCount>=2&&delimiter!=']') error("Illegal:]]>");

			if(next==']')
				cbrCount++;
			else
				cbrCount=0;

			next=peek(0);
		}
	}

	private final void read(char c)
	{
		int a=read();
		if(a!=c) error("expected:'"+c+"' actual:'"+((char)a)+"'");
	}

	private final int read()
	{
		int result;
		if(peekCount==0)
			result=peek(0);
		else
		{
			result=peek[0];
			peek[0]=peek[1];
		}
		peekCount--;
		column++;
		if(result=='\n')
		{
			line++;
			column=1;
		}
		return result;
	}

	/**
	 * Does never read more than needed
	 */

	private final int peek(int pos)
	{
		try
		{
			while(pos>=peekCount)
			{
				int nw;
				if(srcBuf.length<=1)
					nw=reader.read();
				else if(srcPos<srcCount)
					nw=srcBuf[srcPos++];
				else
				{
					srcCount=reader.read(srcBuf,0,srcBuf.length);
					if(srcCount<=0)
						nw=-1;
					else
						nw=srcBuf[0];
					srcPos=1;
				}
				if(nw=='\r')
				{
					wasCR=true;
					peek[peekCount++]='\n';
				}
				else
				{
					if(nw=='\n')
					{
						if(!wasCR) peek[peekCount++]='\n';
					}
					else
						peek[peekCount++]=nw;
					wasCR=false;
				}
			}
			return peek[pos];
		}
		catch(IOException e)
		{
			throw new XmlReaderException(e);
		}
	}

	private final String readName()
	{

		int pos=txtPos;
		int c=peek(0);
		if((c<'a'||c>'z')&&(c<'A'||c>'Z')&&c!='_'&&c!=':'&&c<0x0c0&&!relaxed)
			error("name expected");

		do
		{
			push(read());
			c=peek(0);
		}
		while((c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')||c=='_'
			||c=='-'||c==':'||c=='.'||c>=0x0b7);

		String result=get(pos);
		txtPos=pos;
		return result;
	}

	private final void skip()
	{

		while(true)
		{
			int c=peek(0);
			if(c>' '||c==-1) break;
			read();
		}
	}

	// --------------- public part
	// starts here...---------------

	public void setInput(Reader reader)
	{
		this.reader=reader;

		line=1;
		column=0;
		type=DOCUMENT;
		name=null;
		namespace=null;
		degenerated=false;
		attributeCount=-1;
		encoding=null;
		version=null;
		standalone=null;

		if(reader==null) return;

		srcPos=0;
		srcCount=0;
		peekCount=0;
		depth=0;

		entityMap=new HashMap();
		entityMap.put("amp","&");
		entityMap.put("apos","'");
		entityMap.put("gt",">");
		entityMap.put("lt","<");
		entityMap.put("quot","\"");
	}

	public void setInput(InputStream is,String encoding)
	{
		srcPos=0;
		srcCount=0;
		String enc=encoding;
		if(is==null) throw new IllegalArgumentException();
		try
		{
			if(enc==null)
			{
				// 读取前4个字节
				int chk=0;
				while(srcCount<4)
				{
					int i=is.read();
					if(i==-1) break;
					chk=(chk<<8)|i;
					srcBuf[srcCount++]=(char)i;
				}
				if(srcCount==4)
				{
					switch(chk)
					{
						case 0x00000feff:
							enc="UTF-32BE";
							srcCount=0;
							break;
						case 0x0fffe0000:
							enc="UTF-32LE";
							srcCount=0;
							break;
						case 0x03c:
							enc="UTF-32BE";
							srcBuf[0]='<';
							srcCount=1;
							break;
						case 0x03c000000:
							enc="UTF-32LE";
							srcBuf[0]='<';
							srcCount=1;
							break;
						case 0x0003c003f:
							enc="UTF-16BE";
							srcBuf[0]='<';
							srcBuf[1]='?';
							srcCount=2;
							break;
						case 0x03c003f00:
							enc="UTF-16LE";
							srcBuf[0]='<';
							srcBuf[1]='?';
							srcCount=2;
							break;
						case 0x03c3f786d:
							while(true)
							{
								int i=is.read();
								if(i==-1) break;
								srcBuf[srcCount++]=(char)i;
								if(i=='>')
								{
									String s=new String(srcBuf,0,srcCount);
									int i0=s.indexOf("encoding");
									if(i0!=-1)
									{
										while(s.charAt(i0)!='"'
											&&s.charAt(i0)!='\'')
											i0++;
										char deli=s.charAt(i0++);
										int i1=s.indexOf(deli,i0);
										enc=s.substring(i0,i1);
									}
									break;
								}
							}
						default:
							if((chk&0x0ffff0000)==0x0feff0000)
							{
								enc="UTF-16BE";
								srcBuf[0]=(char)((srcBuf[2]<<8)|srcBuf[3]);
								srcCount=1;
							}
							else if((chk&0x0ffff0000)==0x0fffe0000)
							{
								enc="UTF-16LE";
								srcBuf[0]=(char)((srcBuf[3]<<8)|srcBuf[2]);
								srcCount=1;
							}
							else if((chk&0x0ffffff00)==0x0efbbbf00)
							{
								enc="UTF-8";
								srcBuf[0]=srcBuf[3];
								srcCount=1;
							}
					}
				}
			}
			if(enc==null) enc="UTF-8";

			int sc=srcCount;
			setInput(new InputStreamReader(is,enc));
			this.encoding=encoding;
			srcCount=sc;
		}
		catch(Exception e)
		{
			throw new XmlReaderException(this
				+" setInput, Invalid stream or encoding:"+e.toString()
				+"(position:"+getPositionDescription()+")",e);
		}
	}

	public boolean getFeature(String feature)
	{
		if(XmlReader.FEATURE_PROCESS_NAMESPACES.equals(feature))
			return processNsp;
		else if(isProp(feature,false,"relaxed"))
			return relaxed;
		else
			return false;
	}

	public String getInputEncoding()
	{
		return encoding;
	}

	public void defineEntityReplacementText(String entity,String value)
		throws XmlReaderException
	{
		if(entityMap==null)
			throw new RuntimeException(
				"entity replacement text must be defined after setInput!");
		entityMap.put(entity,value);
	}

	public Object getProperty(String property)
	{
		if(isProp(property,true,"xmldecl-version")) return version;
		if(isProp(property,true,"xmldecl-standalone")) return standalone;
		if(isProp(property,true,"location"))
			return location!=null?location:reader.toString();
		return null;
	}

	public int getPrefixCount(int depth)
	{
		if(depth>this.depth) throw new IndexOutOfBoundsException();
		return nspCounts[depth];
	}

	public String getPrefix(int pos)
	{
		return nspStack[pos<<1];
	}

	public String getPrefixUri(int pos)
	{
		return nspStack[(pos<<1)+1];
	}

	public String getNamespace(String prefix)
	{

		if("xml".equals(prefix))
			return "http://www.w3.org/XML/1998/namespace";
		if("xmlns".equals(prefix)) return "http://www.w3.org/2000/xmlns/";

		for(int i=(getPrefixCount(depth)<<1)-2;i>=0;i-=2)
		{
			if(prefix==null)
			{
				if(nspStack[i]==null) return nspStack[i+1];
			}
			else if(prefix.equals(nspStack[i])) return nspStack[i+1];
		}
		return null;
	}

	public int getDepth()
	{
		return depth;
	}

	public String getPositionDescription()
	{
		CharBuffer cb=new CharBuffer(type<TYPES.length?TYPES[type]
			:"unknown");
		cb.append(' ');
		if(type==ELEMENT||type==ELEMENT_END)
		{
			if(degenerated) cb.append("(empty)");
			cb.append('<');
			if(type==ELEMENT_END) cb.append('/');
			if(prefix!=null) cb.append("{"+namespace+"}"+prefix+":");
			cb.append(name);

			int cnt=attributeCount<<2;
			for(int i=0;i<cnt;i+=4)
			{
				cb.append(' ');
				if(attributes[i+1]!=null)
					cb.append("{"+attributes[i]+"}"+attributes[i+1]+":");
				cb.append(attributes[i+2]+"='"+attributes[i+3]+"'");
			}
			cb.append('>');
		}
		else if(type==IGNORABLE_WHITESPACE)
			;
		else if(type!=TEXT)
			cb.append(getText());
		else if(isWhitespace)
			cb.append("(whitespace)");
		else
		{
			String text=getText();
			if(text.length()>16) text=text.substring(0,16)+"...";
			cb.append(text);
		}
		cb.append("@"+line+":"+column);
		if(location!=null)
		{
			cb.append(" in ");
			cb.append(location);
		}
		else if(reader!=null)
		{
			cb.append(" in ");
			cb.append(reader.toString());
		}
		return cb.getString();
	}

	public int getLineNumber()
	{
		return line;
	}
	public int getColumnNumber()
	{
		return column;
	}

	public boolean isWhitespace()
	{
		if(type!=TEXT&&type!=IGNORABLE_WHITESPACE&&type!=CDSECT)
			exception(ILLEGAL_TYPE);
		return isWhitespace;
	}

	public String getText()
	{
		return type<TEXT||(type==ENTITY_REF&&unresolved)?null:get(0);
	}

	public char[] getTextCharacters(int[] poslen)
	{
		if(type>=TEXT)
		{
			if(type==ENTITY_REF)
			{
				poslen[0]=0;
				poslen[1]=name.length();
				return name.toCharArray();
			}
			poslen[0]=0;
			poslen[1]=txtPos;
			return txtBuf;
		}

		poslen[0]=-1;
		poslen[1]=-1;
		return null;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public String getName()
	{
		return name;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public boolean isEmptyElementTag()
	{
		if(type!=ELEMENT) exception(ILLEGAL_TYPE);
		return degenerated;
	}

	public int getAttributeCount()
	{
		return attributeCount;
	}

	public String getAttributeType(int index)
	{
		return "CDATA";
	}

	public boolean isAttributeDefault(int index)
	{
		return false;
	}

	public String getAttributeNamespace(int index)
	{
		if(index>=attributeCount) throw new IndexOutOfBoundsException();
		return attributes[index<<2];
	}

	public String getAttributeName(int index)
	{
		if(index>=attributeCount) throw new IndexOutOfBoundsException();
		return attributes[(index<<2)+2];
	}

	public String getAttributePrefix(int index)
	{
		if(index>=attributeCount) throw new IndexOutOfBoundsException();
		return attributes[(index<<2)+1];
	}

	public String getAttributeValue(int index)
	{
		if(index>=attributeCount) throw new IndexOutOfBoundsException();
		return attributes[(index<<2)+3];
	}

	public String getAttributeValue(String namespace,String name)
	{

		for(int i=(attributeCount<<2)-4;i>=0;i-=4)
		{
			if(attributes[i+2].equals(name)
				&&(namespace==null||attributes[i].equals(namespace)))
				return attributes[i+3];
		}

		return null;
	}

	public int getTokenType()
	{
		return type;
	}

	public int next()
	{

		txtPos=0;
		isWhitespace=true;
		int minType=9999;
		token=false;
		do
		{
			nextImpl();
			if(type<minType) minType=type;
			// if(curr<=TEXT)type=curr;
		}
		while(minType>ENTITY_REF // ignorable
			||(minType>=TEXT&&peekType()>=TEXT));

		type=minType;
		if(type>TEXT) type=TEXT;

		return type;
	}

	public int nextToken()
	{

		isWhitespace=true;
		txtPos=0;

		token=true;
		nextImpl();
		return type;
	}

	// ----------------------------------------------------------------------
	// utility methods to make XML
	// parsing easier ...

	public int nextTag()
	{

		next();
		if(type==TEXT&&isWhitespace) next();

		if(type!=ELEMENT_END&&type!=ELEMENT) exception("unexpected type");

		return type;
	}
	/** 读出指定类型、名称空间和名字的标记 */
	public void require(int type,String namespace,String name)
	{
		if(type!=this.type
			||(namespace!=null&&!namespace.equals(getNamespace()))
			||(name!=null&&!name.equals(getName())))
			exception("expected:"+TYPES[type]+"{"+namespace+"}"+name);
	}

	public String nextText()
	{
		if(type!=ELEMENT) exception("precondition:ELEMENT");

		next();

		String result;

		if(type==TEXT)
		{
			result=getText();
			next();
		}
		else
			result="";

		if(type!=ELEMENT_END) exception("ELEMENT_END expected");

		return result;
	}

	public void setFeature(String feature,boolean value)
	{
		if(XmlReader.FEATURE_PROCESS_NAMESPACES.equals(feature))
			processNsp=value;
		else if(isProp(feature,false,"relaxed"))
			relaxed=value;
		else
			exception("unsupported feature:"+feature);
	}

	public void setProperty(String property,Object value)
	{
		if(isProp(property,true,"location"))
			location=value;
		else
			throw new XmlReaderException("unsupported property:"+property);
	}

	/**
	 * Skip sub tree that is currently
	 * porser positioned on. <br>
	 * NOTE:parser must be on ELEMENT and
	 * when funtion returns parser will be
	 * positioned on corresponding
	 * ELEMENT_END.
	 */

	// Implementation copied from
	// Alek's mail...
	public void skipSubTree()
	{
		require(ELEMENT,null,null);
		int level=1;
		while(level>0)
		{
			int type=next();
			if(type==ELEMENT_END)
				--level;
			else if(type==ELEMENT) ++level;
		}
	}

}