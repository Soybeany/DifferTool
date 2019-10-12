package differ.fast.utils;

import differ.fast.model.Range;

/**
 * 传统 莱文斯坦(距离) 工具类
 * <br>Created by Soybeany on 2019/9/27.
 */
public class LevenshteinUtils {

    // ****************************************公开方法****************************************

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(T[] source, T[] target, ICallback<T> callback) {
        compare(source, target, null, null, IWeightProvider.Std.get(), callback);
    }

    /**
     * 对比指定的两个数组，范围使用的是数组的下标，包含from，包含to
     */
    public static <T> void compare(T[] source, T[] target, Range sRange, Range tRange, IWeightProvider<T> weight, ICallback<T> callback) {
        Info<T> info = new Info<>(source, target, sRange, tRange, weight);
        Node node = calculate(info, info.getSLength() - 1, info.getTLength() - 1);
        callback.onStart();
        int distance = parseNode(info, node, callback, true);
        callback.onFinal(distance);
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
            curNode = leftTopNode.createRightBottom();
        }
        // 不相等，则取左、左上角、上三者的最小值，然后+1；
        else {
            Node leftNode = calculate(info, sIndex - 1, tIndex);
            Node topNode = calculate(info, sIndex, tIndex - 1);
            // 删除
            if (leftNode.lt(leftTopNode) && leftNode.lt(topNode)) {
                curNode = info.withVDelete(leftNode.createRight());
            }
            // 新增
            else if (topNode.lt(leftTopNode)) {
                curNode = info.withVAdd(topNode.createBottom());
            }
            // 修改
            else {
                curNode = info.withVModify(leftTopNode.createRightBottom());
            }
        }
        info.setValue(sIndex, tIndex, curNode);
        return curNode;
    }

    /**
     * @return 莱文斯坦距离
     */
    private static <T> int parseNode(Info<T> info, Node node, ICallback<T> callback, boolean isEnd) {
        if (null == node.pre) {
            return 0;
        }
        // 使用递归，将反序的节点处理变为正序处理
        int distance = parseNode(info, node.pre, callback, false);
        // 相等
        if (node.value == node.pre.value) {
            callback.onElementSame(info.source[node.sIndexInArr], info.target[node.tIndexInArr], isEnd);
            return distance;
        }
        // 修改 对角线，相邻节点的下标相差1
        if (node.sIndexInArr == node.pre.sIndexInArr + 1 && node.tIndexInArr == node.pre.tIndexInArr + 1) {
            callback.onElementModify(info.source[node.sIndexInArr], info.target[node.tIndexInArr], isEnd);
        }
        // 删除
        else if (node.sIndexInArr == node.pre.sIndexInArr + 1) {
            callback.onElementDelete(node.tIndexInArr + 1, info.source[node.sIndexInArr], info.target[Math.max(node.tIndexInArr, 0)], isEnd);
        }
        // 新增
        else {
            callback.onElementAdd(node.sIndexInArr + 1, info.source[Math.max(node.sIndexInArr, 0)], info.target[node.tIndexInArr], isEnd);
        }
        return distance + 1;
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
        void onElementSame(T source, T target, boolean isEnd);

        /**
         * 元素增加时的回调
         */
        void onElementAdd(int addPos, T source, T target, boolean isEnd);

        /**
         * 元素修改时的回调
         */
        void onElementModify(T source, T target, boolean isEnd);

        /**
         * 元素删除时的回调
         */
        void onElementDelete(int delPos, T source, T target, boolean isEnd);

        /**
         * 处理后的回调
         */
        void onFinal(int distance);
    }

    private static class Info<T> {

        Node[][] matrix;

        private T[] source;
        private T[] target;

        // 坐标系切换的偏移量
        private int sOffset = -1;
        private int tOffset = -1;

        private IWeightProvider<T> weight;

        /**
         * @param sRange 可空，表示不限制范围
         * @param tRange 可空，表示不限制范围
         */
        Info(T[] source, T[] target, Range sRange, Range tRange, IWeightProvider<T> weight) {
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
            this.weight = weight;
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

        Node withVAdd(Node node) {
            return node.withVDelta(weight.getAddAction() + weight.getAddElement(target[node.tIndexInArr]));
        }

        /**
         * 与指定node左上角的node对比
         */
        Node withVModify(Node node) {
            return node.withVDelta(weight.getModifyAction() + weight.getModifyElement(source[node.sIndexInArr], target[node.tIndexInArr]));
        }

        Node withVDelete(Node node) {
            return node.withVDelta(weight.getDeleteAction() + weight.getDeleteElement(source[node.sIndexInArr]));
        }

        private void init() {
            // 原点
            matrix[0][0] = new Node(0, sOffset, tOffset, null);
            // 横向初始化
            Node node = matrix[0][0];
            for (int i = 1; i < matrix[0].length; i++) {
                node = matrix[0][i] = withVDelete(node.createRight());
            }
            // 纵向初始化
            node = matrix[0][0];
            for (int i = 1; i < matrix.length; i++) {
                node = matrix[i][0] = withVAdd(node.createBottom());
            }
        }
    }

    /**
     * 节点，使用数组下标，同时是入参数组的位点坐标
     */
    private static class Node {
        int value;
        int sIndexInArr;
        int tIndexInArr;

        Node pre;

        Node(int value, int sIndexInArr, int tIndexInArr, Node pre) {
            this.value = value;
            this.sIndexInArr = sIndexInArr;
            this.tIndexInArr = tIndexInArr;
            this.pre = pre;
        }

        boolean lt(Node node) {
            return value < node.value;
        }

        Node withVDelta(int delta) {
            value += delta;
            return this;
        }

        Node createRight() {
            return createNext(1, 0);
        }

        Node createBottom() {
            return createNext(0, 1);
        }

        Node createRightBottom() {
            return createNext(1, 1);
        }

        private Node createNext(int sDelta, int tDelta) {
            return new Node(value, sIndexInArr + sDelta, tIndexInArr + tDelta, this);
        }
    }
}
