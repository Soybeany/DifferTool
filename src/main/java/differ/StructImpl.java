package differ;

import differ.util.FastStruct;
import differ.util.Unit;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/9/15.
 */
public class StructImpl {

    private static String textA = "what is that thing, what about that guy, who are you?";
    private static String textB = "what can I do for you? is that guy who talking?";
//    private static String textB = "that guy who talking?";

    public static void main(String[] args) {
        // 获得比较用的结构
        FastStruct source = new FastStruct(textA, true);
        FastStruct target = new FastStruct(textB, false);

        // 获取第一个单元
        Unit sourceUnit = source.getFirstUnit();
        Unit targetUnit = target.getFirstUnit();

        LinkedList<WordMatchResult> results = new LinkedList<>();
        while (null != targetUnit) {
            WordMatchResult result = getMatchResult(source, target, 0, targetUnit);
//            posOffset = result.getSourceStartPos() - sourcePos;
//            if (posOffset != 0) {
//                WordMatchResult delete = new WordMatchResult();
//
//                results.add()
//            }
//            if (results.isEmpty() || results.getLast().getPos() < result.getPos()) {
//                results.add(result);
//            } else {
//
//            }
            // 下一内容单元
            targetUnit = targetUnit.nextContentUnit();
        }
    }

    /**
     * 尝试获得匹配的结果
     *
     * @param source    快速结构 (被匹配文本)
     * @param fromIndex 从此下标开始匹配 (被匹配文本)
     * @param unit      待匹配的单元 (待匹配文本)
     * @return 匹配出的结果
     */
    private static WordMatchResult getMatchResult(FastStruct source, FastStruct target, int fromIndex, Unit unit) {
        // 普通匹配
        String key = FastStruct.getNewKey(null, unit);
        List<Unit> units = source.unitsMap.get(key);
        // 没有找到匹配
        if (null == units) {
            WordMatchResult result = new WordMatchResult();
            result.getTarget().singleUnit(unit);
            return result;
        }
        // 有更深层次
        List<Unit> tmpUnits = units;
        Unit tmpUnit = unit;
        int matchCount = 0, tmpMatchCount = matchCount;
        while (null != tmpUnits && FastStruct.hasKeyInExUnitsMap(tmpUnits)) {
            // 正式赋值
            units = tmpUnits;
            unit = tmpUnit;
            matchCount = tmpMatchCount;
            // 临时赋值
            tmpUnit = unit.nextContentUnit();
            if (null == tmpUnit) {
                break;
            }
            key = FastStruct.getNewKey(key, tmpUnit);
            tmpUnits = source.exUnitsMap.get(tmpMatchCount++).get(key);
        }
        // 有后续单元
        return getMatchResultInChain(source, target, units, fromIndex, matchCount, unit);
    }

    /**
     * 从单元链中获取结果
     */
    private static WordMatchResult getMatchResultInChain(FastStruct source, FastStruct target, List<Unit> sourceUnits, int fromIndex, int matchCount, Unit unit) {
        if (null == sourceUnits) {
            throw new RuntimeException("数据源不能为null");
        }
        WordMatchResult result = new WordMatchResult();
        TerminalUnit sourceTU = result.getSource();
        TerminalUnit targetTU = result.getTarget();
        int exMatchCount, maxExMatchCount = 0; // 额外的匹配数
        for (Unit sourceUnit : sourceUnits) {
            if (sourceUnit.charIndex < fromIndex) {
                continue;
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
        result.matchCount = matchCount + maxExMatchCount;
        sourceTU.from = source.getUnitWithOffset(sourceTU.to, -result.matchCount);
        targetTU.from = target.getUnitWithOffset(targetTU.to, -result.matchCount);
        return result;
    }

    /**
     * 单词匹配结果
     */
    private static class WordMatchResult {
        int matchCount; // 匹配数

        private TerminalUnit mSource;
        private TerminalUnit mTarget;

        TerminalUnit getSource() {
            if (null == mSource) {
                mSource = new TerminalUnit();
            }
            return mSource;
        }

        TerminalUnit getTarget() {
            if (null == mTarget) {
                mTarget = new TerminalUnit();
            }
            return mTarget;
        }

        int getSourceStartPos() {
            if (null == mSource) {
                return 0;
            }
            return mSource.from.charIndex;
        }

        int getSourceEndPos() {
            if (null == mSource) {
                return 0;
            }
            return mSource.to.charIndex;
        }

        /**
         * 获得变动列表
         */
        List<Change> getChanges() {
            return null;
        }
    }

    /**
     * 终端单元
     */
    private static class TerminalUnit {
        Unit from;
        Unit to;

        void singleUnit(Unit unit) {
            from = unit;
            to = unit;
        }
    }

    /**
     * 内容的变动
     */
    private static class Change {
        static final int ADD = 1; // 增加
        static final int MODIFY = 2; // 修改
        static final int DELETE = 3; // 删除

        /**
         * 变更类型
         */
        int type;

        /**
         * 数据源
         */
        Content source;

        /**
         * 目标
         */
        Content target;
    }

    /**
     * 内容信息
     */
    private static class Content {
        /**
         * 开始的下标(包含)
         */
        int fromIndex;

        /**
         * 结束的下标(不包含)
         */
        int toIndex;

        /**
         * 文本
         */
        String text;
    }
}
