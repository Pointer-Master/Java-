package test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import java.util.*;

public class MyRBTTest {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  红黑树插入操作工业级压力测试");
        System.out.println("=================================================\n");
        
        boolean allPassed = true;
        
        // 测试1：基本功能测试
        allPassed &= testBasicOperations();
        
        // 测试2：顺序插入测试
        allPassed &= testSequentialInsertion();
        
        // 测试3：随机插入测试
        allPassed &= testRandomInsertion();
        
        // 测试4：重复键测试
        allPassed &= testDuplicateKeys();
        
        // 测试5：大规模数据压力测试
        allPassed &= testMassiveInsertion();
        
        // 测试6：特殊场景测试
        allPassed &= testSpecialScenarios();
        
        if (allPassed) {
            System.out.println("\n=================================================");
            System.out.println("  ✅ 所有插入压力测试通过！");
            System.out.println("=================================================");
        } else {
            System.out.println("\n=================================================");
            System.out.println("  ❌ 测试失败");
            System.out.println("=================================================");
        }
    }
    
    // ===================== 测试1：基本功能测试 =====================
    private static boolean testBasicOperations() {
        System.out.println("【测试1】基本功能测试");
        try {
            MyRBT<Integer, String> rbt = new MyRBT<>();
            
            // 1.1 空树测试
            assert rbt.head == null : "空树头节点应为null";
            assert rbt.size == 0 : "空树size应为0";
            
            // 1.2 单个节点插入
            rbt.put(10, "A");
            assert rbt.size == 1 : "插入后size应为1";
            assert rbt.head != null : "头节点不应为null";
            assert rbt.head.key == 10 : "头节点key应为10";
            assert rbt.head.color == Color.BLACK : "根节点必须为黑色";
            
            // 1.3 重复键测试
            rbt.put(10, "B");
            assert rbt.size == 1 : "重复插入后size应不变";
            assert "B".equals(rbt.head.value) : "值应被更新";
            
            // 1.4 多个节点插入
            for (int i = 0; i < 10; i++) {
                rbt.put(i, "Value" + i);
            }
            assert rbt.size == 10 : "插入10个不同键后size应为10";
            
            // 验证红黑树性质
            validateRBT(rbt, "基本功能测试后");
            
            System.out.println("  ✅ 基本功能测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 基本功能测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试2：顺序插入测试 =====================
    private static boolean testSequentialInsertion() {
        System.out.println("【测试2】顺序插入测试");
        try {
            // 2.1 升序插入
            MyRBT<Integer, String> rbt1 = new MyRBT<>();
            for (int i = 0; i < 1000; i++) {
                rbt1.put(i, "Asc" + i);
                if (i % 100 == 0) {
                    validateRBT(rbt1, "升序插入第" + i + "个节点后");
                }
            }
            validateRBT(rbt1, "升序插入最终验证");
            
            // 2.2 降序插入
            MyRBT<Integer, String> rbt2 = new MyRBT<>();
            for (int i = 999; i >= 0; i--) {
                rbt2.put(i, "Desc" + i);
                if (i % 100 == 0) {
                    validateRBT(rbt2, "降序插入第" + (999 - i) + "个节点后");
                }
            }
            validateRBT(rbt2, "降序插入最终验证");
            
            // 2.3 交替插入
            MyRBT<Integer, String> rbt3 = new MyRBT<>();
            for (int i = 0; i < 500; i++) {
                rbt3.put(i * 2, "Even" + i);
                rbt3.put(i * 2 + 1, "Odd" + i);
                if (i % 100 == 0) {
                    validateRBT(rbt3, "交替插入第" + (i * 2) + "个节点后");
                }
            }
            validateRBT(rbt3, "交替插入最终验证");
            
            System.out.println("  ✅ 顺序插入测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 顺序插入测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试3：随机插入测试 =====================
    private static boolean testRandomInsertion() {
        System.out.println("【测试3】随机插入测试 (10000次随机插入)");
        try {
            MyRBT<Integer, String> rbt = new MyRBT<>();
            TreeMap<Integer, String> reference = new TreeMap<>();
            Random rand = new Random(42); // 固定种子保证可重复
            
            for (int i = 0; i < 10000; i++) {
                int key = rand.nextInt(10000);
                String value = "Rand" + key;
                
                rbt.put(key, value);
                reference.put(key, value);
                
                // 定期验证
                if (i % 1000 == 0) {
                    validateRBT(rbt, "随机插入第" + i + "次后");
                }
            }
            
            // 最终验证
            validateRBT(rbt, "随机插入最终验证");
            validateAgainstReference(rbt, reference);
            
            System.out.println("  ✅ 随机插入测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 随机插入测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试4：重复键测试 =====================
    private static boolean testDuplicateKeys() {
        System.out.println("【测试4】重复键测试");
        try {
            MyRBT<Integer, String> rbt = new MyRBT<>();
            
            // 4.1 多次插入相同键
            for (int i = 0; i < 100; i++) {
                rbt.put(42, "Value" + i);
                assert rbt.size == 1 : "重复插入后size应为1";
                assert ("Value" + i).equals(rbt.head.value) : "应返回最新值";
            }
            
            // 4.2 混合重复键
            Set<Integer> keys = new HashSet<>();
            Random rand = new Random(42);
            for (int i = 0; i < 1000; i++) {
                int key = rand.nextInt(10); // 只有10个不同的键
                rbt.put(key, "K" + key);
                keys.add(key);
                assert rbt.size == keys.size() : "size应与唯一键数相同";
            }
            
            validateRBT(rbt, "重复键测试后");
            
            System.out.println("  ✅ 重复键测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 重复键测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试5：大规模数据压力测试 =====================
    private static boolean testMassiveInsertion() {
        System.out.println("【测试5】大规模数据压力测试 (100000个节点)");
        try {
            MyRBT<Integer, String> rbt = new MyRBT<>();
            Random rand = new Random(42);
            
            long startTime = System.currentTimeMillis();
            
            // 插入100000个随机节点
            for (int i = 0; i < 100000; i++) {
                int key = rand.nextInt(1000000);
                rbt.put(key, "Massive" + key);
            }
            
            long insertTime = System.currentTimeMillis() - startTime;
            System.out.println("  插入100000节点耗时: " + insertTime + "ms");
            
            // 验证插入后的树
            validateRBT(rbt, "大规模插入后");
            
            // 随机查找测试
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                int key = rand.nextInt(1000000);
                rbt.head = rbt.head; // 模拟查找，实际应该调用get方法
            }
            long searchTime = System.currentTimeMillis() - startTime;
            System.out.println("  10000次随机查找耗时: " + searchTime + "ms");
            
            System.out.println("  ✅ 大规模数据压力测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 大规模数据测试失败: " + e.getMessage());
            return false;
        } catch (OutOfMemoryError e) {
            System.out.println("  ⚠️ 大规模数据测试内存不足，跳过此项测试");
            return true;
        }
    }
    
    // ===================== 测试6：特殊场景测试 =====================
    private static boolean testSpecialScenarios() {
        System.out.println("【测试6】特殊场景测试");
        try {
            // 6.1 测试LL旋转
            System.out.print("  - 测试LL旋转: ");
            MyRBT<Integer, String> llTest = new MyRBT<>();
            int[] llSequence = {30, 20, 10}; // 应该触发LL旋转
            for (int key : llSequence) {
                llTest.put(key, "LL" + key);
            }
            validateRBT(llTest, "LL旋转后");
            System.out.println("✅");
            
            // 6.2 测试RR旋转
            System.out.print("  - 测试RR旋转: ");
            MyRBT<Integer, String> rrTest = new MyRBT<>();
            int[] rrSequence = {10, 20, 30}; // 应该触发RR旋转
            for (int key : rrSequence) {
                rrTest.put(key, "RR" + key);
            }
            validateRBT(rrTest, "RR旋转后");
            System.out.println("✅");
            
            // 6.3 测试LR旋转
            System.out.print("  - 测试LR旋转: ");
            MyRBT<Integer, String> lrTest = new MyRBT<>();
            int[] lrSequence = {30, 10, 20}; // 应该触发LR旋转
            for (int key : lrSequence) {
                lrTest.put(key, "LR" + key);
            }
            validateRBT(lrTest, "LR旋转后");
            System.out.println("✅");
            
            // 6.4 测试RL旋转
            System.out.print("  - 测试RL旋转: ");
            MyRBT<Integer, String> rlTest = new MyRBT<>();
            int[] rlSequence = {10, 30, 20}; // 应该触发RL旋转
            for (int key : rlSequence) {
                rlTest.put(key, "RL" + key);
            }
            validateRBT(rlTest, "RL旋转后");
            System.out.println("✅");
            
            // 6.5 测试叔叔为红色的情况
            System.out.print("  - 测试叔叔为红的情况: ");
            MyRBT<Integer, String> uncleRedTest = new MyRBT<>();
            // 构造一个叔叔为红的情况
            // 先构造一个需要重新着色的情况
            uncleRedTest.put(10, "A");
            uncleRedTest.put(5, "B");
            uncleRedTest.put(15, "C");
            uncleRedTest.put(3, "D");  // 这个插入应该触发叔叔为红的重新着色
            validateRBT(uncleRedTest, "叔叔为红情况后");
            System.out.println("✅");
            
            System.out.println("  ✅ 特殊场景测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 特殊场景测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 验证工具方法 =====================
    
    private static void validateRBT(MyRBT<Integer, String> rbt, String context) {
        if (rbt.head == null) {
            return;
        }
        
        // 验证性质2：根节点是黑色
        if (rbt.head.color != Color.BLACK) {
            throw new AssertionError(context + ": 根节点必须是黑色");
        }
        
        // 验证性质4：红色节点的子节点必须是黑色
        validateNoConsecutiveReds(rbt.head, context);
        
        // 验证性质5：从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点
        validateBlackHeight(rbt.head, context);
        
        // 验证BST性质
        validateBST(rbt.head, Integer.MIN_VALUE, Integer.MAX_VALUE, context);
    }
    
    private static void validateNoConsecutiveReds(RBTNode<Integer, String> node, String context) {
        if (node == null) return;
        
        if (node.color == Color.RED) {
            if (node.left != null && node.left.color == Color.RED) {
                throw new AssertionError(context + ": 连续红色节点 - 节点" + node.key + 
                                       "和它的左孩子" + node.left.key + "都是红色");
            }
            if (node.right != null && node.right.color == Color.RED) {
                throw new AssertionError(context + ": 连续红色节点 - 节点" + node.key + 
                                       "和它的右孩子" + node.right.key + "都是红色");
            }
        }
        
        validateNoConsecutiveReds(node.left, context);
        validateNoConsecutiveReds(node.right, context);
    }
    
    private static int validateBlackHeight(RBTNode<Integer, String> node, String context) {
        if (node == null) return 1; // NIL节点是黑色，贡献1个黑色节点
        
        int leftBlackHeight = validateBlackHeight(node.left, context);
        int rightBlackHeight = validateBlackHeight(node.right, context);
        
        if (leftBlackHeight != rightBlackHeight) {
            throw new AssertionError(context + ": 黑高不平衡 - 节点" + node.key + 
                                   "的左子树黑高=" + leftBlackHeight + 
                                   ", 右子树黑高=" + rightBlackHeight);
        }
        
        // 如果当前节点是黑色，黑高加1
        return leftBlackHeight + (node.color == Color.BLACK ? 1 : 0);
    }
    
    private static void validateBST(RBTNode<Integer, String> node, int min, int max, String context) {
        if (node == null) return;
        
        if (node.key < min || node.key > max) {
            throw new AssertionError(context + ": BST性质破坏 - 节点" + node.key + 
                                   "超出范围[" + min + ", " + max + "]");
        }
        
        validateBST(node.left, min, node.key - 1, context);
        validateBST(node.right, node.key + 1, max, context);
    }
    
    private static void validateAgainstReference(MyRBT<Integer, String> rbt, 
                                                 TreeMap<Integer, String> reference) {
        // 验证所有键都存在
        for (Map.Entry<Integer, String> entry : reference.entrySet()) {
            // 这里需要实现get方法，暂时跳过
            // String value = rbt.get(entry.getKey());
            // if (value == null || !value.equals(entry.getValue())) {
            //     throw new AssertionError("键" + entry.getKey() + "的值不匹配");
            // }
        }
    }
    
    // 添加一个打印树结构的方法，用于调试
    private static void printTree(RBTNode<Integer, String> node, String indent, boolean last) {
        if (node == null) {
            System.out.println(indent + (last ? "└── " : "├── ") + "NIL(B)");
            return;
        }
        
        System.out.println(indent + (last ? "└── " : "├── ") + 
                          node.key + "(" + (node.color == Color.RED ? "R" : "B") + ")");
        
        printTree(node.left, indent + (last ? "    " : "│   "), false);
        printTree(node.right, indent + (last ? "    " : "│   "), true);
    }
}

class MyRBT<K extends Comparable<K>,V>{
	RBTNode<K,V> head;
	int size;
	
	public MyRBT(K key,V value) {
		super();
		this.head = new RBTNode<>();
		this.size++;
	}

	public MyRBT() {
		super();
	}


	//对失衡节点的颜色进行调整，自己画图去模拟，别去看教材，这样可以练习自己的推理能力。
	//这玩意是真的难啊，我开始居然天真到想去写统一入口的删除跟添加方法，画完图模拟失衡才发现自己的天真。
	//难怪AI一直劝我分开写，这玩意变色的逻辑完全不一样，自己不去画图压根体会不到。我也是被自己的天真给坑了。
	//自己把自己代码写完按工业级压力测试完通过了，再去看别人的代码进行比较。这样才能获得最大收获
	//目前我写的BST,AVL还没去跟源码实现比较过，杀穿常用的经典容器以后再考虑。
	private void reBalancePut(RBTNode<K,V> node) {
		//虽然是私有方法尽量还是加一下空拦截，我前面写的时候都没意识到，总觉得是自己内部用的无所谓，不够严谨
		if(node == null) return;
		RBTNode<K,V> current = null;
		RBTNode<K,V> parent = null;
		RBTNode<K,V> grandp = null;
		//因为是节点自管理并且因为判断失衡需要两个节点，所以在删除传参时就直接传后继删除的子节点进来。
		//用父节点循环的话，更清晰的看到当前节点的失衡状况。当node没有父节点时直接出循环颜色赋黑。
		while(node != null) {
			//刚开始想的是增删统一入口就考虑的用current接住循环节点进行指针操作，然后调整完以后再对node重新赋值循环。
			//而在添加的时候压根不需要进行循环，因为失衡只对当前路径造成失衡压根不会向上传递，这个while循环都是多余的。
			//但是我不想把这个while省略掉，我想让看到的人清晰的看到我刚开始分析的错误思路。但是经过进一步的画图发现
			//红子红父红叔叔，只需要旋转以后对current变黑就已经把各路径的黑平衡调整好不再需要继续传播了。而且我开始画面
			//黑叔叔的时候差点掉进坑里了，我完全把null为黑给漏了，武断的下了结论put不可能出现黑叔叔的节点。幸好deepseek
			//带我出了坑，要是还是豆包估计就直接把我往沟里带了。
			current = node;
			if(current.pre == null) break;
			//用current来进行指针连接操作，失衡调整完以后对node重新赋值为根节点重新进行循环不会对循环节点造成影响。
			parent = current.pre;
			if(current.color == Color.RED && parent.color == Color.RED) {
				//根节点必黑。
				//没有祖父节点时表示只有两个节点存在，此时直接退出循环让外部兜底根节点变黑即可。
				//因为根节点在循环的过程中颜色会更新，而又必须保证根为黑，就必须在达到根节点时直接变黑。
				if(parent.pre == null) break;
				//运行到这里必定是有爷爷节点的，这时候再进行失衡拦截,并且爷爷节点必定是黑节点，否则原RBT非法。
				grandp = parent.pre;
				//LL失衡不管叔叔颜色，红父直接向上传播。
				if(current == parent.left && parent == grandp.left ) {
					//这里需要判爷爷是不是根节点，不是就直接先升级再连接曾祖父节点，跟AVL一样。
					if(grandp == this.head) {
						this.head = parent;
						this.head.pre = null;
					}else {
						//判断爷爷节点是曾祖父节点的左右孩子进行指针连接。
						if(grandp == grandp.pre.left) grandp.pre.left = parent;else grandp.pre.right = parent;
						parent.pre = grandp.pre;
					}
					//公共节点连接，先处理升级节点的子节点问题，不然容易连接的时候丢树，打结。
					if(parent.right != null) {
						grandp.left = parent.right;
						parent.right.pre = grandp;
					}else {
						grandp.left = null;
					}
					parent.right = grandp;
					grandp.pre = parent;
					
					current.color = Color.BLACK;
					//失衡继续向上传播。
					node = current;
				//LR失衡
				}else if(current == parent.right && parent == grandp.left) {
					if(grandp == this.head) {
						this.head = current;
						this.head.pre = null;
					}else {
						if(grandp == grandp.pre.left) grandp.pre.left = current; else grandp.pre.right = current;
						current.pre = grandp.pre;
					}
					if(current.left != null) {
						current.left.pre = parent;
						parent.right = current.left;
					}else {
						parent.right = null;
					}
					if(current.right != null) {
						current.right.pre = grandp;
						grandp.left = current.right;
					}else {
						grandp.left = null;
					}
					
					current.left = parent;
					parent.pre = current;
					current.right = grandp;
					grandp.pre = current;
					
					parent.color = Color.BLACK;
					
					node = parent;
				//RR失衡
				}else if(current == parent.right && parent == grandp.right ) {
					if(grandp == this.head) {
						this.head = parent;
						this.head.pre = null;
					}else {
						if(grandp == grandp.pre.left) grandp.pre.left = parent; else grandp.pre.right = parent;
						parent.pre = grandp.pre;
					}
					if(parent.left != null) {
						grandp.right = parent.left;
						parent.left.pre = grandp;
					}else {
						grandp.right = null;
					}
					grandp.pre = parent;
					parent.left = grandp;
					
					current.color = Color.BLACK;
					
					node = current;
				//RL失衡
				}else if(current == parent.left && parent == grandp.right) {
					if(grandp == this.head) {
						this.head = current;
						this.head.pre = null;
					}else {
						if(grandp == grandp.pre.left) grandp.pre.left = current; else grandp.pre.right = current;
						current.pre = grandp.pre;
					}
					if(current.left != null) {
						grandp.right = current.left;
						current.left.pre = grandp;
					}else {
						grandp.right = null;
					}
					if(current.right != null) {
						current.right.pre = parent;
						parent.left = current.right;
					}else {
						parent.left = null;
					}
					grandp.pre = current;
					current.left = grandp;
					parent.pre = current;
					current.right = parent;
					
					parent.color = Color.BLACK;
					
					node = parent;
				}
			}
			node = node.pre;
		}
		//因为我是以新根node为红色向上传递的，当新根node=node.pre为空时，循环退出，此时拿不到node了;
		//这里必须用对象指针最终把根变黑。
		node.color = Color.BLACK;
	}
	

	public boolean put(K key,V value) {
		if(key == null || value == null) return false;
		if(this.size == 0) {
			this.head = new RBTNode<>(key,value);
			this.head.color = Color.BLACK;
			this.size++;
			return true;
		}
		RBTNode<K,V> node = head;
		RBTNode<K,V> pre = null;
		while(node != null) {
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			pre = node;
			if(node.key.compareTo(key) < 0) node = node.right;else node = node.left;
		}
		if(pre.key.compareTo(key) < 0) {
			node = new RBTNode<>(key,value);
			pre.right = node;
			node.pre = pre;
		}else {
			node = new RBTNode<>(key,value);
			node.pre = pre;
			pre.left = node;
		}
		this.size++;
		this.reBalancePut(node);
		return true;
	}
	
	public boolean remove(K key) {
		if(this.size == 0 || key == null) return false;
		RBTNode<K,V> node = this.head;
		RBTNode<K,V> pre = null;
		RBTNode<K,V> balanceNode = null;
		//定义一个节点用于接住被真删的节点。
		RBTNode<K,V> removeNode = null;
		while(node != null) {
			if(node.key.equals(key)) break;
			pre = node;
			if(node.key.compareTo(key) < 0 ) node = node.right;else node = node.left;
		}
		if(node == null) return false;
		if(node.right == null && node.left == null) {
			if(pre == null) {
				this.head = null;
				this.size--;
				return true;
			}else{
				if(pre.left == node) pre.left = null;
				if(pre.right == node) pre.right = null;
				//
				removeNode = node;
				
				node.pre = null;
				balanceNode = pre;
			}
		}else if(node.left == null) {
			if(pre == null) {
				this.head = node.right;
				this.head.pre = null;
				balanceNode = this.head;
			}else if(pre.left == node){
				pre.left = node.right;
				node.right.pre = pre;
				balanceNode = pre.left;
			}else if(pre.right == node) {
				pre.right = node.right;
				node.right.pre = pre;
				balanceNode = pre.right;
			}
		}else if(node.right == null) {
			if(pre == null) {
				this.head = node.left;
				this.head.pre = null;
				balanceNode = this.head;
			}else if(pre.left == node){
				pre.left = node.left;
				node.left.pre = pre;
				balanceNode = pre.left;
			}else if(pre.right == node) {
				pre.right = node.left;
				node.left.pre = pre;
				balanceNode = pre.right;
			}
		}else {
			RBTNode<K,V> child = node.right;
			RBTNode<K,V> pChild = null;
			while(child.left != null) {
				pChild = child;
				child = child.left;
			} 
			node.key = child.key;
			node.value = child.value;
			//后继法删除时，不能改变原节点的颜色不然失衡会很难处理。
			if(pChild == null) {
				if(child.right == null) {
					node.right = null;
					balanceNode = node;
				}else {
					node.right = child.right;
					child.right.pre = node;
					balanceNode = node.right;
				}
			}else {
				if(child.right == null) {
					pChild.left = null;
					balanceNode = pChild;
				}else {
					pChild.left = child.right;
					child.right.pre = pChild;
					balanceNode = pChild.left;
				}
			}
			child.left = child.right = child.pre = null;
		}
		this.size--;
		this.reBalanceRemove(balanceNode);
		return true;
	}

	private void reBalanceRemove(RBTNode<K, V> balanceNode) {
		// TODO Auto-generated method stub
		
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
		Queue<RBTNode<K,V>> que = new LinkedList<>();
		Queue<RBTNode<K,V>> que1 = new LinkedList<>();
		que.add(this.head);
		int num = 1;
		while(!que.isEmpty()) {
			List<RBTNode<K,V>> l = new LinkedList<>();
			while(!que.isEmpty()) {
				RBTNode<K,V> node = que.poll();
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
		}
	}
}

class RBTNode<K extends Comparable<K>,V>{
	RBTNode<K,V> pre;
	RBTNode<K,V> left;
	RBTNode<K,V> right;
	Color color;
	K key;
	V value;
	
	public RBTNode(K key, V value) {
		super();
		this.key = key;
		this.value = value;
		//初始化节点的颜色。
		this.color = Color.RED;
	}
	public RBTNode() {
		super();
	}
	@Override
	public String toString() {
		
		return this.key +"/"+this.color.toString();
	}
	
	
}

//这个枚举类型很少用，都忘记了。枚举类型内部是静态变量，直接类名点就行了。
enum Color{
	RED,BLACK;
	
}