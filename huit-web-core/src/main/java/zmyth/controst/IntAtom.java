package zmyth.controst;
/**
 *	整形判断
 * 
 * @author quxin
 * */
public class IntAtom implements Atom{
	/**左边、右边*/
	int left,right;
	/**运算符*/
	int sign;
	
	public IntAtom(int sign){
		this.sign=sign;
	}
	/**设置元素*/
	public void setElement(int l,int r){
		left=l;
		right=r;
	}
	/**设置左边元素*/
	public void setLeft(int l){
		left=l;
	}
	/**设置右边元素*/
	public void setRight(int r){
		right=r;
	}
	/**返回结果*/
	public boolean result() {
		if((sign&Atom.LESS)!=0&&left<right)
			return true;
		if((sign&Atom.GREATER)!=0&&left>right)
			return true;
		if((sign&Atom.EQUAL)!=0&&left==right)
			return true;
		return false;
	}
}