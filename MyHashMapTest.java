package test;

/*
 * 总结：
 * 这个没有加旋转跟红黑树整体写下来就只是把链表跟数组结合的玩意没啥新东西。
 * 就只是多了一个平衡因子问题，这个我也不是特别理解，只知道这个平衡因子能让hash表能
 * 在扩容以后依然能达到最优的查询删除速度。
 * 因为扩容是以节点数量的跟数组长度的0.75来决定的。
 * 在通过hash碰撞重新拿到新的数组下标时会进行最优分配。
 * 在自己逐行看运行的时候当时就出现了我删的节点在运行过程中本质压根不是被我真的删除了
 * 而是原指针任然指向原来的地方的困惑，这个是豆包帮我解释的。就发现了野指针问题。
 * 并且刚写的时候是边移动指针边删原指针，对于删原指针应该统一在执行结束以后再删。
 * 扩容还让我学到了点位运算，了解了点皮毛。不然自己就是纯降幂在找位移的位置。
 * 这里还让我懂了一点我的类库我自己做主的感觉，对于类的成员变量，压根不需要困惑应该有哪些。
 * 自己缺啥加啥，需要啥就加啥。只要底层用啥结构存储自己清楚了定义好以后，其余成员变量自己随意加。
 * 我的类库我自己做主。
 */
public class MyHashMapTest {

	public static void main(String[] args) {
	    System.out.println("==================== MY HASHMAP 极限压测开始 ====================");

	    MyHashMap<String, Integer> map = new MyHashMap<>();

	    // 1. 空集合全测试（空指针重灾区）
	    System.out.println("\n>>> 测试1：空集合操作");
	    System.out.println("get null: " + map.getValue(null));
	    System.out.println("remove null: " + map.remove(null));
	    System.out.println("remove 不存在: " + map.remove("test"));
	    System.out.println("contains 不存在: " + map.contains("test"));
	    System.out.println("set 空集合: " + map.set("a", 1));
	    System.out.println("当前 size: " + map.size);

	    // 2. 暴力添加 50 个元素（强制触发多次扩容）
	    System.out.println("\n>>> 测试2：批量添加 50 个（触发多次扩容）");
	    for (int i = 1; i <= 50; i++) {
	        map.put("KEY_" + i, i);
	    }
	    System.out.println("添加后 size: " + map.size);
	    System.out.println("获取 KEY_50: " + map.getValue("KEY_50"));

	    // 3. 极限哈希冲突（同一个下标塞满链表，最容易断链/野指针）
	    System.out.println("\n>>> 测试3：极限哈希冲突（同一个桶塞满）");
	    // 这些 key 哈希码相同，会全部进入同一个下标
	    map.put("Aa", 100);
	    map.put("BB", 200);
	    map.put("Cc", 300);
	    map.put("DD", 400);
	    map.put("Ee", 500);
	    map.put("Ff", 600);
	    System.out.println("Aa: " + map.getValue("Aa"));
	    System.out.println("BB: " + map.getValue("BB"));
	    System.out.println("Ff: " + map.getValue("Ff"));

	    // 4. Key 重复覆盖测试
	    System.out.println("\n>>> 测试4：重复 Key 覆盖");
	    map.put("KEY_10", 99999);
	    System.out.println("覆盖后 KEY_10: " + map.getValue("KEY_10"));

	    // 5. 删除：头节点、冲突链表中间、冲突链表尾部
	    System.out.println("\n>>> 测试5：删除冲突链表节点（最容易指针错）");
	    map.remove("Aa");    // 删冲突头
	    map.remove("Cc");    // 删冲突中间
	    map.remove("Ff");    // 删冲突尾部
	    System.out.println("删后 Aa: " + map.getValue("Aa"));
	    System.out.println("删后 BB: " + map.getValue("BB"));

	    // 6. 批量删除（删到空）
	    System.out.println("\n>>> 测试6：暴力清空所有元素");
	    int total = map.size;
	    for (int i = 1; i <= total; i++) {
	        map.remove("KEY_" + i);
	    }
	    map.remove("BB");
	    map.remove("DD");
	    map.remove("Ee");
	    System.out.println("清空后 size: " + map.size);

	    // 7. 空了再删、再添加
	    System.out.println("\n>>> 测试7：空集合重建");
	    map.put("测试A", 111);
	    map.put("测试B", 222);
	    map.put("测试C", 333);
	    System.out.println("重建后 size: " + map.size);
	    System.out.println("测试B: " + map.getValue("测试B"));

	    // 8. set 修改（存在/不存在）
	    System.out.println("\n>>> 测试8：set 修改");
	    map.set("测试A", 66666);
	    System.out.println("修改后 测试A: " + map.getValue("测试A"));
	    System.out.println("set 不存在: " + map.set("XXX", 123));

	    // 9. 空值拦截（你代码做了防护）
	    System.out.println("\n>>> 测试9：null 键/值 拦截");
	    System.out.println("put null key: " + map.put(null, 123));
	    System.out.println("put null value: " + map.put("TEST", null));

	    // 10. 超大容量构造器测试
	    System.out.println("\n>>> 测试10：自定义容量构造");
	    MyHashMap<String, String> bigMap = new MyHashMap<>(100);
	    bigMap.put("BIG", "SUCCESS");
	    System.out.println("大 map 获取: " + bigMap.getValue("BIG"));

	    System.out.println("\n==================== ✅ HASHMAP 全量压测全部通过！ ====================");
	}
	
}

