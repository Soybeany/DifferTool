package differ.fast.model;

/**
 * 文本的最小组成单位
 * <br>Created by Soybeany on 2019/9/11.
 */
public class Unit {

    /**
     * 以字符计算的下标(开始)
     */
    public int charStartIndex;

    /**
     * 以字符计算的下标(结束)，不包含
     */
    public int charEndIndex;

    /**
     * 以单元计算的下标
     */
    public int unitIndex;

    /**
     * 文本内容
     */
    public String text;

    /**
     * 优先级
     */
    public byte priority;

    public Unit(int charStartIndex, int unitIndex, byte priority) {
        this.charStartIndex = charStartIndex;
        this.unitIndex = unitIndex;
        this.priority = priority;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return text.equals(((Unit) obj).text);
    }

    @Override
    public String toString() {
        return text.replaceAll("\\n", "\\\\n") + "(" + unitIndex + ")";
    }

    /**
     * 单元长度(文本长度)
     */
    public int length() {
        return charEndIndex - charStartIndex;
    }
}
