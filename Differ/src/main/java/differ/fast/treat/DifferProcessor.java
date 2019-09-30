package differ.fast.treat;

import differ.fast.model.Change;
import differ.fast.model.Paragraph;
import differ.fast.model.Range;
import differ.fast.model.Unit;
import differ.fast.pretreat.StructureUtils;
import differ.fast.utils.ImprovedLSUtils;
import differ.fast.utils.LevenshteinUtils;

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
    public static List<Change> getUnitChanges(String source, String target) throws Exception {
        LinkedList<Paragraph> sPList = StructureUtils.toParams(source);
        LinkedList<Paragraph> tPList = StructureUtils.toParams(target);
        // 字符串预处理
        Paragraph[] sParagraph = toArr(sPList);
        Paragraph[] tParagraph = toArr(tPList);

        List<Change> uChanges = new LinkedList<>();
        // 段落对比
        LevenshteinUtils.Result pResult = ImprovedLSUtils.compare(sParagraph, tParagraph);
//        pResult.print(sParagraph, tParagraph);
        for (Change pChange : pResult.changes) {
            // 若不为修改(即新增/删除)，下标转换即可
            if (pChange.type != Change.MODIFY) {
                uChanges.add(toUnitChange(pChange, sParagraph, tParagraph));
                continue;
            }
            // 若为修改，对比文本内容
            pickOutDiffBetweenParagraph(uChanges, pChange, sParagraph, tParagraph);
        }
        System.out.println("相似度:" + getSimilarity(sPList, tPList, uChanges));
        return uChanges;
    }

    private static float getSimilarity(LinkedList<Paragraph> sPList, LinkedList<Paragraph> tPList, List<Change> uChanges) {
        Unit u1 = sPList.getLast().units.getLast();
        Unit u2 = tPList.getLast().units.getLast();
        int maxCount = Math.max(sPList.getLast().units.getLast().unitIndex, tPList.getLast().units.getLast().unitIndex) + 1;
        int changeCont = 0;
        for (Change change : uChanges) {
            changeCont += change.count;
        }
        return changeCont * 1.0f / maxCount;
    }

    private static void print(List<Change> changes, String input1, String input2) {
        for (Change change : changes) {
            String msg = change.type + "  " + change.count + "\n";
            msg += "source:" + input1.substring(change.source.from, change.source.to) + "\n";
            msg += "target:" + input2.substring(change.target.from, change.target.to);
            System.out.println(msg + "\n");
        }
    }

    /**
     * 段落列表 转 段落数组
     */
    private static Paragraph[] toArr(List<Paragraph> paragraphs) {
        return paragraphs.toArray(new Paragraph[0]);
    }

    /**
     * 段落数组 转 单元数组
     */
    private static Unit[] toArr(Paragraph[] paragraphs, Range range) {
        List<Unit> units = new LinkedList<>();
        for (int i = range.from; i < range.to; i++) {
            units.addAll(paragraphs[i].units);
        }
        return units.toArray(new Unit[0]);
    }

    /**
     * 将段落差异转变为单元级别的差异
     */
    private static Change toUnitChange(Change pChange, Paragraph[] sParagraph, Paragraph[] tParagraph) {
        Change uChange = new Change(pChange.type, new Range(), new Range());
        int sCount = pRangeToURange(sParagraph, pChange.source, uChange.source);
        int tCount = pRangeToURange(tParagraph, pChange.target, uChange.target);
        uChange.count = (uChange.type == Change.ADD ? tCount : sCount);
        return uChange;
    }

    /**
     * 段落范围转单元范围
     *
     * @return 变动的单元数
     */
    private static int pRangeToURange(Paragraph[] paragraphs, Range pRange, Range uRange) {
        // 若表示位点，特殊处理
        if (pRange.from == pRange.to) {
            uRange.from = uRange.to = getIndex(paragraphs, pRange.from);
            return 0;
        }
        // 单元范围
        uRange.from = getIndex(paragraphs, pRange.from);
        uRange.to = getIndex(paragraphs, pRange.to);
        // 累计的单元数
        int count = 0;
        for (int i = pRange.from; i < pRange.to; i++) {
            count += paragraphs[i].units.size();
        }
        return count;
    }

    /**
     * 从段落间提取差异
     */
    private static void pickOutDiffBetweenParagraph(List<Change> uChanges, Change pChange, Paragraph[] sParagraph, Paragraph[] tParagraph) {
        Unit[] uSource = toArr(sParagraph, pChange.source);
        Unit[] uTarget = toArr(tParagraph, pChange.target);
        // 下标转换(局部->全局)
        LevenshteinUtils.Result result = ImprovedLSUtils.compare(uSource, uTarget);
        for (Change change : result.changes) {
            shiftIndexFromLocalToGlobal(uSource, change.source);
            shiftIndexFromLocalToGlobal(uTarget, change.target);
            uChanges.add(change);
        }
    }

    /**
     * 将下标由局部转换为全局
     */
    private static void shiftIndexFromLocalToGlobal(Unit[] units, Range range) {
        range.from = getIndex(units, range.from);
        range.to = getIndex(units, range.to);
    }

    private static int getIndex(Paragraph[] paragraphs, int index) {
        // 到达末尾
        if (index == paragraphs.length) {
            Unit lastUnit = paragraphs[index - 1].units.getLast();
            return lastUnit.charIndex + lastUnit.text.length();
        }
        // 未到达末尾
        return paragraphs[index].units.getFirst().charIndex;
    }

    /**
     * 获得下标
     */
    private static int getIndex(Unit[] units, int index) {
        if (index == units.length) {
            Unit lastUnit = units[index - 1];
            return lastUnit.charIndex + lastUnit.text.length();
        }
        return units[index].charIndex;
    }
}
