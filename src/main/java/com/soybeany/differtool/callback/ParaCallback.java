package com.soybeany.differtool.callback;

import com.soybeany.differtool.compare.CompareToolImpl;
import com.soybeany.differtool.compare.ICompareTool;
import com.soybeany.differtool.model.Change;
import com.soybeany.differtool.model.Para;
import com.soybeany.differtool.model.Range;
import com.soybeany.differtool.model.Unit;
import com.soybeany.differtool.utils.IWeightProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2019/10/12.
 */
public class ParaCallback implements CompareToolImpl.ICallback<Para> {

    private List<Change.Index> changes;
    private ICompareTool compareTool;
    private IWeightProvider<Unit> weight;

    private UnitCallback callback;
    private Change.Index mLastChange; // 上一次使用的变更

    public ParaCallback(List<Change.Index> changes, ICompareTool compareTool, IWeightProvider<Unit> weight) {
        this.changes = changes;
        this.compareTool = compareTool;
        this.weight = weight;

        callback = new UnitCallback(changes);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onElementSame(LinkedList<Change.Obj<Para>> objs) {
        for (Change.Obj<Para> obj : objs) {
            compareNewlineUnit(obj);
        }
    }

    @Override
    public void onElementChange(int changeType, LinkedList<Change.Obj<Para>> objs) {
        for (Change.Obj<Para> obj : objs) {
            compareTool.compare(obj.source.getUnitArr(), obj.target.getUnitArr(), weight, callback);
            compareNewlineUnit(obj);
        }
    }

    @Override
    public void onFinal() {

    }

    private void compareNewlineUnit(Change.Obj<Para> obj) {
        Unit sNewlineUnit = obj.source.newlineUnit;
        Unit tNewlineUnit = obj.target.newlineUnit;
        // 两者均为null，相等，不作额外处理
        if (sNewlineUnit == null && tNewlineUnit == null) {
            return;
        }
        int pointIndex;
        // 删除
        if (tNewlineUnit == null) {
            pointIndex = obj.target.charEndIndex;
            saveChange(Change.DELETE, sNewlineUnit.charStartIndex, sNewlineUnit.charEndIndex, pointIndex, pointIndex);
        }
        // 新增
        else if (sNewlineUnit == null) {
            pointIndex = obj.source.charEndIndex;
            saveChange(Change.ADD, pointIndex, pointIndex, tNewlineUnit.charStartIndex, tNewlineUnit.charEndIndex);
        }
        // 修改
        else if (!sNewlineUnit.equals(tNewlineUnit)) {
            // todo 可进一步的找出不同（子类重写该步的实现，再进行对比）
            saveChange(Change.MODIFY, sNewlineUnit.charStartIndex, sNewlineUnit.charEndIndex, tNewlineUnit.charStartIndex, tNewlineUnit.charEndIndex);
        }
    }

    private void saveChange(int changeType, int sCharStartIndex, int sCharEndIndex, int tCharStartIndex, int tCharEndIndex) {
        Change.Index change = getSuitableChange(changeType, sCharStartIndex, tCharStartIndex);
        change.source.to = sCharEndIndex;
        change.target.to = tCharEndIndex;
    }

    /**
     * 获得适当的变更(重用/新建)
     */
    private Change.Index getSuitableChange(int changeType, int sourceCharIndex, int targetCharIndex) {
        // 若满足重用条件，则继续使用上一变更
        if (null != mLastChange
                && mLastChange.type == changeType
                && mLastChange.source.to == sourceCharIndex
                && mLastChange.target.to == targetCharIndex) {
            mLastChange.count++;
            return mLastChange;
        }
        // 创建并初始化变更
        mLastChange = new Change.Index(changeType, new Range(), new Range());
        mLastChange.source.setup(sourceCharIndex, sourceCharIndex);
        mLastChange.target.setup(targetCharIndex, targetCharIndex);
        changes.add(mLastChange);
        return mLastChange;
    }
}
