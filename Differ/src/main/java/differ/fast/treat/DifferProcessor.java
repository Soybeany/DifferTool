package differ.fast.treat;

import differ.fast.model.Change;
import differ.fast.model.Para;
import differ.fast.pretreat.ParaExtractor;
import differ.fast.treat.callback.ParaCallback;
import differ.fast.utils.ImprovedLSUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 差异处理器
 * <p>
 * 预处理文本为 段落、单元 的结构
 * 按段落分段，并计算段落的md5值
 * 对段落列表进行差异分析
 * 对差异段落中的单元进行差异对比
 * <br>Created by Soybeany on 2019/9/29.
 */
public class DifferProcessor {

    /**
     * 获得单元级别的差异列表
     */
    public static List<Change.Index> getUnitChanges(String source, String target) throws Exception {
        Para[] sParas = ParaExtractor.format(source);
        Para[] tParas = ParaExtractor.format(target);
        // 段落对比
        LinkedList<Change.Index> result = new LinkedList<>();
        ImprovedLSUtils.compare(sParas, tParas, new ParaCallback(result));
//        print(result, source, target);
        return result;
    }

    private static void print(List<Change.Index> changes, String input1, String input2) {
        for (Change.Index change : changes) {
            String msg = "type:" + change.type + "  count:" + change.count
                    + "\n";
            msg += "source:" + replaceNewLine(input1.substring(change.source.from, change.source.to)) + "(" + change.source.from + "~" + change.source.to + ")"
                    + "\n";
            msg += "target:" + replaceNewLine(input2.substring(change.target.from, change.target.to)) + "(" + change.target.from + "~" + change.target.to + ")";
            System.out.println(msg + "\n");
        }
    }

    private static String replaceNewLine(String input) {
        return input.replaceAll("\n", "\\\\n");
    }
}
