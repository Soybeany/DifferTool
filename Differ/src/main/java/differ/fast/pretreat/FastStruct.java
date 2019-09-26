package differ.fast.pretreat;

import differ.fast.model.CompareResult;
import differ.fast.model.MatchScope;
import differ.fast.model.TerminalUnit;
import differ.fast.model.Unit;

import java.util.*;

/**
 * 由文本转化的快速访问结构
 * 先将超出exMap的稳定部分找出来，排序
 * 因为超出exMap最大长度的单元不会重用，所以不需考虑重叠
 * 从最长的开始入座，顺序没发生变化则不需额外处理，否则source为删除，target为新增
 * 入座完毕后，作为大区间的划分依据
 * 剩余的单元在逐渐缩小的范围中匹配第一个找到的结果
 * <br>Created by Soybeany on 2019/9/19.
 */
public class FastStruct {

    //    private static final int MAX_COUNT = 2; // 若map中某个key对应的value，其Size超出此限制，则在exKeys中存在下一级的key
    private static final int MAX_COUNT = 2 << 4; // 若map中某个key对应的value，其Size超出此限制，则在exKeys中存在下一级的key
    private static final String SEPARATOR = "-"; // 组合key中使用的分隔符

    /**
     * 由原文本转化的索引结构
     */
    public final Map<String, TreeSet<Unit>> unitsMap = new HashMap<>();

    /**
     * 针对相同key可能存在多个匹配结果，导致value过长，尝试增长key以减少匹配的value
     * key：复数的单元text
     * value：key中最后一个单元的集合
     */
    public final LinkedList<Map<String, TreeSet<Unit>>> exKeys = new LinkedList<>();

    /**
     * 首单元
     */
    public final Unit firstUnit;

    /**
     * 末尾单元
     */
    public final Unit lastUnit;

    /**
     * exKeys中最大的单元数
     */
    public final int maxUnitCountInExKeys;

    /**
     * @param needEx 是否生成额外的单元映射
     */
    public FastStruct(String input, boolean needEx) {
        TerminalUnit unit = setupUnitsMap(unitsMap, input);
        firstUnit = unit.from;
        lastUnit = unit.to;
        if (needEx) {
            setupExKeys(exKeys, unitsMap);
        }
        maxUnitCountInExKeys = needEx ? exKeys.size() + 1 : 0;
    }

    /**
     * 获得指定内容单元的匹配结果(指定范围中链最长的一条)
     *
     * @param source      快速结构 (被匹配文本)
     * @param scope       匹配的范围(被匹配文本)
     * @param contentUnit 待匹配的单元 (待匹配文本)
     * @param speedFirst  是否启用速度优先模式，即在新的exKeys中没有找到匹配，则使用上一层级的第一个满足条件的结果
     * @return 匹配出的结果，没有则返回null
     */
    public static CompareResult getMatchResult(FastStruct source, MatchScope scope, Unit contentUnit, boolean speedFirst) {
        // 普通匹配
        String key = FastStruct.getNewKey(null, contentUnit);
        TreeSet<Unit> units = source.unitsMap.get(key);
        // 没有找到匹配
        if (null == units) {
            return null;
        }
        // 有更深层次
        TreeSet<Unit> tmpUnits = units;
        Unit tmpContentUnit;
        int exLevel = 0, tmpExLevel = exLevel;
        boolean matchFirst = false;
        while (FastStruct.hasKeyInExKeys(tmpUnits)) {
            // 临时赋值
            tmpContentUnit = contentUnit.nextContentUnit();
            if (null == tmpContentUnit) {
                break;
            }
            key = FastStruct.getNewKey(key, tmpContentUnit);
            tmpUnits = source.exKeys.get(tmpExLevel++).get(key);
            // 若能匹配，记住层数
            if (null != tmpUnits) {
                units = tmpUnits;
                contentUnit = tmpContentUnit;
                exLevel = tmpExLevel;
            } else {
                matchFirst = speedFirst;
            }
        }
        // 有后续单元
        return getMatchResultInChain(units, exLevel, scope, contentUnit, matchFirst);
    }

