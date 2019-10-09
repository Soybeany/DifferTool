package differ.fast.utils;

import differ.fast.model.Range;

/**
 * 改良的 莱文斯坦(距离) 工具类
 * 支持长数组，因能对长数组进行分段比较
 * <br>Created by Soybeany on 2019/9/27.
 */
public class ImprovedLSUtils {

    private static final int SECTION_LENGTH = 5; // 每个分段的长度
    private static final int OFFSET = SECTION_LENGTH + 1;

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(T[] source, T[] target, LevenshteinUtils.ICallback<T> callback) {
        // 若长度均没有超过阈值，则直接使用经典算法进行计算
        if (source.length <= SECTION_LENGTH && target.length <= SECTION_LENGTH) {
            LevenshteinUtils.compare(source, target, callback);
            return;
        }
        // 范围中的循环
        int sourceMaxIndex = source.length - 1;
        int targetMaxIndex = target.length - 1;
        Range sRange = new Range().setup(0, Math.min(sourceMaxIndex, SECTION_LENGTH));
        Range tRange = new Range().setup(0, Math.min(targetMaxIndex, SECTION_LENGTH));
        boolean hasSContent, hasTContent;
        CallbackWrapper<T> callbackWrapper = new CallbackWrapper<>(callback);
        callback.onStart();
        while ((hasSContent = sRange.from < source.length) | (hasTContent = tRange.from < target.length)) {
            // 特殊处理
            if (!hasSContent) { // 数据源已遍历完
                for (int i = tRange.from; i < target.length; i++) {
                    callbackWrapper.onElementAdd(sRange.from, source[sourceMaxIndex], target[i]);
                }
                break;
            }
            if (!hasTContent) { // 目标已遍历完
                for (int i = sRange.from; i < source.length; i++) {
                    callbackWrapper.onElementDelete(tRange.from, source[i], target[targetMaxIndex]);
                }
                break;
            }
            // 常规处理
            LevenshteinUtils.compare(source, target, sRange, tRange, callbackWrapper);
            // 偏移
            sRange.shift(OFFSET, source.length, sourceMaxIndex);
            tRange.shift(OFFSET, target.length, targetMaxIndex);
        }
        callback.onFinal(callbackWrapper.distance);
    }

    private static class CallbackWrapper<T> implements LevenshteinUtils.ICallback<T> {
        private LevenshteinUtils.ICallback<T> mCallback;
        int distance;

        CallbackWrapper(LevenshteinUtils.ICallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onStart() {
            // 留空
        }

        @Override
        public void onElementSame(T source, T target) {
            mCallback.onElementSame(source, target);
        }

        @Override
        public void onElementAdd(int addPos, T source, T target) {
            mCallback.onElementAdd(addPos, source, target);
        }

        @Override
        public void onElementModify(T source, T target) {
            mCallback.onElementModify(source, target);
        }

        @Override
        public void onElementDelete(int delPos, T source, T target) {
            mCallback.onElementDelete(delPos, source, target);
        }

        @Override
        public void onFinal(int distance) {
            this.distance += distance;
        }
    }
}
