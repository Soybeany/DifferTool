package com.soybeany.differtool.compare;

import com.soybeany.differtool.model.Change;
import com.soybeany.differtool.utils.IWeightProvider;

import java.util.LinkedList;

/**
 * <br>Created by Soybeany on 2019/10/14.
 */
public interface ICompareTool {

    /**
     * 对比指定的两个数组
     */
    <T> void compare(T[] source, T[] target, IWeightProvider<T> weight, ICallback<T> callback);

    interface ICallback<T> {
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
}
