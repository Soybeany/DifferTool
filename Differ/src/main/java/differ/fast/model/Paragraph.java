package differ.fast.model;

import differ.fast.utils.Md5Utils;

import java.util.LinkedList;

/**
 * 段落信息
 * <br>Created by Soybeany on 2019/9/27.
 */
public class Paragraph {

    /**
     * 段落中包含的单元(包含)
     */
    public final LinkedList<Unit> units = new LinkedList<>();

    /**
     * 段落内容的md5值
     */
    public byte[] md5;

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return Md5Utils.hasSameContent(md5, ((Paragraph) obj).md5);
    }

    @Override
    public String toString() {
        return units.toString();
    }
}
