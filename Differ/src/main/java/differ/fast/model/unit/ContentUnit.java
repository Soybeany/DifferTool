package differ.fast.model.unit;

import differ.fast.model.IAffixOwner;
import differ.fast.pretreat.PriorityUtils;

/**
 * 内容单元
 * <br>Created by Soybeany on 2019/10/3.
 */
public class ContentUnit extends BaseUnit implements IAffixOwner {

    /**
     * 分隔单元，可能为null
     */
    public AffixUnit separateUnit;

    public static ContentUnit newEmptyContentUnit(int charIndex, int unitIndex) {
        ContentUnit unit = new ContentUnit(charIndex, unitIndex, PriorityUtils.PRIORITY_OTHER);
        unit.text = "";
        unit.charEndIndex = charIndex;
        return unit;
    }

    public ContentUnit(int charIndex, int unitIndex, byte priority) {
        super(charIndex, unitIndex, priority);
    }

    @Override
    public int getAffixCharStartIndex() {
        return charEndIndex;
    }

    @Override
    public int getAffixCharEndIndex() {
        return null != separateUnit ? separateUnit.charEndIndex : charEndIndex;
    }

    @Override
    public AffixUnit getAffix() {
        return separateUnit;
    }
}
