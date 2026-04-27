package test;

import java.util.LinkedList;
import java.util.List;

/*
 * 单向链表
 * 总结：
 * 这个我当时刚学完java语言刚接触到集合这章，对对象及引用完全停留在只理解字面意思的程度。
 * 当时写这个单链表的时候改了1-2版。自己逐行看代码执行结果，自己分析运行到哪里了。
 * 这个时候对指针执行到哪里了压根没啥概念，多是叫豆包告诉我哪个方法有问题，让她帮我定位，不给代码跟错误具体
 * 定位，纯折磨自己一行一行吃代码练习看代码纠错能力。
 * 单链表也没啥坑点主要就是自己要能看懂程序每行运行到哪里了。
 * 最重要的地方是不要用debug，那玩意对练习看代码能力纯副作用。
 * 
 */
public class MyLinkedListTest {

	public static void main(String[] args) {
	    System.out.println("==================== 单向链表 极限压测开始 ====================");

	    MyLinkedList<Integer, String> list = new MyLinkedList<>();

	    // ==========================================
	    // 1. 空链表全场景测试（最容易空指针）
	    // ==========================================
	    System.out.println("\n>>> 测试1：空链表行为");
	    list.display();
	    System.out.println("删除 null: " + list.remove(null));
	    System.out.println("删除不存在: " + list.remove(999));
	    System.out.println("contains 不存在: " + list.contains(999));
	    System.out.println("getKey null: " + list.getKey(null));
	    System.out.println("getValue null: " + list.getValue(null));
	    System.out.println("isEmpty: " + list.isEmpty());

	    // ==========================================
	    // 2. 暴力 put 100 个节点（压测遍历）
	    // ==========================================
	    System.out.println("\n>>> 测试2：批量 put 100 个节点");
	    for (int i = 1; i <= 100; i++) {
	        list.put(i, "值" + i);
	    }
	    System.out.println("插入后 size: " + list.size);
	    System.out.println("isEmpty: " + list.isEmpty());

	    // ==========================================
	    // 3. 覆盖重复 key（必须正确覆盖）
	    // ==========================================
	    System.out.println("\n>>> 测试3：覆盖重复 key");
	    list.put(50, "我被覆盖了");
	    System.out.println("获取 key=50: " + list.getValue(50));

	    // ==========================================
	    // 4. 头部疯狂插入（最容易指针错乱）
	    // ==========================================
	    System.out.println("\n>>> 测试4：头部狂插 insert");
	    for (int i = 999; i >= 990; i--) {
	        SimpleNode<Integer, String> node = new SimpleNode<>(i, "头部插入" + i);
	        list.insert(0, node);
	    }

	    // ==========================================
	    // 5. 尾部插入
	    // ==========================================
	    System.out.println("\n>>> 测试5：尾部插入");
	    SimpleNode<Integer, String> tailNode = new SimpleNode<>(88888, "尾节点");
	    list.insert(list.size, tailNode);

	    // ==========================================
	    // 6. 中间位置插入
	    // ==========================================
	    System.out.println("\n>>> 测试6：中间位置插入");
	    SimpleNode<Integer, String> midNode = new SimpleNode<>(77777, "中间节点");
	    list.insert(20, midNode);

	    // ==========================================
	    // 7. 非法下标插入（边界）
	    // ==========================================
	    System.out.println("\n>>> 测试7：非法下标插入");
	    SimpleNode<Integer, String> errNode = new SimpleNode<>(-1, "错误");
	    System.out.println("index=-1: " + list.insert(-1, errNode));
	    System.out.println("index=超大: " + list.insert(99999, errNode));
	    System.out.println("insert null: " + list.insert(0, null));

	    // ==========================================
	    // 8. set 替换（头、中、尾）
	    // ==========================================
	    System.out.println("\n>>> 测试8：set 替换节点");
	    list.set(0, new SimpleNode<>(11111, "替换头"));
	    list.set(15, new SimpleNode<>(22222, "替换中间"));
	    list.set(list.size - 1, new SimpleNode<>(33333, "替换尾"));

	    // ==========================================
	    // 9. set 传重复 key（必须先删再插）
	    // ==========================================
	    System.out.println("\n>>> 测试9：set 重复 key 节点");
	    list.set(5, new SimpleNode<>(50, "重复key覆盖"));

	    // ==========================================
	    // 10. 删除头部 N 次
	    // ==========================================
	    System.out.println("\n>>> 测试10：疯狂删头部");
	    for (int i = 0; i < 10; i++) {
	        list.remove(999 - i);
	    }

	    // ==========================================
	    // 11. 删除中间、删除尾部
	    // ==========================================
	    System.out.println("\n>>> 测试11：删除中间 & 尾部");
	    list.remove(77777);
	    list.remove(88888);

	    // ==========================================
	    // 12. 删除不存在节点
	    // ==========================================
	    System.out.println("\n>>> 测试12：删除不存在节点");
	    System.out.println(list.remove(123456789));

	    // ==========================================
	    // 13. 删除 null
	    // ==========================================
	    System.out.println("\n>>> 测试13：删除 null");
	    System.out.println(list.remove(null));

	    // ==========================================
	    // 14. 暴力清空链表
	    // ==========================================
	    System.out.println("\n>>> 测试14：暴力清空到空");
	    int total = list.size;
	    for (int i = 0; i < total; i++) {
	        list.remove(1); // 从头删到尾
	    }
	    System.out.println("清空后 size: " + list.size);
	    list.display();

	    // ==========================================
	    // 15. 空了再删、再插、再覆盖
	    // ==========================================
	    System.out.println("\n>>> 测试15：空链表重建");
	    list.put(1, "A");
	    list.put(2, "B");
	    list.put(3, "C");
	    list.insert(0, new SimpleNode<>(0, "头部"));
	    list.set(2, new SimpleNode<>(99, "替换"));

	    // ==========================================
	    // 16. clear 清空
	    // ==========================================
	    System.out.println("\n>>> 测试16：clear 清空");
	    list.clear();
	    System.out.println("clear 后 size: " + list.size);
	    System.out.println("isEmpty: " + list.isEmpty());

	    System.out.println("\n==================== 单向链表 全量压测完成！ ====================");
	}

}
class MyLinkedList<K,V>{
	//拿到整个链表。
	SimpleNode<K,V> head;
	//这个标记是用来对链表特定位置进行插入用的。
	int index = -1;
	//记录链表长度
	int size;
	//对外提供自定义头结点。
	public MyLinkedList(K key,V value) {
		this.head = new SimpleNode<K,V>(key,value);
		this.size++;
		this.index++;
	}
	
