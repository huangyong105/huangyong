package zlib.set;

public class NodeQueue implements Container
{

	Node head;
	Node tail;
	int count;
	private Node buffer;

	public int size()
	{
		return this.count;
	}

	public boolean isEmpty()
	{
		return (this.head==null);
	}

	public boolean isFull()
	{
		return false;
	}

	protected Node getHead()
	{
		return this.head;
	}

	protected Node getTail()
	{
		return this.tail;
	}

	public boolean contain(Object paramObject)
	{
		Node localNode=this.head;
		while(localNode!=null)
		{
			if(localNode.getSource()==paramObject) return true;
			localNode=localNode.next;
		}
		return false;
	}

	public boolean add(Object paramObject)
	{
		return (addTail(paramObject)!=null);
	}

	public Object get()
	{
		if(this.tail==null) return null;
		return this.tail.getSource();
	}

	public Object remove()
	{
		return removeHead();
	}

	public Node createNode(Object paramObject)
	{
		Node localNode=this.buffer;
		if(localNode==null) return newNode(paramObject);
		this.buffer=localNode.next;
		localNode.clear();
		localNode.setSource(paramObject);
		return localNode;
	}

	protected Node newNode(Object paramObject)
	{
		return new Node(paramObject);
	}

	public Node addHead(Object paramObject)
	{
		Node localNode=createNode(paramObject);
		if(this.head!=null)
		{
			localNode.next=this.head;
			this.head=localNode;
		}
		else
		{
			this.head=(this.tail=localNode);
		}
		this.count+=1;
		return localNode;
	}

	public Node addTail(Object paramObject)
	{
		Node localNode=createNode(paramObject);
		if(this.tail!=null)
		{
			this.tail.next=localNode;
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
		Node localNode=this.head;
		if(localNode==null) return null;
		this.head=localNode.next;
		if(this.head==null) this.tail=null;
		this.count-=1;
		Object localObject=localNode.getSource();
		bufferNode(localNode);
		return localObject;
	}

	protected void bufferNode(Node paramNode)
	{
		paramNode.clear();
		paramNode.setSource(null);
		paramNode.next=this.buffer;
		this.buffer=paramNode;
	}

	public void clear()
	{
		if(this.tail!=null)
		{
			Node localNode=this.head;
			while(localNode!=null)
			{
				localNode.setSource(null);
				localNode=localNode.next;
			}
			this.tail.next=this.buffer;
			this.buffer=this.head;
			this.head=(this.tail=null);
		}
		this.count=0;
	}

	public class Node
	{

		Node prev;
		Node next;
		Object source;

		public Node()
		{
		}

		public Node(Object paramObject)
		{
			this.source=paramObject;
		}

		public Node prev()
		{
			return this.prev;
		}

		public Node next()
		{
			return this.next;
		}

		public Object getSource()
		{
			return this.source;
		}

		public void setSource(Object paramObject)
		{
			this.source=paramObject;
		}

		public void clear()
		{
			this.prev=(this.next=null);
		}
	}
}