 package zlib.set;
 
 public final class IntList
   implements Cloneable
 {
   public static final int CAPACITY = 10;
   int[] array;
   int size;
 
   public IntList()
   {
     this(10);
   }
 
   public IntList(int paramInt)
   {
     if (paramInt < 1)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, invalid capatity:" + paramInt);
     this.array = new int[paramInt];
     this.size = 0;
   }
 
   public IntList(int[] paramArrayOfInt)
   {
     this(paramArrayOfInt, (paramArrayOfInt != null) ? paramArrayOfInt.length : 0);
   }
 
   public IntList(int[] paramArrayOfInt, int paramInt)
   {
     if (paramArrayOfInt == null)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, null array");
     if (paramInt > paramArrayOfInt.length)
       throw new IllegalArgumentException(super.getClass().getName() + 
         " <init>, invalid length:" + paramInt);
     this.array = paramArrayOfInt;
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
 
   public int[] getArray()
   {
     return this.array;
   }
 
   public void setCapacity(int paramInt)
   {
     int[] arrayOfInt1 = this.array;
     int i = arrayOfInt1.length;
     if (paramInt <= i) return;
     for (; i < paramInt; i = (i << 1) + 1);
     int[] arrayOfInt2 = new int[i];
     System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, this.size);
     this.array = arrayOfInt2;
   }
 
   public int get(int paramInt)
   {
     return this.array[paramInt];
   }
 
   public int getFirst()
   {
     return this.array[0];
   }
 
   public int getLast()
   {
     return this.array[(this.size - 1)];
   }
 
   public boolean contain(int paramInt)
   {
     return (indexOf(paramInt, 0) >= 0);
   }
 
   public int indexOf(int paramInt)
   {
     return indexOf(paramInt, 0);
   }
 
   public int indexOf(int paramInt1, int paramInt2)
   {
     int i = this.size;
     if (paramInt2 >= i) return -1;
     int[] arrayOfInt = this.array;
     for (int j = paramInt2; j < i; ++j)
     {
       if (paramInt1 == arrayOfInt[j]) return j;
     }
     return -1;
   }
 
   public int lastIndexOf(int paramInt)
   {
     return lastIndexOf(paramInt, this.size - 1);
   }
 
   public int lastIndexOf(int paramInt1, int paramInt2)
   {
     if (paramInt2 >= this.size) return -1;
     int[] arrayOfInt = this.array;
     for (int i = paramInt2; i >= 0; --i)
     {
       if (paramInt1 == arrayOfInt[i]) return i;
     }
     return -1;
   }
 
   public int set(int paramInt1, int paramInt2)
   {
     if (paramInt2 >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " set, invalid index=" + paramInt2);
     int i = this.array[paramInt2];
     this.array[paramInt2] = paramInt1;
     return i;
   }
 
   public boolean add(int paramInt)
   {
     if (this.size >= this.array.length) setCapacity(this.size + 1);
     this.array[(this.size++)] = paramInt;
     return true;
   }
 
   public void add(int paramInt1, int paramInt2)
   {
     if (paramInt2 < this.size)
     {
       if (this.size >= this.array.length) setCapacity(this.size + 1);
       if (this.size > paramInt2)
         System.arraycopy(this.array, paramInt2, this.array, paramInt2 + 1, this.size - paramInt2);
       this.array[paramInt2] = paramInt1;
       this.size += 1;
     }
     else
     {
       if (paramInt2 >= this.array.length) setCapacity(paramInt2 + 1);
       this.array[paramInt2] = paramInt1;
       this.size = (paramInt2 + 1);
     }
   }
 
   public void addAt(int paramInt1, int paramInt2)
   {
     if (paramInt2 < this.size)
     {
       if (this.size >= this.array.length) setCapacity(this.size + 1);
       this.array[(this.size++)] = this.array[paramInt2];
       this.array[paramInt2] = paramInt1;
     }
     else
     {
       if (paramInt2 >= this.array.length) setCapacity(paramInt2 + 1);
       this.array[paramInt2] = paramInt1;
       this.size = (paramInt2 + 1);
     }
   }
 
   public boolean remove(int paramInt)
   {
     int i = indexOf(paramInt, 0);
     if (i < 0) return false;
     removeIndex(i);
     return true;
   }
 
   public boolean removeAt(int paramInt)
   {
     int i = indexOf(paramInt, 0);
     if (i < 0) return false;
     removeIndexAt(i);
     return true;
   }
 
   public int removeIndex(int paramInt)
   {
     if (paramInt >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " removeIndex, invalid index=" + paramInt);
     int[] arrayOfInt = this.array;
     int i = arrayOfInt[paramInt];
     int j = this.size - paramInt - 1;
     if (j > 0) System.arraycopy(arrayOfInt, paramInt + 1, arrayOfInt, paramInt, j);
     this.size -= 1;
     return i;
   }
 
   public int removeIndexAt(int paramInt)
   {
     if (paramInt >= this.size)
       throw new ArrayIndexOutOfBoundsException(super.getClass().getName() + 
         " removeIndexAt, invalid index=" + paramInt);
     int[] arrayOfInt = this.array;
     int i = arrayOfInt[paramInt];
     arrayOfInt[paramInt] = arrayOfInt[(--this.size)];
     return i;
   }
 
   public void clear()
   {
     this.size = 0;
   }
 
   public int[] toArray()
   {
     int[] arrayOfInt = new int[this.size];
     System.arraycopy(this.array, 0, arrayOfInt, 0, this.size);
     return arrayOfInt;
   }
 
   public int toArray(int[] paramArrayOfInt)
   {
     int i = (paramArrayOfInt.length > this.size) ? this.size : paramArrayOfInt.length;
     System.arraycopy(this.array, 0, paramArrayOfInt, 0, i);
     return i;
   }
 
   public Object clone()
   {
     try
     {
       IntList localIntList = (IntList)super.clone();
       int[] arrayOfInt = localIntList.array;
       localIntList.array = new int[localIntList.size];
       System.arraycopy(arrayOfInt, 0, localIntList.array, 0, localIntList.size);
       return localIntList;
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
 * Qualified Name:     zlib.set.IntList
 * JD-Core Version:    0.5.3
 */
