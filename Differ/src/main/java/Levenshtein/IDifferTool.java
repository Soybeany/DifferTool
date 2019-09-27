package Levenshtein;

/**
 * 差异对比工具
 * <br>Created by Soybeany on 2019/9/26.
 */
public interface IDifferTool<T> {

    int calculate(T[] input1, T[] input2);

}
