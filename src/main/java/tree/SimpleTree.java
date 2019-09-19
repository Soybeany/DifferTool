package tree;

import tree.node.TreeNode;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/9/9.
 */
public class SimpleTree {

    private TreeNode mRoot; // 根节点

    public void addNode(TreeNode node) {
        if (null == node) {
            throw new RuntimeException("不允许插入null");
        }
        if (null == mRoot) {
            mRoot = node;
        } else {
            addNodeInner(mRoot, node);
        }
    }

    public boolean removeNode(TreeNode node) {
        if (null == node) {
            throw new RuntimeException("不允许移除null");
        }
        if (mRoot == node) {
            mRoot = null;
            addChildNode(node);
        } else {
            return removeNodeInner(mRoot, node);
        }
        return true;
    }

    /**
     * 中序遍历
     */
    public List<TreeNode> traverseInLDR() {
        List<TreeNode> result = new LinkedList<>();
        innerTraverseInLDR(result, mRoot);
        return result;
    }

    private void innerTraverseInLDR(List<TreeNode> result, TreeNode node) {
        if (null == node) {
            return;
        }
        innerTraverseInLDR(result, node.left);
        result.add(node);
        innerTraverseInLDR(result, node.right);
    }

    private void addNodeInner(TreeNode contrast, TreeNode node) {
        int contrastValue = contrast.getValue();
        int nodeValue = node.getValue();
        // 左节点
        if (nodeValue < contrastValue) {
            if (null == contrast.left) {
                contrast.left = node;
                node.parent = contrast;
            } else {
                addNodeInner(contrast.left, node);
            }
        }
        // 右节点
        else if (nodeValue > contrastValue) {
            if (null == contrast.right) {
                contrast.right = node;
                node.parent = contrast;
            } else {
                addNodeInner(contrast.right, node);
            }
        }
        // 相等则抛异常
        else {
            throw new RuntimeException("不允许节点的值相等");
        }
    }

    private boolean removeNodeInner(TreeNode contrast, TreeNode node) {
        if (null == contrast) {
            return false;
        }
        int contrastValue = contrast.getValue();
        int nodeValue = node.getValue();
        // 左节点
        if (nodeValue < contrastValue) {
            return removeNodeInner(contrast.left, node);
        }
        // 右节点
        else if (nodeValue > contrastValue) {
            return removeNodeInner(contrast.right, node);
        }
        // 节点相等
        else {
            TreeNode parent = contrast.parent;
            if (parent.left == node) {
                parent.left = null;
            } else if (parent.right == node) {
                parent.right = null;
            } else {
                throw new RuntimeException("值相等，但并非同一对象");
            }
            addChildNode(node);
        }
        return true;
    }

    private void addChildNode(TreeNode node) {
        if (null != node.left) {
            addNode(node.left);
        }
        if (null != node.right) {
            addNode(node.right);
        }
    }

}
