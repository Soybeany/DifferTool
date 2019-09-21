package differ;

import differ.fast.model.CompareResult;
import differ.fast.treat.ContentTraverser;

import java.util.LinkedList;

/**
 * <br>Created by Soybeany on 2019/9/15.
 */
public class StructImpl {

    // todo 额外测试场景：低优先级开头、全文只有低优先级
//    private static String textA = "what is that thing, \nwhat about that guy,\n who are you?";
    private static String textB = "what can I do for you? is that guy who talking?";
    private static String textA = "what is that thing, \nwhat about that guy,\n who are you?";
//    private static String textB = "that guy who talking?";

    public static void main(String[] args) {
        LinkedList<CompareResult> results = ContentTraverser.getRawResult(textB, textA);
        int a = 2;
    }
}
