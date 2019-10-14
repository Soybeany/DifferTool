package com.soybeany.differtool.compare;

import com.soybeany.differtool.model.Change;
import com.soybeany.differtool.model.Range;
import com.soybeany.differtool.utils.IWeightProvider;
import com.soybeany.differtool.utils.LevenshteinUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 对比工具实现类
 * 支持长数组，使用分段方式进行比较
 * <br>Created by Soybeany on 2019/9/27.
 */
public class CompareToolImpl implements ICompareTool {

    private int sectionLength;

    public CompareToolImpl(int sectionLength) {
        if (sectionLength < 1) {
            throw new RuntimeException("分段长度必须大于0");
        }
        this.sectionLength = sectionLength;
    }

    @Override
    public <T> void compare(T[] source, T[] target, IWeightProvider<T> weight, ICallback<T> callback) {
        // 变量初始化
        int sourceMaxIndex = source.length - 1, targetMaxIndex = target.length - 1;
        Range sRange = new Range().setup(0, Math.min(sourceMaxIndex, sectionLength - 1));
        Range tRange = new Range().setup(0, Math.min(targetMaxIndex, sectionLength - 1));
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
            sRange.shift(sectionLength - sOffset, source.length, sourceMaxIndex);
            tRange.shift(sectionLength - tOffset, target.length, targetMaxIndex);
        }
        callbackWrapper.flushAllChanges();
        callback.onFinal();
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
                default:
                    mLastInfo = null;
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