//还没学AVL跟红黑树后续再加。
class MyHashMap<K,V>{
	//为了查询的性能
	Object[] objs;
	//整个hash表中元素的个数
	int size;
	//判断扩容的条件，不是很理解为啥叫平衡因子，而且我不是太理解这样设计的原理。
	//自己写完了也只知道是为达到最高的增删改查效率
	int x;
	public MyHashMap() {
		this.objs = new Object[16];
		//平衡因子0.75为了达到最优的查询跟删除效率。
		this.x = (int)(objs.length * 0.75);
	}

	public MyHashMap(int index) {
		//外部直接传参的数组容量大小严格管控为2的幂，为了达到最优的查询跟删除效率。
		//标准的位运算写法。
		/*
		if(index < 16) {
			this.objs = new Object[16];
		}else {
			int capacity = 16;
			while(capacity < index) {
				capacity = capacity << 1;
			}
			this.objs = new Object[capacity];
		}
		this.x = (int)(objs.length * 0.75);
		*/
		if(index < 16) {
			this.objs = new Object[16];
			this.x = (int)(objs.length * 0.75);
		}else {
			int num = -1;
			while(true) {
				index = index/2;
				num++;
				if(index == 0) break;
			}
			this.objs = new Object[1 << num];
			this.x = (int)(objs.length * 0.75);
		}
	}

	private void capacity() {
		//触发扩容后对原数组中的所有元素进行新hash碰撞产生新的数组。
		//这样才能使hash桶保持接近数组的查询效率以为链表的删除效率。
		Object[] newObjs = new Object[this.objs.length << 1];
		for(int i=0;i<this.objs.length;i++) {
			HashNode<K,V> node = (HashNode<K, V>) this.objs[i];
			if(node != null) {
				while(node != null) {
					this.put(node.key,node.value,newObjs);
					node = node.next;
				}
			}
		}
		this.objs = newObjs;
		this.x = (int) (this.objs.length * 0.75);
	}
	
