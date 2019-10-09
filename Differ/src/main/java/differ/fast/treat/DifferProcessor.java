package differ.fast.treat;

import differ.fast.model.Change;
import differ.fast.model.Paragraph;
import differ.fast.pretreat.StructureUtils;
import differ.fast.treat.callback.ParagraphCallback;
import differ.fast.utils.LevenshteinUtils;

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
        StructureUtils.Result sResult = StructureUtils.format(source);
        StructureUtils.Result tResult = StructureUtils.format(target);
        // 段落对比
        ParagraphCallback callback = new ParagraphCallback();
        LevenshteinUtils.compare(sResult.getParagraphs(), tResult.getParagraphs(), callback);
        System.out.println("相似度:" + (1 - callback.getChangedUnitCount() * 1.0f / Math.max(sResult.getUnitCount(), tResult.getUnitCount())));
//        print(callback.changes, source, target);
        return callback.changes;
    }

    private static class Callback implements LevenshteinUtils.ICallback<Paragraph> {

        @Override
        public void onStart() {

        }

        @Override
        public void onElementSame(Paragraph source, Paragraph target) {
            System.out.println("same:" + "s:" + source.pIndex + " t:" + target.pIndex);
        }

        @Override
        public void onElementAdd(int addPos, Paragraph source, Paragraph target) {
            System.out.println("add:" + "s:" + source.pIndex + " t:" + target.pIndex + " addPos:" + addPos);
        }

        @Override
        public void onElementModify(Paragraph source, Paragraph target) {
            System.out.println("modify:" + "s:" + source.pIndex + " t:" + target.pIndex);
        }

        @Override
        public void onElementDelete(int delPos, Paragraph source, Paragraph target) {
            System.out.println("delete:" + "s:" + source.pIndex + " t:" + target.pIndex + " delPos:" + delPos);
        }

        @Override
        public void onFinal(int distance) {

        }
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