    /**
     * 从单元链中获取结果
     *
     * @param lastMatchedContentUnit 匹配项的最后一个内容单元
     * @param matchFirst             只需要匹配头一个满足条件的结果
     */
    private static CompareResult getMatchResultInChain(TreeSet<Unit> sourceUnits, int exLevel, MatchScope scope, Unit lastMatchedContentUnit, boolean matchFirst) {
        if (null == sourceUnits) {
            throw new RuntimeException("数据源不能为null");
        }
        CompareResult result = new CompareResult();
        TerminalUnit sourceTU = result.source;
        TerminalUnit targetTU = result.target;
        int matchCount, localMaxMatchCount, globalMaxMatchCount = 0; // 额外的匹配数
        // 快速匹配模式
        Unit firstUnit;
        if (matchFirst && null != (firstUnit = sourceUnits.first()) && scope.getMaxMatchCount(firstUnit, exLevel) > 0) {
            globalMaxMatchCount = 1;
            sourceTU.to = sourceUnits.first();
            targetTU.to = lastMatchedContentUnit;
        }
        // 传统的精确查找
        else {
            for (Unit sourceUnit : scope.getUnitsInScope(sourceUnits, exLevel)) {
                // 获得本地理论能够达到的最大匹配数
                localMaxMatchCount = scope.getMaxMatchCount(sourceUnit, exLevel);
                // 若本地理论能达到的最大匹配数比全局的小，则不需要再比较了，比较下一个元素
                if (localMaxMatchCount <= globalMaxMatchCount) {
                    continue;
                }
                // 归零
                matchCount = 0;
                // 沿链匹配
                Unit tmpSourceUnit = sourceUnit, tmpTargetUnit = lastMatchedContentUnit, toSourceUnit, toTargetUnit;
                do {
                    toSourceUnit = tmpSourceUnit;
                    toTargetUnit = tmpTargetUnit;
                    matchCount++;
                    tmpSourceUnit = tmpSourceUnit.nextContentUnit();
                    tmpTargetUnit = tmpTargetUnit.nextContentUnit();
                } while (null != tmpSourceUnit && tmpSourceUnit.equals(tmpTargetUnit) && matchCount <= localMaxMatchCount);
                // 选出最长匹配
                if (globalMaxMatchCount < matchCount) {
                    globalMaxMatchCount = matchCount;
                    sourceTU.to = toSourceUnit;
                    targetTU.to = toTargetUnit;
                }
            }
        }
        // 记录起始单元
        result.matchUnitCount = exLevel + globalMaxMatchCount;
        if (null != sourceTU.to && result.matchUnitCount != 0) {
            int offset = 1 - result.matchUnitCount;
            sourceTU.from = sourceTU.to.getContentUnitWithOffset(offset);
            targetTU.from = targetTU.to.getContentUnitWithOffset(offset);
            return result;
        } else {
            return null;
        }
    }

    // ****************************************内部方法****************************************

    /**
     * 根据旧键及当前单元，生成新键
     */
    private static String getNewKey(String oldKey, Unit unit) {
        return (null != oldKey ? oldKey + SEPARATOR : "") + unit.text;
    }

    /**
     * 判断指定的单元集在ExKeys中是否有键
     */
    private static boolean hasKeyInExKeys(TreeSet<Unit> units) {
        return null != units && units.size() >= MAX_COUNT;
    }

    /**
     * 设置获得额外键列表
     */
    private static void setupExKeys(LinkedList<Map<String, TreeSet<Unit>>> exKeys, Map<String, TreeSet<Unit>> map) {
        while (true) {
            map = getExKeyMap(map);
            if (map.isEmpty()) {
                break;
            }
            exKeys.add(map);
        }
    }

    /**
     * 将文本拆分为单元映射链
     *
     * @return 首单元
     */
    private static TerminalUnit setupUnitsMap(Map<String, TreeSet<Unit>> unitsMap, String input) {
        Unit firstUnit = null, curUnit, lastUnit = null;
        TextSeparator separator = new TextSeparator(input);
        while (null != (curUnit = separator.getNextUnit())) {
            // 放入普通列表
            getNewKeyNonNullListWithOldKey(unitsMap, null, curUnit).add(curUnit);
            // 建立链
            if (null != lastUnit) { // 非首单元
                lastUnit.nextUnit = curUnit;
                curUnit.preUnit = lastUnit;
            } else { // 首单元
                firstUnit = curUnit;
            }
            lastUnit = curUnit;
        }
        TerminalUnit unit = new TerminalUnit();
        unit.from = (null != firstUnit && firstUnit.isLowPriorityUnit() ? firstUnit.nextContentUnit() : firstUnit);
        unit.to = (null != lastUnit && lastUnit.isLowPriorityUnit() ? lastUnit.preContentUnit() : lastUnit);
        return unit;
    }

    /**
     * 获得额外键映射
     */
    private static Map<String, TreeSet<Unit>> getExKeyMap(Map<String, TreeSet<Unit>> input) {
        Map<String, TreeSet<Unit>> result = new HashMap<>();
        for (Map.Entry<String, TreeSet<Unit>> entry : input.entrySet()) {
            String oldKey = entry.getKey();
            TreeSet<Unit> value = entry.getValue();
            // 列表元素数目小于指定值则不作处理
            if (value.size() < MAX_COUNT) {
                continue;
            }
            // 非内容单元则不作处理(列表中全部元素优先级一样，只需取一个元素判断即可)
            if (value.first().isLowPriorityUnit()) {
                continue;
            }
            // 新映射中赋值
            for (Unit unit : value) {
                Unit nextUnit = unit.nextContentUnit();
                if (null == nextUnit) {
                    continue;
                }
                getNewKeyNonNullListWithOldKey(result, oldKey, nextUnit).add(nextUnit);
            }
        }
        return result;
    }

    /**
     * 使用旧key生成新key，并从Map中获得非空列表
     */
    private static Set<Unit> getNewKeyNonNullListWithOldKey(Map<String, TreeSet<Unit>> source, String oldKey, Unit unit) {
        String key = getNewKey(oldKey, unit);
        TreeSet<Unit> units = source.get(key);
        if (null == units) {
            source.put(key, units = new TreeSet<>());
        }
        return units;
    }
}
