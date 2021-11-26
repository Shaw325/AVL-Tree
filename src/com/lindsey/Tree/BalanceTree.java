package com.lindsey.Tree;

import jdk.jfr.Description;

/**
 * @ClassName: BalanceTree
*   @Description: TODO
*   @Author: lindsey.Shaw
*   @Date: 2021/11/23
*   @version: 1.0
 */
public class BalanceTree implements ITree<BalanceTree.Node> {

    private Node root;

    private int size = 0;


    @Override
    public void add(int val) {
        Node node = new Node(val);
        if(node == null) throw new RuntimeException("Can't insert null into tree.");
        if(root == null) {
            root = node;
            size++;
            return;
        }
        // 找到待插入位置的父结点
        // 挂载结点
        /**
         * 调节平衡因子
         *
         * 插入左子树时，bf+1
         * 插入右子树时，bf-1
         *
         * 1. 插入结点的BF从1调至0时，不需要更改祖先结点的平衡因子，只要更改当前的平衡因子
         * 2. 插入结点的BF从0调至1、-1时，需要更改祖先结点的平衡因子，直到根结点
         */
        Node p = findParent(node);
        // 判断大小是从左树插入还是右树插入
        if(node.data < p.data){
            p.left = node;
        }else{
            p.right = node;
        }
        node.parent = p;
        Node move = p;
        // 回溯回第一个结点触发旋转的结点，逐个更新height
        while(move != null){
            adjustHeight(move);
            if(Math.abs(bf(move)) == 2){
                // 从这个失衡的结点开始平衡
                System.out.println("平衡前");
                TreePrinter.show(this.root);
                balance(move);
                System.out.println("平衡后");
                TreePrinter.show(this.root);
                break;
            }
            move = move.parent;
        }
        size++;
    }

    private void balance(Node node) {
        Node left = node.left;
        Node right = node.right;
        int lh = nodeHeight(node.left);
        int rh = nodeHeight(node.right);
        if(lh > rh && left != null){// 左边比右边高
            if(left.left !=null){
                // 右旋
                rotate_R(node);
            }else{
                // 左右双旋
                rotate_LR(node);
            }
        }else if(lh < rh && right != null){
            if(right.right != null){
                // 左旋
                rotate_L(node);
            }else{
                // 右左双旋
                rotate_RL(node);
            }
        }
    }

    private void rotate_RL(Node node) {
        Node right = node.right;
        rotate_R(right);
        rotate_L(node);
    }

    private void rotate_L(Node node) {
        Node p = node.parent;
        Node right = node.right;
        Node subL = right.left;
        node.right = subL;
        if(subL != null){
            subL.parent = node;
        }
        right.left = node;
        if(p == null){
            right.parent = this.root.parent;
            this.root = right;
        }else{
            if(p.left == node){
                p.left = right;
            }else if(p.right == node){
                p.right = right;
            }
            right.parent = p;
        }
        node.parent = right;
        adjustHeight(node);
        adjustHeight(right);
    }

    private void rotate_LR(Node node) {
        Node left = node.left;
        rotate_L(left);
        rotate_R(node);
    }

    private void rotate_R(Node node) {
        Node p = node.parent;
        Node left = node.left;
        Node subR = left.right;
        node.left = subR;
        if(subR != null){
            subR.parent = node;
        }
        left.right = node;
        if(p == null){
            left.parent = this.root.parent;
            this.root = left;
        }else{
            if(p.left == node){
                p.left = left;
            }else if(p.right == node){
                p.right = left;
            }
            left.parent = p;
        }
        node.parent = left;
        adjustHeight(node);
        adjustHeight(left);

    }

    private void adjustHeight(Node node) {
        Node left = node.left;
        Node right = node.right;
        int lh = nodeHeight(left);
        int rh = nodeHeight(right);
        node.height = Math.max(lh,rh) + 1;
    }

    private int bf(Node node) {
        int lh = nodeHeight(node.left);
        int rh = nodeHeight(node.right);
        return lh-rh;
    }

    private int nodeHeight(Node node) {
        if(node == null){
            return 0;
        }
        return node.height;
    }

    private Node findParent(Node node) {
        Node p = root;
        Node t = null;
        while(p != null){
            t = p;
            if(node.data < p.data){
                p = p.left;
            }else{
                p = p.right;
            }
        }
        return t;
    }

    @Override
    public void remove(int val) {
        if(root == null) throw new RuntimeException("Tree is null now!");
        Node p = root,t = null;
        while(p != null){
            if(p.data == val){
                deleteNode(p,t);
                size--;
                break;
            }
            t = p;
            if(val > p.data){
                p = p.right;
            }else{
                p = p.left;
            }
        }
    }

    private void deleteNode(Node p, Node parent) {
        // 特殊处理根结点的删除
        if(parent == null && p.right == null && p.left == null){
            root = null;
            return;
        }
        // 第一种情况，当前删除的结点没有子结点，那么直接清除,这时候要改变父结点的指针域
        if(p.left == null){
            if(p == parent.left){
                parent.left = p.right;
            }else if(p == parent.right){
                parent.right = p.right;

            }
            if(p.right != null){
                p.right.parent = parent;
            }
        }else if(p.right == null){
            if(p == parent.left){
                parent.left = p.left;
            }else if(p == parent.right){
                parent.right = p.left;
            }
            if(p.left != null){
                p.left.parent = parent;
            }
        }else if(p.left != null){ // 第二种情况，当前删除的结点如果有左子树，那么从左子树拿出一个最大值填充为当前结点，然后将那个最大值删除掉
            Node lmax = p.left,lp = p;
            while (lmax.right != null){
                lp = lmax;
                lmax = lmax.right;
            }
            p.data = lmax.data;
            deleteNode(lmax,lp);
        }else if(p.right != null){// 第二种情况，当前删除的结点如果有右子树，那么从右子树拿出一个最小值填充为当前结点，然后将那个最小值删除掉
            Node rmin = p.right,rp = p;
            while (rmin.right != null){
                rp = rmin;
                rmin = rmin.left;
            }
            p.data = rmin.data;
            deleteNode(rmin,rp);
        }
        // 因为实际删除的是最底下的结点，从最底下的父结点开始调节平衡
        Node move = parent;
        while(move != null){
            adjustHeight(move);
            if(Math.abs(bf(move)) == 2){
                // 从这个失衡的结点开始平衡
                balance(move);
            }
            move = move.parent;
        }
    }

    static class Node implements TreeNode{

        private int data;

        private Node left,right,parent;

        private int height = 1;

        @Override
        public Object val() {
            return data;
        }

        public Node(int data) {
            this.data = data;
        }

        public Node(int data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        @Override
        public Node right() {
            return right;
        }

        @Override
        public Node left() {
            return left;
        }
    }

    public static void main(String[] args) {
        BalanceTree balanceTree = new BalanceTree();
        for (int i = 0; i < 64; i++) {
            balanceTree.add(i);
        }


//        balanceTree.add(9);
//        balanceTree.remove(7);
    }

}
