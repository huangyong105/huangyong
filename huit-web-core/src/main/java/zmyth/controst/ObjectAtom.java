package zmyth.controst;
/**
 * 对象判断
 * 
 * @author quxin
 * */
public class ObjectAtom implements Atom{
	/**左边、右边*/
	Object left,right;
	/**设置元素*/
	public void setElement(Object l,Object r){
		left=l;
		right=r;
	}
	/**设置左边元素*/
	public void setLeft(Object l){
		left=l;
	}
	/**设置右边元素*/
	public void setRight(Object r){
		right=r;
	}
	/**返回结果*/
	public boolean result() {
		if(left==null&&right==null)
			return true;
		if(left==null||right==null)
			return false;
		return left.equals(right);
	}
}