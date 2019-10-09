package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

/**
 * 传统 莱文斯坦(距离) 工具类
 * <br>Created by Soybeany on 2019/9/27.
 */
public class LevenshteinUtils {

    private static final int WEIGHT_ADD = 2;
    private static final int WEIGHT_MODIFY = 1;
    private static final int WEIGHT_DELETE = 2;

    // ****************************************公开方法****************************************

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(T[] source, T[] target, ICallback<T> callback) {
        compare(source, target, null, null, callback);
    }

    /**
     * 对比指定的两个数组，范围使用的是数组的下标，包含from，包含to
     */
    public static <T> void compare(T[] source, T[] target, Range sRange, Range tRange, ICallback<T> callback) {
        Info<T> info = new Info<>(source, target, sRange, tRange);
        Node node = calculate(info, info.getSLength() - 1, info.getTLength() - 1);
        callback.onStart();
        parseNode(info, node, callback);
        callback.onFinal(node.value);
    }

    // ****************************************内部方法****************************************

    private static Node calculate(Info info, int sIndex, int tIndex) {
        Node curNode = info.getValue(sIndex, tIndex);
        if (null != curNode) {
            return curNode;
        }
        Node leftTopNode = calculate(info, sIndex - 1, tIndex - 1);

        // 若元素相等，取矩阵左上角的值
        if (info.isElementEqual(sIndex, tIndex)) {
            curNode = leftTopNode.createNext(1, 1, 0);
        }
        // 不相等，则取左、左上角、上三者的最小值，然后+1；
        else {
            Node leftNode = calculate(info, sIndex - 1, tIndex);
            Node topNode = calculate(info, sIndex, tIndex - 1);
            // 删除
            if (leftNode.lt(leftTopNode) && leftNode.lt(topNode)) {
                curNode = leftNode.createNext(1, 0, WEIGHT_DELETE);
            }
            // 新增
            else if (topNode.lt(leftTopNode)) {
                curNode = topNode.createNext(0, 1, WEIGHT_ADD);
            }
            // 修改
            else {
                curNode = leftTopNode.createNext(1, 1, WEIGHT_MODIFY);
            }
        }
        info.setValue(sIndex, tIndex, curNode);
        return curNode;
    }

    private static <T> void parseNode(Info<T> info, Node node, ICallback<T> callback) {
        if (null == node.pre) {
            return;
        }
        // 使用递归，将反序的节点处理变为正序处理
        parseNode(info, node.pre, callback);
        // 相等
        if (node.value == node.pre.value) {
            callback.onElementSame(info.source[node.sIndex], info.target[node.tIndex]);
        }
        // 修改 对角线，相邻节点的下标相差1
        else if (node.sIndex == node.pre.sIndex + 1 && node.tIndex == node.pre.tIndex + 1) {
            callback.onElementModify(info.source[node.sIndex], info.target[node.tIndex]);
        }
        // 删除
        else if (node.sIndex == node.pre.sIndex + 1) {
            callback.onElementDelete(node.tIndex + 1, info.source[node.sIndex], info.target[Math.max(node.tIndex, 0)]);
        }
        // 新增
        else {
            callback.onElementAdd(node.sIndex + 1, info.source[Math.max(node.sIndex, 0)], info.target[node.tIndex]);
        }
    }

    // ****************************************内部类****************************************

    /**
     * 处理结果的回调，使用参数为位点坐标
     */
    public interface ICallback<T> {

        /**
         * 处理前的回调
         */
        void onStart();

        /**
         * 元素相等时的回调
         */
        void onElementSame(T source, T target);

        /**
         * 元素增加时的回调，对应{@link Change#ADD}
         */
        void onElementAdd(int addPos, T source, T target);

        /**
         * 元素修改时的回调，对应{@link Change#MODIFY}
         */
        void onElementModify(T source, T target);

        /**
         * 元素删除时的回调，对应{@link Change#DELETE}
         */
        void onElementDelete(int delPos, T source, T target);

        /**
         * 处理后的回调
         */
        void onFinal(int distance);
    }

    private static class Info<T> {

        private T[] source;
        private T[] target;
        Node[][] matrix;

        // 坐标系切换的偏移量
        private int sOffset = -1;
        private int tOffset = -1;

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
                this.sOffset = sRange.from - 1;
                sLength = sRange.length();
            }
            if (null != tRange) {
                this.tOffset = tRange.from - 1;
                tLength = tRange.length();
            }
            matrix = new Node[tLength + 1][sLength + 1];
            init();
        }

        int getSLength() {
            return matrix[0].length;
        }

        int getTLength() {
            return matrix.length;
        }

        boolean isElementEqual(int sIndex, int tIndex) {
            return source[sIndex + sOffset].equals(target[tIndex + tOffset]);
        }

        Node getValue(int sIndex, int tIndex) {
            return matrix[tIndex][sIndex];
        }

        void setValue(int sIndex, int tIndex, Node value) {
            matrix[tIndex][sIndex] = value;
        }

        private void init() {
            // 原点
            matrix[0][0] = new Node(0, tOffset, tOffset, null);
            // 横向初始化
            Node node = matrix[0][0];
            for (int i = 1; i < matrix[0].length; i++) {
                matrix[0][i] = node.createNext(1, 0, WEIGHT_DELETE);
                node = matrix[0][i];
            }
            // 纵向初始化
            node = matrix[0][0];
            for (int i = 1; i < matrix.length; i++) {
                matrix[i][0] = node.createNext(0, 1, WEIGHT_ADD);
                node = matrix[i][0];
            }
        }
    }

    /**
     * 节点，使用矩阵下标，同时是入参数组的位点坐标
     */
    private static class Node {
        int value;
        int sIndex;
        int tIndex;

        Node pre;

        Node(int value, int sIndex, int tIndex, Node pre) {
            this.value = value;
            this.sIndex = sIndex;
            this.tIndex = tIndex;
            this.pre = pre;
        }

        boolean lt(Node node) {
            return value < node.value;
        }

        Node createNext(int sDelta, int tDelta, int vDelta) {
            return new Node(value + vDelta, sIndex + sDelta, tIndex + tDelta, this);
        }
    }
}
