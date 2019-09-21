package differ.fast.model;

/**
 * 终端单元
 */
public class TerminalUnit {
    public Unit from;
    public Unit to;

    public void singleUnit(Unit unit) {
        from = unit;
        to = unit;
    }

    @Override
    public String toString() {
        return isDefined() ? "[" + from.text + "~" + to.text + "]" : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TerminalUnit) {
            boolean isFromTheSame = null == from && null == ((TerminalUnit) obj).from ||
                    null != from && from.equals(((TerminalUnit) obj).from);
            boolean isToTheSame = null == to && null == ((TerminalUnit) obj).to ||
                    null != to && to.equals(((TerminalUnit) obj).to);
            return isFromTheSame && isToTheSame;
        }
        return super.equals(obj);
    }

    /**
     * 调整结束单元
     */
    public void adjustEndUnit(int offset) {
        to = to.getContentUnitWithOffset(offset, true);
    }

    /**
     * 是否已被定义，即赋值
     */
    public boolean isDefined() {
        return null != from;
    }
}
