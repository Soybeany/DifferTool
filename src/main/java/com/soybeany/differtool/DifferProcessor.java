package com.soybeany.differtool;

import com.soybeany.differtool.callback.ParaCallback;
import com.soybeany.differtool.compare.CompareToolImpl;
import com.soybeany.differtool.compare.ICompareTool;
import com.soybeany.differtool.extractor.IParaExtractor;
import com.soybeany.differtool.extractor.ParaExtractor;
import com.soybeany.differtool.model.Change;
import com.soybeany.differtool.model.Para;
import com.soybeany.differtool.model.Unit;
import com.soybeany.differtool.utils.IWeightProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * 差异处理器，工具类的入口
 * <p>
 * 预处理文本为 段落、单元 的结构
 * 按段落分段，并计算段落的md5值
 * 对段落列表进行差异分析
 * 对差异段落中的单元进行差异对比
 * <br>Created by Soybeany on 2019/9/29.
 */
public class DifferProcessor {

    /**
     * 计算并获得单元级别的差异
     */
    public static List<Change.Index> calculate(String source, String target) throws Exception {
        return calculate(source, target, new Config());
    }

    public static List<Change.Index> calculate(String source, String target, Config config) throws Exception {
        // 文本预处理
        ParaExtractor.Result sResult = config.paraExtractor.format(source);
        ParaExtractor.Result tResult = config.paraExtractor.format(target);
        // 段落对比
        LinkedList<Change.Index> result = new LinkedList<>();
        config.para.compareTool.compare(sResult.paras, tResult.paras, config.para.weight, config.getParaCallback(result));
        return result;
    }

    /**
     * 打印结果
     */
    public static void print(List<Change.Index> changes, String input1, String input2) {
        for (Change.Index change : changes) {
            String msg = "type:" + change.type + "  count:" + change.count
                    + "\n";
            msg += "source:" + replaceNewLine(input1.substring(change.source.from, change.source.to)) + "(" + change.source.from + "~" + change.source.to + ")"
                    + "\n";
            msg += "target:" + replaceNewLine(input2.substring(change.target.from, change.target.to)) + "(" + change.target.from + "~" + change.target.to + ")";
            System.out.println(msg + "\n");
        }
    }

    private static String replaceNewLine(String input) {
        return input.replaceAll("\n", "\\\\n");
    }

    /**
     * 配置
     */
    public static class Config {

        public final Detail<Para> para = new Detail<>();
        public final Detail<Unit> unit = new Detail<>();

        IParaExtractor paraExtractor = new ParaExtractor();

        /**
         * 设置段落提取器
         */
        public Config paraExtractor(IParaExtractor extractor) {
            if (null == extractor) {
                throw new RuntimeException("段落提取器不能为null");
            }
            this.paraExtractor = extractor;
            return this;
        }

        /**
         * 使用内置的“优先级单元权重”
         */
        public Config withPriorityUnitWeight() {
            unit.weight(PriorityUnitWeightProvider.get());
            return this;
        }

        /**
         * 获得段落回调，若有需要，可通过继承重写
         */
        public ParaCallback getParaCallback(LinkedList<Change.Index> result) {
            return new ParaCallback(result, unit.compareTool, unit.weight);
        }
    }

    public static class Detail<T> {
        /**
         * 对比工具
         */
        ICompareTool compareTool = new CompareToolImpl(50);

        /**
         * 权重
         */
        @SuppressWarnings("unchecked")
        IWeightProvider<T> weight = IWeightProvider.Std.get();

        /**
         * 设置对比工具
         */
        public Detail<T> compareTool(ICompareTool compareTool) {
            if (null == compareTool) {
                throw new RuntimeException("对比工具不能为null");
            }
            this.compareTool = compareTool;
            return this;
        }

        /**
         * 设置权重
         */
        public Detail<T> weight(IWeightProvider<T> weight) {
            if (null == weight) {
                throw new RuntimeException("权重提供者不能为null");
            }
            this.weight = weight;
            return this;
        }
    }

    private static class PriorityUnitWeightProvider implements IWeightProvider<Unit> {
        private static final PriorityUnitWeightProvider mInstance = new PriorityUnitWeightProvider();

        static PriorityUnitWeightProvider get() {
            return mInstance;
        }

        @Override
        public int getAddAction() {
            return 1;
        }

        @Override
        public int getDeleteAction() {
            return 1;
        }

        @Override
        public int getModifyAction() {
            return 1;
        }

        @Override
        public int getAddElement(Unit ele) {
            return ele.priority;
        }

        @Override
        public int getDeleteElement(Unit ele) {
            return ele.priority;
        }

        @Override
        public int getModifyElement(Unit ele1, Unit ele2) {
            if (ele1.priority > ele2.priority) {
                return 2 * ele1.priority - ele2.priority;
            }
            return 2 * ele2.priority - ele1.priority;
//        return Math.max(ele1.priority, ele2.priority) + Math.abs(ele1.priority - ele2.priority);
        }
    }
}
