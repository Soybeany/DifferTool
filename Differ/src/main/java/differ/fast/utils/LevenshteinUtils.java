package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

import java.util.LinkedList;

/**
 * 传统 莱文斯坦(距离) 工具类
 * <br>Created by Soybeany on 2019/9/27.
 */
public class LevenshteinUtils {

    // ****************************************公开方法****************************************

    public static <T> Result compare(T[] source, T[] target) {
        Result result = new Result();
        compare(result, source, target, null, null);
        return result;
    }

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(Result result, T[] source, T[] target, Range sRange, Range tRange) {
        Info<T> info = new Info<>(source, target, sRange, tRange);
        int sourceIndex = (null != sRange ? sRange.to : source.length) - 1;
        int targetIndex = (null != tRange ? tRange.to : target.length) - 1;
        parseNode(result.changes, calculate(info, sourceIndex, targetIndex));
    }

    // ****************************************内部方法****************************************

    private static <T> Node calculate(Info<T> info, int sourceIndex, int targetIndex) {
        Node curNode = info.getValue(sourceIndex, targetIndex);
        if (null == curNode) {
            Node leftTopNode = calculate(info, sourceIndex - 1, targetIndex - 1);
            // 若元素相等，取矩阵左上角的值
            if (info.isElementEqual(sourceIndex, targetIndex)) {
                curNode = leftTopNode.createNext(sourceIndex, targetIndex);
            }
            // 不相等，则取左、左上角、上三者的最小值，然后+1；
            else {
                Node leftNode = calculate(info, sourceIndex - 1, targetIndex);
                Node topNode = calculate(info, sourceIndex, targetIndex - 1);
                // 删除
                if (leftNode.lt(leftTopNode) && leftNode.lt(topNode)) {
                    curNode = leftNode.createNext(sourceIndex, targetIndex);
                }
                // 新增
                else if (topNode.lt(leftTopNode)) {
                    curNode = topNode.createNext(sourceIndex, targetIndex);
                }
                // 修改
                else {
                    curNode = leftTopNode.createNext(sourceIndex, targetIndex);
                }
                curNode.value += 1;
            }
            info.setValue(sourceIndex, targetIndex, curNode);
        }
        return curNode;
    }

    private static void parseNode(LinkedList<Change> changes, Node node) {
        if (0 == node.value) {
            return;
        }
        // 使用递归，将反序的节点处理变为正序处理
        parseNode(changes, node.pre);
        // 相等
        if (node.value == node.pre.value) {
            return;
        }
        // 修改 对角线，相邻节点的下标相差1
        if (node.sourceIndex == node.pre.sourceIndex + 1 && node.targetIndex == node.pre.targetIndex + 1) {
            Change change = getSuitableChange(changes, Change.MODIFY, node.sourceIndex, node.targetIndex);
            change.source.to = node.sourceIndex + 1; // from与to之间需相差1
            change.target.to = node.targetIndex + 1;
        }
        // 删除
        else if (node.sourceIndex == node.pre.sourceIndex + 1) {
            Change change = getSuitableChange(changes, Change.DELETE, node.sourceIndex, node.targetIndex);
            change.source.to = node.sourceIndex + 1;
        }
        // 新增
        else {
            Change change = getSuitableChange(changes, Change.ADD, node.sourceIndex, node.targetIndex);
            change.target.to = node.targetIndex + 1;
        }
    }


    /**
     * 获得合适的变更(复用/新增)
     */
    private static Change getSuitableChange(LinkedList<Change> changes, int type, int sourceIndex, int targetIndex) {
        Change change = changes.isEmpty() ? null : changes.getLast();
        if (null != change && change.type == type && change.isChangeContinuous(type, sourceIndex, targetIndex)) {
            change.count++;
            return change;
        }
        change = new Change(type, new Range(), new Range());
        changes.add(change);
        // 初始化设置
        switch (type) {
            case Change.DELETE:
                change.source.from = sourceIndex;
                change.target.setup(targetIndex + 1, targetIndex + 1); // target的下一位下标作为删除基点
                break;
            case Change.ADD:
                change.target.from = targetIndex;
                change.source.setup(sourceIndex + 1, sourceIndex + 1); // source下一位下标作为插入基点
                break;
            case Change.MODIFY:
                change.source.from = sourceIndex;
                change.target.from = targetIndex;
                break;
        }
        return change;
    }

