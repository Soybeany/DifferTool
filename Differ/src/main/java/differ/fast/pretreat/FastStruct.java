package differ.fast.pretreat;

import differ.fast.model.Unit;

import java.util.*;

/**
 * 将文本转化为快速访问结构
 * <br>Created by Soybeany on 2019/9/19.
 */
public class FastStruct {

    private static final int MAX_COUNT = 20; // value列表允许的最大数量
    private static final String SEPARATOR = "-"; // 组合key中使用的分隔符

    public final Map<String, List<Unit>> unitsMap;
    public final List<Map<String, List<Unit>>> exContentUnitsMap;

    /**
     * @param needEx 是否生成额外的单元映射
     */
    public FastStruct(String input, boolean needEx) {
        unitsMap = toUnitsMap(input);
        exContentUnitsMap = needEx ? toExContentUnitsMap(unitsMap) : null;
    }

    /**
     * 根据旧键及当前单元，生成新键
     */
    public static String getNewKey(String oldKey, Unit unit) {
        return (null != oldKey ? oldKey + SEPARATOR : "") + unit.text;
    }

    /**
     * 判断指定的单元列表在ExUnitsMap中是否有键
     */
    public static boolean hasKeyInExUnitsMap(List<Unit> units) {
        return null != units && units.size() >= MAX_COUNT;
    }

    /**
     * 获得首个单元
     */
    public Unit getFirstUnit() {
        return unitsMap.get(null).get(0);
    }

    /**
     * 获得额外 单元映射链 集
     */
    private static List<Map<String, List<Unit>>> toExContentUnitsMap(Map<String, List<Unit>> map) {
        List<Map<String, List<Unit>>> results = new LinkedList<>();
        while (true) {
            map = getLongKeyContentMap(map);
            if (map.isEmpty()) {
                break;
            }
            results.add(map);
        }
        return results;
    }

    /**
     * 将文本拆分为单元映射链
     *
     * @return 拆分后的单元  key为null时表示首单元
     */
    private static Map<String, List<Unit>> toUnitsMap(String input) {
        Map<String, List<Unit>> result = new HashMap<>();
        Unit curUnit, lastUnit = null;
        TextSeparator separator = new TextSeparator(input);
        while (null != (curUnit = separator.getNextUnit())) {
            // 放入普通列表
            getNonNullList(result, null, curUnit).add(curUnit);
            // 建立链
            if (null != lastUnit) {
                lastUnit.nextUnit = curUnit;
                curUnit.preUnit = lastUnit;
            } else {
                result.put(null, Collections.singletonList(curUnit));
            }
            lastUnit = curUnit;
        }
        return result;
    }

    /**
     * 获得长Key的内容映射
     */
    private static Map<String, List<Unit>> getLongKeyContentMap(Map<String, List<Unit>> input) {
        Map<String, List<Unit>> result = new HashMap<>();
        for (Map.Entry<String, List<Unit>> entry : input.entrySet()) {
            String oldKey = entry.getKey();
            List<Unit> list = entry.getValue();
            // 列表元素数目小于指定值则不作处理
            if (list.size() < MAX_COUNT) {
                continue;
            }
            // 非内容单元则不作处理(列表中全部元素优先级一样，只需取一个元素判断即可)
            if (list.get(0).isLowPriorityUnit()) {
                continue;
            }
            // 新映射中赋值
            for (Unit unit : list) {
                Unit nextUnit = unit.nextContentUnit();
                if (null == nextUnit) {
                    continue;
                }
                getNonNullList(result, oldKey, nextUnit).add(nextUnit);
            }
        }
        return result;
    }

    /**
     * 从Map中获得非空列表
     */
    private static List<Unit> getNonNullList(Map<String, List<Unit>> source, String oldKey, Unit unit) {
        String key = getNewKey(oldKey, unit);
        List<Unit> units = source.get(key);
        if (null == units) {
            source.put(key, units = new LinkedList<>());
        }
        return units;
    }
}
