package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 改良的 莱文斯坦(距离) 工具类
 * 支持长数组，因能对长数组进行分段比较
 * <br>Created by Soybeany on 2019/9/27.
 */
public class ImprovedLSUtils {

    private static final int SECTION_LENGTH = 50; // 每个分段的长度，必须大于0

    @SuppressWarnings("unchecked")
    public static <T> void compare(T[] source, T[] target, ICallback<T> callback) {
        compare(source, target, IWeightProvider.Std.get(), callback);
    }

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(T[] source, T[] target, IWeightProvider<T> weight, ICallback<T> callback) {
        // 变量初始化
        int sourceMaxIndex = source.length - 1, targetMaxIndex = target.length - 1;
        Range sRange = new Range().setup(0, Math.min(sourceMaxIndex, SECTION_LENGTH - 1));
        Range tRange = new Range().setup(0, Math.min(targetMaxIndex, SECTION_LENGTH - 1));
        boolean hasSContent, hasTContent;
        int sOffset = 0, tOffset = 0;
        CallbackWrapper<T> callbackWrapper = new CallbackWrapper<>(callback);
        // 开始处理
        callback.onStart();
        while ((hasSContent = sRange.from < source.length) | (hasTContent = tRange.from < target.length)) {
            // 特殊处理
            if (!hasSContent) { // 数据源已遍历完
                for (int i = tRange.from + tOffset; i < target.length; i++) {
                    callbackWrapper.onElementAdd(sRange.from, source[sourceMaxIndex], target[i]);
                }
                break;
            }
            if (!hasTContent) { // 目标已遍历完
                for (int i = sRange.from + sOffset; i < source.length; i++) {
                    callbackWrapper.onElementDelete(tRange.from, source[i], target[targetMaxIndex]);
                }
                break;
            }
            // 常规处理
            LevenshteinUtils.compare(source, target, sRange, tRange, weight, callbackWrapper);
            // 将末尾的增删移到下一次
            sOffset = callbackWrapper.getRollback(Change.DELETE);
            tOffset = callbackWrapper.getRollback(Change.ADD);
            // 偏移
            sRange.shift(SECTION_LENGTH - sOffset, source.length, sourceMaxIndex);
            tRange.shift(SECTION_LENGTH - tOffset, target.length, targetMaxIndex);
        }
        callbackWrapper.flushAllChanges();
        callback.onFinal();
    }

    public interface ICallback<T> {
        /**
         * 处理前的回调
         */
        void onStart();

        /**
         * 元素相同的回调
         */
        void onElementSame(LinkedList<Change.Obj<T>> objs);

        /**
         * 元素改变的回调
         */
        void onElementChange(int changeType, LinkedList<Change.Obj<T>> objs);

        /**
         * 处理后的回调
         */
        void onFinal();
    }

    private static class ChangeInfo<T> {
        int changeType; // 变更类型
        final LinkedList<Change.Obj<T>> changes = new LinkedList<>();

        ChangeInfo(int changeType) {
            this.changeType = changeType;
        }
    }

    private static class CallbackWrapper<T> implements LevenshteinUtils.ICallback<T> {
        private ICallback<T> mCallback;

        private final LinkedList<ChangeInfo<T>> mChanges = new LinkedList<>();
        private ChangeInfo<T> mEndInfo;
        private ChangeInfo<T> mLastInfo;

        CallbackWrapper(ICallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onStart() {
            // 清理上一次的变更，以免造成影响
            mChanges.clear();
            mEndInfo = null;
            mLastInfo = null;
        }

        @Override
        public void onElementSame(T source, T target) {
            getChangeList(Change.SAME).add(new Change.Obj<T>(Change.SAME, source, target));
        }

        @Override
        public void onElementAdd(int addPos, T source, T target) {
            getChangeList(Change.ADD).add(new Change.Obj<>(Change.ADD, addPos > 0, source, target));
        }

        @Override
        public void onElementModify(T source, T target) {
            getChangeList(Change.MODIFY).add(new Change.Obj<T>(Change.MODIFY, source, target));
        }

        @Override
        public void onElementDelete(int delPos, T source, T target) {
            getChangeList(Change.DELETE).add(new Change.Obj<>(Change.DELETE, delPos > 0, source, target));
        }

        @Override
        public void onFinal(int distance, int sSize, int tSize) {
            // 缓存最后的变更
            cacheEndChange();
            // 冲刷变更
            flushChanges();
        }

        int getRollback(int changeType) {
            if (null == mEndInfo || mEndInfo.changeType != changeType) {
                return 0;
            }
            return mEndInfo.changes.size();
        }

        void flushAllChanges() {
            if (null != mEndInfo) {
                flushChange(mEndInfo);
            }
            flushChanges();
        }

        /**
         * 缓存末尾的变更
         */
        private void cacheEndChange() {
            switch (mLastInfo.changeType) {
                case Change.ADD:
                case Change.DELETE:
                    mEndInfo = mLastInfo;
                    mChanges.removeLast();
                    break;
            }
        }

        /**
         * 获得变更列表(重用/新建)
         */
        private List<Change.Obj<T>> getChangeList(int changeType) {
            if (null == mLastInfo || mLastInfo.changeType != changeType) {
                mChanges.add(mLastInfo = new ChangeInfo<>(changeType));
            }
            return mLastInfo.changes;
        }

        /**
         * 冲刷变更列表
         */
        private void flushChanges() {
            Iterator<ChangeInfo<T>> iterator = mChanges.iterator();
            while (iterator.hasNext()) {
                flushChange(iterator.next());
                iterator.remove();
            }
        }

        /**
         * 冲刷变更
         */
        private void flushChange(ChangeInfo<T> info) {
            if (Change.SAME == info.changeType) {
                mCallback.onElementSame(info.changes);
            } else {
                mCallback.onElementChange(info.changeType, info.changes);
            }
        }
    }
}
