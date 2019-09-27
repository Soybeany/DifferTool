package Levenshtein;

import org.junit.Test;

/**
 * 计算莱文斯坦距离的工具类
 * <br>Created by Soybeany on 2019/9/26.
 */
public class DistanceUtils {

    @Test
    public void test() {
        IDifferTool<Character> tool = new TwoLineImpl<>();
        int distance = 0;
//        for (int i = 0; i < 10000; i++) {
        distance += tool.calculate(toCharArr("saturday"), toCharArr("sundays"));
//        }
        System.out.println("最长距离:" + distance);
    }

    private static Character[] toCharArr(String input) {
        int length = input.length();
        Character[] result = new Character[length];
        for (int i = 0; i < length; i++) {
            result[i] = input.charAt(i);
        }
        return result;
    }
}
