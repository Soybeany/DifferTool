package Levenshtein;

import java.util.LinkedList;
import java.util.List;

/**
 * 递归实现(经典)
 * <br>Created by Soybeany on 2019/9/26.
 */
public class RecurrenceImpl<T> implements IDifferTool<T> {

    @Override
    public int calculate(T[] input1, T[] input2) {
        Info<T> info = new Info<>(input1, input2);
        int sourceIndex = input1.length - 1;
        int targetIndex = input2.length - 1;
        Integer value = calculate(info, sourceIndex, targetIndex);
        LinkedList<String> list = new LinkedList<>();
        traverse(info, list, sourceIndex, targetIndex);
        return value;
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

    private static <T> void traverse(Info<T> info, List<String> result, int sourceIndex, int targetIndex) {
        Integer value = info.getValue(sourceIndex, targetIndex);
        if (-1 == sourceIndex || -1 == targetIndex) {
            return;
        }
        // 值相等的不处理
        int leftTop = info.getValue(sourceIndex - 1, targetIndex - 1);
        if (info.isElementEqual(sourceIndex, targetIndex)) {
//            result.add("相等:" + info.source[sourceIndex]);
            traverse(info, result, sourceIndex - 1, targetIndex - 1);
        }
        // 不相等，按需走
        else {
            int tmpValue = value - 1;
            int left = info.getValue(sourceIndex - 1, targetIndex);
            if (left == tmpValue) {
                result.add("source删除:" + info.source[sourceIndex]);
                traverse(info, result, sourceIndex - 1, targetIndex);
            } else if (leftTop == tmpValue) {
                result.add("source:" + info.source[sourceIndex] + "  target:" + info.target[targetIndex]);
                traverse(info, result, sourceIndex - 1, targetIndex - 1);
            } else {
                result.add("target增加:" + info.target[targetIndex]);
                traverse(info, result, sourceIndex, targetIndex - 1);
            }
        }
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

}
