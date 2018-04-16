package tech.huit.conf;

/** 
 * @department 成都-产品运营-商务智能-java  
 * @description 配置文件修改监听器
 * @author huit
 * @date 2014年10月23日 上午11:19:43 
 */

public interface SystemConfModifyListener {

	/** 
	 * @description 配置文件更新事件通知
	 * @author huit
	 * @date 2014年10月23日 上午11:40:13 
	 */
	public void onModify();
}
