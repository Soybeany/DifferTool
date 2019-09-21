package differ.fast.model;

import java.util.List;

/**
 * 对比结果
 */
public class CompareResult {
    public int matchUnitCount; // 匹配的单元数

    public TerminalUnit source = new TerminalUnit();
    public TerminalUnit target = new TerminalUnit();

    @Override
    public String toString() {
        return source + " - " + target;
    }

    /**
     * 获得 源内容单元 的偏移
     *
     * @return 偏移量，若信息有缺失则返回null
     */
    public Integer getSourceContentUnitOffset(Unit unit) {
        if (null == source.from) {
            return null;
        }
        return source.from.contentUnitIndex - unit.contentUnitIndex;
    }

    /**
     * 获得 源内容单元 的偏移
     *
     * @return 偏移量，若信息有缺失则返回null
     */
    public Integer getSourceContentUnitOffset(CompareResult input) {
        if (null == source.from || null == input.source.to) {
            return null;
        }
        return source.from.contentUnitIndex - input.source.to.contentUnitIndex;
    }

    /**
     * 是否匹配类型
     */
    public boolean isTypeMatch() {
        return source.equals(target);
    }

    public boolean isTypeAdd() {
        return !source.isDefined() && target.isDefined();
    }

    public boolean isTypeModify() {
        return source.isDefined() && target.isDefined() && !isTypeMatch();
    }

    public boolean isTypeDelete() {
        return source.isDefined() && !target.isDefined();
    }

    /**
     * 减少匹配到的单元
     */
    public void decreaseMatchUnit(int count) {
        if (count >= matchUnitCount) {
            throw new RuntimeException("只能减少比匹配数少的单元");
        }
        matchUnitCount -= count;
        source.adjustEndUnit(-count);
        target.adjustEndUnit(-count);
    }

    /**
     * 获得变动列表
     */
    public List<Change> getChanges() {
        return null;
    }
}
