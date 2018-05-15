# 并查集算法


## 应用场景：  
输入一些整数，这些整数可以表示的是电子电路中的触点，而整数对 表示的是连接触点之间的电路；或者这些整数可能是社交网络中的人
，而整数对表示的是朋友关系。
## 知识储备
#### 等价类

我们假设对象（p与q）之间存在的关系是一种相连的话，那么这种“关系”存在一种等价性(例如1 < 2,2 < 3,则1 < 3),这就意味着它具有：   
自反性：p 和 p是相连的;  
对称性：如果p和q是相连的，那么q和p也是相连的;  
传递性：如果p和q是相连的且q和r是相连的，那么p和r也是相连的。  
等价关系能够将对象分为多个等价类，当且仅当两个对象相连的时候他们才输入同一个等价类。  
####术语
将对象称为*触点*,将对象间的堆成称为*连接*，将等价类称为*连通分量*或简称为分量
  
![触点](../dynamic_connect/image/1.png)   


![连通分量](../dynamic_connect/image/2.png)

## 算法实现

#### union-find算法
API  
![api](../dynamic_connect/image/UF.png)  
如果两个触点在不同的分量中，union()操作将会将两个分量归并。find()操作会返回给定触点所在连通分量的标识符。connencted()判断两个触点是
否在于同一个分量之中。count()方法会返回所有连通分量的数量。一开始我们有N个分量，将两个分量归并的每次union()操作都会使分量总数减一。


````java
/**
* 该算法非常巧妙的将数组下标作为触点的标识符，
* 用数组的值表示触点间的关系 
*/
public class UF {
    private int[] id;              //分量(以触点作为索引)
    private int count;             //分量数量
    public  UF(int N){
        count = N;
        id = new int[N];
        for (int i = 0;i < N;i++){
            id[i] = i;
        }
    }

    public int count(){
        return count;
    }

    public boolean connected(int p,int q){
        return find(p) == find(q);
    }

    public int find(int p){
        return id[p];
    }

    public void union(int p,int q){
        //将p和q归并到相同的分量中
        int pID = find(p);
        int qID = find(q);

        //如果p和q已经在相同的分量之中则不需要采取任何行动
        if (pID == qID) return;

        //将p的分量重命名为q的名称
        for (int i = 0;i < id.length;i++){
            if (id[i] == pID){
                id[i] = qID;
            }
        }
        count--;
    }
}
````
find()操作的速度显然是很快的，因为它只需访问id[]数组一次。但quick-find算法一般
无法处理大型问题，因为对于每一次输入union()都需要扫描整个id[]数组，这将花费O(n^2)的时间。
很明显用这样的算法处理大量数据的是非常糟糕的选择，我们需要寻找更好的算法。

#### quick-union算法
需要讨论的下一个算法的重点是提高union()方法的速度，它和quick-find算法算是互补的，基于相同的
数据结构—以触点作为索引的id[]数组，但我们赋予这些值的意义不同。
```java
/**
 * quick-find算法
 */
public class QU {
    private int[] id;              //分量(以触点作为索引)
    private int count;             //分量数量
    public  QU(int N){
        count = N;
        id = new int[N];
        for (int i = 0;i < N;i++){
            id[i] = i;
        }
    }

    public int count(){
        return count;
    }

    public boolean connected(int p,int q){
        return find(p) == find(q);
    }

    public int find(int p){
        //递归查找根节点(即分量的名称)
        while (p != id[p]){
            p = id[p];
        }
        return p;
    }

    public void union(int p,int q){
        int pRoot = find(p);
        int qRoot = find(q);
        if (pRoot == qRoot){
            return;
        }
        id[pRoot] = qRoot;

        count--;
    }
}

```
quick-union算法以数组的索引作为触点的标识，以数组的值连接两个触点，比如id[1] = 1,id[2] = 1,id[3] = 3;
这样id[1]与id[2]就产生了关联形成了连通分量，而id[3]只与自己关联，如果你有过对于树的数据结构的了解的话，
就能很容易理解这里，这里相当于将id[2]变成了id[1]的子树。    
quick-union算法看起来比quick-find算法更快，但它也存在着缺陷，在对0到n数进行union()时，union()操作访问数组
的次数为2n + 1(触点0的深度为n-1,触点i的深度为0)。因此，处理N对整数所需要的所有find()操作访问数组的总次数为
3+5+7+...+(2N - 1) 约为 N^2。可以看出quick-union算法在最坏的情况下也是比较糟糕的，因此我们需要接着对算法进行
优化。

#### 加权quick-union算法

只需要简单地修改quick-union算法就能保证像这样的糟糕的情况不再出现。与其在union()中随意将一棵树连接到另一颗树，
我们现在记录每一颗树的大小并总是将较小的树连接到较大的树上。这项改动需要添加一个数组和一些代码来记录这些信息。
```java
public class WeightedQuickUnionUF {
    private int[] id;               //父链接数组(由触点索引)
    private int[] sz;               //(由触点索引的)各个根节点所对应的分量的大小
    private int count;              //连同分量的数量

    public WeightedQuickUnionUF(int N){
        count = N;
        id = new int[N];
        for (int i = 0;i < N;i++){
            id[i] = i;               //最初每个节点都指向它们本身
        }
        for (int i = 0;i < N;i++){
            sz[i] = 1;
        }
    }

    public int count(){
        return count;
    }

    public boolean connected(int p,int q){
        return find(p) == find(q);
    }

    public int find(int p){
        //跟随连接找到根节点
        while (p != id[p]){
            p = id[p];
        }
        return p;
    }

    public void union(int p,int q){
        int i = find(p);
        int j = find(q);
        if (i == j){
            return;
        }
        //将小树的根节点链接到大树的根节点
        if (sz[i] < sz[j]){
            id[i] = j;
            sz[j] += sz[i];
        }else {
            id[j] = i;
            sz[i] += sz[j];
        }
        count--;
    }
}
```