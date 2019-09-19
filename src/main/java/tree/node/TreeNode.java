package tree.node;

/**
 * <br>Created by Soybeany on 2019/9/9.
 */
public class TreeNode {
    /**
     * 左子节点
     */
    public TreeNode left;

    /**
     * 右子节点
     */
    public TreeNode right;

    /**
     * 父节点
     */
    public TreeNode parent;

    /**
     * 详情
     */
    public final Detail detail = new Detail();

    @Override
    public String toString() {
        return getValue() + "";
    }

    public TreeNode(int value) {
        detail.oldC.index = value;
    }

    /**
     * 获得代表节点的值
     */
    public int getValue() {
        return detail.oldC.index;
    }
}
