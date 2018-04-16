package edu.dbke.socket.cp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import edu.dbke.socket.cp.Desc;
import edu.dbke.socket.cp.ProtocolType;

/**
 * c++版协议生成,文档生成工具
 * @author huitang
 */
public class CodeDocGenerator {
	private static String sourceFile = "F:\\MyEclipse Professional 2014\\nio-cp";//原代码存放路径
	private static String[] packageFilter = new String[] { "voip", "message" }; //{ "message", "voip", "uuc", "Opt", "StringPacket", "uuc" };//协议包过滤规则
	private static String codeSaveFile = "F:\\MyEclipse Professional 2014\\nio-cp\\cppqt";
	private static String docSaveFile = "F:\\MyEclipse Professional 2014\\nio-cp\\doc";
	private static String sourceExt = ".java";//原文件扩展名
	private static int sourceCount;//源文件数量
	private static BufferedWriter bw;

	public static void main(String[] args) throws Exception {
		PacketCheckUitl.main(null);
		File file = new File(codeSaveFile);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		try {
			getCodeFile(sourceFile);
			genCppHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}

		file = new File(docSaveFile);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		File fileWrite = new File(docSaveFile + "\\packet.txt");
		fileWrite.createNewFile();
		bw = new BufferedWriter(new FileWriter(docSaveFile + "/packet.txt"));
		try {
			getFile(sourceFile);
			genHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}
		bw.close();
	}

