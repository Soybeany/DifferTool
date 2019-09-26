package differ.fast.treat;

import differ.TimeSpendRecorder;
import differ.fast.model.CompareResult;
import differ.fast.model.MatchScope;
import differ.fast.model.Unit;
import differ.fast.pretreat.FastStruct;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 内容遍历者
 * <br>Created by Soybeany on 2019/9/22.
 */
public class ContentTraverser {

    private static final int MIN_COUNT_OF_SECTION_SEPARATOR = 3; // 区域分隔符至少需具有的单元数
    private static final Comparator<CompareResult> COMPARATOR_SOURCE = new Comparator<CompareResult>() {
        @Override
        public int compare(CompareResult o1, CompareResult o2) {
            return o2.matchUnitCount - o1.matchUnitCount;
        }
    };

    /**
     * 获得对比结果
     */
    public static List<CompareResult> getResult(String input1, String input2) {
        // 转换为快速结构
        FastStruct sStruct = new FastStruct(input1, true);
        TimeSpendRecorder.INSTANCE.record("结构转换1");
        FastStruct tStruct = new FastStruct(input2, false);
        TimeSpendRecorder.INSTANCE.record("结构转换2");
        MatchScope scope = new MatchScope(tStruct.firstUnit, tStruct.lastUnit);
        // 获得分区结果
        List<CompareResult> sections = getSections(sStruct, tStruct, scope);

        // 获得粗略结果
//        Set<CompareResult> rawResult = getRawResult(sStruct, tStruct);

        return sections;
    }

    private static List<CompareResult> getSections(FastStruct sStruct, FastStruct tStruct, MatchScope scope) {
        List<CompareResult> results = new LinkedList<>();
        Unit targetUnit = tStruct.firstUnit;
        while (null != targetUnit) {
            CompareResult result = FastStruct.getMatchResult(sStruct, scope, targetUnit, true);
            // 如果没有找到匹配，或匹配的长度不够，使用下一单元继续
            if (null == result || result.matchUnitCount < MIN_COUNT_OF_SECTION_SEPARATOR) {
                targetUnit = targetUnit.nextContentUnit();
                continue;
            }
            targetUnit = result.target.to.nextContentUnit();
            results.add(result);
            // 添加新的区域限制
            scope.setExclude(result.source);
        }
        System.out.println("分区:" + scope.activation.size());
        TimeSpendRecorder.INSTANCE.record("获得对比结果");
        Collections.sort(results, COMPARATOR_SOURCE);
        TimeSpendRecorder.INSTANCE.record("排序对比结果");
        return results;
    }
}
