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
        onPreHandle(sentryUnit, results);
        while (null != sentryUnit.target) {
            CompareResult result = ContentComparator.getMatchResult(sStruct, sentryUnit.source, sentryUnit.target);
//            // 跳过已处理的目标单元
            if (result.matchUnitCount > 1) {
                sentryUnit.target = sentryUnit.target.getContentUnitWithOffset(result.matchUnitCount - 1, true);
            }
            Integer contentUnitOffset = result.getSourceContentUnitOffset(sentryUnit.source);
            // 内容非新增
            if (null != contentUnitOffset) {
                // 内容删除
                if (contentUnitOffset > 1) {
                    onHandleDelete(sentryUnit, results, contentUnitOffset);
                }
                // 内容相同，比较匹配结束后的下一单元
                else if (contentUnitOffset == 0) {
                    onHandleTheSame(sentryUnit, result);
                }
                // 上一次遍历的内容过于超前
                else if (contentUnitOffset < 0) {
                    onHandleSurpass(sentryUnit, results, result);
                }
            }
            // 保存匹配的结果
            results.add(result);
            // 下一内容单元
            sentryUnit.target = sentryUnit.target.nextContentUnit();
        }
        Unit sourceLastUnit = sentryUnit.source.nextContentUnit();
        if (null != sourceLastUnit) {
            CompareResult delete = new CompareResult();
            delete.source.from = sourceLastUnit;
            delete.source.to = sStruct.getLastUnit();
            results.add(delete);
        }
        return results;
    }

    private static void onPreHandle(SentryUnit sentryUnit, LinkedList<CompareResult> results) {
        // 如果以低优先级单元开头，则需要额外处理
        if (sentryUnit.source.isLowPriorityUnit() || sentryUnit.target.isLowPriorityUnit()) {
            CompareResult result = new CompareResult();
            result.matchUnitCount = sentryUnit.source.equals(sentryUnit.target) ? 1 : 0;
            if (sentryUnit.source.isLowPriorityUnit()) {
                result.source.singleUnit(sentryUnit.source);
                sentryUnit.source = sentryUnit.source.nextContentUnit();
            }
            if (sentryUnit.target.isLowPriorityUnit()) {
                result.target.singleUnit(sentryUnit.target);
                sentryUnit.target = sentryUnit.target.nextContentUnit();
            }
            results.add(result);
        }
    }

    private static void onHandleDelete(SentryUnit sentryUnit, LinkedList<CompareResult> results, int contentUnitOffset) {
        CompareResult delete = new CompareResult();
        delete.source.from = sentryUnit.source;
        delete.source.to = sentryUnit.source.getContentUnitWithOffset(contentUnitOffset - 1, true);
        sentryUnit.source = delete.source.to.getContentUnitWithOffset(2, false);
        results.add(delete);
    }

    private static void onHandleTheSame(SentryUnit sentryUnit, CompareResult result) {
        sentryUnit.source = result.source.to.getContentUnitWithOffset(1, false);
    }

    private static void onHandleSurpass(SentryUnit sentryUnit, LinkedList<CompareResult> results, CompareResult result) {
        int totalMatchUnitCount = 0, firstHandleIndex = 0;
        boolean needHandle = true;
        for (int i = results.size() - 1; i >= 0; i--) {
            CompareResult tobeCheck = results.get(i);
            Integer offset = result.getSourceContentUnitOffset(tobeCheck);
            if (null != offset) {
                // 找到匹配的位置
                if (offset > 0) {
                    break;
                }
                // 匹配的单元数 不及 之前累计的单元数
                totalMatchUnitCount += tobeCheck.matchUnitCount;
                if (result.matchUnitCount <= totalMatchUnitCount) {
                    needHandle = false;
                    break;
                }
            }
            firstHandleIndex = i;
        }
        if (needHandle) {
            CompareResult tmpResult = null;
            for (int i = results.size() - 1; i >= firstHandleIndex; i--) {
                CompareResult tobeCheck = results.get(i);
                if (tobeCheck.isTypeMatch()) {
                    tmpResult = tobeCheck;
                    results.remove(i);
                } else if (tobeCheck.isTypeDelete() && null != tmpResult) {
                    tobeCheck.target = tmpResult.target;
                    if (tobeCheck.source.from == result.source.from) {
                        tobeCheck.source.singleUnit(null);
                    } else {
                        tobeCheck.source.to = result.source.from;
                    }
                }
            }
            sentryUnit.source = result.source.to.getContentUnitWithOffset(1, false);
        }
    }

    /**
     * 哨兵单元
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
