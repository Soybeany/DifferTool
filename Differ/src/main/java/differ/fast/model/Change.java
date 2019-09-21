package differ.fast.model;

/**
 * 内容的变动
 */
public class Change {
    static final int ADD = 1; // 增加
    static final int MODIFY = 2; // 修改
    static final int DELETE = 3; // 删除

    /**
     * 变更类型
     */
    int type;

    /**
     * 数据源
     */
    Content source;

    /**
     * 目标
     */
    Content target;
}
