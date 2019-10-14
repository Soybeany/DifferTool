package com.soybeany.differtool.extractor;

import com.soybeany.differtool.model.Para;
import com.soybeany.differtool.model.Unit;
import com.soybeany.differtool.utils.Md5Utils;
import com.soybeany.differtool.utils.PriorityUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 段落提取器
 * <br>Created by Soybeany on 2019/10/12.
 */
public class ParaExtractor implements IParaExtractor {
    private static final char DIVIDER = '-'; // 用于区分内容位置

    private static void setupEndOfParagraph(List<Para> result, Para paragraph, StringBuilder contentBuilder, Unit lastUnit) throws Exception {
        // 计算md5值，并清空StringBuilder中缓存的字符
        if (contentBuilder.length() > 0) {
            paragraph.md5 = Md5Utils.getByteArr(contentBuilder.toString());
            contentBuilder.delete(0, contentBuilder.length());
        }
        // 将段落放入结果列表
        result.add(paragraph);
        // 设置段落的结束下标
        paragraph.charEndIndex = lastUnit.charEndIndex;
    }

    @Override
    public Result format(String text) throws Exception {
        // 初始化变量
        int paraIndex = 0;
        Para para = new Para(paraIndex, 0);
        UnitExtractor extractor = new UnitExtractor(text);
        Unit curUnit, lastUnit = null;
        List<Para> paras = new LinkedList<>();
        int unitCount = 0;
        StringBuilder contentBuilder = new StringBuilder();
        // 整理单元
        while (null != (curUnit = extractor.getNextUnit())) {
            // 段落分隔单元
            if (PriorityUtils.PRIORITY_NEWLINE == curUnit.priority && curUnit.length() > 1) {
                para.newlineUnit = curUnit;
                setupEndOfParagraph(paras, para, contentBuilder, lastUnit);
                // 切换到新的段落，清空缓存
                para = new Para(++paraIndex, curUnit.charEndIndex);
            }
            // 内容分隔单元
            else {
                para.units.add(curUnit);
                contentBuilder.append(curUnit.text).append(DIVIDER);
            }
            lastUnit = curUnit;
            ++unitCount;
        }
        // 补充最后一个段落的设置
        if (!para.units.isEmpty()) {
            setupEndOfParagraph(paras, para, contentBuilder, lastUnit);
        }
        return new Result(paras, unitCount);
    }
}
