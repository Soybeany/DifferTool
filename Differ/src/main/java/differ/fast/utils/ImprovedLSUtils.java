package differ.fast.utils;

import differ.fast.model.Change;
import differ.fast.model.Range;

import java.util.LinkedList;

/**
 * 改良的 莱文斯坦(距离) 工具类
 * 支持长数组，因能对长数组进行分段比较
 * <br>Created by Soybeany on 2019/9/27.
 */
public class ImprovedLSUtils extends LevenshteinUtils {

    private static final int SECTION_LENGTH = 50; // 每个分段的长度

    /**
     * 对比指定的两个数组
     */
    public static <T> Result compare(T[] source, T[] target) {
        // 若长度没有超过阈值，则直接使用经典算法进行计算
        if (source.length <= SECTION_LENGTH && target.length <= SECTION_LENGTH) {
            return LevenshteinUtils.compare(source, target);
        }
        // 范围中的循环
        Result result = new Result();
        Range sRange = new Range().setup(0, SECTION_LENGTH), tRange = new Range().setup(0, SECTION_LENGTH);
        boolean hasSContent, hasTContent;
        while ((hasSContent = sRange.from < source.length) | (hasTContent = tRange.from < target.length)) {
            LinkedList<Change> changes = result.changes;
            // 内容已遍历完，提取结束
            if (!hasSContent) {
                changes.add(new Change(Change.ADD, sRange, tRange.to(target.length)));
                break;
            } else if (!hasTContent) {
                changes.add(new Change(Change.DELETE, sRange.to(source.length), tRange));
                break;
            }
            // 常规处理
            LevenshteinUtils.compare(result, source, target, sRange, tRange);
            if (changes.isEmpty()) {
                continue;
            }
            Change lastChange = changes.getLast();
            int sOffset = 0, tOffset = 0;
            if (Change.DELETE == lastChange.type && lastChange.isChangeContinuous(Change.DELETE, sRange.to, tRange.to)) {
                changes.removeLast();
                sOffset = lastChange.source.length();
            } else if (Change.ADD == lastChange.type && lastChange.isChangeContinuous(Change.ADD, sRange.to, tRange.to)) {
                changes.removeLast();
                tOffset = lastChange.target.length();
            }
            sRange.shift(SECTION_LENGTH - sOffset, source.length);
            tRange.shift(SECTION_LENGTH - tOffset, target.length);
        }
        return result;
    }

}
