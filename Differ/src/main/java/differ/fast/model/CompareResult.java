package differ.fast.model;

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

    public Integer getNaturalOrderInSource() {
        return source.isDefined() ? source.from.unitIndex : null;
    }

    public Integer getNaturalOrderInTarget() {
        return target.isDefined() ? target.from.unitIndex : null;
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

}
