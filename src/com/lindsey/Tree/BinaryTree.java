package com.lindsey.Tree;

import org.w3c.dom.Node;

/**
 * @ClassName: BinaryTree
 * @Description: TODO
 * @Author: lindsey.Shaw
 * @Date: 2021/11/23
 * @version: 1.0
 **/
public class BinaryTree implements ITree<BinaryTree.Node>{

    private Node root;

    private int size = 0;

    public BinaryTree() {}

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
        Node p = findParent(node);
        // 判断大小是从左树插入还是右树插入
        if(node.data < p.data){
            p.left = node;
            size++;
        }else{
            p.right = node;
            size++;
        }

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

    /**
     * 三种情况
     * 1. 删除的结点是叶子结点，此时可以直接删除
     * 2. 删除的结点是分叉结点，且存在左树，删除结点中左树最大结点代替，然后删除左树最大结点
     * 3. 删除的结点是分叉结点，且存在右树，删除结点中右树最小结点代替，然后删除右树最小结点
     * @param val
     */
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

    /**
     *
     * @param p
     * @param parent
     */
    private void deleteNode(Node p,Node parent) {
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
        }else if(p.right == null){
            if(p == parent.left){
                parent.left = p.left;
            }else if(p == parent.right){
                parent.right = p.left;
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
    }

    static class Node implements TreeNode{

        private int data;

        private Node left,right;

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
        public TreeNode right() {
            return right;
        }

        @Override
        public TreeNode left() {
            return left;
        }
    }

    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        binaryTree.add(5);
        binaryTree.add(3);
        binaryTree.add(4);
        TreePrinter.show(binaryTree.root);
//        binaryTree.remove(5);
//        System.out.println("After：");
//        TreePrinter.show(binaryTree.root);
//        System.out.println("Debugger");
    }
}
