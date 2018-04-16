package zmyth.excel;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import zlib.field.FieldKit;
import zlib.field.FieldValue;
import zlib.set.ArrayList;
import zlib.text.TextKit;
import zmyth.xlib.ClassLoadFactory;

/**
 * excel表数据读写
 * 
 * @author Liujiang
 */
public class ExcelUtil
{
	/** 构造方法 */
	public ExcelUtil()
	{
	}
	/**
	 * 根据文件名读取excel文件
	 * 
	 * @param fileName 文件路径
	 * @param classMap excel表对应java类
	 * @return
	 * @throws Exception
	 */
	public static void read(String fileName,HashMap<String,String> classMap)
	{
		// 检查文件名是否为空或者是否是Excel格式的文件
		if(fileName==null||fileName.length()==0
			||!fileName.matches("^.+\\.(?i)((xls)|(xlsx))$"))
			throw new IllegalArgumentException(
				"POIExcelUtil parse, fileName is err="+fileName);
		// 检查文件是否存在
		String str=replaceVariable(fileName);
		if(str==null) str=fileName;
		File file=new File(str);
		if(file==null||!file.exists())
			throw new IllegalArgumentException(
				"POIExcelUtil parse, open file fail, src="+fileName);
		boolean isExcel2003=true;
		// 对文件的合法性进行验证
		if(fileName.matches("^.+\\.(?i)(xlsx)$")) isExcel2003=false;
		try
		{
			// 调用本类提供的根据流读取的方法
			read(file.getName(),new FileInputStream(file),isExcel2003,
				classMap);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 根据文件名读取excel文件
	 * 
	 * @param fileName 文件路径
	 * @param table excel表
	 * @return
	 * @throws Exception
	 */
	public static Workbook getWorkBook(String fileName)
	{
		// 检查文件名是否为空或者是否是Excel格式的文件
		if(fileName==null||fileName.length()==0
			||!fileName.matches("^.+\\.(?i)((xls)|(xlsx))$"))
			throw new IllegalArgumentException(
				"POIExcelUtil parse, fileName is err="+fileName);
		// 检查文件是否存在
		String str=replaceVariable(fileName);
		if(str==null) str=fileName;
		File file=new File(str);
		if(file==null||!file.exists())
			throw new IllegalArgumentException(
				"POIExcelUtil parse, open file fail, src="+fileName);
		boolean isExcel2003=true;
		// 对文件的合法性进行验证
		if(fileName.matches("^.+\\.(?i)(xlsx)$")) isExcel2003=false;
		try
		{
			// 调用本类提供的根据流读取的方法
			return getWorkBook(new FileInputStream(file),isExcel2003);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据文件名,表名获取excel表
	 * 
	 * @param fileName 文件路径
	 * @param tableName excel表
	 * @return
	 * @throws Exception
	 */
	public static Sheet getWorkSheet(String fileName,String tableName)
	{
		Workbook wb=getWorkBook(fileName);
		if(wb==null) return null;
		Sheet sheet=wb.getSheet(tableName);
		return sheet;
	}

	/** 字符串变量替换 */
	public static String replaceVariable(String paramString)
	{
		// 变量前缀
		char variablePrefix='[';
		// 变量后缀
		char variableSuffix=']';
		int i=paramString.indexOf(variablePrefix);
		if(i>=0)
		{
			int j=paramString.indexOf(variableSuffix);
			if(j<0) return paramString;
			String str1=paramString.substring(i,j+1);
			String str2=System.getProperty(str1);
			if(str2==null) return null;
			i=paramString.indexOf(str1);
			if(i<0) return paramString;
			return paramString.substring(0,i)+str2
				+paramString.substring(i+str1.length());
		}
		return paramString;
	}

	/**
	 * 根据流读取Excel文件
	 * 
	 * @param inputStream
	 * @param isExcel2003
	 * @return
	 */
	public static void read(String fileName,InputStream inputStream,
		boolean isExcel2003,HashMap<String,String> classMap)
	{
		try
		{
			// 根据版本选择创建Workbook的方式
			Workbook wb=isExcel2003?new HSSFWorkbook(inputStream)
				:new XSSFWorkbook(inputStream);
			inputStream.close();
			read(fileName,wb,classMap);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 根据流获取Excel文件
	 * 
	 * @param inputStream
	 * @param isExcel2003
	 * @return
	 */
	private static Workbook getWorkBook(InputStream inputStream,
		boolean isExcel2003)
	{
		try
		{
			// 根据版本选择创建Workbook的方式
			Workbook wb=isExcel2003?new HSSFWorkbook(inputStream)
				:new XSSFWorkbook(inputStream);
			inputStream.close();
			return wb;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取数据
	 * 
	 * @param wb
	 * @return
	 */
	private static void read(String fileName,Workbook wb,
		HashMap<String,String> classMap)
	{
		if(classMap==null) return;
		// 得到一个sheet
		Sheet sheet=null;
		String value=null;
		// 行数
		int totalRows=0;
		// 列数
		int totalCells=0;
		// 容错处理，从此下标开始计算有效行列（防止配置时跳行、跳列引发读取错误）
		int rowLoc=0,cellLoc=0;
		// 从第六行读取
		int startRow=5;
		// 域名行
		Row fieldNameRow=null;
		// 域名列
		Cell fieldNameCell=null;
		Row row=null;
		Cell cell=null;
		Class cla=null;
		for(int i=0,length=wb.getNumberOfSheets();i<length;i++)
		{
			rowLoc=0;
			cellLoc=0;
			sheet=wb.getSheetAt(i);
			if(sheet==null) continue;
			// 对应转化为的class类
			String sheetName=sheet.getSheetName();
			value=classMap.get(fileName+"_"+sheetName);
			if(value==null) continue;
			Object obj=null;
			try
			{
				cla=ClassLoadFactory.loadClass(value);
				totalRows=sheet.getLastRowNum();
				// 找出有效的初始行
				for(int r=0;r<totalRows;r++)
				{
					if(sheet.getRow(rowLoc)!=null) break;
					rowLoc++;
				}
				// 必须大于等于6行（第一行是字段名称，第二行是字段类型，第三行是字段注释，四五行预留）
				if(totalRows<rowLoc+startRow) continue;
				// 通常以（有效的）第一行的列数为准
				fieldNameRow=sheet.getRow(rowLoc);
				if(fieldNameRow==null) continue;
				totalCells=fieldNameRow.getLastCellNum();
				for(int l=0;l<totalCells;l++)
				{
					if(fieldNameRow.getCell(l)!=null) break;
					cellLoc++;
				}
				// 循环Excel的行
				String cellValue=null;
				for(int r=rowLoc+startRow;r<=totalRows;r++)
				{
					row=sheet.getRow(r);
					if(row==null) continue;
					cellValue=null;
					obj=FieldKit.constructDeclared(cla,null);
					ExcelSample sample=(ExcelSample)obj;
					// // 根据行数定sid
					// sample.setSid(r-startRow+1);
					// 循环Excel的列
					for(int c=cellLoc;c<=totalCells;c++)
					{
						cellValue=null;
						fieldNameCell=fieldNameRow.getCell(c);
						if(fieldNameCell==null) continue;
						String fieldName=fieldNameCell.toString().trim();
						cell=row.getCell(c);
						if(cell!=null)
						{
							cellValue=cell.toString();
							// 数值去掉小数点后的0
							if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
							{
								// 在excel里,日期也是数字,在此要进行判断
								if(DateUtil.isCellDateFormatted(cell))
								{
									// 此处存的是时间毫秒数
									cellValue=String.valueOf(cell.getDateCellValue().getTime());
								}
								else
									cellValue=getRightStr(cellValue);
							}
						}
						FieldKit.setDeclaredField(cla,sample,fieldName,
							new FieldValue(null,cellValue));
						// 设置唯一域的域名(配置表中的第一列为主键列)
						if(sample.getFactory().getUniqueFieldName()==null)
						{
							sample.getFactory()
								.setUniqueFieldName(fieldName);
						}
						boolean b=sample.getFactory().getUniqueFieldName()
										.equals(fieldName);
						if(b)
						{
							sample.setFieldId(cellValue);
						}
					}
					// 设置模板
					sample.initialize();
					sample.getFactory().setSample(sample);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取指定excel文件某行信息并赋值给对象
	 * 
	 * @param obj 对象
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param rowNum 读取指定行的数据
	 */
	public static Object setExcelRowDataToObj(Object obj,String fileName,
		String tableName,int rowNum)
	{
		return setExcelRowDataToObj(obj,fileName,tableName,rowNum,0);
	}

	/**
	 * 读取指定excel文件某行信息并赋值给对象
	 * 
	 * @param obj 对象
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param rowNum 读取指定行的数据
	 * @param cellNum 从指定列开始读取
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Object setExcelRowDataToObj(Object obj,String fileName,
		String tableName,int rowNum,int cellNum)
	{
		Sheet sheet=getWorkSheet(fileName,tableName);
		if(sheet==null) return null;
		// 列数
		int totalCells=0;
		// 域名行
		Row fieldNameRow=null;
		// 域名列
		Cell fieldNameCell=null;
		Row row=null;
		Cell cell=null;
		Class cla=obj.getClass();
		ExcelSample sample=null;
		if(obj instanceof ExcelSample)
			sample=(ExcelSample)obj;
		try
		{
			// 通常以（有效的）第一行的列数为准
			fieldNameRow=sheet.getRow(0);
			if(fieldNameRow==null) return null;
			totalCells=fieldNameRow.getLastCellNum();
			row=sheet.getRow(rowNum);
			if(row==null) return null;
			String cellValue=null;
			// 循环Excel的列
			for(int c=cellNum;c<=totalCells;c++)
			{
				fieldNameCell=fieldNameRow.getCell(c);
				if(fieldNameCell==null) continue;
				String fieldName=fieldNameCell.toString().trim();
				cell=row.getCell(c);
				if(cell!=null)
				{
					cellValue=cell.toString();
					// 数值去掉小数点后的0
					if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
					{
						// 在excel里,日期也是数字,在此要进行判断
						if(DateUtil.isCellDateFormatted(cell))
							// 此处存的是时间毫秒数
							cellValue=String.valueOf(cell.getDateCellValue().getTime());
						else
							cellValue=getRightStr(cellValue);
					}
				}
				FieldKit.setDeclaredField(cla,obj,fieldName,new FieldValue(
					null,cellValue));
				if(sample!=null)
				{
					// 设置唯一域的域名(配置表中的第一列为主键列)
					if(sample.getFactory().getUniqueFieldName()==null)
					{
						sample.getFactory()
							.setUniqueFieldName(fieldName);
					}
					boolean b=sample.getFactory().getUniqueFieldName()
									.equals(fieldName);
					if(b)
						sample.setFieldId(cellValue);
				}
			}
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定excel文件某列的信息
	 * 
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param cellName excel表指定列名
	 */
	public static String[] getExcelCellData(String fileName,
		String tableName,String cellName)
	{
		Sheet sheet=getWorkSheet(fileName,tableName);
		if(sheet==null) return null;
		// 总列数
		int totalCells=0;
		// 总行数
		int totalRows=0;
		// 指定列所在位置
		int cellLoc=-1;
		// 从第六行读取
		int startRow=5;
		// 域名行
		Row fieldNameRow=null;
		Row row=null;
		Cell cell=null;
		try
		{
			// 通常以（有效的）第一行的列数为准
			fieldNameRow=sheet.getRow(0);
			if(fieldNameRow==null) return null;
			totalCells=fieldNameRow.getLastCellNum();
			totalRows=sheet.getLastRowNum();
			for(int i=0;i<=totalCells;i++)
			{
				cell=fieldNameRow.getCell(i);
				if(cell==null) continue;
				if(cellName.equals(cell.toString()))
				{
					cellLoc=i;
					break;
				}
			}
			// 没找到指定列
			if(cellLoc==-1) return null;
			String[] values=new String[totalRows-startRow+1];
			for(int i=startRow;i<=totalRows;i++)
			{
				row=sheet.getRow(i);
				if(row==null) continue;
				cell=row.getCell(cellLoc);
				if(cell==null) continue;
				values[i-startRow]=cell.toString();
			}
			return values;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 处理整数后自动加零的情况
	 * 
	 * @param sNum
	 * @return
	 */
	public static String getRightStr(String sNum)
	{
		DecimalFormat decimalFormat=new DecimalFormat("#.000000");
		String resultStr=decimalFormat.format(new Double(sNum));
		int index=resultStr.indexOf(".");
		if(index==0) resultStr=String.valueOf(0)+resultStr;
		if(resultStr.matches("^[-+]?\\d+\\.[0]+$"))
		{
			resultStr=resultStr.substring(0,resultStr.indexOf("."));
		}
		return resultStr;
	}

	/**
	 * 数据写入Excel
	 * 
	 * @param dataLst 数据
	 * @param outputFile 输出文件路径
	 */
	public static boolean writeData(Object obj,String outputFile)
	{
		if(obj==null) return false;
		// TODO 此处当前是测试用的，所以直接强制转型，正式用时注意
		ArrayList dataLst=(ArrayList)obj;
		// 检查文件名是否为空或者是否是Excel格式的文件
		if(outputFile==null
			||!outputFile.matches("^.+\\.(?i)((xls)|(xlsx))$"))
			return false;
		boolean isExcel2003=true;
		// 对文件的合法性进行验证
		if(outputFile.matches("^.+\\.(?i)(xlsx)$")) isExcel2003=false;
		File outFile=new File(outputFile);
		String parent=outFile.getParent();
		if(parent!=null)
		{
			File tree=new File(parent);
			if(!tree.exists()) tree.mkdirs();
		}
		try
		{
			if(!outFile.exists()) outFile.createNewFile();
			FileOutputStream fos=null;
			fos=new FileOutputStream(outputFile);
			if(isExcel2003)
			{
				HSSFWorkbook book=new HSSFWorkbook();
				HSSFSheet sheet=book.createSheet("TestCreateExcel");
				HSSFRow hRow=null;
				HSSFCell cell=null;
				ArrayList cells=null;
				for(int row=0,size=dataLst.size();row<size;row++)
				{
					hRow=sheet.createRow(row);
					cells=(ArrayList)dataLst.get(row);
					if(cells==null) continue;
					for(int col=0,len=cells.size();col<len;col++)
					{
						cell=hRow.createCell(col);
						cell.setCellValue(cells.get(col)==null
							?TextKit.EMPTY_STRING:cells.get(col).toString());
					}
				}
				book.write(fos);
			}
			else
			{
				XSSFWorkbook book=new XSSFWorkbook();
				XSSFSheet sheet=book.createSheet("TestCreateExcel");
				XSSFRow hRow=null;
				XSSFCell cell=null;
				ArrayList cells=null;
				for(int row=0,size=dataLst.size();row<size;row++)
				{
					hRow=sheet.createRow(row);
					cells=(ArrayList)dataLst.get(row);
					if(cells==null) continue;
					for(int col=0,len=cells.size();col<len;col++)
					{
						cell=hRow.createCell(col);
						cell.setCellValue(cells.get(col)==null
							?TextKit.EMPTY_STRING:cells.get(col).toString());
					}
				}
				book.write(fos);
			}
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 读取数据
	 * 
	 * @param wb
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static ArrayList read(Sheet sheet,Class cla)
	{
		if(sheet==null) return null;
		ArrayList list=new ArrayList();
		// 行数
		int totalRows=0;
		// 列数
		int totalCells=0;
		// 容错处理，从此下标开始计算有效行列（防止配置时跳行、跳列引发读取错误）
		int rowLoc=0,cellLoc=0;
		// 从第六行读取
		int startRow=5;
		// 域名行
		Row fieldNameRow=null;
		// 域名列
		Cell fieldNameCell=null;
		Row row=null;
		Cell cell=null;
		Object obj=null;
		ExcelSample sample=null;
		try
		{
			totalRows=sheet.getLastRowNum();
			// 找出有效的初始行
			for(int r=0;r<totalRows;r++)
			{
				if(sheet.getRow(rowLoc)!=null) break;
				rowLoc++;
			}
			// 必须大于等于6行（第一行是字段名称，第二行是字段类型，第三行是字段注释，四五行预留）
			if(totalRows<rowLoc+startRow) return null;
			// 通常以（有效的）第一行的列数为准
			fieldNameRow=sheet.getRow(rowLoc);
			if(fieldNameRow==null) return null;
			totalCells=fieldNameRow.getLastCellNum();
			for(int l=0;l<totalCells;l++)
			{
				if(fieldNameRow.getCell(l)!=null) break;
				cellLoc++;
			}
			// 循环Excel的行
			String cellValue=null;
			for(int r=rowLoc+startRow;r<=totalRows;r++)
			{
				row=sheet.getRow(r);
				if(row==null) continue;
				sample=null;
				obj=FieldKit.constructDeclared(cla,null);
				if(obj instanceof ExcelSample)
				{
					sample=(ExcelSample)obj;
				}
				cellValue=null;
				// 循环Excel的列
				for(int c=cellLoc;c<=totalCells;c++)
				{
					cellValue=null;
					fieldNameCell=fieldNameRow.getCell(c);
					if(fieldNameCell==null) continue;
					String fieldName=fieldNameCell.toString().trim();
					cell=row.getCell(c);
					if(cell!=null)
					{
						cellValue=cell.toString();
						// 数值去掉小数点后的0
						if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
						{
							// 在excel里,日期也是数字,在此要进行判断
							if(DateUtil.isCellDateFormatted(cell))
								// 此处存的是时间毫秒数
								cellValue=String.valueOf(cell.getDateCellValue().getTime());
							else
								cellValue=getRightStr(cellValue);
						}
					}
					FieldKit.setDeclaredField(obj.getClass(),obj,fieldName,
						new FieldValue(null,cellValue));
					if(sample!=null)
					{
						// 设置唯一域的域名(配置表中的第一列为主键列)
						if(sample.getFactory().getUniqueFieldName()==null)
						{
							sample.getFactory()
								.setUniqueFieldName(fieldName);
						}
						boolean b=sample.getFactory().getUniqueFieldName()
										.equals(fieldName);
						if(b)
							sample.setFieldId(cellValue);
					}
				}
				list.add(obj);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}