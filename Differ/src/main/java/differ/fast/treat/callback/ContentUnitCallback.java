package differ.fast.treat.callback;

import differ.fast.model.Change;
import differ.fast.model.unit.ContentUnit;

import java.util.List;

/**
 * 简单的回调(对象)
 * <br>Created by Soybeany on 2019/10/7.
 */
public class ContentUnitCallback extends BaseCallback<ContentUnit> {
    public ContentUnitCallback(List<Change.Index> changes) {
        super(changes);
    }

    @Override
    public void onElementModify(ContentUnit source, ContentUnit target) {
        saveChange(Change.MODIFY, source.charIndex, source.charEndIndex, target.charIndex, target.charEndIndex);
        super.onElementModify(source, target);
    }
}
