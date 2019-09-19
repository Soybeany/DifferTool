package differ.util;

/**
 * 内容单元 的访问器
 * <br>Created by Soybeany on 2019/9/19.
 */
public interface IContentUnitAccessor {

    /**
     * 根据基准单元，获得
     */
    Unit getUnitWithOffset(Unit base, int offset);

}
