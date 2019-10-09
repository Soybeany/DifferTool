package differ.fast.model;

import differ.fast.model.unit.AffixUnit;
import differ.fast.model.unit.ContentUnit;
import differ.fast.utils.Md5Utils;

import java.util.LinkedList;

/**
 * 段落信息
 * <br>Created by Soybeany on 2019/9/27.
 */
public class Paragraph implements ICharIndexOwner, IAffixOwner {

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
    public final LinkedList<ContentUnit> units = new LinkedList<>();

    /**
     * 段落中正文内容的md5值
     */
    public byte[] contentMd5;

    /**
     * 段落中其余内容的md5值
     */
    public byte[] othersMd5;

    /**
     * 表示换行的单元，可能为null(最后一段)
     */
    public AffixUnit newLineUnit;

    public Paragraph(int pIndex, int charIndex) {
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
        return Md5Utils.hasSameContent(contentMd5, ((Paragraph) other).contentMd5);
    }

    @Override
    public int getCharStartIndex() {
        return charIndex;
    }

    @Override
    public int getCharEndIndex() {
        return charEndIndex;
    }

    @Override
    public int getAffixCharStartIndex() {
        return charEndIndex;
    }

    @Override
    public int getAffixCharEndIndex() {
        return null != newLineUnit ? newLineUnit.charEndIndex : charEndIndex;
    }

    @Override
    public AffixUnit getAffix() {
        return newLineUnit;
    }

    /**
     * 正文外的内容是否相等
     */
    public boolean isOthersTheSame(Paragraph other) {
        return Md5Utils.hasSameContent(othersMd5, other.othersMd5);
    }

    public ContentUnit[] getContentUnitArr() {
        return units.toArray(new ContentUnit[0]);
    }
}
