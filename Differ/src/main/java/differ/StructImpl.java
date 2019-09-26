package differ;

import differ.fast.model.CompareResult;
import differ.fast.treat.ContentTraverser;
import differ.fast.treat.SectionUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/9/15.
 */
public class StructImpl {

    // todo 额外测试场景：低优先级开头、全文只有低优先级
    private static String textA = "what is that thing, \nwhat about that guy,\n who are you?";
    private static String textB = "what can I do for you? is that guy who talking?";
//    private static String textA = "what can I do for you? what is your problem? I don't known what happen. is that guy who talking?";
//    private static String textA = "what is that thing, \nwhat about that guy,\n who are you?";
//    private static String textB = "that guy who talking?";

    @Test
    public void test() throws IOException {
        TimeSpendRecorder.INSTANCE.start();
//        List<CompareResult> results = ContentTraverser.getResult(textA, textB);
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\test.txt"), loadFile("D:\\test2.txt"));
        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\x1.txt"), loadFile("D:\\x2.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\test.txt"), loadFile("D:\\test3.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\newTest1.txt"), loadFile("D:\\newTest2.txt"));
        List<CompareResult> sections = SectionUtils.toSections(results);
        TimeSpendRecorder.INSTANCE.stop();
        int a = 2;
    }

    private static String loadFile(String filePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while (null != (line = reader.readLine())) {
                builder.append(line).append("\n");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
}
