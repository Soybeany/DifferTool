package differ.fast.model;

import differ.fast.model.unit.AffixUnit;

/**
 * 附属单元持有者
 * <br>Created by Soybeany on 2019/10/5.
 */
public interface IAffixOwner {

    /**
     * 获得附属单元的开始字符下标
     */
    int getAffixCharStartIndex();

    /**
     * 获得附属单元的结束字符下标
     */
    int getAffixCharEndIndex();

    /**
     * 获得附属单元，允许为null
     */
    AffixUnit getAffix();
}
