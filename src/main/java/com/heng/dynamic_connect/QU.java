package com.heng.dynamic_connect;

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
