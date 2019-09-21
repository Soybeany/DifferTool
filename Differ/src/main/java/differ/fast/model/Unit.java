package differ.fast.model;

import differ.fast.pretreat.PriorityUtils;

import java.util.LinkedList;

/**
 * 由一段文本拆分而成的单元，主要分为ContentUnit与LowPriorityUnit
 * <br>Created by Soybeany on 2019/9/11.
 */
public class Unit {

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
    public int partIndex;

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

    // ****************************************内容单元特有****************************************

    /**
     * 以内容单元计算的下标(只有内容单元有值，否则为null)
     */
    public Integer contentUnitIndex;

    /**
     * 内容单元的列表(只有内容单元有值，否则为null)
     */
    public LinkedList<Unit> contentUnits;

    public Unit(int charIndex, int unitIndex, int partIndex, int priority) {
        this.charIndex = charIndex;
        this.unitIndex = unitIndex;
        this.partIndex = partIndex;
        this.priority = priority;
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
        return text + "(p:" + priority + " - ci:" + charIndex + " - ui:" + unitIndex + " - cui:" + contentUnitIndex + ")";
    }

    /**
     * 获得下一个内容单元
     */
    public Unit nextContentUnit() {
        Unit unit = nextUnit;
        while (null != unit && unit.isLowPriorityUnit()) {
            unit = unit.nextUnit;
        }
        return unit;
    }

    /**
     * 判断此单元是否为低优先级单元
     */
    public boolean isLowPriorityUnit() {
        return !PriorityUtils.isHighPriority(priority);
    }

    /**
     * 获得与指定单元有指定偏移的单元
     *
     * @param allowExceed 是否允许单元超出    若允许，超出了将返回null；否则，返回最后一个单元
     */
    public Unit getContentUnitWithOffset(int offset, boolean allowExceed) {
        if (isLowPriorityUnit()) {
            throw new RuntimeException("只有内容单元能调用此方法");
        }
        // 判断是否超出范围
        int index = contentUnitIndex + offset;
        if (index < contentUnits.size()) {
            return contentUnits.get(index);
        }
        return allowExceed ? null : contentUnits.getLast();
    }
}
