package Levenshtein;

/**
 * 双行的实现，节省内存
 * <br>Created by Soybeany on 2019/9/26.
 */
public class TwoLineImpl<T> implements IDifferTool<T> {

    @Override
    public int calculate(T[] input1, T[] input2) {
        Info<T> info = new Info<>(input1, input2);
        // 初始化第一行
        for (int targetIndex = 0; targetIndex < info.matrix[0].length; targetIndex++) {
            info.matrix[0][targetIndex] = targetIndex;
        }
        // 遍历
        int value;
        for (int targetIndex = 0; targetIndex < input2.length; targetIndex++) {
            info.setValue(-1, targetIndex, targetIndex + 1);
            for (int sourceIndex = 0; sourceIndex < input1.length; sourceIndex++) {
                int leftTop = info.getValue(sourceIndex - 1, targetIndex - 1);
                // 元素相等，取左上角的值
                if (info.isElementEqual(sourceIndex, targetIndex)) {
                    value = leftTop;
                }
                // 不相等，则取左、左上角、上三者的最小值，然后+1；
                else {
                    int left = info.getValue(sourceIndex - 1, targetIndex);
                    int top = info.getValue(sourceIndex, targetIndex - 1);
                    value = Math.min(leftTop, Math.min(left, top)) + 1;
                }
                info.setValue(sourceIndex, targetIndex, value);
            }
        }

        return info.getValue(input1.length - 1, input2.length - 1);
    }

    private static class Info<T> {
        T[] source;
        T[] target;
        int[][] matrix = new int[2][]; // 矩阵

        Info(T[] source, T[] target) {
            this.source = source;
            this.target = target;
            init();
        }

        boolean isElementEqual(int sourceIndex, int targetIndex) {
            return source[sourceIndex].equals(target[targetIndex]);
        }

        Integer getValue(int sourceIndex, int targetIndex) {
            return matrix[(targetIndex + 1) % 2][sourceIndex + 1];
        }

        void setValue(int sourceIndex, int targetIndex, Integer value) {
            matrix[(targetIndex + 1) % 2][sourceIndex + 1] = value;
        }

        private void init() {
            int length = source.length + 1;
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = new int[length];
            }
        }
    }
}
