package differ.fast.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * 匹配的范围
 * <br>Created by Soybeany on 2019/9/25.
 */
public class MatchScope {
    private static final Comparator<TerminalUnit> SOURCE_INDEX_COMPARATOR = new Comparator<TerminalUnit>() {
        @Override
        public int compare(TerminalUnit o1, TerminalUnit o2) {
            return o1.from.unitIndex - o2.from.unitIndex;
        }
    };

    private final TerminalUnit scope = new TerminalUnit(); // 整体范围
    public final TreeSet<TerminalUnit> activation = new TreeSet<>(SOURCE_INDEX_COMPARATOR); // 范围中激活的区域，即可用部分

    private TerminalUnit tmpUnit = new TerminalUnit();

    public MatchScope(Unit from, Unit to) {
        if (!to.gt(from)) {
            throw new RuntimeException("结束单元必须位于开始单元之后");
        }
        // 设置整体范围
        scope.from = from;
        scope.to = to;
        // 设置激活的区域
        TerminalUnit unit = new TerminalUnit();
        unit.from = from;
        unit.to = to;
        activation.add(unit);
    }

    /**
     * 设置整体范围中局部排除的范围
     */
    public void setExclude(TerminalUnit unit) {
        TerminalUnit outer = getOuterTerminalUnit(unit);
        if (null == outer) {
            throw new RuntimeException("无法找到匹配的单元");
        }
        TerminalUnit tUnit = new TerminalUnit();
        tUnit.to = outer.to;
        tUnit.from = unit.to;
        outer.to = unit.from;
        activation.add(tUnit);
    }

    /**
     * 获得指定单元集在范围中的部分
     *
     * @param offset 偏移量，用于留给此前已匹配的单元
     */
    public Set<Unit> getUnitsInScope(TreeSet<Unit> units, int offset) {
        return units.subSet(scope.from.getContentUnitWithOffset(offset), true, scope.to, true);
    }

    /**
     * 获得范围中能够匹配的最大数目
     *
     * @param offset 偏移量，用于留给此前已匹配的单元
     */
    public int getMaxMatchCount(Unit unit, int offset) {
        tmpUnit.from = unit.getContentUnitWithOffset(-offset);
        tmpUnit.to = unit;
        TerminalUnit outer = getOuterTerminalUnit(tmpUnit);
        return null != outer ? outer.to.contentUnitIndex - unit.contentUnitIndex : 0;
    }

    /**
     * 通过指定的单元查找到外层单元(即完全包含指定单元)
     *
     * @return 找到的单元，若找不到，返回null
     */
    private TerminalUnit getOuterTerminalUnit(TerminalUnit unit) {
        TerminalUnit floor = activation.floor(unit);
        if (null == floor || floor.to.lt(unit.to)) {
            return null;
        }
        return floor;
    }
}
