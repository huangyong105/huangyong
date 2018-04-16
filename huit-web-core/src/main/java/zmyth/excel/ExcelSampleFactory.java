package zmyth.excel;

import java.util.HashMap;

import zlib.field.FieldKit;
import zlib.field.FieldValue;

/**
 * 类说明：EXCEL表样本工厂
 * 
 * @version 1.0
 * @author liujiang
 */

public class ExcelSampleFactory
{

	/* static fields */
	/** （主键）唯一域的域名，其值作为sampleMap的key */
	String uniqueFieldName;

	/* fields */
	/** 样本数组map */
	HashMap<String,ExcelSample> sampleMap=new HashMap<String,ExcelSample>();

	/** 模板数组域（缓存） */
	Object[] samples;

	/**
	 * 构造函数
	 * 
	 * @param uniqueFiledName 主键域名
	 */
	public ExcelSampleFactory(String uniqueFieldName)
	{
		this.uniqueFieldName=uniqueFieldName;
	}

	/* properties */
	/** 获取唯一域的域名 */
	public String getUniqueFieldName()
	{
		return uniqueFieldName;
	}
	/** 设置唯一域的域名 */
	public void setUniqueFieldName(String uniqueFieldName)
	{
		this.uniqueFieldName=uniqueFieldName;
	}
	/**
	 * 通过指定域名及域值获得指定样本编号对应的样本
	 * 
	 * @param fieldName 域名
	 * @param fieldValue 域值
	 */
	public ExcelSample getSample(String fieldValue)
	{
		if(uniqueFieldName==null||fieldValue==null) return null;
		synchronized(sampleMap)
		{
			return sampleMap.get(fieldValue);
		}
	}
	/**
	 * 获得样本数组，数组中可能会包括空元素， 不可对数组进行操作，也不可直接使用数组中的样本
	 */
	public Object[] getSamples()
	{
		if(samples!=null) return samples;
		synchronized(sampleMap)
		{
			samples=sampleMap.values().toArray();
		}
		return samples;
	}
	/** 设置指定的样本 */
	public void setSample(ExcelSample sample)
	{
		if(uniqueFieldName==null) return;
		FieldValue value=FieldKit.getDeclaredField(sample.getClass(),sample,
			uniqueFieldName);
		if(value==null) return;
		synchronized(sampleMap)
		{
			sampleMap.put(String.valueOf(value.value),sample);
		}
		samples=null;
	}
	/* methods */
	/** 新建一个指定样本编号的样本 */
	public ExcelSample newSample(String fieldValue)
	{
		ExcelSample sample=getSample(fieldValue);
		if(sample==null) return null;
		return (ExcelSample)sample.clone();
	}

}