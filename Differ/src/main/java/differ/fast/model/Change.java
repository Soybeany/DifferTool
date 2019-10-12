package differ.fast.model;

/**
 * 内容的变动
 */
public abstract class Change {
    public static final int UNDEFINED = -1; // 未定义
    public static final int SAME = 0; // 相等
    public static final int ADD = 1; // 增加
    public static final int MODIFY = 2; // 修改
    public static final int DELETE = 3; // 删除

    /**
     * 变更类型
     */
    public int type = UNDEFINED;

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

        public Obj(int type, T source, T target) {
            this.type = type;
            this.source = source;
            this.target = target;
        }

        public Obj(int type, boolean isPosAtEnd, T source, T target) {
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

        public Index(int type, Range source, Range target) {
            this.type = type;
            this.source = source;
            this.target = target;
        }
    }
}
