package code.gen;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerate {
    private static final String PROJECT_PATH = "~/git/im";
//    private static final String PROJECT_PATH = "C:\\work\\git\\uuc-git\\";//pc
    private static final String PROJECT_NAME = "im";
    private static final String SRC_PATH = "\\src\\main\\java\\";
    private static final String BASE_PACKAGE = "tech.huit.uuc";
    private static final String MODULE = "auth";
    private static final String MAPPER_PACKAGE = BASE_PACKAGE + ".dao." + MODULE;
    private static final String MAPPER_SAVE_PATH = PROJECT_PATH + PROJECT_NAME + "-dao\\" + SRC_PATH + MAPPER_PACKAGE;
    private static final String ENTITY_PACKAGE = BASE_PACKAGE + ".entity." + MODULE;
    private static final String ENTITY_SAVE_PATH = PROJECT_PATH + PROJECT_NAME + "-entity\\" + SRC_PATH + ENTITY_PACKAGE;
    private static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service." + MODULE;
    private static final String SERVICE_SAVE_PATH = PROJECT_PATH + PROJECT_NAME + "-service\\" + SRC_PATH + SERVICE_PACKAGE;
    public static final String url = "jdbc:mysql://10.0.6.200:6006/im?useUnicode=true&characterEncoding=utf-8";
    public static final String username = "im";
    public static final String password = "im";
    public static final String tableName = "t_user";

    private static final String BASE_ENTITY_CLASS = "tech.huit.entity.AbstractEntity";//不用修改
    private static final String BASE_MAPPER_CLASS = "tech.huit.dao.AbstractMapper";//不用修改
    private static final String BASE_SERVICE_CLASS = "tech.huit.service.AbstractService";//不用修改
    public static final String tableNamePrefix = "t";// 数据库表名的前缀，比如前缀设置为t，那么t_user将会生成User.java
    public static final String ENTITY_CLASS_NAME;// 实体类类名，如User
    public static final List<String> columnList = new ArrayList<String>();
    public static boolean isOverWrite = true;//如果该文件已经存在，是否重新生成并覆盖原来的

    static {
        ENTITY_CLASS_NAME = firstCharToUpperCase(handleUnderLineAndPrefix(tableName));
    }

    private static final String MAPPER_CLASS_NAME = ENTITY_CLASS_NAME + "Mapper";

    public static void main(String[] args) {
        // 生成实体类，如User.java
        String s1 = generateEntity();
        generateFile(s1, ENTITY_SAVE_PATH, ENTITY_CLASS_NAME + ".java");
        //
        //		// 生存Mapper.java，如UserMapper.java
        String s2 = generateMapperJava();
//        generateFile(s2, MAPPER_SAVE_PATH, MAPPER_CLASS_NAME + ".java");
        //		//
        //		// 生存Mapper.xml，如UserMapper.xml
        String s3 = generateMapperXML();
        generateFile(s3, MAPPER_SAVE_PATH, MAPPER_CLASS_NAME + ".xml");
        //
        //		//		// 生存Service.java，如UserService.java
        String s4 = generateService();
//        generateFile(s4, SERVICE_SAVE_PATH, ENTITY_CLASS_NAME + "Service.java");
    }

    public static void generateFile(String data, String filePath, String fileName) {
        try {
            filePath = filePath.replace(".", "/");
            File path = new File(filePath);
            if (!path.exists() || !path.isDirectory()) {
                boolean isCreate = path.mkdirs();
                if (isCreate) {
                    System.out.println("crate dir error:" + path);
                }
            }
            File out = new File(filePath + "/" + fileName);
            if (out.exists() && !isOverWrite) {
                System.err.println(filePath + "/" + fileName + "已经存在！创建文件失败！");
                return;
            } else {
                System.out.println(filePath + "/" + fileName + "创建成功！请刷新查看");
            }
            FileOutputStream os = new FileOutputStream(out);
            FileChannel fos = os.getChannel();
            ByteBuffer bytedata = ByteBuffer.wrap(data.getBytes());

            // bytedata.flip();
            fos.write(bytedata);
            bytedata.clear();
            fos.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<ColumnInfo> list = new ArrayList<ColumnInfo>();

    public static String generateEntity() {
        try {
            list = new ArrayList<ColumnInfo>();
            Connection conn = DriverManager.getConnection(url, username, password);
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});
            while (resultSet.next()) {
                String name = resultSet.getString("TABLE_NAME");
                if (tableName.equals(name)) {
                    // ResultSet rs
                    // =getConnection.getMetaData().getColumns(null,
                    // getXMLConfig.getSchema(),tableName.toUpperCase(),
                    // "%");//其他数据库不需要这个方法的，直接传null，这个是oracle和db2这么�
                    ResultSet rs = dbmd.getColumns(null, "%", tableName, "%");
                    while (rs.next()) {
                        if (!StringUtils.equals(rs.getString("COLUMN_NAME"), "id")) {
                            ColumnInfo entity = new ColumnInfo();
                            String colName = rs.getString("COLUMN_NAME");
                            int size = rs.getInt("COLUMN_SIZE");
                            entity.setSize(size);
                            if ("extends".equals(colName)) {//关键字处理
                                colName = colName + "_";
                            }
                            entity.setColumnName(underscoreToCamel(colName));
                            entity.setTypeName(rs.getString("TYPE_NAME"));
                            entity.setRemarks(rs.getString("REMARKS"));
                            list.add(entity);
                            columnList.add(colName);
                        }
                    }
                }
            }

            StringBuffer sb = new StringBuffer();
            sb.append("package ").append(ENTITY_PACKAGE).append(";\n\n");
            sb.append("import " + BASE_ENTITY_CLASS + ";\n");
            for (ColumnInfo c : list) {
                String typeName = sqlType2JavaType(c);
                String importStr = "";
                switch (typeName) {
                    case "Timestamp":
                        importStr = "import java.sql.Timestamp;";
                        break;
                    case "Time":
                        importStr = "import java.sql.Time;";
                        break;
                    case "Date":
                        importStr = "import java.util.Date;";
                        break;
                    default:
                        break;
                }
                if (!sb.toString().contains(importStr)) {
                    sb.append(importStr + " \n");
                }
            }
            sb.append("public class " + ENTITY_CLASS_NAME + " extends AbstractEntity { \n");
            //			sb.append("public class " + ENTITY_CLASS_NAME + " implements Serializable {\n");
            //			sb.append("\tprivate static final long serialVersionUID = 1L;\n");

            for (ColumnInfo c : list) {
                String typeName = c.getTypeName();
                String columnName = c.getColumnName();
                String remarks = c.getRemarks();
                if (StringUtils.isNotBlank(remarks)) {
                    sb.append("\t/** " + remarks + " **/\n");
                }
                sb.append("\tprivate " + sqlType2JavaType(c) + " " + columnName + ";\n\n");
            }
            for (ColumnInfo c : list) {
                String type = sqlType2JavaType(c);
                String columnName = c.getColumnName();

                if ("Boolean".equals(sqlType2JavaType(c))) {
                    sb.append("\tpublic " + type + " " + columnName + "(){\n");
                } else {
                    sb.append("\tpublic " + type + " get" + firstCharToUpperCase(columnName) + "(){\n");
                }
                sb.append("\t\treturn this." + columnName + ";\n\t}\n");

                if ("Boolean".equals(sqlType2JavaType(c)) && columnName.startsWith("is")) {
                    sb.append("\tpublic void set" + columnName.substring(2) + "(" + type + " " + columnName + "){\n");
                } else {
                    sb.append("\tpublic void set" + firstCharToUpperCase(columnName) + "(" + type + " " + columnName + "){\n");
                }
                sb.append("\t\tthis." + columnName + " = " + columnName + ";\n\t}\n");
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateMapperJava() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(MAPPER_PACKAGE)) {
            sb.append("package ").append(MAPPER_PACKAGE).append(";\n\n");
        }
        sb.append("import " + BASE_MAPPER_CLASS + ";\n");
        sb.append("public interface ").append(ENTITY_CLASS_NAME).append("Mapper extends AbstractMapper {\n\n}");
        return sb.toString();
    }

    public static String generateService() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(SERVICE_PACKAGE)) {
            sb.append("package ").append(SERVICE_PACKAGE).append(";\n\n");
        }
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n")
                .append("import org.springframework.stereotype.Service;\n\n")
                .append("import ")
                .append(MAPPER_PACKAGE + "." + MAPPER_CLASS_NAME)
                .append(";\n")
                .append("import ")
                .append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME)
                .append(";\n")
                .append("import " + BASE_MAPPER_CLASS + ";\n")
                .append("import " + BASE_SERVICE_CLASS + ";\n")

                //				.append("import ").append(MAPPER_PACKAGE).append(".").append("AbstractMapper;\n\n")
                .append("@Service\n").append("public class ").append(ENTITY_CLASS_NAME)
                .append("Service extends AbstractService<").append(ENTITY_CLASS_NAME).append(">{\n\n").append("\t@Autowired\n")
                .append("\tprivate ").append(ENTITY_CLASS_NAME).append("Mapper ").append(firstCharToLowerCase(ENTITY_CLASS_NAME))
                .append("Mapper;\n\n").append("\tpublic ").append(ENTITY_CLASS_NAME).append("Service() {\n")
                .append("\t\tsuper(").append(ENTITY_CLASS_NAME).append(".class);\n\t}\n\n").append("\t@Override\n")
                .append("\tpublic AbstractMapper getAbstractMapper() {\n").append("\t\treturn this.")
                .append(firstCharToLowerCase(ENTITY_CLASS_NAME)).append("Mapper;\n").append("\t}\n").append("}");
        return sb.toString();
    }

    public static String generateMapperXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("\n")
                .append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n")
                .append("<mapper namespace=\"").append(MAPPER_PACKAGE).append(".").append(MAPPER_CLASS_NAME).append("\">\n");
        //baseSql
        sb.append("\t<sql id=\"Base_Column_List\" >\n");
        if (columnList.size() > 0) {
            sb.append("\t");
        }
        for (String columnName : columnList) {
            sb.append(columnName).append(",");
        }
        if (columnList.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }
        sb.append("\t</sql>\n");

        // insert
        sb.append("\t<insert id=\"insert\" parameterType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\"")
                .append(" useGeneratedKeys=\"true\" keyProperty=\"id\">\n").append("\t\tinsert into ")
                .append(tableName).append("\n\t\t(");
        for (int i = 0; i < columnList.size(); i++) {
            sb.append(columnList.get(i));
            if (i != columnList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") \n").append("\t\tvalues \n").append("\t\t(");
        for (int i = 0; i < columnList.size(); i++) {
            sb.append("#{").append(underscoreToCamel(columnList.get(i))).append("}");
            if (i != columnList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") \n");
        sb.append("\t</insert>");

        // deleteById
        sb.append("\n\t<delete id=\"deleteById\" parameterType=\"int\">\n").append("\t\tdelete from ")
                .append(tableName).append(" where id=#{id}").append("\n\t</delete>");

        // selectById
        sb.append("\n\t<select id=\"selectById\" resultType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\">\n")
                .append("\t\tselect * from ").append(tableName).append(" where id=#{id}").append("\n\t</select>");

        //selectOneBySelective
        sb.append("\n\t<select id=\"selectOneBySelective\" resultType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\">\n");
        sb.append("\t\tselect * from ").append(tableName).append("\n");
        sb.append("\t\t<where>\n");
        appendField(sb);
        sb.append("\t\t</where>");
        sb.append("\n\t</select>");

        // deleteByIds
        sb.append("\n\t<delete id=\"deleteByIds\" parameterType=\"int\">\n")
                .append("\t\tdelete from ")
                .append(tableName)
                .append("\n")
                .append("\t\t<where>\n")
                .append("\t\t<choose>\n")
                .append("\t\t\t<when test=\"null != ids\">\n")
                .append("\t\t\t\t<foreach collection=\"ids\" item=\"id\" separator=\",\" open=\"id in (\" close=\")\">\n")
                .append("\t\t\t\t#{id}\n").append("\t\t\t\t</foreach>\n").append("\t\t\t</when>\n")
                .append("\t\t\t<otherwise>\n").append("\t\t\t0=1\n").append("\t\t\t</otherwise>\n")
                .append("\t\t</choose>\n").append("\t\t</where>\n").append("\t</delete>");

        // update
        sb.append("\n\t<update id=\"update\" parameterType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\">\n")
                .append("\t\tupdate ").append(tableName).append("\n").append("\t\t<set>\n");
        appendSetField(sb);
        sb.append("\t\t</set>\n").append("\t\twhere id=#{id}\n").append("\t</update>");

        // listPaged
        sb.append("\n\t<select id=\"listPaged\" resultType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\">\n");
        sb.append("\t\tselect * from ").append(tableName).append("\n");
        sb.append("\t\t<where>\n");
        appendWhereField(sb, "entity.");
        sb.append("\t\t</where>\n");
        sb.append("\t\t<if test=\"tableParam != null and tableParam.length > -1\">\n");
        sb.append("\t\tlimit #{tableParam.start},#{tableParam.length}\n").append("\t\t</if>");
        sb.append("\n\t</select>");

        //listAll
        sb.append("\n\t<select id=\"listAll\" resultType=\"").append(ENTITY_PACKAGE + "." + ENTITY_CLASS_NAME).append("\">\n")
                .append("\t\tselect * from ").append(tableName).append("\n\t</select>");

        // count
        sb.append("\n\t<select id=\"count\" resultType=\"int\">\n").append("\t\tselect count(*) from ").append(tableName).append("\n")
                .append("\t\t<where>\n");
        appendWhereField(sb);
        sb.append("\t\t</where>\n");
        sb.append("\t</select>").append("\n</mapper>");
        return sb.toString();
    }

    private static void appendField(StringBuffer sb) {
        appendField(sb, "", false);
    }

    private static void appendWhereField(StringBuffer sb) {
        appendField(sb, "", false);
    }

    private static void appendWhereField(StringBuffer sb, String fieldPrd) {
        appendField(sb, fieldPrd, false);
    }

    private static void appendSetField(StringBuffer sb) {
        appendField(sb, "", true);
    }

    private static void appendField(StringBuffer sb, String fieldPrd, boolean isSet) {
        for (int i = 0; i < columnList.size(); i++) {
            String columnName = columnList.get(i);
            sb.append("\t\t\t<if test=\"").append(fieldPrd).append(underscoreToCamel(columnName)).append(" != null\">\n");
            sb.append("\t\t\t\t");
            if (!isSet) {
                sb.append("and ");
            }
            sb.append(columnName).append(" = ").append("#{").append(fieldPrd).append(underscoreToCamel(columnName)).append("}");
            if (isSet) {
                sb.append(",");
            }
            sb.append("\n\t\t\t</if>\n");
        }
    }


    public static String handleUnderLineAndPrefix(String tableName) {
        tableName = tableName.substring(tableNamePrefix.length(), tableName.length());
        tableName = underscoreToCamel(tableName);
        return tableName;
    }

    public static String sqlType2JavaType(ColumnInfo columnInfo) {
        String name = columnInfo.getTypeName();
        if (name.equalsIgnoreCase("TINYINT") || name.equalsIgnoreCase("TINYINT unsigned")) {
            return "Byte";
        } else if (name.equalsIgnoreCase("smallint")) {
            return "Short";
        } else if (name.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (name.equalsIgnoreCase("bigint") || name.equalsIgnoreCase("int unsigned")) {
            return "Long";
        } else if (name.equalsIgnoreCase("float")) {
            return "Float";
        } else if (name.equalsIgnoreCase("decimal") || name.equalsIgnoreCase("numeric")
                || name.equalsIgnoreCase("real") || name.equalsIgnoreCase("money")
                || name.equalsIgnoreCase("smallmoney") || name.equalsIgnoreCase("double")) {
            return "Double";
        } else if (name.equalsIgnoreCase("varchar") || name.equalsIgnoreCase("char")
                || name.equalsIgnoreCase("nvarchar") || name.equalsIgnoreCase("nchar")
                || name.equalsIgnoreCase("text")) {
            return "String";
        } else if (name.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (name.equalsIgnoreCase("DATE")) {
            return "Date";
        } else if (name.equalsIgnoreCase("TIME")) {
            return "Date";
        } else if (name.equalsIgnoreCase("Timestamp")) {
            return "Timestamp";
        } else if (name.equalsIgnoreCase("BIT")) {
            return "Boolean";
        }
        return null;
    }

    public static String firstCharToUpperCase(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static String firstCharToLowerCase(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static String underscoreToCamel(String s) {
        char[] cs = s.toCharArray();
        StringBuffer sb = new StringBuffer();
        boolean isBeforeUnderline = false;
        for (char c : cs) {
            if (c == '_') {
                isBeforeUnderline = true;
                continue;
            }
            if (isBeforeUnderline) {
                c = Character.toUpperCase(c);
                isBeforeUnderline = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
