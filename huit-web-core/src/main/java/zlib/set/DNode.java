/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：双端节点对象
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class DNode
{

	/* fields */
	/** 前节点 */
	DNode prev;
	/** 后节点 */
	DNode next;
	/** 关联的元素 */
	Object element;

	/* constructors */
	/** 构造一个指定元素的起始节点 */
	public DNode()
	{
	}
	/** 构造一个指定元素的起始节点 */
	public DNode(Object element)
	{
		this.element=element;
	}
	/** 构造一个指定前后节点和关联元素的节点 */
	DNode(DNode prev,DNode next,Object element)
	{
		this.prev=prev;
		this.next=next;
		this.element=element;
	}
	/* properties */
	/** 获得前节点 */
	public DNode prev()
	{
		return prev;
	}
	/** 获得后节点 */
	public DNode next()
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
	/** 在节点前添加指定元素的新节点 */
	public DNode addPrev(Object obj)
	{
		DNode n=new DNode(prev,this,obj);
		if(prev!=null) prev.next=n;
		prev=n;
		return n;
	}
	/** 移除节点前的节点 */
	public DNode removePrev()
	{
		if(prev==null) return null;
		DNode n=prev;
		prev=prev.prev;
		if(prev!=null) prev.next=this;
		return n;
	}
	/** 在节点后添加指定元素的新节点 */
	public DNode addNext(Object obj)
	{
		DNode n=new DNode(this,next,obj);
		if(next!=null) next.prev=n;
		next=n;
		return n;
	}
	/** 移除节点后的节点 */
	public DNode removeNext()
	{
		if(next==null) return null;
		DNode n=next;
		next=next.next;
		if(next!=null) next.prev=this;
		return n;
	}

}