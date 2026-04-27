package test;
/*
 * 双向链表
 * 总结：
 * 当时写这个的时候因为有单链表的基础了，写这个还比较顺。
 * 坑点主要是添加的时候记得连接前驱节点，跟删除的时候节点要分头尾中间节点位置去删
 * 自己还要去画图感受指针断开跟连接，多自己画图感受指针的连接。
 * 这个时候我就对指针跟引用还有对象就有了很深的理解了。不只是停留在字面意思了。
 * 而且还反推出了反射到底是个啥意思，反射就只是更高级别的引用而已。
 * 
 */
public class MyLinkedListTst01 {

	public static void main(String[] args) {
	    System.out.println("==================== 双向链表 极限压测开始 ====================");

	    MyLinkedList1<String> list = new MyLinkedList1<>();

	    // 1. 空链表全场景测试（最容易空指针）
	    System.out.println("\n>>> 测试1：空链表操作");
	    System.out.println(list);
	    System.out.println("get(-1): " + list.get(-1));
	    System.out.println("get(0): " + list.get(0));
	    System.out.println("remove(null): " + list.remove(null));
	    System.out.println("remove(不存在): " + list.remove("test"));
	    System.out.println("contains(null): " + list.contains(null));
	    System.out.println("set 非法下标: " + list.set(0, "aaa"));
	    Object[] arrEmpty = list.toArray();
	    System.out.println("空数组长度: " + arrEmpty.length);

	    // 2. 暴力添加 50 个元素（压测遍历 + 尾插）
	    System.out.println("\n>>> 测试2：批量添加 50 个元素");
	    for (int i = 1; i <= 50; i++) {
	        list.add("元素" + i);
	    }
	    System.out.println("当前 size: " + list.size);
	    System.out.println(list);

	    // 3. 头部疯狂插入（双向指针最容易乱）
	    System.out.println("\n>>> 测试3：头部狂插");
	    for (int i = 999; i >= 990; i--) {
	        list.insert(0, "头部" + i);
	    }
	    System.out.println("插入后 size: " + list.size);

	    // 4. 尾部插入
	    System.out.println("\n>>> 测试4：尾部插入");
	    list.insert(list.size, "尾插1");
	    list.insert(list.size, "尾插2");

	    // 5. 中间位置插入
	    System.out.println("\n>>> 测试5：中间插入");
	    list.insert(25, "中间插入");

	    // 6. 非法下标插入（边界）
	    System.out.println("\n>>> 测试6：非法下标插入");
	    System.out.println("insert -1: " + list.insert(-1, "错误"));
	    System.out.println("insert 超大: " + list.insert(99999, "错误"));

	    // 7. set 替换（头、中、尾）
	    System.out.println("\n>>> 测试7：set 替换");
	    list.set(0, "【替换头】");
	    list.set(30, "【替换中间】");
	    list.set(list.size - 1, "【替换尾】");

	    // 8. 删除头部 N 次
	    System.out.println("\n>>> 测试8：疯狂删头部");
	    for (int i = 0; i < 10; i++) {
	        list.remove("头部" + (999 - i));
	    }

	    // 9. 删除中间、删除尾部
	    System.out.println("\n>>> 测试9：删除中间 & 尾部");
	    list.remove("中间插入");
	    list.remove("尾插1");

	    // 10. 删除不存在元素
	    System.out.println("\n>>> 测试10：删除不存在的值");
	    System.out.println(list.remove("这玩意绝对没有"));

	    // 11. 删除 null
	    System.out.println("\n>>> 测试11：删除 null");
	    System.out.println(list.remove(null));

	    // 12. add null 测试（你代码拦截了）
	    System.out.println("\n>>> 测试12：添加 null");
	    System.out.println(list.add(null));

	    // 13. 暴力删到空
	    System.out.println("\n>>> 测试13：暴力清空链表");
	    int total = list.size;
	    for (int i = 0; i < total; i++) {
	        list.remove(list.get(0));
	    }
	    System.out.println("清空后 size: " + list.size);
	    System.out.println(list);

	    // 14. 空链表重建 + 全功能再测一遍
	    System.out.println("\n>>> 测试14：空链表重建");
	    list.add("A");
	    list.add("B");
	    list.add("C");
	    list.insert(0, "0");
	    list.insert(3, "3");
	    list.set(2, "替换B");
	    list.remove("A");
	    System.out.println(list);
	    System.out.println("get(0): " + list.get(0));
	    System.out.println("contains(C): " + list.contains("C"));

	    // 15. toArray 完整转换
	    System.out.println("\n>>> 测试15：toArray 数组");
	    Object[] array = list.toArray();
	    for (Object o : array) {
	        System.out.print(o + " ");
	    }

	    System.out.println("\n==================== 双向链表 全量压测完成！ ====================");
	}
	
}

class MyLinkedList1<V>{
	//拿到整个双向链表
	DoubleNode<V> head;
	//链表中元素个数
	int size;
	//对外提供头结点定义入口
	public MyLinkedList1(V value) {
		if(value == null) return;
		this.head = new DoubleNode<V>(value);
		this.size++;
	}

