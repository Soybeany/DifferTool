package differ.fast.model;

/**
 * 内容的变动
 */
public abstract class Change {
    public static final byte ADD = 1; // 增加
    public static final byte MODIFY = 2; // 修改
    public static final byte DELETE = 3; // 删除

    /**
     * 变更类型
     */
    public byte type;

    /**
     * 使用对象表示
     */
    public static class Obj<T> extends Change {

        /**
         * 数据源
         */
        public T source;

        /**
         * 目标
         */
        public T target;

        public Obj(byte type, T source, T target) {
            this.type = type;
            this.source = source;
            this.target = target;
        }

        @Override
        public String toString() {
            return type + "  " + source + "  " + target;
        }
    }

    /**
     * 使用下标表示
     */
    public static class Index extends Change {

        /**
         * 所包含变动的数目
         */
        public int count = 1;

        /**
         * 数据源
         */
        public Range source;

        /**
         * 目标
         */
        public Range target;

        public Index(byte type, Range source, Range target) {
            this.type = type;
            this.source = source;
            this.target = target;
        }

        /**
         * 判断变更是否连续
         */
        public boolean isChangeContinuous(int sourceIndex, int targetIndex) {
            return source.to == sourceIndex && target.to == targetIndex;
        }
    }
}
