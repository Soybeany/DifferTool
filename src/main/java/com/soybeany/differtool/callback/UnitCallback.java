package com.soybeany.differtool.callback;

import com.soybeany.differtool.compare.CompareToolImpl;
import com.soybeany.differtool.model.Change;
import com.soybeany.differtool.model.Range;
import com.soybeany.differtool.model.Unit;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/10/11.
 */
public class UnitCallback implements CompareToolImpl.ICallback<Unit> {

    private List<Change.Index> changes;

    public UnitCallback(List<Change.Index> changes) {
        this.changes = changes;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onElementSame(LinkedList<Change.Obj<Unit>> objs) {
    }

    @Override
    public void onElementChange(int changeType, LinkedList<Change.Obj<Unit>> objs) {
        Change.Obj<Unit> firstUnit = objs.getFirst();
        Change.Obj<Unit> lastUnit = objs.getLast();
        Change.Index change = new Change.Index(changeType, new Range(), new Range());
        change.count = objs.size();
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
    public void onFinal() {
    }

    private int getIndex(boolean isPosAtEnd, Unit unit) {
        return isPosAtEnd ? unit.charEndIndex : unit.charStartIndex;
    }

    private void setRange(Range range, Unit first, Unit end) {
        range.from = first.charStartIndex;
        range.to = end.charEndIndex;
    }
}
