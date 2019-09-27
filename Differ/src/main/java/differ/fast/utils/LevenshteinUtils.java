package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

import java.util.LinkedList;
import java.util.List;

/**
 * 传统 莱文斯坦(距离) 工具类
 * <br>Created by Soybeany on 2019/9/27.
 */
public class LevenshteinUtils {

    public static <T> Result compare(T[] input1, T[] input2) {
        return compare(input1, input2, null, null);
    }

    /**
     * 对比指定的两个数组
     */
    public static <T> Result compare(T[] input1, T[] input2, Range sRange, Range tRange) {
        Info<T> info = new Info<>(input1, input2, sRange, tRange);
        int sourceIndex = (null != sRange ? sRange.to : input1.length) - 1;
        int targetIndex = (null != tRange ? tRange.to : input2.length) - 1;
        calculate(info, sourceIndex, targetIndex);
        LinkedList<Change> changes = new LinkedList<>();
        traverse(info, changes, sourceIndex, targetIndex);
        return new Result(changes, input1.length, input2.length);
    }

    private static <T> Integer calculate(Info<T> info, int sourceIndex, int targetIndex) {
        Integer value = info.getValue(sourceIndex, targetIndex);
        if (null == value) {
            Integer leftTop = calculate(info, sourceIndex - 1, targetIndex - 1);
            // 若元素相等，取矩阵左上角的值
            if (info.isElementEqual(sourceIndex, targetIndex)) {
                value = leftTop;
            }
            // 不相等，则取左、左上角、上三者的最小值，然后+1；
            else {
                Integer left = calculate(info, sourceIndex - 1, targetIndex);
                Integer top = calculate(info, sourceIndex, targetIndex - 1);
                value = Math.min(leftTop, Math.min(left, top)) + 1;
            }
            info.setValue(sourceIndex, targetIndex, value);
        }
        return value;
    }

    private static <T> void traverse(Info<T> info, LinkedList<Change> result, int sourceIndex, int targetIndex) {
        Integer value = info.getValue(sourceIndex, targetIndex);
        if (0 == value) {
            return;
        }
        // 值相等的不处理
        if (info.isElementEqual(sourceIndex, targetIndex)) {
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
            return;
        }
        // 不相等，按需走
        int tmpValue = value - 1;
        Change change = result.isEmpty() ? null : result.getFirst();
        Integer left = info.getValue(sourceIndex - 1, targetIndex);
        Integer top = info.getValue(sourceIndex, targetIndex - 1);
        // “删除”操作
        if (null == left || left == tmpValue) {
            if (null == change || Change.DELETE != change.type) {
                change = new Change(Change.DELETE, new Range(), null);
            }
            change.source.setupWith1Offset(sourceIndex);
            traverse(info, result, sourceIndex - 1, targetIndex);
        }
        // “新增”操作
        else if (null == top || top == tmpValue) {
            if (null == change || Change.ADD != change.type) {
                change = new Change(Change.ADD, null, new Range());
            }
            change.target.setupWith1Offset(targetIndex);
            traverse(info, result, sourceIndex, targetIndex - 1);
        }
        // “修改”操作
        else {
            if (null == change || Change.MODIFY != change.type) {
                change = new Change(Change.MODIFY, new Range(), new Range());
            }
            change.source.setupWith1Offset(sourceIndex);
            change.target.setupWith1Offset(targetIndex);
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
        }
        result.add(change);
    }

    private static class Info<T> {
        T[] source;
        T[] target;
        Integer[][] matrix;

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
            matrix = new Integer[tLength + 1][sLength + 1];
            init();
        }

        boolean isElementEqual(int sourceIndex, int targetIndex) {
            if (sourceIndex >= 0 && targetIndex >= 0) {
                return source[sourceIndex].equals(target[targetIndex]);
            }
            return false;
        }

        Integer getValue(int sourceIndex, int targetIndex) {
            int sIndex = sourceIndex - sFromIndex + 1;
            int tIndex = targetIndex - tFromIndex + 1;
            return (sIndex >= 0 && tIndex >= 0) ? matrix[tIndex][sIndex] : null;
        }

        void setValue(int sourceIndex, int targetIndex, Integer value) {
            matrix[targetIndex - tFromIndex + 1][sourceIndex - sFromIndex + 1] = value;
        }

        private void init() {
            // 原点
            matrix[0][0] = 0;
            // 横向初始化
            for (int i = 1; i < matrix[0].length; i++) {
                matrix[0][i] = i;
            }
            // 纵向初始化
            for (int i = 1; i < matrix.length; i++) {
                matrix[i][0] = i;
            }
        }
    }

    public static class Result {
        /**
         * 变更的内容列表
         */
        public final List<Change> changes;

        /**
         * 莱文斯坦距离
         */
        public final int distance;

        /**
         * 相似度，范围0~1
         */
        public final float similarity;

        Result(List<Change> changes, int length1, int length2) {
            this.changes = changes;
            this.distance = changes.size();
            this.similarity = distance * 1.0f / Math.max(length1, length2);
        }
    }
}
