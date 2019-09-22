package differ.fast.treat;

import differ.fast.model.CompareResult;
import differ.fast.model.TerminalUnit;
import differ.fast.model.Unit;
import differ.fast.pretreat.FastStruct;

import java.util.List;

/**
 * 内容比较器
 * <br>Created by Soybeany on 2019/9/21.
 */
public class ContentComparator {

    private static final int PART_SWIFT_ALLOW_COUNT = 1; // 对比过程中，允许的行偏移数

    public static CompareResult getMatchResult(FastStruct source, Unit curSourceUnit, Unit contentUnit) {
        return getMatchResult(source, curSourceUnit, null, null, contentUnit);
    }

    /**
     * 尝试获得匹配的结果
     *
     * @param source        快速结构 (被匹配文本)
     * @param curSourceUnit 已匹配到的源单元 (被匹配文本)
     * @param minUnit       命中的单元至少要在此单元之后(被匹配文本)[可选]
     * @param maxUnit       命中的单元至多只能在此单元之前(被匹配文本)[可选]
     * @param contentUnit   待匹配的单元 (待匹配文本)
     * @return 匹配出的结果
     */
    public static CompareResult getMatchResult(FastStruct source, Unit curSourceUnit, Unit minUnit, Unit maxUnit, Unit contentUnit) {
        // 普通匹配
        String key = FastStruct.getNewKey(null, contentUnit);
        List<Unit> units = source.unitsMap.get(key);
        // 没有找到匹配
        if (null == units) {
            CompareResult result = new CompareResult();
            result.target.singleUnit(contentUnit);
            return result;
        }
        // 有更深层次
        List<Unit> tmpUnits = units;
        Unit tmpContentUnit = contentUnit;
        int matchCount = 0, tmpMatchCount = matchCount;
        while (FastStruct.hasKeyInExUnitsMap(tmpUnits)) {
            // 正式赋值
            units = tmpUnits;
            contentUnit = tmpContentUnit;
            // 临时赋值
            tmpContentUnit = contentUnit.nextContentUnit();
            if (null == tmpContentUnit) {
                break;
            }
            key = FastStruct.getNewKey(key, tmpContentUnit);
            tmpUnits = source.exContentUnitsMap.get(tmpMatchCount++).get(key);
            // 若能匹配，记住层数
            if (null != tmpUnits) {
                matchCount = tmpMatchCount;
            }
        }
        // 有后续单元
        return getMatchResultInChain(units, curSourceUnit, minUnit, maxUnit, matchCount, contentUnit);
    }

    /**
     * 从单元链中获取结果
     */
    private static CompareResult getMatchResultInChain(List<Unit> sourceUnits, Unit curSourceUnit, Unit minUnit, Unit maxUnit, int matchCount, Unit unit) {
        if (null == sourceUnits) {
            throw new RuntimeException("数据源不能为null");
        }
        CompareResult result = new CompareResult();
        TerminalUnit sourceTU = result.source;
        TerminalUnit targetTU = result.target;
        int exMatchCount, maxExMatchCount = 0; // 额外的匹配数
        for (Unit sourceUnit : sourceUnits) {
            // 范围限制
            if (sourceUnit.partIndex < curSourceUnit.partIndex - PART_SWIFT_ALLOW_COUNT) {
                continue;
            } else if (sourceUnit.partIndex > curSourceUnit.partIndex + PART_SWIFT_ALLOW_COUNT) {
                break;
            } else if (null != minUnit && sourceUnit.contentUnitIndex < minUnit.contentUnitIndex + matchCount) {
                continue;
            } else if (null != maxUnit && sourceUnit.contentUnitIndex > maxUnit.contentUnitIndex) {
                break;
            }
            // 归零
            exMatchCount = 0;
            // 沿链匹配
            Unit tmpSourceUnit = sourceUnit, tmpTargetUnit = unit, toSourceUnit, toTargetUnit;
            do {
                toSourceUnit = tmpSourceUnit;
                toTargetUnit = tmpTargetUnit;
                exMatchCount++;
                tmpSourceUnit = tmpSourceUnit.nextContentUnit();
                tmpTargetUnit = tmpTargetUnit.nextContentUnit();
            } while (null != tmpSourceUnit && tmpSourceUnit.equals(tmpTargetUnit));
            // 选出最长匹配
            if (maxExMatchCount < exMatchCount) {
                maxExMatchCount = exMatchCount;
                sourceTU.to = toSourceUnit;
                targetTU.to = toTargetUnit;
            }
        }
        // 记录起始单元
        result.matchUnitCount = matchCount + (matchCount == 0 ? maxExMatchCount : maxExMatchCount - 1);
        if (result.matchUnitCount != 0) {
            int offset = 1 - result.matchUnitCount;
            sourceTU.from = sourceTU.to.getContentUnitWithOffset(offset, true);
            targetTU.from = targetTU.to.getContentUnitWithOffset(offset, true);
        } else {
            result.target.singleUnit(unit);
        }
        return result;
    }
}
