package test;

import java.util.concurrent.*;
import java.util.*;

public class MyConcurrentSkipListMapTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== MyConcurrentSkipListMap 测试 ===");
        System.out.println("\n1. 基本功能测试:");
        basicTest();
        
        System.out.println("\n2. 并发插入测试:");
        concurrentPutTest();
        
        System.out.println("\n3. 并发混合操作测试:");
        concurrentMixedTest();
        
        System.out.println("\n4. 性能测试:");
        performanceTest();
    }
    
    static void basicTest() {
        MyConcurrentSkipListMap<Integer, String> map = new MyConcurrentSkipListMap<>();
        
        // 测试插入
        System.out.println("插入 5: " + map.put(5, "Five"));
        System.out.println("插入 3: " + map.put(3, "Three"));
        System.out.println("插入 7: " + map.put(7, "Seven"));
        System.out.println("插入 1: " + map.put(1, "One"));
        System.out.println("插入 9: " + map.put(9, "Nine"));
        System.out.println("插入 3(重复): " + map.put(3, "THREE-NEW")); // 应该返回true（覆盖）
        
        // 测试获取
        System.out.println("\n查询测试:");
        System.out.println("get(3): " + map.get(3));
        System.out.println("get(5): " + map.get(5));
        System.out.println("get(7): " + map.get(7));
        System.out.println("get(1): " + map.get(1));
        System.out.println("get(9): " + map.get(9));
        System.out.println("get(2): " + map.get(2));  // 不存在的key
        
        // 测试size
        System.out.println("\n当前size: " + map.size());
        
        // 测试包含
        System.out.println("包含key 3: " + map.containsKey(3));
        System.out.println("包含key 5: " + map.containsKey(5));
        System.out.println("包含key 2: " + map.containsKey(2));
        
        // 测试删除
        System.out.println("\n删除测试:");
        System.out.println("删除 5: " + map.remove(5));
        System.out.println("删除 5(再次): " + map.remove(5));  // 再次删除
        System.out.println("删除 100(不存在): " + map.remove(100));
        System.out.println("get(5)删除后: " + map.get(5));
        
        // 测试遍历有序性
        System.out.println("\n遍历测试（验证有序性）:");
        MyConcurrentSkipListMap.Node<Integer, String> current = map.head.next[0];
        while (current != null) {
            System.out.print(current.key + ":" + current.value + " ");
            current = current.next[0];
        }
        
        // 测试清空
        System.out.println("\n\n清空前size: " + map.size());
        map.clear();
        System.out.println("清空后size: " + map.size());
        System.out.println("清空后是否为空: " + map.isEmpty());
    }
    
    static void concurrentPutTest() throws Exception {
        System.out.println("\n=== 并发插入测试（10个线程，每个插入1000个元素）===");
        MyConcurrentSkipListMap<Integer, Integer> map = new MyConcurrentSkipListMap<>();
        int threadCount = 10;
        int perThread = 1000;
        int totalOps = threadCount * perThread;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.execute(() -> {
                for (int j = 0; j < perThread; j++) {
                    int key = threadId * perThread + j;
                    map.put(key, key * 10);
                }
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("并发插入完成");
        System.out.println("期望size: " + totalOps);
        System.out.println("实际size: " + map.size());
        
        // 验证数据
        int correctCount = 0;
        for (int i = 0; i < totalOps; i++) {
            Integer value = map.get(i);
            if (value != null && value == i * 10) {
                correctCount++;
            }
        }
        System.out.println("数据一致性验证: " + correctCount + "/" + totalOps);
        
        // 验证有序性
        int lastKey = Integer.MIN_VALUE;
        MyConcurrentSkipListMap.Node<Integer, Integer> current = map.head.next[0];
        int count = 0;
        boolean sorted = true;
        while (current != null) {
            if (current.key < lastKey) {
                System.out.println("有序性错误: " + lastKey + " < " + current.key);
                sorted = false;
                break;
            }
            lastKey = current.key;
            count++;
            current = current.next[0];
        }
        System.out.println("有序性验证: " + (sorted ? "通过" : "失败"));
        System.out.println("遍历节点数: " + count);
    }
    
    static void concurrentMixedTest() throws Exception {
        System.out.println("\n=== 并发混合操作测试（5个线程，每个操作2000次）===");
        MyConcurrentSkipListMap<Integer, String> map = new MyConcurrentSkipListMap<>();
        int threadCount = 5;
        int operations = 2000;
        
        // 先填充一些数据
        for (int i = 0; i < 1000; i++) {
            map.put(i, "初始值-" + i);
        }
        
        System.out.println("初始size: " + map.size());
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.execute(() -> {
                Random rand = new Random(threadId);
                for (int i = 0; i < operations; i++) {
                    int op = rand.nextInt(100);
                    int key = rand.nextInt(1500);  // 范围比初始大，包含不存在的key
                    
                    if (op < 60) {  // 60%插入/更新
                        map.put(key, "线程" + threadId + "-值" + i);
                    } else if (op < 90) {  // 30%查询
                        map.get(key);
                    } else {  // 10%删除
                        map.remove(key);
                    }
                }
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        System.out.println("混合操作完成");
        System.out.println("操作后size: " + map.size());
        
        // 验证有序性
        int lastKey = Integer.MIN_VALUE;
        MyConcurrentSkipListMap.Node<Integer, String> current = map.head.next[0];
        int count = 0;
        boolean sorted = true;
        while (current != null) {
            if (current.key < lastKey) {
                System.out.println("有序性错误: " + lastKey + " < " + current.key);
                sorted = false;
                break;
            }
            lastKey = current.key;
            count++;
            current = current.next[0];
        }
        System.out.println("有序性验证: " + (sorted ? "通过" : "失败"));
        System.out.println("遍历节点数: " + count);
    }
    
    static void performanceTest() throws Exception {
        System.out.println("\n=== 性能测试（不同线程数下的吞吐量）===");
        int[] threadCounts = {1, 2, 4, 8, 16};
        
        for (int threads : threadCounts) {
            System.out.println("\n使用 " + threads + " 个线程（每个线程执行10000次操作）:");
            
            long myTime = testMySkipListThroughput(threads, 10000);
            System.out.println("MyConcurrentSkipListMap 耗时: " + myTime + "ms");
            System.out.println("吞吐量: " + (threads * 10000 * 1000L / myTime) + " ops/s");
        }
    }
    
    static long testMySkipListThroughput(int threadCount, int opsPerThread) throws Exception {
        MyConcurrentSkipListMap<Integer, String> map = new MyConcurrentSkipListMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.execute(() -> {
                Random rand = new Random(threadId);
                for (int i = 0; i < opsPerThread; i++) {
                    int op = rand.nextInt(100);
                    int key = rand.nextInt(1000);
                    
                    if (op < 70) {  // 70% 插入/更新
                        map.put(key, "value-" + key);
                    } else if (op < 90) {  // 20% 查询
                        map.get(key);
                    } else {  // 10% 删除
                        map.remove(key);
                    }
                }
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        return System.currentTimeMillis() - startTime;
    }
    
    static class MyConcurrentSkipListMap<K,V> {
        //比较器可能会是外部传入的所以不能所有线程共享，如果为null，则用K的自然顺序
        private final Comparator<? super K> comparator;
        //所有线程共享，设置为常量不允许修改
        private final static int MAX_LEVEL = 32;
        private final static Random RANDOM = new Random();
        private final static double PROBABILITY = 0.5;   
        //状态变量，需要所有线程可见
        private volatile int size = 0;
        //跳表当前层高。
        private volatile int currentLevel = 1;
        //头节点创建后引用不变，节点管理中心。
        final Node<K,V> head = new Node<>(null,null,MAX_LEVEL);
            
        public MyConcurrentSkipListMap(Comparator<? super K> comparator) {
            super();
            this.comparator = comparator;
        }
    
        public MyConcurrentSkipListMap() {
            this.comparator = null;
        }
    
        public synchronized int size() {
            return this.size;
        }
        
        public synchronized boolean isEmpty() {
            return this.size == 0;
        }
        
        public synchronized V get(K key) {
            if(key == null) throw new NullPointerException("key不能为空"); 
            if(size == 0) return null;
            Node<K,V> current = head;
            for(int i=currentLevel-1;i>=0;i--) {
                while(current.next[i] != null && this.cpr(current.next[i].key, key, comparator) < 0) {
                    current = current.next[i];
                }
            }
            current = current.next[0];
            if(current != null && this.cpr(current.key, key, comparator) == 0) {
                return current.value;
            }
            return null;
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private int cpr(Object key1,Object key2,Comparator c) {
            return (c != null) ? c.compare(key1, key2) : ((Comparable<Object>)key1).compareTo(key2);
        }
    
        //这个方法会在synchronized方法中调用保证一致性。
        private int randomLevel() {
            //节点层高
            int level = 1;
            while(RANDOM.nextDouble() < PROBABILITY && level < MAX_LEVEL){
                level++;
            }
            return level;
        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public synchronized boolean put(K key,V value) {
            if(key == null || value == null) throw new NullPointerException("key或value不能为空");
            //拿到数据管理中心
            Node<K,V> current = this.head;
            //表示当前节点层高,用来表示节点所在层级，并且在更新节点内部的节点数组时表示节点数组的索引。
            //这句话非常绕，我自己理解都很困难。而且在考虑实现怎么用这个节点层高我也想了非常长时间。
            int newLevel = this.randomLevel();
            //对节点的next[]数组进行初始化，这里选择用MAX_LEVEL而不是用max(this.randomLevel()-1,currentLevel-1)是为了用栈内存的极小开销
            //实现程序运行效率的最优。先在堆中创建一个节点数组对象，而栈内存的会随着栈针的释放而释放，对整个程序的内存开销几乎无影响，不需要通过CPU进行计算，
            //对CPU的缓存更友好。
            Node<K,V> update[] = new Node[MAX_LEVEL];
            //根据节点层高对整个跳表进行的先广度后深度的遍历。外层循环如果用当前节点层高进行遍历，程序会因为数组越界问题崩溃。
            //而用跳表层高进行遍历天然不存在数组越界问题。这里需要自己去模拟程序的运行来理解，我在这里被折磨了10多天，甚至一度
            //玩了一周多的游戏、种田、开荒，也不想去面对我的跳表的实现。太过于抽象了
            for(int i=currentLevel-1;i>=0;i--) {
            	 //通过内循环直接对当前节点的节点数组的最大广度进行拦截。并且外循环的当前节点指针永远是停在刚进入的节点进行广度跟深度遍历
                //Redis版的这个跳表实现抽象程度如果自己不去画图加模拟程序运行，压根理解不了这简单的几行代码所蕴含的设计的思想牛逼之处。
                //我在current.next != null 挣扎过很久，也尝试过用for循环对current.next[]数组进行遍历，全部行不通。因为只要一切换数组下标
                //我的循环current指针就压根不再指向原来的for循环的遍历了。但是由于最开始元宝给过我一版简单的Redis版本的跳表实现我看过插入的
                //一部分源码，我在最开始尝试过很多思考以后就一直抓着current = current.next[0]表示current在第0层的下一个这句代码。
                //因为这句代码我开始理解不了让元宝给我分析过，我就一直抓住了current.next[i]表示当前节点在第i层的下一个节点来实现，不然这个跳表
                //根本实现不了。
                while(current.next[i] != null && this.cpr(current.next[i].key, key, comparator) < 0) {
                    current = current.next[i];
                }
                //拿到每层的前驱节点，相当于拿到了update[i]前驱节点位置.以及update.next[i]后驱动节点位置，其中i表示跳表当前层高。
                //在判断完第0层数据层的当前节点的后继节点的key跟待插入目标节点的key比较以后。再用随机生成的节点层高newLevel-1
                //表示目标节点所在层级，用for循环就能直接拿到节点在每层需要插入的前驱位置update[newLevel-1]以及后继位置
                //update[newLevel-1].next[newLevel-1]就可以对整个跳表的插入进行更新。跳表的指针操作也是遵循先外围再自身的连接原则。
                //跳表的指针操作只能通过update节点数组进行，因为update节点数组能通过索引表示当前节点所在层级，而current.next[i]只能表示
                //当前节点所在层级的下一个节点位置，压根进行不了指针操作。
                //平面化的理解就是拿到一个动态指针数组，索引为0-MAX_LEVEL，这个指针数组在函数没有结束前永远牢牢指向每层的current或者null。
                //当想要使用这个指针时，只需要把遍历出来即可。其中current表示数组指针的最大索引指针。
                update[i] = current;
            }
            
            //拿到当前节点在数据层第0层的下一个节点，比较key大小.
            //在循环中出来的current的key是比目标key更小的节点并且永远是当前层的最大索引指针或者null
            //所以后继节点的key绝不可能比目标key更小
            current = current.next[0];
            //当后继节点不为空且key与目标key相等时直接更新数据层节点的value。这里元宝说不要用equals，cpr比较一致性
            if(current != null && this.cpr(current.key, key, comparator) == 0) {
                current.value = value;
                return true;
            }else {
                //先创建新节点，方便指针操作
                Node<K,V> node = new Node(key,value,newLevel);  
                //在真正执行插入节点时，会有节点层高比跳表层高更高的问题
                if(currentLevel < newLevel) {//当后继节点为null或者后继节点key跟目标key不相等时
                    for(int i=currentLevel;i<newLevel;i++) {
                        update[i] = head;
                    }
                    this.currentLevel = newLevel;
                }
                
                for(int i=0;i<newLevel;i++) {
                    node.next[i] = update[i].next[i];
                    update[i].next[i] = node;
                }
                
            }
            this.size++;
            return true;
        }
        
        public boolean containsKey(K key) {
            return this.get(key) != null;
        }
        
        public synchronized void clear() {
            for(int i=0;i<head.next.length;i++) {
                head.next[i] = null;
            }
            this.size = 0;
            this.currentLevel = 1;
        }
        
        @SuppressWarnings("unchecked")
        public synchronized boolean remove(K key) {
            if(key == null) throw new NullPointerException("空指针异常");
            Node<K,V> current = head;
            Node<K,V> update[] = new Node[MAX_LEVEL];
            for(int i=currentLevel-1;i>=0;i--) {
                while(current.next[i] != null && this.cpr(current.next[i].key, key, comparator) < 0) {
                    current = current.next[i];
                }
                update[i] = current;
            }
            current = current.next[0];
         
            if(current == null || this.cpr(current.key, key, comparator) != 0) {
                return false;
            }
            //这里我最开始就是这么写的，但是当时自己写的时候并不理解为什么自己会这么写这个删除逻辑。
            //后面我尝试过用update[i].next[i].next ！=null写，被元宝毙了。这里隐藏了跳表的最大特点在里面。
            //因为current已经被我重新赋值为目标节点了。当current.next[i]就表示目标节点在第i层的下一个节点位置。
            for(int i=0;i<current.next.length;i++) {
                update[i].next[i] = current.next[i];
            }
            
            //删除目标节点时，目标节点可能为跳表入口节点，在进行物理摘除时跳表入口可能会被清空为null必须重新对整个跳表的层高进行更新
            for(int i=currentLevel-1;i>=0;i--) {
                if(head.next[i] != null) {
                    this.currentLevel = i+1;
                    break;
                }
            }
            
            this.size--;
            return true;
        }
    
        //静态内部类避免内存泄漏
        static final class Node<K,V>{
            //节点数组创建后引用不变
            final Node<K,V>[] next;
            //key一旦赋值就是唯一且有序的，不允许修改
            final K key;
            //节点的数据会被修改，需所有线程可见
            volatile V value;
            
            @SuppressWarnings("unchecked")
            public Node(K key, V value,int nodeLevel) {
                super();
                this.next = new Node[nodeLevel];
                this.key = key;
                this.value = value;
            }
            
        }
    }
}