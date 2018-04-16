package zlib.set;

public class NodeDeque extends NodeQueue
{

	public NodeQueue.Node addHead(Object paramObject)
	{
		NodeQueue.Node localNode=createNode(paramObject);
		if(this.head!=null)
		{
			localNode.next=this.head;
			this.head.prev=localNode;
			this.head=localNode;
		}
		else
		{
			this.head=(this.tail=localNode);
		}
		this.count+=1;
		return localNode;
	}

	public NodeQueue.Node addTail(Object paramObject)
	{
		NodeQueue.Node localNode=createNode(paramObject);
		if(this.tail!=null)
		{
			this.tail.next=localNode;
			localNode.prev=this.tail;
			this.tail=localNode;
		}
		else
		{
			this.head=(this.tail=localNode);
		}
		this.count+=1;
		return localNode;
	}

	public Object removeHead()
	{
		NodeQueue.Node localNode=this.head;
		if(localNode==null) return null;
		this.head=localNode.next;
		if(this.head!=null)
			this.head.prev=null;
		else
			this.tail=null;
		this.count-=1;
		Object localObject=localNode.getSource();
		bufferNode(localNode);
		return localObject;
	}

	public Object removeTail()
	{
		NodeQueue.Node localNode=this.tail;
		if(localNode==null) return null;
		this.tail=localNode.prev;
		if(this.tail!=null)
			this.tail.next=null;
		else
			this.head=null;
		this.count-=1;
		Object localObject=localNode.getSource();
		bufferNode(localNode);
		return localObject;
	}
}