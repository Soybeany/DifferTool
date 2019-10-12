package differ.fast.treat.callback;

import differ.fast.model.Change;
import differ.fast.model.Range;
import differ.fast.model.Unit;
import differ.fast.utils.ImprovedLSUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/10/11.
 */
public class UnitCallback implements ImprovedLSUtils.ICallback<Unit> {

    public final List<Change.Index> changes = new LinkedList<>();

    @Override
    public void onStart() {

    }

    @Override
    public void onElementHandled(LinkedList<Change.Obj<Unit>> objs) {
        Change.Obj<Unit> firstUnit = objs.getFirst();
        byte changeType = firstUnit.type;
        // 若元素相同，不作处理
        if (Change.SAME == changeType) {
            return;
        }
        Change.Obj<Unit> lastUnit = objs.getLast();
        Change.Index change = new Change.Index(changeType, new Range(), new Range());
        switch (changeType) {
            case Change.ADD:
                change.source.from = change.source.to = getIndex(firstUnit.isPosAtEnd, firstUnit.source);
                setRange(change.target, firstUnit.target, lastUnit.target);
                break;
            case Change.DELETE:
                setRange(change.source, firstUnit.source, lastUnit.source);
                change.target.from = change.target.to = getIndex(firstUnit.isPosAtEnd, firstUnit.target);
                break;
            case Change.MODIFY:
                setRange(change.source, firstUnit.source, lastUnit.source);
                setRange(change.target, firstUnit.target, lastUnit.target);
                break;
        }
        changes.add(change);
    }

    @Override
    public void onFinal(int distance) {

    }

    private int getIndex(boolean isPosAtEnd, Unit unit) {
        return isPosAtEnd ? unit.charEndIndex : unit.charStartIndex;
    }

    private void setRange(Range range, Unit first, Unit end) {
        range.from = first.charStartIndex;
        range.to = end.charEndIndex;
    }
}
