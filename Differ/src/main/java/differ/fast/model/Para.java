package differ.fast.model;

import differ.fast.utils.Md5Utils;

import java.util.LinkedList;

/**
 * 段落
 * <br>Created by Soybeany on 2019/10/12.
 */
public class Para {

    /**
     * 段落的下标
     */
    public int pIndex;

    /**
     * 以字符计算的下标(开始)
     */
    public int charIndex;

    /**
     * 以字符计算的下标(结束)
     */
    public int charEndIndex;

    /**
     * 段落中包含的内容单元
     */
    public final LinkedList<Unit> units = new LinkedList<>();

    /**
     * 段落的md5值
     */
    public byte[] md5;

    /**
     * 换行的单元
     */
    public Unit newlineUnit;

    public Para(int pIndex, int charIndex) {
        this.pIndex = pIndex;
        this.charIndex = charIndex;
    }

    @Override
    public String toString() {
        return units.toString();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object other) {
        return Md5Utils.hasSameContent(md5, ((Para) other).md5);
    }

    public Unit[] getUnitArr() {
        return units.toArray(new Unit[0]);
    }
}
