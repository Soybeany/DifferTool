package com.soybeany.differtool.utils;

/**
 * <br>Created by Soybeany on 2019/10/12.
 */
public interface IWeightProvider<T> {

    // ****************************************操作****************************************

    int getAddAction();

    int getDeleteAction();

    int getModifyAction();

    // ****************************************元素变更****************************************

    int getAddElement(T ele);

    int getDeleteElement(T ele);

    int getModifyElement(T ele1, T ele2);

    /**
     * 标准实现
     */
    class Std implements IWeightProvider {
        private static final Std mInstance = new Std();

        public static Std get() {
            return mInstance;
        }

        @Override
        public int getAddAction() {
            return 1;
        }

        @Override
        public int getDeleteAction() {
            return 1;
        }

        @Override
        public int getModifyAction() {
            return 1;
        }

        @Override
        public int getAddElement(Object ele) {
            return 0;
        }

        @Override
        public int getDeleteElement(Object ele) {
            return 0;
        }

        @Override
        public int getModifyElement(Object ele1, Object ele2) {
            return 0;
        }
    }
}