	public MyLinkedList() {
		super();
	}

	public boolean isEmpty() {
		return this.size == 0;
	}
	
	public void clear() {
		this.head = null;
		this.size = 0;
	}
	
	public void display() {
		if(size == 0) return;
		SimpleNode<K,V> node = head;
		while(node != null) {
			System.out.println(node);
			node = node.next;
		}
	}
	
	public boolean put(K key,V value) {
		//不允许空键值对。
		if(key == null || value == null) return false;
		//没有头节点直接加。
		if(size == 0) {
			head = new SimpleNode<K,V>(key,value);
			index++;
			size++;
			return true;
		}
		SimpleNode<K,V> node = head;
		while(node != null) {
			if(node.key.equals(key)) {
				//找到相同节点直接覆盖。
				node.value = value;
				return true;
			}
			//单向链表只需要指向下一个节点就行了。
			//自己对引用要完全吃透不然容易改不了指针。
			if(node.next == null) {
				node.next = new SimpleNode<K,V>(key,value);
				break;
			}
			node = node.next;
		}
		index++;
		size++;
		return true;
	}
	
	public boolean contains(Object obj) {
		//遍历
		if(size == 0 || obj == null) return false;
		SimpleNode<K,V> node = head;
		while(node != null) {
			if(node.key.equals(obj) || node.value.equals(obj)) {
				return true;
			}
			node = node.next;
		}
		return false;
	}
	
	public K getKey(V value) {
		//遍历
		if(size == 0 || value == null) return null;
		SimpleNode<K,V> node = head;
		while(node != null) {
			if(node.value.equals(value)) {
				return node.key;
			}
			node = node.next;
		}
		return null;
	}
	
