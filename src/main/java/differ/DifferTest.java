package differ;

import differ.other.OtherImpl;

/**
 * <br>Created by Soybeany on 2019/9/8.
 */
public class DifferTest {

    private static IDifferUtil differUtil = new OtherImpl();

    public static void main(String[] args) {
        String a = "你们真是非常好呀";
        String b = "你真的好呀";
        String[] differ = differUtil.showDiffer(a, b);
    }

}
