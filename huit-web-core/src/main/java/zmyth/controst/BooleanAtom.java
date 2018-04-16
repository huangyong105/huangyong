package zmyth.controst;

public class BooleanAtom implements Atom{
	/**元素*/
	boolean source;

	/**设置元素*/
	public void setElement(boolean source){
		this.source=source;
	}
	/**返回结果*/
	public boolean result() {
		return source;
	}
}