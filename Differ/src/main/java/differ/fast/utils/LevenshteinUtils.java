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

    // ****************************************公开方法****************************************

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
        int instance = calculate(info, sourceIndex, targetIndex);
        LinkedList<Change> changes = new LinkedList<>();
        traverse(info, changes, sourceIndex, targetIndex);
        return new Result(changes, instance, input1.length, input2.length);
    }

    // ****************************************内部方法****************************************

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
        int tmpValue = value - 1, type;
        Integer left = info.getValue(sourceIndex - 1, targetIndex);
        Integer top = info.getValue(sourceIndex, targetIndex - 1);
        // “删除”操作
        if (null == left || left == tmpValue) {
            type = Change.DELETE;
            traverse(info, result, sourceIndex - 1, targetIndex);
        }
        // “新增”操作
        else if (null == top || top == tmpValue) {
            type = Change.ADD;
            traverse(info, result, sourceIndex, targetIndex - 1);
        }
        // “修改”操作
        else {
            type = Change.MODIFY;
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
        }
        // 保存变更
        saveChange(result, type, sourceIndex, targetIndex);
    }

    /**
     * 保存变更
     */
    private static void saveChange(LinkedList<Change> result, int type, int sourceIndex, int targetIndex) {
        Change change = getSuitableChange(result, type, sourceIndex, targetIndex);
        switch (type) {
            case Change.DELETE:
                change.source.to = sourceIndex + 1;
                break;
            case Change.ADD:
                change.target.to = targetIndex + 1;
                break;
            case Change.MODIFY:
                change.source.to = sourceIndex + 1;
                change.target.to = targetIndex + 1;
                break;
        }
    }

    /**
     * 获得合适的变更(复用/新增)
     */
    private static Change getSuitableChange(LinkedList<Change> result, int type, int sourceIndex, int targetIndex) {
        Change change = result.isEmpty() ? null : result.getLast();
        if (null != change && change.type == type && isChangeContinuous(change, type, sourceIndex, targetIndex)) {
            return change;
        }
        change = new Change(type, new Range(), new Range());
        result.add(change);
        // 初始化设置
        switch (type) {
            case Change.DELETE:
                change.source.from = sourceIndex;
                change.target.with0Length(targetIndex + 1);
                break;
            case Change.ADD:
                change.target.from = targetIndex;
                change.source.with0Length(sourceIndex + 1);
                break;
            case Change.MODIFY:
                change.source.from = sourceIndex;
                change.target.from = targetIndex;
                break;
        }
        return change;
    }

    /**
     * 判断变更是否连续
     */
    private static boolean isChangeContinuous(Change change, int type, int sourceIndex, int targetIndex) {
        switch (type) {
            case Change.DELETE:
                return change.source.to == sourceIndex;
            case Change.ADD:
                return change.target.to == targetIndex;
            case Change.MODIFY:
                return change.source.to == sourceIndex && change.target.to == targetIndex;
        }
        return false;
    }

    // ****************************************内部类****************************************

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

        Result(List<Change> changes, int instance, int length1, int length2) {
            this.changes = changes;
            this.distance = instance;
            this.similarity = distance * 1.0f / Math.max(length1, length2);
        }
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
}
