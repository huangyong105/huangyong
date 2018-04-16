package zmyth.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/** excel工具类
 * 
 *  @author liujiang
 *  
 *  */
public class ExcelKit
{
	/** 读取指定excel文件某行信息并赋值给对象 
	 * @param obj 对象
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param rowNum 读取指定行的数据
	 * */
	public static Object setExcelRowDataToObj(Object obj,String fileName,String tableName,int rowNum)
	{
		// 默认从第一列开始读取
		return setExcelRowDataToObj(obj,fileName,tableName,rowNum,0);
	}
	
	/** 读取指定excel文件某行信息并赋值给对象 
	 * @param obj 对象
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param rowNum 读取指定行的数据
	 * @param cellNum 从指定列开始读取
	 * 	 * */
	public static Object setExcelRowDataToObj(Object obj,String fileName,String tableName,int rowNum,int cellNum)
	{
		return ExcelUtil.setExcelRowDataToObj(obj,fileName,tableName,rowNum,cellNum);
	}
	
	/** 获取指定excel文件某列的信息
	 * @param fileName excel文件名（含路径）
	 * @param tableName excel表名
	 * @param cellName excel表指定列名
	 * */
	public static String[] getExcelCellData(String fileName,String tableName,String cellName)
	{
		return ExcelUtil.getExcelCellData(fileName,tableName,cellName);
	}
	
	/**
	 * 根据文件名,表名获取excel表
	 * 
	 * @param fileName 文件路径
	 * @param tableName excel表
	 */
	public static Sheet getWorkSheet(String fileName,String tableName)
	{
		Workbook wb=ExcelUtil.getWorkBook(fileName);
		if(wb==null) return null;
		Sheet sheet=wb.getSheet(tableName);
		return sheet;
	}
	
	/**
	 * 根据文件名获取excel文件
	 * 
	 * @param fileName 文件路径
	 */
	public static Workbook getWorkBook(String fileName)
	{
		return ExcelUtil.getWorkBook(fileName);
	}
}
