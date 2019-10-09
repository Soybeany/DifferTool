package differ.fast.pretreat;

import differ.fast.model.unit.AffixUnit;
import differ.fast.model.unit.BaseUnit;
import differ.fast.model.unit.ContentUnit;

/**
 * 单元提取器
 * <br>Created by Soybeany on 2019/9/11.
 */
public class UnitExtractor {

    public BaseUnit curUnit;
    public ContentUnit lastContentUnit;
    public final StringBuilder contentBuilder = new StringBuilder();
    public final StringBuilder splitBuilder = new StringBuilder();

    private String text;
    private int length;

    private int charIndex;
    private int unitIndex;

    public UnitExtractor(String text) {
        this.text = text;
        this.length = text.length();
    }

    /**
     * 获得下一单元
     *
     * @return 新拆分的单元，null则表示拆分完毕
     */
    public BaseUnit getNextUnit() {
        // 若已到底，则不再创建
        if (charIndex >= length) {
            return null;
        }
        // 创建新单元
        char curC = text.charAt(charIndex);
        byte priority = PriorityUtils.getPriority(curC);
        BaseUnit unit;
        ++unitIndex;
        if (PriorityUtils.isHighPriority(priority)) {
            unit = new ContentUnit(charIndex, unitIndex, priority);
        } else {
            unit = new AffixUnit(charIndex, unitIndex, priority);
        }
        // 单元分类
        switch (unit.priority & PriorityUtils.SEPARATE_SWITCHER) {
            case PriorityUtils.SEPARATE_SINGLE:
                unit.text = curC + "";
                charIndex++;
                break;
            case PriorityUtils.SEPARATE_GROUP:
                StringBuilder builder = new StringBuilder().append(curC);
                while (++charIndex < length) {
                    char c = text.charAt(charIndex);
                    if (unit.priority != PriorityUtils.getPriority(c)) {
                        break;
                    }
                    builder.append(c);
                }
                unit.text = builder.toString();
                break;
            default:
                throw new RuntimeException("使用了未知的分隔类型");
        }
        unit.charEndIndex = charIndex;
        return unit;
    }
}
