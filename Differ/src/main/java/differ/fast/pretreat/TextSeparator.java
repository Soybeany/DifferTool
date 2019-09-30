package differ.fast.pretreat;

import differ.fast.model.Unit;

/**
 * 文本分隔者
 * <br>Created by Soybeany on 2019/9/11.
 */
public class TextSeparator {

    private String text;
    private int length;

    private int charIndex;
    private int unitIndex;

    private int partIndex;

    public TextSeparator(String text) {
        this.text = text;
        this.length = text.length();
    }

    /**
     * 获得下一单元
     *
     * @return 新拆分的单元，null则表示拆分完毕
     */
    public Unit getNextUnit() {
        // 若已到底，则不再创建
        if (charIndex >= length) {
            return null;
        }
        // 创建新单元
        char curC = text.charAt(charIndex);
        int priority = PriorityUtils.getPriority(curC);
        Unit unit = new Unit(charIndex, unitIndex++, partIndex, priority);
        int separateType = unit.priority & PriorityUtils.SEPARATE_SWITCHER;
        // 补充单元信息
        if (PriorityUtils.PRIORITY_NEWLINE == priority) {
            unit.paramIndex = ++partIndex;
            partIndex++;
        }
        // 单元分类
        switch (separateType) {
            case PriorityUtils.SEPARATE_SINGLE:
                unit.text = curC + "";
                charIndex++;
                return unit;
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
                return unit;
        }
        throw new RuntimeException("使用了未知的分隔类型");
    }
}
