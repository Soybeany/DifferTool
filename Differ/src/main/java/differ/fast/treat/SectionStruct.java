package differ.fast.treat;

import differ.fast.model.CompareResult;
import differ.fast.model.TerminalUnit;

import java.util.*;

/**
 * 用于确定稳定单元组的分区结构
 * <br>Created by Soybeany on 2019/9/24.
 */
public class SectionStruct {

    private static final Comparator<Node> COMPARATOR = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.result.getNaturalOrderInSource() - o2.result.getNaturalOrderInSource();
        }
    };

    private TreeSet<Node> naturalOrderSet = new TreeSet<>(COMPARATOR);
    private TreeSet<Node> matchSet = new TreeSet<>(COMPARATOR);

    /**
     * @param results 稳定的单元集合
     */
    public SectionStruct(Set<CompareResult> results) {
        Node node, lower;
        for (CompareResult result : results) {
            node = new Node(result);
            // 放入自然序的集合
            naturalOrderSet.add(node);
            // 分析对比结果
            lower = matchSet.lower(node);
            if (isNodeUnableAddToMatchSet(lower, node) || isNodeUnableAddToMatchSet(matchSet.higher(node), node)) {
                if (null == lower) {
                    continue;
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
    }


    /**
     * 判断节点是否能被添加到匹配集合中
     */
    private boolean isNodeUnableAddToMatchSet(Node contrast, Node node) {
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