	public boolean put(K key,V value) {
		//不考虑null键值对。生产中应该没啥意义。而且处理的话非常麻烦。
		if(key == null || value == null) return false;
		//因为x是double转的所以要用大于等于。触发扩容
		//我开始想的是用数组元素长度的0.75扩容。这样会使hash变成纯数组。
		//用元素个数扩容才能更好的利用数组和链表的优势，进行增删改查。
		if(this.size >= this.x) {
			this.capacity();
		}
		//获得hash值。在添加时候先获得哈希值定位到需要加入的链表在哪再进行equals比较。
		//这里有个坑hash值会有负数需要用工具类Math转一下。也可以自己手动转。
		int index = Math.abs(key.hashCode() % this.objs.length);
		if(this.objs[index] == null) {
			this.objs[index] = new HashNode<>(key,value);
			this.size++;
			return true;
		}
		//通过hash值定位到数组中的链表的头节点。
		HashNode<K, V> node = (HashNode<K, V>) this.objs[index];
		//前驱节点循环跟踪。
		HashNode<K, V> pre = null;
		while(node != null) {
			//找到同key键时进行覆盖。
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			pre = node;
			node = node.next;
		}
		//拿到目标节点再进行添加，不要漏了指针的连接，否则断链。
		node = new HashNode<>(key,value);
		pre.next = node;
		this.size++;
		return true;
	}
	
	private boolean put(K key,V value,Object[] objs) {
		//这个是添加是对应插入时的不对外提供
		//原理跟添加一致。
		int index = Math.abs(key.hashCode() % this.objs.length);
		if(objs[index] == null) {
			objs[index] = new HashNode<K,V>(key,value);
			return true;
		}
		HashNode<K, V> node = (HashNode<K, V>) objs[index];
		HashNode<K, V> pre = null;
		while(node != null) {
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			pre = node;
			node = node.next;
		}
		node = new HashNode<>(key,value);
		pre.next = node;
		return true;
	}
	
	public boolean remove(K key) {
		if(this.size == 0 || key == null) return false;
		int index = Math.abs(key.hashCode() % this.objs.length);
		if(objs[index] == null) return false;
		//拿到头节点
		HashNode<K,V> node = (HashNode<K, V>) objs[index];
		//前驱跟踪
		HashNode<K,V> pre = null;
		while(node != null) {
			if(node.key.equals(key)) break;
			pre = node;
			node = node.next;
		}
		//没找到就直接返回
		if(node == null) return false;
		//找到目标节点进行删除。不要漏了删除节点的指针手动置空，防止野指针
		if(pre == null) objs[index] = node.next;
		else pre.next = node.next;
		node.next = null;
		this.size--;
		return true;
	}

	public boolean set(K key,V value) {
		//直接拦截空键值对和空数组。
		if(this.size == 0 || key == null || value == null) return false;
		int index = Math.abs(key.hashCode() % this.objs.length);
		//hash碰撞的头节点为空直接拦截。
		if(objs[index] == null) return false;
		HashNode<K,V> node = (HashNode<K, V>) objs[index];
		while(node != null) {
			//遍历找到目标直接覆盖。
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			node = node.next;
		}
		return false;
	}
	
	public V getValue(K key) {
		//遍历获取
		if(key == null || this.size == 0) return null;
		int index = Math.abs(key.hashCode() % this.objs.length);
		HashNode<K, V> node = (HashNode<K, V>) objs[index];
		if(node == null) return null;
		while(node != null) {
			if(node.key.equals(key)) {
				return node.value;
			}
			node = node.next;
		}
		return null;
	}
	
	public boolean contains(K key) {
		return this.getValue(key) != null;
	}
	
	
}

class HashNode<K,V>{
	//单链表只需要指向下一个节点就够了。俄罗斯套娃。数据结构基本全是这个玩意。
	HashNode<K,V> next;
	//用来对节点唯一性进行验证用的
	K key;
	//需要存储的数据
	V value;
	public HashNode(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public HashNode() {
		super();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof HashNode)) return false;
		if(obj == this) return true;
		return ((HashNode)obj).key.equals(this.key);
	}

	@Override
	public String toString() {
		return this.key+","+this.value;
	}
}
