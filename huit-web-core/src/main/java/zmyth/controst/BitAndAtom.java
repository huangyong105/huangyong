package zmyth.controst;
/**
 * 位与判断
 * 
 * @author quxin
 * */
public class BitAndAtom implements Atom{
	/**左边、右边*/
	int left,right;
	/**设置元素(左侧与上右侧)*/
	public void setElement(int l,int r){
		left=l;
		right=r;
	}
	/**返回结果*/
	public boolean result() {
		return (left&right)!=0;
	}
}