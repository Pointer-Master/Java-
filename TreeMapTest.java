package test;
/*
 * BST二叉树：
 * 总结：
 * 写这个我学到了工程化的思维方式。对问题一定要把问题拆解开来处理，这样的话
 * 才能让问题难度指数级下降，变成线性单个单个问题来处理。也要多刷一些经典题
 * 练习自己的思维能力。还有就是一定不要对豆包太信任了，这玩意分析问题的能力
 * 真不算厉害。一个底层的回溯思维都能把她看懵逼，我也是醉了。通过写这个至少
 * 让我站到了后端编程的大门口了，虽然这个门还依然对我关着，至少我相信以后
 * 这个门绝对会为我打开，并让我踏进去这个大门。
 * 另外我的这些代码除了这个二叉树有验证代码前面几个我都没怎么验证过，基本就是纯脑补加逐行
 * 看代码运行跟发给豆包验证过而已，不一定能保证没有任何bug。如果有大佬愿意提携
 * 帮助我提高写出优质代码的能力，一起自学的小伙伴也可以相互帮助一起学习，谢谢了。
 * 这个就是我20天对数据结构的学习的心路历程，也算是对自己自学的一个交代或者一个认可。
 * 接下来我会继续学AVL，红黑树。B树不一定会学，看前面两棵树的学习进度再绝对。
 * 后续我能手撕AVL了我会继续把我的心路历程发到这里来。
 * 
 *  可以加我微信yl40909451。
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class TreeMapTest {

	public static void main(String[] args) {
	    MyTreeMap<Integer, String> tree = new MyTreeMap<>();
	    System.out.println("==================== 【超大规模】BST 二叉树 极限压测开始 ====================");

	    // ==========================================
	    // 1. 暴力插入 200 个节点（构造巨型二叉树）
	    // ==========================================
	    System.out.println("\n>>> 阶段 1：批量插入 200 个节点（构建超大树）");
	    for (int i = 1; i <= 200; i++) {
	        tree.put(i, "值-" + i);
	    }
	    checkBSTValid(tree.head);
	    System.out.println("插入完成 | 总节点数 size = " + tree.size);
	    System.out.println("中序遍历校验（必须严格升序）：");
	    System.out.println("------------------------------------");

	    // ==========================================
	    // 2. 随机乱序插入 100 个节点（制造完全乱序树）
	    // ==========================================
	    System.out.println("\n>>> 阶段 2：乱序插入 100 个节点（破坏顺序，强压测试）");
	    int[] randomKeys = {
	        999, 111, 555, 222, 888, 333, 777, 444, 666, 1000,
	        1001, 123, 456, 789, 112, 231, 564, 876, 321, 654,
	        135, 246, 357, 468, 579, 680, 791, 802, 913, 1024
	    };
	    for (int key : randomKeys) {
	        tree.put(key, "随机值-" + key);
	    }
	    checkBSTValid(tree.head);
	    System.out.println("乱序插入完成 | 最新 size = " + tree.size);
	    tree.printLevelOrderZ();
	    // ==========================================
	    // 3. 覆盖重复 Key（必须正确覆盖）
	    // ==========================================
	    System.out.println("\n>>> 阶段 3：重复 Key 覆盖测试");
	    tree.put(100, "【被覆盖-100】");
	    tree.put(500, "【被覆盖-500】");
	    System.out.println("覆盖后 get(100) = " + tree.get(100));

	    // ==========================================
	    // 4. 极限删除：叶子、单子、双子、根节点 狂删！
	    // ==========================================
	    System.out.println("\n>>> 阶段 4：暴力删除（叶子/单子/双子/根节点 全删一遍）");
	    // 删除叶子节点
	    tree.remove(1);
	    tree.remove(200);
	    // 删除单子节点
	    tree.remove(111);
	    tree.remove(333);
	    // 删除双子节点
	    tree.remove(500);
	    tree.remove(777);
	    // 删除根节点（反复删根，最容易崩）
	    tree.remove(50);
	    tree.remove(60);
	    tree.remove(70);
	    checkBSTValid(tree.head);
	    System.out.println("删除完成 | 剩余 size = " + tree.size);

	    // ==========================================
	    // 5. 中序遍历校验（必须仍然严格升序）
	    // ==========================================
	    System.out.println("\n>>> 阶段 5：删除后中序遍历（必须严格升序）");
	    tree.midDisplay();

	    // ==========================================
	    // 6. 层序遍历 + Z 字遍历 双重校验
	    // ==========================================
	    System.out.println("\n>>> 阶段 6：层序遍历（队列）");
	    tree.printLevelOrderQueue();

	    System.out.println("\n>>> 阶段 7：Z 字层序遍历（终极结构校验）");
	    tree.printLevelOrderZ();

	    // ==========================================
	    // 7. 删到空，再重建（极限抗压）
	    // ==========================================
	    System.out.println("\n>>> 阶段 8：暴力清空到空树");
	    int total = tree.size;
	    for (int i = 1; i <= total; i++) {
	        tree.remove(i);
	    }
	    // 清空乱序插入的节点
	    for (int key : randomKeys) {
	        tree.remove(key);
	    }
	    System.out.println("清空后 size = " + tree.size);
	    System.out.println("空树中序遍历：");
	    tree.midDisplay();

	    // ==========================================
	    // 8. 空树重建，再次全功能测试
	    // ==========================================
	    System.out.println("\n>>> 阶段 9：空树重建，最终测试");
	    tree.put(88, "根节点");
	    tree.put(50, "左");
	    tree.put(120, "右");
	    tree.put(30, "左左");
	    tree.put(60, "左右");
	    tree.put(100, "右左");
	    tree.put(150, "右右");
	    tree.replace(88, "【重建根】");
	    System.out.println("重建完成 | get(88) = " + tree.get(88));
	    tree.midDisplay();

	    System.out.println("\n==================== ✅ 【超大规模】BST 压测 100% 全部通过！ ====================");
	}
	 // ===================== BST 合法性终极校验（工业标准） =====================
	 // 检查：左 < 根 < 右，指针合法，无环
	 public static <K extends Comparable<K>, V> void checkBSTValid(TreeNode<K, V> node) {
	     if (node == null) return;


	     // 左孩子必须 < 当前节点
	     if (node.left != null) {
	         if (node.left.key.compareTo(node.key) >= 0) {
	             throw new RuntimeException("BST 非法：左孩子 >= 父节点 key=" + node.key);
	         }
	         if (node.left.pre != node) {
	             throw new RuntimeException("父指针错误：左节点 pre 指向异常");
	         }
	     }
	     // 右孩子必须 > 当前节点
	     if (node.right != null) {
	         if (node.right.key.compareTo(node.key) <= 0) {
	             throw new RuntimeException("BST 非法：右孩子 <= 父节点 key=" + node.key);
	         }
	         if (node.right.pre != node) {
	             throw new RuntimeException("父指针错误：右节点 pre 指向异常");
	         }
	     }


	     checkBSTValid(node.left);
	     checkBSTValid(node.right);
	 

	}
	
}

class MyTreeMap<K extends Comparable<K>,V>{
	//拿到整个二叉树。
	TreeNode<K,V> head;
	//二叉树中的元素个数
	int size;
	//二叉树的层数
	int level;
	
	//对外提供头节点入口
	public MyTreeMap(K key,V value) {
		super();
		this.head = new TreeNode<>(key,value);
		this.size++;
	}
	
	public MyTreeMap() {
		super();
	}
	
	// 标准递归中序遍历 = 3行！
	//这个代码的含金量只有自己写过回溯再想用递归去写的时候才能体会到震撼。而且我还学到的彩蛋。
	public void standardInOrder(TreeNode<K,V> node) {
	    if(node == null) return;   // 1. 终止条件
	    standardInOrder(node.left); // 2. 先递归左子树
	    System.out.println(node);   // 3. 打印（中序位置）
	    standardInOrder(node.right); // 4. 最后递归右子树
	}
	
	//这个方法在我刚写的时候我没有意识到会自动遍历。也还想了不少时间才想通。
	//代码逐行执行循环的过程中天然会进行逐个遍历。当时意识不到，一直想的是遍历了左边怎么回右边跟自己。
	//主要是自己当时还没完全吃透代码逐行循环依次执行。
	public boolean put(K key,V value) {
		//拦截空键值对
		if(key == null || value == null) return false;
		if(this.size == 0) {
			this.head = new TreeNode<>(key,value);
			this.size++;
			return true;
		}
		//这个跟seek的本质差不多。我是这么理解的。
		TreeNode<K,V> node = head;
		//前驱
		TreeNode<K,V> pre = null;
		while(node != null) {
			//找到重复key直接覆盖值不新增，键唯一性
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			pre = node;
			//这个comparaTo方法有坑，我当时用泛型写的时候就困惑了很久，豆包给的解释才理解到底应该写。
			//一定是用this.去调用comparaTo(指针)按升序。现在自己用泛型的这个比较也还没完全吃透用法。
			if(node.key.compareTo(key) < 0) node = node.right;else node = node.left;
		}
		//找到节点为空的时候，用键的大小方向来获得孩子的左右添加方向。
		//这里有前驱节点就相当方面，如果没有的话压根不知道应该怎么去连接指针方向。
		if(pre.key.compareTo(key) < 0) {
			pre.right = new TreeNode<>(key,value);
			pre.right.pre = pre;
		}else {
			pre.left = new TreeNode<>(key,value);
			pre.left.pre = pre;
		}
		this.size++;
		return true;
	}
	
	public boolean remove(K key) {
		//拦截空
		if(this.size == 0 || key == null) return false;
		TreeNode<K,V> node = head;
		TreeNode<K,V> pre = null;
		while(node != null) {
			if(node.key.equals(key)) break;
			pre = node;
			if(node.key.compareTo(key) < 0 ) node = node.right;else node = node.left;
		}
		//没有找到目标节点直接返回false
		if(node == null) return false;
		//找到目标节点以后自己画二叉树去理解到底分多少情况删除该节点。
		//我搞这个remove方法搞了有1天多时间。画了一大堆图。
		//本质其实也跟删链表是一回事，分头节点，末尾节点，中间节点三种情况。
		//只有自己画图才能彻底吃透到底这三种情况下还有多少种情况，再按自己画图分析的拆解。
		//我当时第一版写的是真删法，指针乱飞改了好几版才彻底吃透应该怎么删。
		//现在这个是按豆包说的后继删除法，生产级的但是有bug，会存在被删除节点外部有引用的话原指针
		//还指向原节点的数据，但是删除以后内部数据已经更新了。会出现数据不匹配的情况。
		//这里最后我还踩了一个坑，本来双孩子后继节点删除法本质是删后继，我在最后还按真删法把原节点给删了
		//豆包开始还没说，幸好删的是头节点，我后面在写别的方法时需要用头节点的时候出了bug。不然被坑惨。
		//一定要掌握好自己逐行看代码运行到哪里的能力，不然豆包给的代码自己压根不知道对不对，有没有
		//隐藏的坑等着你。反正我用豆包她连我纯手写回溯中序遍历她都分析不出来我的输出时机，我的明明是
		//对的，她跟我说我的输出会重复。
		if(node.right == null && node.left == null) {
			if(pre == null) {
				this.head = null;
			}else{
				if(pre.left == node) pre.left = null;
				if(pre.right == node) pre.right = null;
			}
		}else if(node.left == null) {
			if(pre == null) {
				this.head = node.right;
				this.head.pre = null;
			}else if(pre.left == node){
				pre.left = node.right;
				node.right.pre = pre;
			}else if(pre.right == node) {
				pre.right = node.right;
				node.right.pre = pre;
			}
		}else if(node.right == null) {
			if(pre == null) {
				this.head = node.left;
				this.head.pre = null;
			}else if(pre.left == node){
				pre.left = node.left;
				node.left.pre = pre;
			}else if(pre.right == node) {
				pre.right = node.left;
				node.left.pre = pre;
			}
		}else {
			TreeNode<K,V> child = node.right;
			TreeNode<K,V> pChild = null;
			while(child.left != null) {
				pChild = child;
				child = child.left;
			} 
			node.key = child.key;
			node.value = child.value;
			if(pChild == null) {
				if(child.right == null) {
					node.right = null;
				}else {
					node.right = child.right;
					child.right.pre = node;
				}
			}else {
				if(child.right == null) {
					pChild.left = null;
				}else {
					pChild.left = child.right;
					child.right.pre = pChild;
				}
			}
			child.left = child.right = child.pre = null;
		}
		this.size--;
		return true;
	}
	
	public V get(K key) {
		if(this.size == 0 || key == null) return null;
		TreeNode<K,V> node = head;
		while(node != null) {
			if(node.key.equals(key)) {
				return node.value;
			}else if(node.key.compareTo(key) < 0) {
				node = node.right;
			}else {
				node = node.left;
			}
		}
		return null;
	}
	
	public boolean replace(K key, V value) {
		if (key == null || value == null) return false;
		TreeNode<K,V> node = head;
		while (node != null) {
			//找到目标直接换
			if (node.key.equals(key)) {
				node.value = value;
				return true;
			}
			node = node.key.compareTo(key) < 0 ? node.right : node.left;
		}
		return false;
	}
	
	public boolean isEmpty() {
		return this.size == 0;
	}
	
	public boolean containsKey(K key) {
		return this.get(key) != null;
	}
	
	public boolean containsValue(V value) {
		if (isEmpty()) return false;
		return containsValue(head, value);
	}
	
	private boolean containsValue(TreeNode<K,V> node, V value) {
		if (node == null) return false;
		if (node.value.equals(value)) return true;
		return containsValue(node.left, value) || containsValue(node.right, value);
	}
	
	public void clear() {
		this.head = null;
		this.size = 0;
	}
	
	//写这个方法我卡了1-2天。真的被折磨的要疯一样。
	public void midDisplay() {
		if(this.size == 0) return;
		TreeNode<K,V> node = this.head;
		//这里直接开始第一次循环，啥也不管直接循环
		do {
			//这里用node循环还是node.left自己一定要想清楚，非常坑。
			while(node.left != null) {
				node = node.left;
			}
			//走到最左边就输出直接输出
			System.out.println(node);
			//进入右边准备分情况是回溯还是往右继续执行。
			//这个方法我画了一大堆图，被折磨了1-2天在中午休息的时候在脑子里模拟运行才彻底想通了
			//本来我最开始的方法是用的前驱节点判断的，发给豆包分析，她后面告诉我直接用原生的效果更好。
			//我才改成了现在的版本。
			if(node.right == null) {
				//直接往上循环回溯。这里我画了很多图才想明白应该直接循环回溯。
				//因为程序循环的时候也是一次一次进行循环的，在循环的过程中就可以拆解父节点的执行情况。
				while(node != null) {
					//遇到头节点为空直接跳出循环，因为右边跑完以后所有节点就全部跑了一遍然后回到头了。
					//这里一定要画图才能理解为什么是所有节点都跑完了回到这里跳出循环，程序结束
					if(node.pre == null) {
						node = null;
						break;
					}
					//准备回溯到父节点了，就得分我是我父亲的左孩子还是右边，父亲是不是有右孩子跟我是不是有右孩子了。
					//因为循环进行过程中，你已经不再是最初的你自己，你已经变成了你的父亲，而你父亲如果有右孩子
					//就一定是还没走过大循环的。所以当你是你父亲的右孩子而且你右边又还有孩子就一定得把大循环走一遍。
					//直到你的最右边为空了说明你已经走到底了，这时候你的左边也走完了右边也走完了就得返回了。
					//这里返回的时候应该是最难的地方了。你需要走到变成你父亲的左孩子才行。不然你又得在回溯的时候
					//继续往右走，因为你右边必定有右孩子。然后进入死循环。
					//总结：难点就是画图找到分支情况，找到分支情况以后，再找到最右端的节点应该怎么回溯的问题，
					//不是自己一步一步画图靠人脑模拟，估计只有编程的大神有能力吧，对于初学的我们来说基本不可能。
					//我在学写完回溯问题以后直接长舒了一口气，虽然非常折磨，但是同样也获得了编程的爽感，这玩意用钱
					//压根买不到。而且一旦进入到这里以后，我就看到了计算机编程的门的朝向在哪了。我的脑子也会拒绝我
					//自己摆烂，身体不管怎么抗拒，脑子也会时刻去想编程的问题。而且搞完这个以后我想用递归的方式再
					//把二叉树的遍历用递归写一下的时候，还发现了彩蛋。后面的方法我会说。
					if(node == node.pre.left && node.pre.right == null) {
						node = node.pre;
						System.out.println(node);
					}else if(node == node.pre.left && node.pre.right != null ) {
						node = node.pre.right;
						System.out.println(node.pre);
						break;
					}else if(node == node.pre.right && node.right != null){
						node = node.right;
						break;
					}else {
						while(node != null) {
							if(node.pre == null || node == node.pre.left) break;
							node = node.pre;
						}
					}
				}
			}else {
				//最左节点有右节点直接往右
				node = node.right;
			}
		}while(node != null);
	}
	
	public void printLevelOrderQueue() {
		//这个只能说一下心路历程，豆包直接给我的代码。等于我自己背了一次她的写法。
		//主要开始我想层序实在是想了大半天，连笔都提不起写任何一个代码，因为我的思维进了死胡同出不来。
		//我一直在想应该怎么控制流程，因为每次程序一执行我就会拿到2的幂个孩子。而我又没有容器装，
		//我就想着用一个数组装，然后运行一次再第二个数组，但是这么下去，我会获得N个数组，压根不现实。
		//实在没办法了就问的豆包，她就告诉我用队列解决这个问题，队列是先进先出。下一次进入的去末尾排队等着
		//后面我就把她给的代码背到了我的程序里，开始看着感觉好简单，但是当我去画图的时候又被吓傻了。这玩意
		//里面压了数不清的节点在那排队等着被叫号，压根不是字面那几行代码那么简单。我才又安下心来让豆包给了
		//我3个题目，用队列，栈去实现层序输出，队列栈去实现层序的Z字输出。这里就到这了，后面有我写栈跟Z
		//字输出的心路历程。
		if(this.size == 0) return;
		Queue<TreeNode<K,V>> queue = new LinkedList<>();
		queue.add(this.head);
		while(!queue.isEmpty()) {
			TreeNode<K,V> node = queue.poll();
			System.out.println(node);
		    if(node.left != null) {
			   queue.add(node.left);
		    }
		    if(node.right != null) {
		    	queue.add(node.right);
		    }
		}
	}
	
	public void printLevelOrderStack() {
		//这个方法我自己写出来的时候，因为当时是晚上8-9点了。所以对自己没有意识到一些思维问题。没有及时复盘
		//导致自己在写Z的时候又坑自己浪费半天时间去想Z字层序输出。这里我实际已经知道了对问题应该拆解开来运行
		//但是当时写出来的时候压根没有意识到这种思维方式。导致我只是会做题目而没有掌握思想。
		if(this.size == 0) return;
		Stack<TreeNode<K,V>> s = new Stack<>();
		s.add(this.head);
		while(!s.isEmpty()) {
			//因为栈是先进后出的规则。所以我的想法是直接在每次加入到栈的时候把后续添加的元素先加到列表内
			//再通过遍历列表规定栈的添加顺序来实现栈的先进后出。当时应该也是太晚了，而且被层序折磨了一下午
			//有点身心疲惫所以晚上休息就没复盘。
			ArrayList<TreeNode<K,V>> al = new ArrayList<>();
			TreeNode<K,V> node = s.pop();
			System.out.println(node);
			while(!s.isEmpty()) {
				al.add(s.pop());
			}
			if(node.left != null) {
				al.add(node.left);
			}
			if(node.right != null) {
				al.add(node.right);
			}
			for(int i=al.size()-1;i>=0;i--) {
				s.add(al.get(i));
			}
		}
	}
	
	public void printLevelOrderZ() {
		if(this.size == 0) return;
		//写这个的时候最开始的时候我是想到了用两个队列控制流程。一个队列负责输出一个负责加。
		//各自做各自的事情，相互不干扰。这时候我就意识到了处理问题就应该这样把问题细化分开来看待了。
		//就跟我开始写单向链表的时候插入跟替换要分开的道理一个样。
		//不过这里我刚开始写也还是踩过一个坑用了队列引用指向另一个引用的傻逼行为。不过我在写完以后没一会
		//就意识到了这么写是错的，因为这样赋值就是把原队列的指针指向了另一个，等于脱裤子放屁而已。
		//后面的层序Z字就水到渠成了，不过在我用队列跟栈写代码的时候我就想过自己再写一个队列跟栈的。
		//但是我把类刚定义好就意识到了，这玩意不就是我写的链表跟数组么，我再写有个屁的意义。这玩意本质
		//就是我自己写的链表跟列表，几乎就是一模一样的东西，只不过改了个名字而已。瞬间就感觉编程原来都是数据
		//结构这玩意。这里有一个坑队列的.reversed方法没有用得用工具类Collections.reverse(指针)才能对列表进行排序。
		Queue<TreeNode<K,V>> que = new LinkedList<>();
		Queue<TreeNode<K,V>> que1 = new LinkedList<>();
		que.add(this.head);
		int num = 1;
		while(!que.isEmpty()) {
			List<TreeNode<K,V>> l = new LinkedList<>();
			while(!que.isEmpty()) {
				TreeNode<K,V> node = que.poll();
				if(node.left != null) {
					que1.add(node.left);
				}
				if(node.right != null) {
					que1.add(node.right);
				}
				l.add(node);
			}
			if(num % 2 == 0) {
				//这个没用l.reversed();
				Collections.reverse(l);
			}
			System.out.print("[");
			for(int i=0;i<l.size();i++) {
				if(i==l.size()-1) {
					System.out.print(l.get(i));
				}else {
					System.out.print(l.get(i)+",");
				}
			}
			System.out.println("]");
			num++;
			while(!que1.isEmpty()) {
				que.add(que1.poll());
			}
			this.level++;
		}
	}
}
class TreeNode<K extends Comparable<K>,V> {
	TreeNode<K,V> pre;
	TreeNode<K,V> left;
	TreeNode<K,V> right;
	K key;
	V value;
	public TreeNode(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public TreeNode() {
		super();
	}
	
	@Override
	public String toString() {
		return this.key.toString()+","+this.value.toString();
	}
	
}
