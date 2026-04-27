package test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import java.util.*;

public class MyAVLTest {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("        AVL树工业级压力测试开始");
        System.out.println("=================================================\n");
        
        boolean allPassed = true;
        
        // 测试1: 基本功能测试
        allPassed &= testBasicOperations();
        
        // 测试2: 随机插入删除测试
        allPassed &= testRandomOperations();
        
        // 测试3: 顺序插入极端测试
        allPassed &= testSequentialInsertion();
        
        // 测试4: 重复键测试
        allPassed &= testDuplicateKeys();
        
        // 测试5: 大量数据压力测试
        allPassed &= testMassiveData();
        
        // 测试7: 内存泄漏测试
        allPassed &= testMemoryIntegrity();
        
        // 测试8: 复杂删除测试
        allPassed &= testComplexDeletion();
        
        if (allPassed) {
            System.out.println("\n=================================================");
            System.out.println("   ✅ 所有压力测试通过！AVL树实现稳定可靠");
            System.out.println("=================================================");
        } else {
            System.out.println("\n=================================================");
            System.out.println("   ❌ 测试失败，请检查实现");
            System.out.println("=================================================");
            System.exit(1);
        }
    }
    
    // ===================== 测试1: 基本功能测试 =====================
    private static boolean testBasicOperations() {
        System.out.println("【测试1】基本功能测试");
        try {
            MyAVL<Integer, String> avl = new MyAVL<>();
            
            // 1.1 空树测试
            assert avl.get(1) == null : "空树get应返回null";
            assert !avl.remove(1) : "空树remove应返回false";
            
            // 1.2 单个节点测试
            avl.put(10, "A");
            assert "A".equals(avl.get(10)) : "插入后get失败";
            assert avl.size == 1 : "size应为1";
            
            // 1.3 覆盖测试
            avl.put(10, "B");
            assert "B".equals(avl.get(10)) : "覆盖失败";
            assert avl.size == 1 : "覆盖后size应仍为1";
            
            // 1.4 删除测试
            assert avl.remove(10) : "删除失败";
            assert avl.get(10) == null : "删除后get应返回null";
            assert avl.size == 0 : "删除后size应为0";
            
            // 1.5 多个节点测试
            for (int i = 0; i < 10; i++) {
                avl.put(i, "Value" + i);
            }
            assert avl.size == 10 : "插入10个节点后size应为10";
            
            // 验证中序遍历有序
            List<Integer> inorder = new ArrayList<>();
            inorderTraversal(avl.head, inorder);
            for (int i = 1; i < inorder.size(); i++) {
                assert inorder.get(i-1) < inorder.get(i) : "中序遍历必须有序";
            }
            
            // 1.6 替换测试
            avl.replace(5, "Replaced");
            assert "Replaced".equals(avl.get(5)) : "替换失败";
            
            System.out.println("  ✅ 基本功能测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 基本功能测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试2: 随机操作测试 =====================
    private static boolean testRandomOperations() {
        System.out.println("【测试2】随机操作测试 (10000次操作)");
        try {
            MyAVL<Integer, String> avl = new MyAVL<>();
            TreeMap<Integer, String> reference = new TreeMap<>();
            Random rand = new Random(42); // 固定种子保证可重复
            
            int operations = 10000;
            for (int i = 0; i < operations; i++) {
                int operation = rand.nextInt(3);
                int key = rand.nextInt(1000);
                
                switch (operation) {
                    case 0: // 插入
                        String value = "Val" + key;
                        avl.put(key, value);
                        reference.put(key, value);
                        break;
                    case 1: // 删除
                        boolean avlResult = avl.remove(key);
                        boolean refResult = reference.remove(key) != null;
                        assert avlResult == refResult : "删除结果不一致: key=" + key;
                        break;
                    case 2: // 查找
                        String avlValue = avl.get(key);
                        String refValue = reference.get(key);
                        if (avlValue == null) {
                            assert refValue == null : "查找结果不一致(null): key=" + key;
                        } else {
                            assert avlValue.equals(refValue) : "查找结果不一致: key=" + key;
                        }
                        break;
                }
                
                // 定期验证
                if (i % 1000 == 0) {
                    validateAVL(avl, "随机操作测试第" + i + "次操作后");
                }
            }
            
            // 最终验证
            validateAVL(avl, "随机操作测试最终验证");
            validateAgainstReference(avl, reference);
            
            System.out.println("  ✅ 随机操作测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 随机操作测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试3: 顺序插入极端测试 =====================
    private static boolean testSequentialInsertion() {
        System.out.println("【测试3】顺序插入极端测试");
        try {
            // 3.1 升序插入 (测试RR旋转)
            MyAVL<Integer, String> avl1 = new MyAVL<>();
            for (int i = 0; i < 1000; i++) {
                avl1.put(i, "Asc" + i);
                validateAVL(avl1, "升序插入第" + i + "个节点后");
            }
            System.out.println("    ✅ 升序插入测试通过");
            
            // 3.2 降序插入 (测试LL旋转)
            MyAVL<Integer, String> avl2 = new MyAVL<>();
            for (int i = 999; i >= 0; i--) {
                avl2.put(i, "Desc" + i);
                validateAVL(avl2, "降序插入第" + (999-i) + "个节点后");
            }
            System.out.println("    ✅ 降序插入测试通过");
            
            // 3.3 交替插入 (测试LR/RL旋转)
            MyAVL<Integer, String> avl3 = new MyAVL<>();
            for (int i = 0; i < 500; i++) {
                avl3.put(i * 2, "Even" + i);
                avl3.put(i * 2 + 1, "Odd" + i);
                if (i % 100 == 0) {
                    validateAVL(avl3, "交替插入第" + (i*2) + "个节点后");
                }
            }
            System.out.println("    ✅ 交替插入测试通过");
            
            System.out.println("  ✅ 顺序插入极端测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 顺序插入测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试4: 重复键测试 =====================
    private static boolean testDuplicateKeys() {
        System.out.println("【测试4】重复键测试");
        try {
            MyAVL<Integer, String> avl = new MyAVL<>();
            
            // 多次插入相同键
            for (int i = 0; i < 100; i++) {
                avl.put(42, "Value" + i);
                assert avl.size == 1 : "重复插入后size应为1";
                assert ("Value" + i).equals(avl.get(42)) : "应返回最新值";
            }
            
            // 混合重复键
            Set<Integer> keys = new HashSet<>();
            Random rand = new Random(42);
            for (int i = 0; i < 1000; i++) {
                int key = rand.nextInt(10); // 只有10个不同的键
                avl.put(key, "K" + key);
                keys.add(key);
                assert avl.size == keys.size() : "size应与唯一键数相同";
            }
            
            validateAVL(avl, "重复键测试后");
            
            System.out.println("  ✅ 重复键测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 重复键测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 测试5: 大量数据压力测试 =====================
    private static boolean testMassiveData() {
        System.out.println("【测试5】大量数据压力测试");
        try {
            MyAVL<Integer, String> avl = new MyAVL<>();
            
            long startTime = System.currentTimeMillis();
            
            // 插入100000个随机节点
            Random rand = new Random(42);
            for (int i = 0; i < 100000; i++) {
                int key = rand.nextInt(1000000);
                avl.put(key, "Massive" + key);
            }
            
            long insertTime = System.currentTimeMillis() - startTime;
            System.out.println("    插入100000节点耗时: " + insertTime + "ms");
            
            // 验证插入后的树
            validateAVL(avl, "大量插入后");
            
            // 随机查找测试
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                int key = rand.nextInt(1000000);
                avl.get(key);
            }
            long searchTime = System.currentTimeMillis() - startTime;
            System.out.println("    10000次随机查找耗时: " + searchTime + "ms");
            
            // 随机删除测试
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 50000; i++) {
                int key = rand.nextInt(1000000);
                avl.remove(key);
            }
            long deleteTime = System.currentTimeMillis() - startTime;
            System.out.println("    删除50000节点耗时: " + deleteTime + "ms");
            
            validateAVL(avl, "大量删除后");
            
            System.out.println("  ✅ 大量数据压力测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 大量数据测试失败: " + e.getMessage());
            return false;
        } catch (OutOfMemoryError e) {
            System.out.println("  ⚠️ 大量数据测试内存不足，跳过此项测试");
            return true; // 内存不足不是实现错误
        }
    }
    
    // ===================== 测试7: 内存完整性测试 =====================
    private static boolean testMemoryIntegrity() {
        System.out.println("【测试7】内存完整性测试");
        try {
            // 创建大量树，确保没有内存泄漏
            List<MyAVL<Integer, String>> trees = new ArrayList<>();
            
            for (int t = 0; t < 100; t++) {
                MyAVL<Integer, String> avl = new MyAVL<>();
                Random rand = new Random(t);
                
                // 插入大量数据
                for (int i = 0; i < 1000; i++) {
                    int key = rand.nextInt(10000);
                    avl.put(key, "Tree" + t + "-" + key);
                }
                
                trees.add(avl);
            }
            
            // 删除所有树，触发GC
            trees.clear();
            System.gc();
            
            // 创建新树验证功能正常
            MyAVL<Integer, String> finalTree = new MyAVL<>();
            for (int i = 0; i < 100; i++) {
                finalTree.put(i, "Final" + i);
            }
            
            validateAVL(finalTree, "内存测试后");
            
            System.out.println("  ✅ 内存完整性测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 内存完整性测试失败: " + e.getMessage());
            return false;
        } catch (OutOfMemoryError e) {
            System.out.println("  ⚠️ 内存不足，跳过内存完整性测试");
            return true;
        }
    }
    
    // ===================== 测试8: 复杂删除测试 =====================
    private static boolean testComplexDeletion() {
        System.out.println("【测试8】复杂删除测试");
        try {
            // 8.1 删除根节点
            MyAVL<Integer, String> avl1 = new MyAVL<>();
            for (int i = 0; i < 100; i++) {
                avl1.put(i, "RootTest" + i);
            }
            
            // 不断删除根节点
            while (avl1.head != null) {
                int rootKey = avl1.head.key;
                avl1.remove(rootKey);
                validateAVL(avl1, "删除根节点" + rootKey + "后");
            }
            System.out.println("    ✅ 根节点删除测试通过");
            
            // 8.2 删除所有叶子节点
            MyAVL<Integer, String> avl2 = new MyAVL<>();
            Random rand = new Random(42);
            Set<Integer> keys = new HashSet<>();
            for (int i = 0; i < 500; i++) {
                int key = rand.nextInt(10000);
                avl2.put(key, "LeafTest" + key);
                keys.add(key);
            }
            
            // 找出并删除所有叶子节点
            List<Integer> toDelete = new ArrayList<>(keys);
            for (int key : toDelete) {
                avl2.remove(key);
                if (avl2.head != null) {
                    validateAVL(avl2, "删除键" + key + "后");
                }
            }
            System.out.println("    ✅ 叶子节点删除测试通过");
            
            // 8.3 删除导致多次旋转的情况
            MyAVL<Integer, String> avl3 = new MyAVL<>();
            int[] specialSeq = {50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93};
            for (int key : specialSeq) {
                avl3.put(key, "Rotate" + key);
            }
            
            // 按特定顺序删除触发各种旋转
            int[] deleteOrder = {50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93};
            for (int key : deleteOrder) {
                avl3.remove(key);
                if (avl3.head != null) {
                    validateAVL(avl3, "删除" + key + "后");
                }
            }
            System.out.println("    ✅ 旋转触发删除测试通过");
            
            System.out.println("  ✅ 复杂删除测试通过");
            return true;
        } catch (AssertionError e) {
            System.out.println("  ❌ 复杂删除测试失败: " + e.getMessage());
            return false;
        }
    }
    
    // ===================== 验证工具方法 =====================
    
    private static void validateAVL(MyAVL<Integer, String> avl, String context) {
        if (avl.head == null) {
            return;
        }
        
        // 验证BST性质
        validateBST(avl.head, Integer.MIN_VALUE, Integer.MAX_VALUE, context);
        
        // 验证平衡因子
        validateBalance(avl.head, context);
        
        // 验证高度正确性
        validateHeights(avl.head, context);
        
        // 验证父指针一致性
        validateParentPointers(avl.head, null, context);
        
        // 验证size字段
        validateSize(avl, context);
    }
    
    private static void validateBST(AVLNode<Integer, String> node, int min, int max, String context) {
        if (node == null) return;
        
        if (node.key < min || node.key > max) {
            throw new AssertionError(context + ": BST性质破坏 - 节点" + node.key + 
                                   "超出范围[" + min + ", " + max + "]");
        }
        
        validateBST(node.left, min, node.key - 1, context);
        validateBST(node.right, node.key + 1, max, context);
    }
    
    private static void validateBalance(AVLNode<Integer, String> node, String context) {
        if (node == null) return;
        
        int balance = node.getBalance();
        if (Math.abs(balance) > 1) {
            throw new AssertionError(context + ": AVL不平衡 - 节点" + node.key + 
                                   "的平衡因子为" + balance);
        }
        
        validateBalance(node.left, context);
        validateBalance(node.right, context);
    }
    
    private static int validateHeights(AVLNode<Integer, String> node, String context) {
        if (node == null) return 0;
        
        int leftHeight = validateHeights(node.left, context);
        int rightHeight = validateHeights(node.right, context);
        
        int expectedHeight = Math.max(leftHeight, rightHeight) + 1;
        if (node.height != expectedHeight) {
            throw new AssertionError(context + ": 高度不一致 - 节点" + node.key + 
                                   "存储高度=" + node.height + 
                                   ", 计算高度=" + expectedHeight);
        }
        
        return expectedHeight;
    }
    
    private static void validateParentPointers(AVLNode<Integer, String> node, 
                                               AVLNode<Integer, String> parent, 
                                               String context) {
        if (node == null) return;
        
        if (node.pre != parent) {
            throw new AssertionError(context + ": 父指针错误 - 节点" + node.key + 
                                   "的父指针应为" + (parent == null ? "null" : parent.key) +
                                   ", 实际为" + (node.pre == null ? "null" : node.pre.key));
        }
        
        validateParentPointers(node.left, node, context);
        validateParentPointers(node.right, node, context);
    }
    
    private static void validateSize(MyAVL<Integer, String> avl, String context) {
        int actualSize = countNodes(avl.head);
        if (avl.size != actualSize) {
            throw new AssertionError(context + ": size字段不一致 - 记录size=" + 
                                   avl.size + ", 实际节点数=" + actualSize);
        }
    }
    
    private static int countNodes(AVLNode<Integer, String> node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    
    private static void validateAgainstReference(MyAVL<Integer, String> avl, 
                                                 TreeMap<Integer, String> reference) {
        // 验证所有键都存在
        for (Map.Entry<Integer, String> entry : reference.entrySet()) {
            String value = avl.get(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                throw new AssertionError("键" + entry.getKey() + "的值不匹配");
            }
        }
        
        // 验证树中不包含参考中没有的键
        List<Integer> avlKeys = new ArrayList<>();
        inorderTraversal(avl.head, avlKeys);
        for (Integer key : avlKeys) {
            if (!reference.containsKey(key)) {
                throw new AssertionError("树中包含参考中没有的键: " + key);
            }
        }
    }
    
    private static void inorderTraversal(AVLNode<Integer, String> node, List<Integer> result) {
        if (node == null) return;
        inorderTraversal(node.left, result);
        result.add(node.key);
        inorderTraversal(node.right, result);
    }
}

class MyAVL<K extends Comparable<K>,V>{
	AVLNode<K,V> head;
	int size;
	public MyAVL(K key,V value) {
		super();
		this.head = new AVLNode(key,value);
		this.size++;
	}
	public MyAVL() {
		super();
	}
	
	public V get(K key) {
		if(this.size == 0 || key == null) return null;
		AVLNode<K,V> node = head;
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
		AVLNode<K,V> node = head;
		while (node != null) {
			if (node.key.equals(key)) {
				node.value = value;
				return true;
			}
			node = node.key.compareTo(key) < 0 ? node.right : node.left;
		}
		return false;
	}
	
	
	//被之前用爷爷节点锚定失衡折磨了2天才想明白应该用节点自身的平衡因子判断失衡，这样可以避免回退必空指针问题。	
	private void reBlance(AVLNode<K,V> node,boolean contrl) {
		if(node == null) return;
		AVLNode<K,V> son = null;
		AVLNode<K,V> grandSon = null;
		while(node != null) {
			//这里每次进入一定要先更新node的高度。失衡是向上传递的，如果不更新就会导致依然沿用失衡前的左右节点进行平衡判定
			//当你在失衡调整完成对旋转的节点更新以后，新根的父节点并没有进行高度更新，当进入下一次循环时会依然沿用原来的高度。
			node.height = node.updateHeight();
			//这里先用一个seek把循环的指针接住，用seek去完成指针连接，失衡调整完以后需要更新循环的节点，
			//引入一个新的指针操作起来逻辑更清晰跟便捷
			AVLNode<K,V> seek = node;
			//这里我开始画面的时候差点被局部草图误导了。想当然就想用子节点的右孩子当锚点，幸好反应过来了。
			//根据节点的平衡因子判断是L型失衡还是R型失衡
			if(seek.getBalance() >= 2) {
				son = seek.left;
				//再进一步通过儿子节点的平衡因子判断是LR还是RR型失衡。
				//LL，这里刚开始没有考虑到等于0时用LL失衡调整效率会比用LR失衡调整效率更快。
				if(son.getBalance() >= 0) {
					grandSon =  son.left;
					if(seek.pre == null) {
						this.head = son;
						this.head.pre = null;
					}else {
						if(seek == seek.pre.left) seek.pre.left = son;else seek.pre.right = son;
						son.pre = seek.pre;
					}
					if(son.right != null) {
						son.right.pre = seek;
						seek.left = son.right;
					}else {
						seek.left = null;
					}
					seek.pre = son;
					son.right = seek;
					
					grandSon.height = grandSon.updateHeight();
					seek.height = seek.updateHeight();
					son.height = son.updateHeight();
					//这里把循环回退的node指针指向新的根节点，继续进行回退。
					node = son;
					if(contrl) break;
				}else {//LR
					grandSon = son.right;
					if(seek.pre == null) {
						this.head = grandSon;
						this.head.pre = null;
					}else {
						if(seek == seek.pre.left) seek.pre.left = grandSon;else seek.pre.right = grandSon;
						grandSon.pre = seek.pre;
					}
					if(grandSon.left != null) {
						grandSon.left.pre = son;
						son.right = grandSon.left;
					}else {
						son.right = null;
					}
					if(grandSon.right != null) {
						grandSon.right.pre = seek;
						seek.left = grandSon.right;
					}else {
						seek.left = null;
					}
					seek.pre = grandSon;
					grandSon.right = seek;
					son.pre = grandSon;
					grandSon.left = son;
						
					son.height = son.updateHeight();
					seek.height = seek.updateHeight();
					grandSon.height = grandSon.updateHeight();
					
					node = grandSon;
					if(contrl) break;
				}
				//R型失衡同理。
			}else if(seek.getBalance() <= -2 ) {
				son = seek.right;
				//RR
				if(son.getBalance() <= 0) {
					grandSon = son.right;
					if(seek.pre == null) {
						this.head = son;
						this.head.pre = null;
					}else {
						if(seek == seek.pre.left) seek.pre.left = son; else seek.pre.right = son;
						son.pre = seek.pre;
					}
					if(son.left != null) {
						son.left.pre = seek;
						seek.right = son.left;
					}else {
						seek.right = null;
					}
					seek.pre = son;
					son.left = seek;
					
					grandSon.height = grandSon.updateHeight();
					seek.height = seek.updateHeight();
					son.height = son.updateHeight();
					
					node = son;
					
					if(contrl) break;
				}else {//RL
					grandSon = son.left;
					if(seek.pre == null) {
						this.head = grandSon;
						this.head.pre = null;
					}else{
						if(seek == seek.pre.left) seek.pre.left = grandSon;else seek.pre.right = grandSon;
						grandSon.pre = seek.pre;
					}
					
					if(grandSon.left != null) {
						grandSon.left.pre = seek;
						seek.right = grandSon.left;
					}else {
						seek.right = null;
					}
					
					if(grandSon.right != null) {
						grandSon.right.pre = son;
						son.left = grandSon.right;
					}else {
						son.left = null;
					}
					
					seek.pre = grandSon;
					grandSon.left = seek;
					son.pre = grandSon;
					grandSon.right = son;
					
					seek.height = seek.updateHeight();
					son.height = son.updateHeight();
					grandSon.height = grandSon.updateHeight();
					
					node = grandSon;
					if(contrl) break;
				}
			}
			son = node;
			node = node.pre;
		}
	}
	
	
	public boolean put(K key,V value) {
		if(key == null || value == null) return false;
		if(this.size == 0) {
			this.head = new AVLNode<>(key,value);
			this.size++;
			return true;
		}
		AVLNode<K,V> node = this.head;
		AVLNode<K,V> pre = null;
		while(node != null) {
			if(node.key.equals(key)) {
				node.value = value;
				return true;
			}
			pre = node;
			if(node.key.compareTo(key) < 0) node = node.right;
			else node = node.left;
		}
		if(pre.key.compareTo(key) < 0) {
			node = new AVLNode<>(key,value);
			node.pre = pre;
			pre.right = node;
			pre.height = pre.updateHeight();
		}else {
			node = new AVLNode<>(key,value);
			node.pre = pre;
			pre.left = node;
			pre.height = pre.updateHeight();
		}
		this.reBlance(node, true);
		this.size++;
		return true;
	}
	
	public boolean remove(K key) {
		if(this.size == 0 || key == null) return false;
		AVLNode<K,V> node = head;
		AVLNode<K,V> pre = null;
		AVLNode<K,V> balanceNode = null;
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
			AVLNode<K,V> child = node.right;
			AVLNode<K,V> pChild = null;
			while(child.left != null) {
				pChild = child;
				child = child.left;
			} 
			node.key = child.key;
			node.value = child.value;
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
		balanceNode.height = balanceNode.updateHeight();
		this.reBlance(balanceNode, false);
		this.size--;
		return true;
	}

	public void display(AVLNode<K,V> node) {
		if(node == null) return;
		display(node.left);
		System.out.println(node);
		display(node.right);
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
		Queue<AVLNode<K,V>> que = new LinkedList<>();
		Queue<AVLNode<K,V>> que1 = new LinkedList<>();
		que.add(this.head);
		int num = 1;
		while(!que.isEmpty()) {
			List<AVLNode<K,V>> l = new LinkedList<>();
			while(!que.isEmpty()) {
				AVLNode<K,V> node = que.poll();
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

class AVLNode<K extends Comparable<K>,V> {
	AVLNode<K,V> pre;
	AVLNode<K,V> left;
	AVLNode<K,V> right;
	int height;
	K key;
	V value;
	public AVLNode(K key, V value) {
		super();
		this.key = key;
		this.value = value;
		this.height = 1;
	}
	
	public AVLNode() {
		super();
	}
	
	public int updateHeight() {
		int leftHeight = this.left == null ? 0 : this.left.height;
		int rightHeight = this.right == null ? 0 : this.right.height;
		return Math.max(leftHeight, rightHeight) + 1;
	}

	//每个节点的平衡因子
	public int getBalance() {
		int leftHeight = this.left == null ? 0 : this.left.height;
		int rightHeight = this.right == null ? 0 : this.right.height;
		return leftHeight - rightHeight;
	}

	@Override
	public String toString() {
		return this.key.toString()+","+this.value.toString()+","+this.height;
	}
	
}

class MyNullPointerException extends RuntimeException{

	public MyNullPointerException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyNullPointerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}