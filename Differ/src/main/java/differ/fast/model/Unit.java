package differ.fast.model;

/**
 * 由一段文本拆分而成的单元，主要分为ContentUnit与LowPriorityUnit
 * <br>Created by Soybeany on 2019/9/11.
 */
public class Unit implements Comparable<Unit> {

    // ****************************************定位标识****************************************

    /**
     * 以字符计算的下标
     */
    public int charIndex;

    /**
     * 以单元计算的下标
     */
    public int unitIndex;

    /**
     * 文本分段的下标(换行符作区分)
     */
    public int paramIndex;

    // ****************************************内容标识****************************************

    /**
     * 文本内容
     */
    public String text;

    /**
     * 优先级
     */
    public int priority;

    // ****************************************链接标识****************************************

    /**
     * 上一个单元
     */
    public Unit preUnit;

    /**
     * 下一个单元
     */
    public Unit nextUnit;

    public Unit(int charIndex, int unitIndex, int paramIndex, int priority) {
        this.charIndex = charIndex;
        this.unitIndex = unitIndex;
        this.paramIndex = paramIndex;
        this.priority = priority;
    }

    @Override
    public int compareTo(Unit o) {
        return unitIndex - o.unitIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Unit) {
            return text.equals(((Unit) obj).text);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return text.replaceAll("\\n", "\\\\n");
    }
}
