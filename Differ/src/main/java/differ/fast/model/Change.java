package differ.fast.model;

/**
 * 内容的变动
 */
public class Change {
    /**
     * 锚点的偏移
     */
    public static final int ANCHOR_OFFSET = 1;

    public static final int ADD = 1; // 增加
    public static final int MODIFY = 2; // 修改
    public static final int DELETE = 3; // 删除

    /**
     * 所包含变动的数目
     */
    public int count = 1;

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

    /**
     * 判断变更是否连续
     */
    public boolean isChangeContinuous(int type, int sourceIndex, int targetIndex) {
        switch (type) {
            case Change.DELETE:
                return source.to == sourceIndex && target.to == targetIndex + ANCHOR_OFFSET;
            case Change.ADD:
                return target.to == targetIndex && source.to == sourceIndex + ANCHOR_OFFSET;
            case Change.MODIFY:
                return source.to == sourceIndex && target.to == targetIndex;
        }
        return false;
    }
}
