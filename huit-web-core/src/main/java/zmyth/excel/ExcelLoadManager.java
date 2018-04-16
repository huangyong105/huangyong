package zmyth.excel;

import java.util.HashMap;

import zlib.log.LogFactory;
import zlib.log.Logger;
import zlib.text.TextKit;

/**
 * Excel配置文件对象模板加载管理器
 * 
 * @author liujiang
 */
public class ExcelLoadManager
{
	public static final ExcelLoadManager manager=new ExcelLoadManager();
	/** 日志记录 */
	private static final Logger log=LogFactory
		.getLogger(ExcelLoadManager.class);
	/** excel表所对应的java类名 */
	HashMap<String,String> classMap=new HashMap<String,String>();
	
	/** 获取实例 */
	public static ExcelLoadManager getInstance()
	{
		return manager;
	}
	
	/** 设置excel表所对应的java类名  
	 * @param key excel表名
	 * @param value excel表对应java类
	 * 
	 * */
	public void mapping(String key,String value)
	{
		classMap.put(key,value);
	}
	
	/** 加载EXCEL模板文件 */
	public void loads()
	{
		String str=System.getProperty("loadExcels");
		if(str==null)
		{
			if(log.isInfoEnabled())
				log.info(" start, null loadExcels property");
			return;
		}
		String[] arrayOfString=TextKit.split(str,':');
		for(int i=0;i<arrayOfString.length;++i)
		{
			if(log.isInfoEnabled())
				log.info("start, parse file="+arrayOfString[i]);
			parse(arrayOfString[i]);
		}
	}
	
	/** 解析文件 */
	public void parse(String str)
	{
		ExcelUtil.read(str,classMap);
	}
//	/** 测试用 */
//	public static void main(String[] args)
//	{
//		HashMap<String,String> classMap=new HashMap<String,String>();
//		classMap.put("avatar","ww.war.Avatar");
//		String str="../app/excelSample/avatar.xlsx";
//		String[] arrayOfString=TextKit.split(str,':');
//		for(int i=0;i<arrayOfString.length;++i)
//		{
//			if(log.isInfoEnabled())
//				log.info("start, parse file="+arrayOfString[i]);
//			ExcelUtil.read(arrayOfString[i],classMap);
//		}
//		ExcelSample[] samples=Avatar.factory.getSamples();
//		for(int i=0;i<samples.length;i++)
//		{
//			if(samples[i]==null) continue;
//			System.out.println("-------------samples[i]="+samples[i]);
//		}
//	}
}
