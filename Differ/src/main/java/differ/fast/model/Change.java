package differ.fast.model;

/**
 * 内容的变动
 */
public abstract class Change {
    public static final byte UNDEFINED = -1; // 未定义
    public static final byte SAME = 0; // 相等
    public static final byte ADD = 1; // 增加
    public static final byte MODIFY = 2; // 修改
    public static final byte DELETE = 3; // 删除

    /**
     * 变更类型
     */
    public byte type = UNDEFINED;

    /**
     * 使用对象表示
     */
    public static class Obj<T> extends Change {

        /**
         * 是否末尾位点，只在表示增删时有意义
         */
        public boolean isPosAtEnd;

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

        public Obj(byte type, boolean isPosAtEnd, T source, T target) {
            this(type, source, target);
            this.isPosAtEnd = isPosAtEnd;
        }

        @Override
        public String toString() {
            return type + "(" + isPosAtEnd + ")  " + source + "  " + target;
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
    }
}
