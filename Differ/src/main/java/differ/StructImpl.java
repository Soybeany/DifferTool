package differ;

import differ.fast.model.Change;
import differ.fast.treat.DifferProcessor;
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

    private static String textA = "oh yes\nhey guy\nwhat can I do for you?\nwhat about that guy,\n who are you?";
    private static String textB = "what can I do for you?\n is that guy\n who talking?";

//    private static String textA = "b d";
//    private static String textB = "c d";

//    private static String textA = "what is that\n";
//    private static String textB = "about the fxxk";
//    private static String textA = "what can I do for you? what is your problem? I don't known what happen. is that guy who talking?";
//    private static String textA = "what is that thing,**\n ok- \n what about that guy,\n who are you?";
//    private static String textA = "what is\n that thing";
//    private static String textB = "that-guy\n\n who talking?";

    @Test
    public void test() throws Exception {
        TimeSpendRecorder.INSTANCE.start();
//        List<CompareResult> results = ContentTraverser.getResult(textA, textB);
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\test.txt"), loadFile("D:\\test2.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\x1.txt"), loadFile("D:\\x2.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\CSN329 - 1.txt"), loadFile("D:\\CSN329 - 2.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\test.txt"), loadFile("D:\\test3.txt"));
//        List<CompareResult> results = ContentTraverser.getResult(loadFile("D:\\newTest1.txt"), loadFile("D:\\newTest2.txt"));
//        List<CompareResult> sections = SectionUtils.toSections(results);
//        String input1 = textA, input2 = textB;
//        String input1 = loadFile("D:\\x1.txt"), input2 = loadFile("D:\\x2.txt");
        String input1 = loadFile("D:\\CSN329 - 1.txt"), input2 = loadFile("D:\\CSN329 - 2.txt");
//        String input1 = loadFile("D:\\test.txt"), input2 = loadFile("D:\\test2.txt");
//        String input1 = loadFile("D:\\what1.txt"), input2 = loadFile("D:\\what2.txt");
//        String input1 = loadFile("D:\\y1.txt"), input2 = loadFile("D:\\y2.txt");
//        Paragraph[] params1 = toParamArr(StructureUtils.toParams(input1));
//        Paragraph[] params2 = toParamArr(StructureUtils.toParams(input2));
//        LevenshteinUtils.Result result = ImprovedLSUtils.compare(params1, params2);
        List<Change.Index> changes = DifferProcessor.getUnitChanges(input1, input2);
//        LevenshteinUtils.Result result = ImprovedLSUtils.compare(toCharArr(input1), toCharArr(input2));
//        LevenshteinUtils.Result result = ImprovedLSUtils.compare(toCharArr(loadFile("D:\\CSN329 - 1.txt")), toCharArr(loadFile("D:\\CSN329 - 2.txt")));
        TimeSpendRecorder.INSTANCE.stop();
        System.out.println("变更数:" + changes.size());
    }

    private static Character[] toCharArr(String input) {
        int length = input.length();
        Character[] result = new Character[length];
        for (int i = 0; i < length; i++) {
            result[i] = input.charAt(i);
        }
        return result;
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
