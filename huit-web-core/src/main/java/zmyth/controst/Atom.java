package zmyth.controst;
/**
 * 用于对比的原子接口
 * 
 * @author quxin
 * */
public interface Atom{
	/**运算符号定义
	 * 	LESS(小于),GREATER(大于),EQUAL(等于),LESS_EQUAL(小于等于),GREATER_EQUAL(大于等于)*/
	public static final int LESS=1,GREATER=2,EQUAL=4,LESS_EQUAL=5,GREATER_EQUAL=6;
	/**返回结果*/
	public boolean result();
}