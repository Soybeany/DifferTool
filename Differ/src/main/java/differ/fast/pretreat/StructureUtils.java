package differ.fast.pretreat;

import differ.fast.model.Paragraph;
import differ.fast.model.Unit;
import differ.fast.utils.Md5Utils;

import java.util.LinkedList;

/**
 * 结构化工具类
 * <br>Created by Soybeany on 2019/9/29.
 */
public class StructureUtils {

    /**
     * 将指定字符串变为段落列表
     */
    public static LinkedList<Paragraph> toParams(String input) throws Exception {
        // 初始化变量
        int partIndex = -1;
        Unit lastUnit = null, curUnit;
        Paragraph paragraph = null;
        StringBuilder builder = new StringBuilder();
        LinkedList<Paragraph> result = new LinkedList<>();
        TextSeparator separator = new TextSeparator(input);
        // 整理单元
        while (null != (curUnit = separator.getNextUnit())) {
            // 若段落不同，则新建一个段落
            if (null == paragraph || curUnit.paramIndex != partIndex) {
                // 将缓存中的内容取出，并计算其md5
                calculateMd5(paragraph, builder);
                // 值设置
                result.add(paragraph = new Paragraph());
                partIndex = curUnit.paramIndex;
            }
            // 值设置
            paragraph.units.add(curUnit);
            builder.append(curUnit.text);
            // 链接设置
            if (null != lastUnit) {
                lastUnit.nextUnit = curUnit;
                curUnit.preUnit = lastUnit;
            }
            lastUnit = curUnit;
        }
        // 补充最后一个段落的设置
        calculateMd5(paragraph, builder);
        return result;
    }

    private static void calculateMd5(Paragraph paragraph, StringBuilder content) throws Exception {
        if (null == paragraph || content.length() == 0) {
            return;
        }
        paragraph.md5 = Md5Utils.getByteArr(content.toString());
        content.delete(0, content.length());
    }
}