    // ****************************************内部类****************************************

    public static class Result {
        /**
         * 变更的内容列表
         */
        public final LinkedList<Change> changes = new LinkedList<>();

        /**
         * 获得 莱文斯坦距离
         */
        public int getDistance() {
            int distance = 0;
            for (Change change : changes) {
                distance += change.count;
            }
            return distance;
        }

        /**
         * 获得 相似度，范围0~1
         */
        public float getSimilarity(int source, int target) {
            return getDistance() * 1.0f / Math.max(source, target);
        }

        public void print(Object[] input1, Object[] input2) {
            for (Change change : changes) {
                String msg = change.type + "  " + change.count + "  ";
                msg += "source:" + getString(input1, change.source.from, change.source.to) + "(" + change.source.from + "-" + change.source.to + ")  ";
                msg += "target:" + getString(input2, change.target.from, change.target.to) + "(" + change.target.from + "-" + change.target.to + ")";
                System.out.println(msg);
            }
        }

        private String getString(Object[] input1, int from, int to) {
            StringBuilder builder = new StringBuilder();
            for (int i = from; i < to; i++) {
                builder.append(input1[i].toString());
            }
            return builder.toString();
        }
    }

    private static class Info<T> {
        T[] source;
        T[] target;
        Node[][] matrix;

        private int sFromIndex;
        private int tFromIndex;

        /**
         * @param sRange 可空，表示不限制范围
         * @param tRange 可空，表示不限制范围
         */
        Info(T[] source, T[] target, Range sRange, Range tRange) {
            this.source = source;
            this.target = target;
            int sLength = source.length;
            int tLength = target.length;
            if (null != sRange) {
                this.sFromIndex = sRange.from;
                sLength = sRange.length();
            }
            if (null != tRange) {
                this.tFromIndex = tRange.from;
                tLength = tRange.length();
            }
            matrix = new Node[tLength + 1][sLength + 1];
            init();
        }

        boolean isElementEqual(int sourceIndex, int targetIndex) {
            return source[sourceIndex].equals(target[targetIndex]);
        }

        Node getValue(int sourceIndex, int targetIndex) {
            return matrix[targetIndex - tFromIndex + 1][sourceIndex - sFromIndex + 1];
        }

        void setValue(int sourceIndex, int targetIndex, Node value) {
            matrix[targetIndex - tFromIndex + 1][sourceIndex - sFromIndex + 1] = value;
        }

        private void init() {
            // 原点
            matrix[0][0] = new Node(0, -1, -1, null);
            // 横向初始化
            Node node = matrix[0][0];
            for (int i = 1; i < matrix[0].length; i++) {
                matrix[0][i] = new Node(i, i - 1, -1, node);
                node = matrix[0][i];
            }
            // 纵向初始化
            node = matrix[0][0];
            for (int i = 1; i < matrix.length; i++) {
                matrix[i][0] = new Node(i, -1, i - 1, node);
                node = matrix[i][0];
            }
        }
    }

    private static class Node {
        int value;
        int sourceIndex;
        int targetIndex;

        Node pre;

        Node(int value, int sourceIndex, int targetIndex, Node pre) {
            this.value = value;
            this.sourceIndex = sourceIndex;
            this.targetIndex = targetIndex;
            this.pre = pre;
        }

        boolean lt(Node node) {
            return value < node.value;
        }

        Node createNext(int sourceIndex, int targetIndex) {
            return new Node(value, sourceIndex, targetIndex, this);
        }
    }
}
