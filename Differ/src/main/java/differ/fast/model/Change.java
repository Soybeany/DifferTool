package differ.fast.model;

/**
 * 内容的变动
 */
public class Change {
    public static final int ADD = 1; // 增加
    public static final int MODIFY = 2; // 修改
    public static final int DELETE = 3; // 删除

    /**
     * 变更类型
     */
    public final int type;

    /**
     * 数据源
     */
    public Range source;

    /**
     * 目标
     */
    public Range target;

    public Change(int type, Range source, Range target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }
}