	private static File getCodeFile(String path) {
		try {
			File file = new File(path);
			if (file.isDirectory() && !file.getName().contains(".svn") && !file.getName().contains("util")
					&& !file.getName().contains("test")) {
				for (File fil : file.listFiles()) {
					getCodeFile(fil.getAbsolutePath());
				}
			} else if (!file.getName().contains(".svn") && file.getName().contains(sourceExt)) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String name = file.getAbsolutePath().replace(sourceFile, "");
				name = name.replace("\\src\\main\\java\\edu\\dbke\\socket\\cp", "");

				int index = name.lastIndexOf('\\');
				if (index > 1) {
					File dir = new File(codeSaveFile + name.substring(0, index));
					dir.mkdirs();
				}
				File fileWrite = new File(codeSaveFile + name.replace(".java", ".h"));
				try {
					fileWrite.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}

				BufferedWriter bw = new BufferedWriter(new FileWriter(fileWrite));
				sourceCount++;
				String line;
				//bw.write("#pragma once\r\n#include \"Packet.h\"\r\n#include <string>\r\n#include \"ByteUtil.h\"");
				bw.write("#include \"packet.h\"\r\n#include <string>\r\nusing namespace std;\r\n#include \"../common/ByteUtil.h\"");
				boolean isMethordBegin = false;
				int emptyLine = 0;
				while ((line = br.readLine()) != null) {
					if (!line.contains("package") && !line.contains("import") && !line.contains("@Override")) {
						if (line.startsWith("public class")) {
							line = line.replace("extends", ": public");
							line = line.replace("public class", "class");
							index = line.indexOf('<');
							if (index != -1) {
								line = line.substring(0, index);
							}
							index = line.indexOf('{');
							if (index != -1) {
								line = line.substring(0, index);
							}
							bw.write(line);
							bw.write("\r\n{\r\n");
							continue;
						} else if (line.trim().startsWith("private ")) {
							continue;
						} else if ((line.trim().contains("get") || line.trim().contains("set"))
								&& line.trim().endsWith("{")) {
							isMethordBegin = true;
							continue;
						} else if (isMethordBegin) {
							if (line.trim().endsWith("}")) {
								isMethordBegin = false;
							}
							continue;
						} else if (line.trim().length() == 0) {
							emptyLine++;
							if (emptyLine > 1) {
								continue;
							}
						} else {
							emptyLine = 0;
						}

						line = line.replace("/**", "/*");
						if (!line.contains("readDate") && !line.contains("writeDate")) {
							line = line.replace("Date", "time_t");
						}
						line = line.replace("public ", "public:");
						line = line.replace("final", "const");
						line = line.replace("protected void", "void");
						line = line.replace("ByteUtil.", "ByteUtil::");
						line = line.replace("this.", "this->");
						line = line.replace("ProtocolType.", "ProtocolType::");
						line = line.replace("boolean", "bool");
						if (!line.contains("Packet") && !line.contains("readString") && !line.contains("256String")
								&& !line.contains("ShortString")) {
							line = line.replace("String", "string");
						} else {
							line = line.replace(", String", ", string");
							line = line.replace("(String", "(string");
						}

						if (line.contains("getBytes()")) {//写string
							int index1 = line.indexOf('(');
							int index2 = line.lastIndexOf('.');
							if (index1 != -1 && index2 != -1) {
								try {
									line = "\t\tByteUtil::writeString(data," + line.substring(index1 + 1, index2)
											+ ");";
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						bw.write(line);
						bw.newLine();
					}
				}
				bw.write(";");
				br.close();
				bw.close();
				System.out.print(file.getName() + " ");
				if (sourceCount % 5 == 0) {
					System.out.println();
				}
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void genCppHeader() throws IOException {
		File fileWrite = new File(codeSaveFile + "/ProtocolType.h");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileWrite));
		bw.write("#ifndef PROTOCOLTYPE_H\n#define PROTOCOLTYPE_H\nclass ProtocolType\n{\npublic:\n\tProtocolType(){}");
		bw.newLine();
		bw.write("\tstatic string GetVersion()\n\t{\n\t\treturn \"" + ProtocolType.VERSION + "\";//协议版本号\n\t}");
		bw.newLine();
		bw.write("\tenum ProtocolTypeDefine {");
		bw.newLine();
		Field[] fileds = ProtocolType.class.getFields();
		for (Field field : fileds) {
			Desc desc = field.getAnnotation(Desc.class);
			//System.out.println(field.toGenericString());
			if (null != desc) {
				if (-32768 == desc.key()) {
					bw.write("\t\t" + field.getName() + " = " + desc.key() + "//" + desc.desc());
					bw.newLine();
				} else {
					bw.write("\t\t" + field.getName() + " = " + desc.key() + ",//" + desc.desc());
					bw.newLine();
				}
			}
		}
		bw.write("\t};\n};\n#endif // PROTOCOLTYPE_H");
		bw.close();
	}

	private static File getFile(String path) {
		try {
			File file = new File(path);
			if (file.isDirectory() && !file.getName().contains(".svn") && !file.getName().contains("util")
					&& !file.getName().contains("test")) {
				for (File fil : file.listFiles()) {
					getFile(fil.getAbsolutePath());
				}
			} else if (!file.getName().contains(".svn") && file.getName().contains(sourceExt)) {
				if (null != packageFilter) {
					boolean isFilter = false;
					for (String filter : packageFilter) {
						if (file.getAbsolutePath().contains(filter)) {
							isFilter = true;
							break;
						}
					}
					if (!isFilter) {
						return null;
					}
				}
				BufferedReader br = new BufferedReader(new FileReader(file));
				sourceCount++;
				String line;
				boolean isBegin = false, isFirst = true, isPacket = false;
				StringBuffer commentBuf = new StringBuffer();
				StringBuffer dataBuf = new StringBuffer();
				String type = "", className = "";
				while ((line = br.readLine()) != null) {
					if (isFirst && line.contains("/**")) {
						isBegin = true;
						isFirst = false;
					} else if (line.contains("*/")) {
						isBegin = false;
					}

					if (isBegin) {
						if (!line.contains("@author")) {
							commentBuf.append(line.replace("/**", "").replace("*", "").trim());
							continue;
						}
					}

					if (line.startsWith("public class")) {
						isPacket = true;
						int index1 = line.indexOf("class") + 6;
						int index2 = line.indexOf("extends");
						try {
							line = line.substring(index1, index2);
							className = line;
						} catch (Exception e) {
							return file;
						}

					} else if ((line.contains("public boolean") || line.contains("public byte")
							|| line.contains("public short") || line.contains("public int")
							|| line.contains("public long") || line.contains("public double")
							|| line.contains("public Date") || line.contains("public String"))
							&& (!line.contains("(")) && isPacket) {
						line = line.trim().substring(6);
						dataBuf.append(line.trim()).append("\r\n");
					} else if (isPacket) {
						if (line.contains("this.type")) {
							type = line.replace("this.type", "");
							type = type.replace("=", "").trim();
							type = type.replace("ProtocolType.", "");
							type = type.replace(";", "").trim();
						}
					}
				}
				if (className.length() > 0) {
					if (type.length() > 0 && !"type".equals(type)) {
						bw.write("关联常量：" + type + "(" + PacketCheckUitl.getCode(type) + ")");
						bw.newLine();
					}
					bw.write("协议类名：" + className);
					bw.newLine();
					bw.write("协议描述：" + commentBuf.toString());
					bw.newLine();
					bw.write(dataBuf.toString());
					bw.newLine();
				}

			}
			System.out.print(file.getName() + " ");
			if (sourceCount % 5 == 0) {
				System.out.println();
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void genHeader() throws IOException {
		File fileWrite = new File(docSaveFile + "/header.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileWrite));
		//bw.write("协议版本号:" + ProtocolType.VERSION);
		//bw.newLine();
		Field[] fileds = ProtocolType.class.getFields();
		for (Field field : fileds) {
			Desc desc = field.getAnnotation(Desc.class);
			if (null != desc) {
				bw.write(desc.key() + "\t" + field.getName() + "\t" + desc.desc());
				bw.newLine();
			}
		}
		bw.close();
	}
}