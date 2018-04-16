package zmyth.controst;
/**
 * 变量方法
 *  用作给判断式增加变量设置和变量填充的抽象类
 *  
 *  @author quxin
 * */
public abstract class Variable{
	/**静态定义空*/
	static int[] INT_NULL=new int[0];
	static Atom[] ATOM_NULL=new Atom[0];
	/**定义的关键节点*/
	private int[] keys=INT_NULL;
	private Atom[] values=ATOM_NULL;
	
	/**定义变量元素*/
	public void setVariable(int key,Atom atom){
		int len=keys.length;
		int[] tempI=new int[len+1];
		System.arraycopy(keys, 0, tempI, 0, len);
		tempI[len]=key;
		keys=tempI;
		
		len=values.length;
		Atom[] tempA=new Atom[len+1];
		System.arraycopy(values, 0, tempA, 0, len);
		tempA[len]=atom;
		values=tempA;
	}
	/**获得定义的元素*/
	protected Atom getVariable(int key){
		for(int i=0;i<key;i++){
			if(keys[i]!=key)continue;
			return values[i];
		}
		return null;
	}
	/**替换元素*/
	protected abstract void change(Object source);
	/**获得等式*/
	protected abstract Atom getAtom();
}