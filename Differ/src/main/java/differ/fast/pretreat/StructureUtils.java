package differ.fast.pretreat;

import differ.fast.model.Paragraph;
import differ.fast.model.unit.AffixUnit;
import differ.fast.model.unit.ContentUnit;
import differ.fast.utils.Md5Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 结构化工具类
 * <br>Created by Soybeany on 2019/9/29.
 */
public class StructureUtils {

    private static final char CONTENT_DIVIDE_CHAR = '-'; // 用于区分内容位置
    private static final char SPLIT_DIVIDE_CHAR = 'A'; // 用于区分分隔符位置

    /**
     * 将指定字符串格式化
     */
    public static Result format(String input) throws Exception {
        // 初始化变量
        int paragraphIndex = 0;
        Paragraph paragraph = new Paragraph(paragraphIndex, 0);
        Result result = new Result();
        UnitExtractor extractor = new UnitExtractor(input);
        // 整理单元
        while (null != (extractor.curUnit = extractor.getNextUnit())) {
            // 内容单元
            if (PriorityUtils.isHighPriority(extractor.curUnit.priority)) {
                paragraph.units.add(extractor.lastContentUnit = (ContentUnit) extractor.curUnit);
                extractor.contentBuilder.append(extractor.curUnit.text).append(CONTENT_DIVIDE_CHAR);
                result.contentUnitCount++;
            }
            // 段落分隔单元
            else if (PriorityUtils.PRIORITY_NEWLINE == extractor.curUnit.priority && extractor.curUnit.text.length() > 1) {
                paragraph.newLineUnit = (AffixUnit) extractor.curUnit;
                setupEndOfParagraph(result.paragraphs, paragraph, extractor);
                // 切换到新的段落，清空缓存
                paragraph = new Paragraph(++paragraphIndex, extractor.curUnit.charEndIndex);
                extractor.lastContentUnit = null;
            }
            // 内容分隔单元
            else {
                makeSureLastContentUnitNonNull(paragraph, extractor);
                extractor.lastContentUnit.separateUnit = (AffixUnit) extractor.curUnit;
                extractor.splitBuilder.append(extractor.curUnit.text).append(SPLIT_DIVIDE_CHAR);
            }
            result.unitCount++;
        }
        // 补充最后一个段落的设置
        if (!paragraph.units.isEmpty()) {
            setupEndOfParagraph(result.paragraphs, paragraph, extractor);
        }
        return result;
    }

    private static void setupEndOfParagraph(List<Paragraph> result, Paragraph paragraph, UnitExtractor extractor) throws Exception {
        // 确保最后内容单元不为null
        makeSureLastContentUnitNonNull(paragraph, extractor);
        // 计算md5值
        calculateMd5AndClearCache(paragraph, extractor);
        // 将段落放入结果列表
        result.add(paragraph);
        // 设置段落的结束下标
        paragraph.charEndIndex = extractor.lastContentUnit.charEndIndex;
    }

    private static void makeSureLastContentUnitNonNull(Paragraph paragraph, UnitExtractor extractor) {
        if (null != extractor.lastContentUnit) {
            return;
        }
        extractor.lastContentUnit = ContentUnit.newEmptyContentUnit(extractor.curUnit.charIndex, extractor.curUnit.unitIndex);
        paragraph.units.add(extractor.lastContentUnit);
    }

    /**
     * 计算段落的md5值，并清空StringBuilder中缓存的字符
     */
    private static void calculateMd5AndClearCache(Paragraph paragraph, UnitExtractor extractor) throws Exception {
        // 计算内容部分
        if (extractor.contentBuilder.length() > 0) {
            paragraph.contentMd5 = Md5Utils.getByteArr(extractor.contentBuilder.toString());
            extractor.contentBuilder.delete(0, extractor.contentBuilder.length());
        }
        // 计算分隔符部分
        if (extractor.splitBuilder.length() > 0) {
            paragraph.othersMd5 = Md5Utils.getByteArr(extractor.splitBuilder.toString());
            extractor.splitBuilder.delete(0, extractor.splitBuilder.length());
        }
    }

    public static class Result {
        final List<Paragraph> paragraphs = new LinkedList<>();
        int unitCount;
        int contentUnitCount;

        public Paragraph[] getParagraphs() {
            return this.paragraphs.toArray(new Paragraph[0]);
        }

        public int getUnitCount() {
            return unitCount;
        }

        public int getContentUnitCount() {
            return contentUnitCount;
        }
    }
}