	public V getValue(K key) {
		//遍历
		if(size == 0 || key == null) return null;
		SimpleNode<K,V> node = head;
		while(node != null) {
			if(node.key.equals(key)) {
				return node.value;
			}
			node = node.next;
		}
		return null;
	}
	
	public boolean remove(K key) {
		//拦截空
		if(size == 0 || key == null) return false;
		SimpleNode<K,V> node = head;
		//一定要会用前驱节点跟踪，这个不是一般的好用
		SimpleNode<K,V> pre = null;
		while(node != null) {
			//找到目标节点出循环处理
			if(node.key.equals(key)) {
				break;
			}
			pre = node;
			node = node.next;
		}
		//找不到直接返回
		if(node == null) return false;
		//找到以后分头节点跟中间节点和尾节点。这里是单链表只要分头节点就行了。
		if(pre == null) this.head = node.next;
		else pre.next = node.next;
		//防止野指针
		node.next = null;
		this.size--;
		this.index--;
		return true;
	}
	
	//这个我开始想的太宽了，搞了挺久的。要把插入跟替换切割开来，一个功能只能做一样事情
	//插入就是插入，替换就是替换不要混到一起写。不然这个方法到底干嘛了自己都不知道。
	public boolean insert(int index,SimpleNode<K,V> node) {
		//拦截不在链表长度范围内以及尾部插入的节点。
		if(index > this.size || index < 0 || node == null) return false;
		if(this.contains(node.key)) return false;
		SimpleNode<K,V> old = this.head;
		//前驱非常好用
		SimpleNode<K,V> pre = null;
		//记录循环次数匹配插入位置。
		int num = 0;
		while(old != null) {
			//找到目标位置
			if(num == index) {
				break;
			}
			num++;
			pre = old;
			old = old.next;
		}
		//找到目标位置以后分头部，尾部，中间。
		if(old == null) {
			node.next = null;
			if(pre == null) this.head = node;
			else pre.next = node;
			
		}else {
			node.next = old;
			if(pre == null) this.head = node;
			else pre.next = node;
		}
		this.size++;
		this.index++;
		return true;
	}
	
	//插入就专门干插入的事。不要考虑添加。
	public boolean set(int index,SimpleNode<K,V> node) {
		//拦截不在链表长度范围内的下标节点替换。
		if(index < 0 || index >= this.size || node == null) return false;
		//判断是否该节点的键值对key是不是存在，存在就直接删除了再加。
		if(this.contains(node.key)) {
			this.remove(node.key);
			this.insert(index, node);
			return true;
		}
		//不存就直接找到需要插入的目标位置直接替换掉原节点。
		SimpleNode<K,V> old = this.head;
		SimpleNode<K,V> pre = null;
		int num = 0;
		while(old != null) {
			if(num == index) {
				break;
			}
			num++;
			pre = old;
			old = old.next;
		}
		//找到目标位置以后分头节点跟其他节点处理。
		//处理的时候记得删原指针。因为在删除在执行的过程中并没有真删除掉目标节点。
		//在java内只要还有指针指向就会存在。所以要手动置空防止野指针。
		if(pre == null) {
			this.head = node;
			node.next = old.next;
		}else {
			pre.next = node;
			node.next = old.next;
		}
		old.next = null;
		return true;
	}
}
class SimpleNode<K,V>{
	//单链表只需要指向下一个节点就够了。俄罗斯套娃。数据结构基本全是这个玩意。
	SimpleNode<K,V> next;
	//用key的唯一性定位节点。这个是反射，我的理解是对象级别的反射。只能看到部分权限的功能和属性。
	//反射的我理解还不够深，只知道类级别的反射可以通过getClass拿到。权限是我目前6已知的最高权限。
	//能访问到类内部的所有功能及属性。
	K key;
	//真实需要存储的数据
	V value;
	public SimpleNode(K key, V value) {
		this.key = key;
		this.value = value;
		this.next = null;
	}
	public SimpleNode() {
		this(null, null);
	}
	
}