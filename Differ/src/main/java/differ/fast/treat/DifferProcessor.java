package differ.fast.treat;

import differ.fast.model.Change;
import differ.fast.model.Unit;
import differ.fast.pretreat.UnitExtractor;
import differ.fast.treat.callback.UnitCallback;
import differ.fast.utils.ImprovedLSUtils;

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
        List<Unit> sUnits = UnitExtractor.format(source);
        List<Unit> tUnits = UnitExtractor.format(target);
        // 段落对比
        UnitCallback callback = new UnitCallback();
        ImprovedLSUtils.compare(toArr(sUnits), toArr(tUnits), UnitWeightProvider.get(), callback);
        print(callback.changes, source, target);
        return callback.changes;
    }

    private static Unit[] toArr(List<Unit> units) {
        return units.toArray(new Unit[0]);
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
