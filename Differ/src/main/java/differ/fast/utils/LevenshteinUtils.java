package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Index;

import java.util.LinkedList;
import java.util.List;

/**
 * 传统 莱文斯坦(距离) 工具类
 * <br>Created by Soybeany on 2019/9/27.
 */
public class LevenshteinUtils {

    /**
     * 对比指定的两个数组
     */
    public static <T> Result compare(T[] input1, T[] input2) {
        Info<T> info = new Info<>(input1, input2);
        int sourceIndex = input1.length - 1;
        int targetIndex = input2.length - 1;
        Integer value = calculate(info, sourceIndex, targetIndex);
        LinkedList<Change> changes = new LinkedList<>();
        traverse(info, changes, sourceIndex, targetIndex);
        return new Result(changes, value * 1.0f / Math.max(input1.length, input2.length));
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
        if (-1 == sourceIndex || -1 == targetIndex) {
            return;
        }
        // 值相等的不处理
        int leftTop = info.getValue(sourceIndex - 1, targetIndex - 1);
        if (info.isElementEqual(sourceIndex, targetIndex)) {
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
            return;
        }
        // 不相等，按需走
        int tmpValue = value - 1;
        Change change = result.isEmpty() ? null : result.getFirst();
        int left = info.getValue(sourceIndex - 1, targetIndex);
        // “删除”操作
        if (left == tmpValue) {
            if (null == change || Change.DELETE != change.type) {
                change = new Change(Change.DELETE, new Index(sourceIndex), null);
            }
            change.source.to = sourceIndex + 1;
            traverse(info, result, sourceIndex - 1, targetIndex);
        }
        // “修改”操作
        else if (leftTop == tmpValue) {
            if (null == change || Change.MODIFY != change.type) {
                change = new Change(Change.MODIFY, new Index(sourceIndex), new Index(targetIndex));
            }
            change.source.to = sourceIndex + 1;
            change.target.to = targetIndex + 1;
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
        }
        // “新增”操作
        else {
            if (null == change || Change.ADD != change.type) {
                change = new Change(Change.ADD, null, new Index(targetIndex));
            }
            change.target.to = targetIndex + 1;
            traverse(info, result, sourceIndex, targetIndex - 1);
        }
        result.add(change);
    }

    private static class Info<T> {
        T[] source;
        T[] target;
        Integer[][] matrix;

        Info(T[] source, T[] target) {
            this.source = source;
            this.target = target;
            matrix = new Integer[target.length + 1][source.length + 1];
            init();
        }

        boolean isElementEqual(int sourceIndex, int targetIndex) {
            return source[sourceIndex].equals(target[targetIndex]);
        }

        Integer getValue(int sourceIndex, int targetIndex) {
            return matrix[targetIndex + 1][sourceIndex + 1];
        }

        void setValue(int sourceIndex, int targetIndex, Integer value) {
            matrix[targetIndex + 1][sourceIndex + 1] = value;
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
         * 相似度，范围0~1
         */
        public final float similarity;

        Result(List<Change> changes, float similarity) {
            this.changes = changes;
            this.similarity = similarity;
        }
    }
}
