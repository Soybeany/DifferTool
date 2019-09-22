package differ.fast.treat;

import differ.fast.model.CompareResult;
import differ.fast.model.Unit;
import differ.fast.pretreat.FastStruct;

import java.util.LinkedList;

/**
 * 内容遍历者
 * <br>Created by Soybeany on 2019/9/22.
 */
public class ContentTraverser {

    /**
     * 获得粗略的结果
     */
    public static LinkedList<CompareResult> getRawResult(String input1, String input2) {
        // 获得比较用的结构
        FastStruct sStruct = new FastStruct(input1, true);
        FastStruct tStruct = new FastStruct(input2, false);
        // 创建哨兵单元
        SentryUnit sentryUnit = new SentryUnit(sStruct.getFirstUnit(), tStruct.getFirstUnit());
        // 结果列表
        LinkedList<CompareResult> results = new LinkedList<>();
        // 预处理
        onPreHandle(sentryUnit, results);
        // 正式处理
        while (null != sentryUnit.target) {
            onHandle(sStruct, sentryUnit, results);
        }
        // 后处理
        onPostHandle(sentryUnit, results);
        // 返回结果
        return results;
    }

    private static void onPreHandle(SentryUnit sentryUnit, LinkedList<CompareResult> results) {
        // 如果不是以低优先级单元开头，则不需要额外处理
        if (!sentryUnit.source.isLowPriorityUnit() && !sentryUnit.target.isLowPriorityUnit()) {
            return;
        }
        CompareResult result = new CompareResult();
        result.matchUnitCount = sentryUnit.source.equals(sentryUnit.target) ? 1 : 0;
        // source
        if (sentryUnit.source.isLowPriorityUnit()) {
            result.source.singleUnit(sentryUnit.source);
            sentryUnit.source = sentryUnit.source.nextContentUnit();
        }
        // target
        if (sentryUnit.target.isLowPriorityUnit()) {
            result.target.singleUnit(sentryUnit.target);
            sentryUnit.target = sentryUnit.target.nextContentUnit();
        }
        results.add(result);
    }

    private static void onHandle(FastStruct sStruct, SentryUnit sentryUnit, LinkedList<CompareResult> results) {
        CompareResult result = ContentComparator.getMatchResult(sStruct, sentryUnit.source, sentryUnit.target);
        handleResult(sStruct, sentryUnit, results, result);
    }

    private static void handleResult(FastStruct sStruct, SentryUnit sentryUnit, LinkedList<CompareResult> results, CompareResult result) {
        Integer contentUnitOffset = result.getSourceContentUnitOffset(sentryUnit.source);
        // 内容新增
        if (null == contentUnitOffset) {
            onHandleAdd(results, result);
        }
        // 内容非新增
        else {
            // 内容相同，比较匹配结束后的下一单元
            if (contentUnitOffset == 0) {
                onHandleTheSame(sentryUnit, results, result);
            }
            // 上一次遍历的内容过于超前
            else if (contentUnitOffset < 0) {
                onHandleSurpass(sStruct, sentryUnit, results, result);
                return;
            }
            // 内容删除
            else {
                onHandleDelete(sentryUnit, results, result, contentUnitOffset);
            }
            // 设置下一源内容单元
            sentryUnit.source = result.source.to.getContentUnitWithOffset(1, false);
        }
        // 设置下一目标内容单元
        sentryUnit.target = result.target.to.getContentUnitWithOffset(1, true);
    }

    private static void onHandleAdd(LinkedList<CompareResult> results, CompareResult result) {
        // 若为连续新增，则合并为同一个结果
        if (!results.isEmpty() && results.getLast().isTypeAdd()) {
            results.getLast().target.to = result.target.to;
        } else {
            results.add(result);
        }
    }

    private static void onHandleDelete(SentryUnit sentryUnit, LinkedList<CompareResult> results, CompareResult result, int contentUnitOffset) {
        CompareResult delete = new CompareResult();
        delete.source.from = sentryUnit.source;
        delete.source.to = sentryUnit.source.getContentUnitWithOffset(contentUnitOffset - 1, false);
        sentryUnit.source = delete.source.to.getContentUnitWithOffset(2, false);
        results.add(delete);
        if (null != result) {
            results.add(result);
        }
    }

    private static void onHandleTheSame(SentryUnit sentryUnit, LinkedList<CompareResult> results, CompareResult result) {
        results.add(result);
    }

    private static void onHandleSurpass(FastStruct sStruct, SentryUnit sentryUnit, LinkedList<CompareResult> results, CompareResult result) {
        // 尝试新范围中找结果
        CompareResult anotherResult = ContentComparator.getMatchResult(sStruct, sentryUnit.source, sentryUnit.source, null, sentryUnit.target);
        handleResult(sStruct, sentryUnit, results, anotherResult);
    }

    private static void onPostHandle(SentryUnit sentryUnit, LinkedList<CompareResult> results) {
        if (!sentryUnit.source.isExceeded) {
            // 补充被删除的单元
            onHandleDelete(sentryUnit, results, null, Integer.MAX_VALUE >> 1);
        }
    }

    /**
     * 哨兵单元(待处理的开始单元)
     */
    private static class SentryUnit {
        Unit source;
        Unit target;

        SentryUnit(Unit source, Unit target) {
            this.source = source;
            this.target = target;
        }
    }
}
