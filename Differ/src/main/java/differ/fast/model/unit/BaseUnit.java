package differ.fast.model.unit;

import differ.fast.model.ICharIndexOwner;

/**
 * 文本的最小组成单位
 * <br>Created by Soybeany on 2019/9/11.
 */
public abstract class BaseUnit implements ICharIndexOwner {

    // ****************************************定位标识****************************************

    /**
     * 以字符计算的下标(开始)
     */
    public int charIndex;

    /**
     * 以字符计算的下标(结束)
     */
    public int charEndIndex;

    /**
     * 以单元计算的下标
     */
    public int unitIndex;

    // ****************************************内容标识****************************************

    /**
     * 文本内容
     */
    public String text;

    /**
     * 优先级
     */
    public byte priority;

    public BaseUnit(int charIndex, int unitIndex, byte priority) {
        this.charIndex = charIndex;
        this.unitIndex = unitIndex;
        this.priority = priority;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return text.equals(((BaseUnit) obj).text);
    }

    @Override
    public String toString() {
        return text.replaceAll("\\n", "\\\\n") + "(" + unitIndex + ")";
    }

    @Override
    public int getCharStartIndex() {
        return charIndex;
    }

    @Override
    public int getCharEndIndex() {
        return charEndIndex;
    }
}
