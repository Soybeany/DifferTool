package differ.fast.treat;

import differ.fast.model.CompareResult;
import differ.fast.model.TerminalUnit;

import java.util.*;

/**
 * 用于确定稳定单元分区的工具类
 * <br>Created by Soybeany on 2019/9/24.
 */
public class SectionUtils {

    private static final Comparator<Node> COMPARATOR_SOURCE = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.result.getNaturalOrderInSource() - o2.result.getNaturalOrderInSource();
        }
    };

    private static final Comparator<Node> COMPARATOR_TARGET = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.result.getNaturalOrderInTarget() - o2.result.getNaturalOrderInTarget();
        }
    };

    /**
     * @param results 稳定的单元集合
     */
    public static List<CompareResult> toSections(Set<CompareResult> results) {
        TreeSet<Node> naturalOrderSet = new TreeSet<>(COMPARATOR_TARGET);
        TreeSet<Node> matchSet = new TreeSet<>(COMPARATOR_SOURCE);
        for (CompareResult result : results) {
            onTraverseNodes(naturalOrderSet, matchSet, result);
        }
        List<CompareResult> result = new LinkedList<>();
        for (Node node : naturalOrderSet) {
            // 添加匹配的单元
            if (node.isMatchPoint) {
                result.add(node.result);
            }
            // 添加新增的单元
            else {
                CompareResult add = new CompareResult();
                add.target = node.result.target;
                result.add(add);
            }
            // 添加被删除的单元
            if (null != node.removedSourceUnits) {
                for (TerminalUnit terminalUnit : node.removedSourceUnits) {
                    CompareResult delete = new CompareResult();
                    delete.source = terminalUnit;
                    result.add(delete);
                }
            }
        }
        return result;
    }

    /**
     * 遍历节点
     */
    private static void onTraverseNodes(TreeSet<Node> naturalOrderSet, TreeSet<Node> matchSet, CompareResult result) {
        Node node = new Node(result);
        // 放入自然序的集合
        naturalOrderSet.add(node);
        // 分析对比结果
        Node lower = matchSet.lower(node);
        if (isNodeUnableAddToMatchSet(lower, node) || isNodeUnableAddToMatchSet(matchSet.higher(node), node)) {
            if (null == lower) {
                return;
            }
            if (null == lower.removedSourceUnits) {
                lower.removedSourceUnits = new LinkedList<>();
            }
            lower.removedSourceUnits.add(result.source);
        }
        // 够格放入匹配集合
        else {
            node.isMatchPoint = true;
            matchSet.add(node);
        }
    }

    /**
     * 判断节点是否能被添加到匹配集合中
     */
    private static boolean isNodeUnableAddToMatchSet(Node contrast, Node node) {
        if (null == contrast) {
            return false;
        }
        int sourceOffset = contrast.result.getNaturalOrderInSource() - node.result.getNaturalOrderInSource();
        int targetOffset = contrast.result.getNaturalOrderInTarget() - node.result.getNaturalOrderInTarget();
        return sourceOffset * targetOffset < 0;
    }

    /**
     * 节点
     */
    private static class Node {

        /**
         * 标记是否匹配点
         */
        boolean isMatchPoint;

        /**
         * 对比结果
         */
        CompareResult result;

        /**
         * 记录已被移除的源单元，用于遍历时还原其位置
         */
        List<TerminalUnit> removedSourceUnits;

        Node(CompareResult result) {
            this.result = result;
        }
    }
}
