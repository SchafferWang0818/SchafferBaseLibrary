---

title: Android之 缓存与LruCache
categories: "android 总结"
tags: 
	- 缓存
	- LruCache

---
# 缓存与LruCache #

- 参考:
	- [ **Android LruCache源码分析，图片缓存算法的实现原理** ](https://mp.weixin.qq.com/s/Zf7YHmoD0mM4RMvsHc5EAw)
	- [ **内存缓存LruCache实现原理** ](https://www.cnblogs.com/liuling/archive/2015/09/24/2015-9-24-1.html)
	- 《 Android 代码艺术探索 》

---
## `LruCache(Least Recently Used Cache)` 与 `LinkedHashMap` ##

### LruCache ###
> **<font color=red>内存缓存规定大小</font>快满时,淘汰<font color=red>近期最少使用</font>的缓存目标.**

> <font color = BlueViolet  size=3>
>**`LruCache` 效果关键在于`this.map = new LinkedHashMap<K, V>(0, 0.75f, true)`中的true按照存取顺序排序。**
>**当在`LruCache`中放入元素时,元素依次放入链表后侧。**
>**当访问过其中一个元素时,元素移除并放入链表后侧。**
>**当内存缓存达到设定的最大值时，将链表头部的对象（近期最少用到的）移除。**
></font>

- 抽象 / 空实现函数

```java
	/**
	 * evicted : = true时是为了腾出空间而调用, = false 时是 put() 或 remove() 中手动调用
	 */
	protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {}
	
	/**
	 * safeSizeOf( K key , V value ) 内部调用,计算当前节点的大小
	 */
	protected int sizeOf(K key, V value) { return 1; }
	
	/**
	 * 获取不到 value 时的创建流程
	 */	
	protected V create(K key) { return null; }
```

- `put(K key , V value)`

![https://i.imgur.com/a4OiomD.jpg](https://i.imgur.com/a4OiomD.jpg)

```java
    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    public final V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        V previous;
        synchronized (this) {
            putCount++;
            //>>>	计算当前存入节点的大小,并加入到使用的总缓存的大小
            size += safeSizeOf(key, value);
            //>>>	存入map时若已经存在同key的value,则返回之前的值.
            previous = map.put(key, value);
            if (previous != null) {
            //>>>	计算之前节点的大小,然后从总缓存删除
                size -= safeSizeOf(key, previous);
            }
        }

        if (previous != null) {
        //>>>	有旧值移除 回调 空实现entryRemoved()
            entryRemoved(false, key, previous, value);
        }
        //>>>	整理总缓存大小,判断是否超过规定的最大值
        trimToSize(maxSize);
        return previous;
    }

```
- `get(K key)`
![https://i.imgur.com/esN87Z5.jpg](https://i.imgur.com/esN87Z5.jpg)

```java
    /**
     * Returns the value for {@code key} if it exists in the cache or can be
     * created by {@code #create}. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    public final V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        V mapValue;
        synchronized (this) {
            //>>>	调用LinkedHashMap 的get方法
            mapValue = map.get(key);
            if (mapValue != null) {
                hitCount++;
                return mapValue;
            }
            missCount++;
        }
        //>>>	获取不到就创建
        V createdValue = create(key);
        if (createdValue == null) {
            return null;
        }

        synchronized (this) {
            createCount++;
            //>>>	存入map时若已经存在同key的value，将原来键为key的对象保存到mapValue
            mapValue = map.put(key, createdValue);
            //>>>	刚创建了新值的情况下没有历史值,mapValue = null
            if (mapValue != null) {
                //>>>	如果mapValue不为空，则撤销上一步的put操作。
                map.put(key, mapValue);
            } else {
            	//>>>	加入新创建的对象之后计算大小
                size += safeSizeOf(key, createdValue);
            }
        }
        //锁定操作结束
		
        if (mapValue != null) {
        	//>>>	回调
            entryRemoved(false, key, createdValue, mapValue);
            return mapValue;
        } else {
            //>>>	每次新加入对象都需要调用trimToSize方法看是否需要回收
            trimToSize(maxSize);
            return createdValue;
        }
    }
    

    /**
     * 移除最久未使用的内容 , 直到使缓存总量小于规定的最大值
     * 如果maxSize传入-1，则清空缓存中的所有对象
     * Remove the eldest entries until the total of remaining entries is at or
     * below the requested size.
     *
     * @param maxSize the maximum size of the cache before returning. May be -1
     *            to evict even 0-sized elements.
     */
    public void trimToSize(int maxSize) {
        while (true) {
            K key;
            V value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }
                //>>>	如果当前size小于maxSize或者map没有任何对象,则结束循环
                if (size <= maxSize) {
                    break;
                }
                Map.Entry<K, V> toEvict = map.eldest();
                if (toEvict == null) {
                    break;
                }
                
                //>>>	移除链表头部的元素，并进入下一次循环
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= safeSizeOf(key, value);
                evictionCount++;
            }
            //>>>	腾出空间而回调
            entryRemoved(true, key, value, null);
        }
    }
    
```
- `remove(K key)`

```java
    /**
     * Removes the entry for {@code key} if it exists.
     * 从内存缓存中根据key值移除某个对象并返回该对象
     * @return the previous value mapped by {@code key}.
     */
    public final V remove(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        V previous;
        synchronized (this) {
        	//>>>	从map中移除value
            previous = map.remove(key);
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        if (previous != null) {
        	//>>>	回调
            entryRemoved(false, key, previous, null);
        }

        return previous;
    }
```
### `LinkedHashMap` 的使用 ###

```java
    /**
     * Constructs an empty <tt>LinkedHashMap</tt> instance with the
     * specified initial capacity, load factor and ordering mode.
     *
     * @param  initialCapacity 初始容量
     * @param  loadFactor      负载因子
     * @param  accessOrder    true = 存取顺序 , false = 插入顺序 , 默认 false
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
```
- `get()`

```java
    public V get(Object key) {
        LinkedHashMapEntry<K,V> e = (LinkedHashMapEntry<K,V>)getEntry(key);
        if (e == null)
            return null;
        e.recordAccess(this);
        return e.value;
    }

	//	LinkedHashMap $ LinkedHashMapEntry 
	void recordAccess(HashMap<K,V> m) {
		LinkedHashMap<K,V> lm = (LinkedHashMap<K,V>)m;
		//>>>	存取顺序时
		if (lm.accessOrder) {
			lm.modCount++;
			//>>>	将自身节点从链表中移除
			remove();
			//>>>	将自身节点添加到链表首节点
			addBefore(lm.header);
		}
	}

```
- `put()`

```java
	public V put(K key, V value) { 
	        if (table == EMPTY_TABLE) { 
	            inflateTable(threshold); 
	        } 
	        if (key == null)  //>>>	HashMap的特点 可以放key为null的值 
	            return putForNullKey(value); 
	        int hash = sun.misc.Hashing.singleWordWangJenkinsHash(key); 
	        int i = indexFor(hash, table.length); 
	        for (HashMapEntry<K,V> e = table[i]; e != null; e = e.next) { 
	            Object k; 
	            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) { 
	            	//>>>	替换原值出来
	                V oldValue = e.value; 
	                e.value = value; 
	                e.recordAccess(this); 
	                //>>>	存在旧值 就把旧值移除掉 并返回旧值  由此可知 当我们更换缓存中已存在的值时，并不会影响它在链表中位置 
	                return oldValue;
	            } 
	        } 
	
	        modCount++; 
	        //>>>	LinkedHashMap 重写了该方法 
	        addEntry(hash, key, value, i);
	        return null; 
	    } 
	
	   //LinkedHashMap # addEntry()   
	   void addEntry(int hash, K key, V value, int bucketIndex) { 
	 
	        LinkedHashMapEntry<K,V> eldest = header.after; 
	        if (eldest != header) { 
	            boolean removeEldest; 
	            size++; 
	            try { 
	                //>>>	hook  默认为false 让用户重写removeEldestEntry 来决定是否移除eldest节点 
	                removeEldest = removeEldestEntry(eldest);
	            } finally { 
	                size--; 
	            } 
	            if (removeEldest) { 
	                removeEntryForKey(eldest.key); 
	            } 
	        } 
	
	        super.addEntry(hash, key, value, bucketIndex);
	    } 
	
	  protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { return false;  } 
	  
	   //HashMap # addEntry()
	   void addEntry(int hash, K key, V value, int bucketIndex) { 
	        if ((size >= threshold) && (null != table[bucketIndex])) { 
	            resize(2 * table.length); 
	            hash = (null != key) ? sun.misc.Hashing.singleWordWangJenkinsHash(key) : 0; 
	            bucketIndex = indexFor(hash, table.length); 
	        } 
	         //>>>	LinkedHashMap重写了该方法 
	        createEntry(hash, key, value, bucketIndex); 
	    } 
	    

	    //LinkedHashMap # createEntry() 
	    void createEntry(int hash, K key, V value, int bucketIndex) { 
	        HashMapEntry<K,V> old = table[bucketIndex]; 
	        LinkedHashMapEntry<K,V> e = new LinkedHashMapEntry<>(hash, key, value, old); 
	        table[bucketIndex] = e; 
	        //>>>	把该节点插入到链表头部 最近使用访问到的在最前边原则 
	        e.addBefore(header);  
	        size++; 
	    }
	
```

---
## 缓存 ##
> **包括内存缓存和磁盘缓存.**



---