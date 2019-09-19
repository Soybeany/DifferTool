package differ.other;

import differ.IDifferUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 被对比的字符串a  用于对比的字符串b
 * 从左到右遍历b，逐个字符在a中进行匹配
 * 若匹配，记录匹配到的第一个位置，b+1与a+1进行比较，若继续匹配，两者继续+1，
 * <br>Created by Soybeany on 2019/9/8.
 */
public class FastImpl implements IDifferUtil {

    private static String a = "你们真是非常好呀";
    private static String b = "你真的好呀是";

    public static void main(String[] args) {

    }

    private List<Result> mResults = new LinkedList<>();

    private Result mLastResult;

    @Override
    public String[] showDiffer(String input1, String input2) {

        char[] chars = input2.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int oldIndex = input1.indexOf(c);
            if (-1 != oldIndex) {
                if (null == mLastResult) {
                    mResults.add(mLastResult = new Result(oldIndex, i));
                }
            }

        }

        return new String[0];
    }

    private void differ(int fromIndex, String input, char c) {
        int oldIndex = input.indexOf(c, fromIndex);

    }

    static int TYPE_MATCH = 1;
    static int TYPE_ADD = 2;
    static int TYPE_REMOVE = 3;
    static int TYPE_MODIFY = 4;

    private static class Result {
        StringBuilder content = new StringBuilder();
        int oldIndex;
        int newIndex;
        int type;

        Result(int oldIndex, int newIndex) {
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
        }

        Result append(char c) {
            content.append(c);
            return this;
        }

        Result type(int type) {
            this.type = type;
            return this;
        }
    }
}
