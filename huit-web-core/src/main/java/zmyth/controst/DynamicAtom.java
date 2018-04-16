package zmyth.controst;

/**
 * 动态元素判定
 * 	定义对应值可以修改
 * @author quxin
 * */
public class DynamicAtom implements Atom{
	/**设置变量*/
	Variable able;
	/**源*/
	Atom source;
	
	/**设置变量对象*/
	public void setVariable(Variable able){
		this.able=able;
		if(able!=null)
			source=able.getAtom();
	}
	/**替换变量的元素*/
	public void change(Object obj){
		if(able!=null)
			able.change(obj);
	}
	/**返回结果*/
	public boolean result() {
		return source.result();
	}
	
}