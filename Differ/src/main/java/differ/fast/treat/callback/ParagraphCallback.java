package differ.fast.treat.callback;

import differ.fast.model.Change;
import differ.fast.model.Paragraph;
import differ.fast.model.unit.ContentUnit;
import differ.fast.utils.ImprovedLSUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <br>Created by Soybeany on 2019/10/8.
 */
public class ParagraphCallback extends BaseCallback<Paragraph> {
    private final ContentUnitCallback mCallback = new ContentUnitCallback(changes);

    public ParagraphCallback() {
        super(new LinkedList<Change.Index>());
    }

    @Override
    public void onElementSame(Paragraph source, Paragraph target) {
        // 正文一致，正文外的内容也一致，整个段落不作额外处理
        if (!source.isOthersTheSame(target)) {
            // 将正文外的内容作细节对比
            Iterator<ContentUnit> iterator = target.units.iterator();
            for (ContentUnit unit : source.units) {
                handleAffixUnit(unit, iterator.next());
            }
        }
        super.onElementSame(source, target);
    }

    @Override
    public void onElementAdd(int addPos, Paragraph source, Paragraph target) {
        super.onElementAdd(addPos, source, target);
    }

    @Override
    public void onElementDelete(int delPos, Paragraph source, Paragraph target) {
        super.onElementDelete(delPos, source, target);
    }

    @Override
    public void onElementModify(Paragraph source, Paragraph target) {
        // 修改则进一步找出差别
        ImprovedLSUtils.compare(source.getContentUnitArr(), target.getContentUnitArr(), mCallback);
        super.onElementModify(source, target);
    }

    @Override
    public void onFinal(int distance) {
        Collections.sort(changes, new Comparator<Change.Index>() {
            @Override
            public int compare(Change.Index o1, Change.Index o2) {
                return o1.target.from - o2.target.from;
            }
        });
    }

    /**
     * 获得发生变更的全部单元数目
     */
    public int getChangedUnitCount() {
        return getChangeCount() + getChangedContentUnitCount();
    }

    /**
     * 获得发生变更的内容单元数目
     */
    public int getChangedContentUnitCount() {
        return mCallback.getChangeCount();
    }
}
