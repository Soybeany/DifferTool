package differ.fast.pretreat;

/**
 * 优先级工具类
 * <br>Created by Soybeany on 2019/9/11.
 */
public class PriorityUtils {

    public static final int SEPARATE_SINGLE = 1;
    public static final int SEPARATE_GROUP = 2;

    private static final int SEPARATE_TYPE_SHIFT = 2;
    public static final int SEPARATE_SWITCHER = (1 << SEPARATE_TYPE_SHIFT) - 1;

    public static final int PRIORITY_CN_CHAR = (6 << SEPARATE_TYPE_SHIFT) + SEPARATE_SINGLE;
    public static final int PRIORITY_EN_LETTER = (5 << SEPARATE_TYPE_SHIFT) + SEPARATE_GROUP;
    public static final int PRIORITY_NUM = (4 << SEPARATE_TYPE_SHIFT) + SEPARATE_GROUP;
    public static final int PRIORITY_OTHER = (3 << SEPARATE_TYPE_SHIFT) + SEPARATE_GROUP;
    public static final int PRIORITY_EN_SYMBOL = (2 << SEPARATE_TYPE_SHIFT) + SEPARATE_GROUP;
    public static final int PRIORITY_SPACE = (1 << SEPARATE_TYPE_SHIFT) + SEPARATE_GROUP;
    public static final int PRIORITY_NEWLINE = SEPARATE_GROUP;

    static {
        System.out.println("cn_char:" + PRIORITY_CN_CHAR + " en_letter:" + PRIORITY_EN_LETTER + " num:" + PRIORITY_NUM + " other:" + PRIORITY_OTHER
                + "\nen_symbol:" + PRIORITY_EN_SYMBOL + " space:" + PRIORITY_SPACE + " newline:" + PRIORITY_NEWLINE);
    }

    /**
     * 获得字符的优先级
     */
    public static int getPriority(char c) {
        if (isEnLetter(c)) {
            return PRIORITY_EN_LETTER;
        } else if (isSpace(c)) {
            return PRIORITY_SPACE;
        } else if (isNewline(c)) {
            return PRIORITY_NEWLINE;
        } else if (isEnSymbol(c)) {
            return PRIORITY_EN_SYMBOL;
        } else if (isNum(c)) {
            return PRIORITY_NUM;
        } else if (isCnChar(c)) {
            return PRIORITY_CN_CHAR;
        }
        return PRIORITY_OTHER;
    }

    /**
     * 判断指定优先级是否为 高优先级
     */
    public static boolean isHighPriority(int priority) {
        return priority >= PRIORITY_OTHER;
    }

    /**
     * 是否中文字符(文字+标点)
     */
    private static boolean isCnChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 是否英文字母
     */
    private static boolean isEnLetter(char c) {
        return 'a' <= c && c <= 'z' ||
                'A' <= c && c <= 'Z';
    }

    /**
     * 是否英文标点
     */
    private static boolean isEnSymbol(char c) {
        return '!' <= c && c <= '/' ||
                ':' <= c && c <= '@' ||
                '[' <= c && c <= '`' ||
                '{' <= c && c <= '~';
    }

    /**
     * 是否空格
     */
    private static boolean isSpace(char c) {
        return ' ' == c;
    }

    /**
     * 是否换行符
     */
    private static boolean isNewline(char c) {
        return 10 == c || 13 == c;
    }

    /**
     * 是否数字
     */
    private static boolean isNum(char c) {
        return '0' <= c && c <= '9';
    }
}
