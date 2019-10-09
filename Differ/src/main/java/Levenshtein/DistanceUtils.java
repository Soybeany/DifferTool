package Levenshtein;

import differ.fast.model.Range;
import org.junit.Test;

/**
 * 计算莱文斯坦距离的工具类
 * <br>Created by Soybeany on 2019/9/26.
 */
public class DistanceUtils {

    @Test
    public void test() {
//        String input1 = "just do your study?";
//        String input2 = "what about, just study";
        String input1 = "saturday";
        String input2 = "sundays";

        Character[] input1Arr = toCharArr(input1);
        Character[] input2Arr = toCharArr(input2);
//        }
        Range sRange = new Range().setup(0, 8);
        Range tRange = new Range().setup(0, 7);
//        LevenshteinUtils.Result result = LevenshteinUtils.compare(input1Arr, input2Arr);
//        for (int i = 0; i < 9000; i++) {
//        result = LevenshteinUtils.compare(input1Arr, input2Arr);
////            result = LevenshteinUtils.compare(input1Arr, input2Arr, sRange, tRange);
////        }
//        result.print(input1Arr, input2Arr);
//
//        System.out.println("最长距离:" + result.getDistance());
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
