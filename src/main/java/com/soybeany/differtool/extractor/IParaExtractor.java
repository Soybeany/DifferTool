package com.soybeany.differtool.extractor;

import com.soybeany.differtool.model.Para;

import java.util.List;

/**
 * <br>Created by Soybeany on 2019/10/14.
 */
public interface IParaExtractor {

    /**
     * 将指定字符串格式化
     */
    Result format(String text) throws Exception;

    class Result {
        public Para[] paras;
        public int unitCount;

        public Result(List<Para> paras, int unitCount) {
            this.paras = paras.toArray(new Para[0]);
            this.unitCount = unitCount;
        }
    }
}
