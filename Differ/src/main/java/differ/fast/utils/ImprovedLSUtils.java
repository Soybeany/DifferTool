package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

import java.util.LinkedList;
import java.util.List;

/**
 * 改良的 莱文斯坦(距离) 工具类
 * 支持长数组，因能对长数组进行分段比较
 * <br>Created by Soybeany on 2019/9/27.
 */
public class ImprovedLSUtils {

    private static final int SECTION_LENGTH = 50; // 每个分段的长度，必须大于0

    /**
     * 对比指定的两个数组
     */
    public static <T> void compare(T[] source, T[] target, ICallback<T> callback) {
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
                    callbackWrapper.onElementAdd(sRange.from, source[sourceMaxIndex], target[i], true);
                }
                break;
            }
            if (!hasTContent) { // 目标已遍历完
                for (int i = sRange.from + sOffset; i < source.length; i++) {
                    callbackWrapper.onElementDelete(tRange.from, source[i], target[targetMaxIndex], true);
                }
                break;
            }
            // 常规处理
            LevenshteinUtils.compare(source, target, sRange, tRange, callbackWrapper);
            // 将末尾的增删移到下一次
            sOffset = callbackWrapper.getRollbackSize(Change.DELETE);
            tOffset = callbackWrapper.getRollbackSize(Change.ADD);
            // 偏移
            sRange.shift(SECTION_LENGTH - sOffset, source.length, sourceMaxIndex);
            tRange.shift(SECTION_LENGTH - tOffset, target.length, targetMaxIndex);
        }
        callbackWrapper.flushEndChange();
        callback.onFinal(callbackWrapper.distance);
    }

    public interface ICallback<T> {
        /**
         * 处理前的回调
         */
        void onStart();

        /**
         * 元素处理后的回调
         */
        void onElementHandled(LinkedList<Change.Obj<T>> objs);

        /**
         * 处理后的回调
         */
        void onFinal(int distance);
    }

    private static class ChangeInfo<T> {
        byte changeType; // 变更类型
        LinkedList<Change.Obj<T>> changes;

        void reset() {
            changeType = Change.UNDEFINED;
            changes = null;
        }
    }

    private static class CallbackWrapper<T> implements LevenshteinUtils.ICallback<T> {
        private ICallback<T> mCallback;
        private final ChangeInfo<T> mInfo = new ChangeInfo<>();
        private final ChangeInfo<T> mEndInfo = new ChangeInfo<>();

        int distance;

        CallbackWrapper(ICallback<T> callback) {
            mCallback = callback;
        }

        @Override
        public void onStart() {
            mInfo.reset();
            mEndInfo.reset();
        }

        @Override
        public void onElementSame(T source, T target, boolean isEnd) {
            getChangeListAndFlush(Change.SAME).add(new Change.Obj<T>(Change.SAME, source, target));
        }

        @Override
        public void onElementAdd(int addPos, T source, T target, boolean isEnd) {
            Change.Obj<T> obj = new Change.Obj<>(Change.ADD, addPos > 0, source, target);
            if (isEnd) {
                cacheEndChange(Change.ADD, obj);
            } else {
                getChangeListAndFlush(Change.ADD).add(obj);
            }
        }

        @Override
        public void onElementModify(T source, T target, boolean isEnd) {
            getChangeListAndFlush(Change.MODIFY).add(new Change.Obj<T>(Change.MODIFY, source, target));
        }

        @Override
        public void onElementDelete(int delPos, T source, T target, boolean isEnd) {
            Change.Obj<T> obj = new Change.Obj<>(Change.DELETE, delPos > 0, source, target);
            if (isEnd) {
                cacheEndChange(Change.DELETE, obj);
            } else {
                getChangeListAndFlush(Change.DELETE).add(obj);
            }
        }

        @Override
        public void onFinal(int distance) {
            flush(mInfo);
            this.distance += distance;
        }

        int getRollbackSize(byte changeType) {
            if (mEndInfo.changeType != changeType) {
                return 0;
            }
            return mEndInfo.changes.size();
        }

        void flushEndChange() {
            flush(mEndInfo);
        }

        /**
         * 缓存末尾的变更
         */
        private void cacheEndChange(byte changeType, Change.Obj<T> obj) {
            if (mInfo.changeType == changeType) {
                mEndInfo.changes = mInfo.changes;
                mInfo.changeType = Change.UNDEFINED;
            }
            if (mEndInfo.changes == null) {
                mEndInfo.changes = new LinkedList<>();
            }
            mEndInfo.changes.add(obj);
            mEndInfo.changeType = changeType;
        }

        /**
         * 获得变更列表，并按需将变更冲刷到目标类
         */
        private List<Change.Obj<T>> getChangeListAndFlush(byte changeType) {
            // 若能重用则重用变更
            if (mInfo.changeType == changeType) {
                return mInfo.changes;
            }
            // 冲刷结果到目标类
            flush(mInfo);
            // 设置属性
            mInfo.changeType = changeType;
            return mInfo.changes = new LinkedList<>();
        }

        /**
         * 冲刷结果
         */
        private void flush(ChangeInfo<T> info) {
            if (Change.UNDEFINED == info.changeType) {
                return;
            }
            mCallback.onElementHandled(info.changes);
        }
    }
}
