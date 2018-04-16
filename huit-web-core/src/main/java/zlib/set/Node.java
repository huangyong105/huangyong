/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：节点对象
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Node
{

	/* fields */
	/** 后节点 */
	Node next;
	/** 关联的元素 */
	Object element;

	/* constructors */
	/** 构造一个指定元素的起始节点 */
	public Node()
	{
	}
	/** 构造一个指定元素的起始节点 */
	public Node(Object element)
	{
		this.element=element;
	}
	/** 构造一个指定前后节点和关联元素的节点 */
	Node(Node next,Object element)
	{
		this.next=next;
		this.element=element;
	}
	/* properties */
	/** 获得后节点 */
	public Node next()
	{
		return next;
	}
	/** 获得关联的元素 */
	public Object getElement()
	{
		return element;
	}
	/** 设置关联的元素 */
	public void setElement(Object element)
	{
		this.element=element;
	}
	/** 在节点后添加指定元素的新节点 */
	public Node add(Object obj)
	{
		next=new Node(next,obj);
		return next;
	}
	/** 移除节点后的节点 */
	public Node remove()
	{
		Node n=next;
		if(n!=null) next=n.next;
		return n;
	}

}