	public MyLinkedList1() {
		super();
	}
	
	public boolean contains(V value) {
		//遍历。没有写get(V value) 懒得弄了。
		if(this.size == 0 || value == null) return false;
		DoubleNode<V> node = this.head;
		while(node != null) {
			if(node.value.equals(value)) {
				return true;
			}
			node = node.next;
		}
		return false;
	}
	
	public boolean add(V value) {
		//空拦截
		if(value == null) return false;
		//头结点直接加
		if(this.head == null) {
			this.head = new DoubleNode<>(value);
			this.size++;
			return true;
		}
		//非头节点循环遍历拿到目标位置再加，记得指针两头都要连。
		DoubleNode<V> node = this.head;
		while(node != null) {
			if(node.next == null) {
				break;
			}
			node = node.next;
		}
		node.next = new DoubleNode<>(value);
		node.next.pre = node;
		this.size++;
		return true;
	}
	
	public boolean insert(int index,V value) {
		//精准拦截非链表长度及尾巴的下标插入
		//超出的下标也可以让他插，我不想这样写，会对用的人有歧义
		if(index < 0 || index > this.size) return false;
		DoubleNode<V> node = this.head;
		//前驱
		DoubleNode<V> pre = null;
		//用来定位目标节点
		int num = 0;
		while(node != null) {
			if(num == index) {
				break;
			}
			num++;
			pre = node;
			node = node.next;
		}
		//拿到目标节点分头部，尾部，中间，完事别漏了指针双向连接。
		//指针的方向多画图。
		if(node == null) {
			if(pre == null) {
				this.head = new DoubleNode<>(value);
			}else {
				node = new DoubleNode<>(value);
				node.pre = pre;
				pre.next = node;
			}
		}else {
			if(pre == null) {
				this.head = new DoubleNode<>(value);
				head.next = node;
				node.pre = head;
			}else {
				DoubleNode<V> newNode = new DoubleNode<>(value);
				pre.next = newNode;
				newNode.pre = pre;
				newNode.next = node;
				node.pre = newNode;
			}
		}
		this.size++;
		return true;
	}
	
	public boolean remove(V value) {
		//拦截空
		if(value == null || this.size == 0) return false;
		DoubleNode<V> node = this.head;
		//前驱
		DoubleNode<V> pre = null;
		while(node != null) {
			
			if(node.value.equals(value)) {
				break;
			}
			pre = node;
			node = node.next;
		}
		//没有找到目标直接返回
		if(node == null) return false;
		//分头节点，尾部节点，中间节点删除，注意指针连接
		if(node.next == null) {
			if(pre == null) this.head = null;else pre.next = null;
		}else {
			if(pre == null) {
				this.head = node.next;
				this.head.pre = null;
			}else {
				pre.next = node.next;
				node.next.pre = pre;
			}
		}
		//最后统一置空防止野指针。
		node.next = node.pre = null;
		this.size--;
		return true;
	}
	
	public boolean set(int index,V value) {
		//精准拦截链表长度以外的替换
		if(index < 0 || index >= this.size) return false;
		DoubleNode<V> node = this.head;
		int num = 0;
		while(node != null) {
			if(index == num) {
				node.value = value;
				return true;
			}
			num++;
			node = node.next;
		}
		return false;
	}
	
	public V get(int index) {
		if(index < 0 || index >= this.size) return null;
		DoubleNode<V> node = head;
		//通过num定位链表节点的index位置
		int num = 0;
		while(node != null) {
			if(num == index) {
				return node.value;
			}
			num++;
			node = node.next;
		}
		return null;
	}

	public Object[] toArray() {
		//就是遍历链表然后一个一个加到数组。
		if(size == 0) return new Object[0];
		Object[] objs = new Object[this.size];
		DoubleNode<V> node = head;
		int index = 0;
		while(node != null) {
			objs[index] = node.value;
			index++;
			node = node.next;
		}
		return objs;
	}
	
	@Override
	public String toString() {
		//StringBuffer是线程安全的性能低，还没自己写过原理不太清楚咋回事。
		//不是并发就用StringBuilder,效率高。
		//直接String加的话会浪费内存。每次加都会在方法区增加一个String对象。
		if(size == 0) return "[]";
		StringBuffer sb = new StringBuffer();
		DoubleNode<V> node = this.head;
		sb.append("[");
		while(node != null) {
			if(node.next != null) {
				sb.append(node.value);
				sb.append(",");
			}else {
				sb.append(node.value);
			}
			node = node.next;
		}
		sb.append("]");
		return sb.toString();
	}
	
}

class DoubleNode<V>{
	//双向链表需要回头节点所以需要定义一个前驱一个后继。也是俄罗斯套娃。
	//前驱可以让链表回头访问。
	DoubleNode<V> pre;
	DoubleNode<V> next;
	//存储数据。有序可以重复。
	V value;
	public DoubleNode(V value) {
		super();
		this.value = value;
	}
	public DoubleNode() {
		super();
	}
	@Override
	public String toString() {
		return this.value.toString();
	}
}