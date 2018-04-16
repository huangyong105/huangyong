 package zlib.set;
 
 public class LongList
   implements Cloneable
 {
   public static final int CAPACITY = 10;
   long[] array;
   int size;
 
   public LongList()
   {
     this(10);
   }
 
   public LongList(int paramInt)
   {
     if (paramInt < 1)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, invalid capatity:" + paramInt);
     this.array = new long[paramInt];
     this.size = 0;
   }
 
   public LongList(long[] paramArrayOfLong)
   {
     this(paramArrayOfLong, (paramArrayOfLong != null) ? paramArrayOfLong.length : 0);
   }
 
   public LongList(long[] paramArrayOfLong, int paramInt)
   {
     if (paramArrayOfLong == null)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, null array");
     if (paramInt > paramArrayOfLong.length)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, invalid length:" + paramInt);
     this.array = paramArrayOfLong;
     this.size = paramInt;
   }
 
   public int size()
   {
     return this.size;
   }
 
   public int capacity()
   {
     return this.array.length;
   }
 
   public boolean isEmpty()
   {
     return (this.size <= 0);
   }
 
   public long[] getArray()
   {
     return this.array;
   }
 
   public void setCapacity(int paramInt)
   {
     long[] arrayOfLong1 = this.array;
     int i = arrayOfLong1.length;
     if (paramInt <= i) return;
     for (; i < paramInt; i = (i << 1) + 1);
     long[] arrayOfLong2 = new long[i];
     System.arraycopy(arrayOfLong1, 0, arrayOfLong2, 0, this.size);
     this.array = arrayOfLong2;
   }
 
   public long get(int paramInt)
   {
     return this.array[paramInt];
   }
 
   public long getFirst()
   {
     return this.array[0];
   }
 
   public long getLast()
   {
     return this.array[(this.size - 1)];
   }
 
   public boolean contain(long paramLong)
   {
     return (indexOf(paramLong, 0) >= 0);
   }
 
   public int indexOf(long paramLong)
   {
     return indexOf(paramLong, 0);
   }
 
   public int indexOf(long paramLong, int paramInt)
   {
     int i = this.size;
     if (paramInt >= i) return -1;
     long[] arrayOfLong = this.array;
     for (int j = paramInt; j < i; ++j)
     {
       if (paramLong == arrayOfLong[j]) return j;
     }
     return -1;
   }
 
   public int lastIndexOf(long paramLong)
   {
     return lastIndexOf(paramLong, this.size - 1);
   }
 
   public int lastIndexOf(long paramLong, int paramInt)
   {
     if (paramInt >= this.size) return -1;
     long[] arrayOfLong = this.array;
     for (int i = paramInt; i >= 0; --i)
     {
       if (paramLong == arrayOfLong[i]) return i;
     }
     return -1;
   }
 
   public long set(long paramLong, int paramInt)
   {
     if (paramInt >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " set, invalid index=" + paramInt);
     long l = this.array[paramInt];
     this.array[paramInt] = paramLong;
     return l;
   }
 
   public boolean add(long paramLong)
   {
     if (this.size >= this.array.length) setCapacity(this.size + 1);
     this.array[(this.size++)] = paramLong;
     return true;
   }
 
   public void add(long paramLong, int paramInt)
   {
     if (paramInt < this.size)
     {
       if (this.size >= this.array.length) setCapacity(this.size + 1);
       if (this.size > paramInt)
         System.arraycopy(this.array, paramInt, this.array, paramInt + 1, this.size - paramInt);
       this.array[paramInt] = paramLong;
       this.size += 1;
     }
     else
     {
       if (paramInt >= this.array.length) setCapacity(paramInt + 1);
       this.array[paramInt] = paramLong;
       this.size = (paramInt + 1);
     }
   }
 
   public void addAt(long paramLong, int paramInt)
   {
     if (paramInt < this.size)
     {
       if (this.size >= this.array.length) setCapacity(this.size + 1);
       this.array[(this.size++)] = this.array[paramInt];
       this.array[paramInt] = paramLong;
     }
     else
     {
       if (paramInt >= this.array.length) setCapacity(paramInt + 1);
       this.array[paramInt] = paramLong;
       this.size = (paramInt + 1);
     }
   }
 
   public boolean remove(long paramLong)
   {
     int i = indexOf(paramLong, 0);
     if (i < 0) return false;
     removeIndex(i);
     return true;
   }
 
   public boolean removeAt(long paramLong)
   {
     int i = indexOf(paramLong, 0);
     if (i < 0) return false;
     removeIndexAt(i);
     return true;
   }
 
   public long removeIndex(int paramInt)
   {
     if (paramInt >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " removeIndex, invalid index=" + paramInt);
     long[] arrayOfLong = this.array;
     long l = arrayOfLong[paramInt];
     int i = this.size - paramInt - 1;
     if (i > 0) System.arraycopy(arrayOfLong, paramInt + 1, arrayOfLong, paramInt, i);
     this.size -= 1;
     return l;
   }
 
   public long removeIndexAt(int paramInt)
   {
     if (paramInt >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " removeIndexAt, invalid index=" + paramInt);
     long[] arrayOfLong = this.array;
     long l = arrayOfLong[paramInt];
     arrayOfLong[paramInt] = arrayOfLong[(--this.size)];
     return l;
   }
 
   public void clear()
   {
     this.size = 0;
   }
 
   public long[] toArray()
   {
     long[] arrayOfLong = new long[this.size];
     System.arraycopy(this.array, 0, arrayOfLong, 0, this.size);
     return arrayOfLong;
   }
 
   public int toArray(long[] paramArrayOfLong)
   {
     int i = (paramArrayOfLong.length > this.size) ? this.size : paramArrayOfLong.length;
     System.arraycopy(this.array, 0, paramArrayOfLong, 0, i);
     return i;
   }
 
   public Object clone()
   {
     try
     {
       LongList localLongList = (LongList)super.clone();
       long[] arrayOfLong = localLongList.array;
       localLongList.array = new long[localLongList.size];
       System.arraycopy(arrayOfLong, 0, localLongList.array, 0, localLongList.size);
       return localLongList;
     }
     catch (CloneNotSupportedException localCloneNotSupportedException)
     {
       throw new RuntimeException(super.getClass().getName() + 
         " clone, capacity=" + this.array.length, localCloneNotSupportedException);
     }
   }
 
   public String toString() {
     return super.toString() + "[size=" + this.size + ", capacity=" + this.array.length + "]";
   }
 }

/* Location:           D:\workspace\ZQserver_1\app\zlib.zip
 * Qualified Name:     zlib.set.LongList
 * JD-Core Version:    0.5.3
 */
