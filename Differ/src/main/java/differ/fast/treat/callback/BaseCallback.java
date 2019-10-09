package differ.fast.treat.callback;

import differ.fast.model.Change;
import differ.fast.model.IAffixOwner;
import differ.fast.model.ICharIndexOwner;
import differ.fast.model.Range;
import differ.fast.model.unit.AffixUnit;
import differ.fast.utils.LevenshteinUtils;

import java.util.List;

/**
 * <br>Created by Soybeany on 2019/10/9.
 */
public abstract class BaseCallback<T extends ICharIndexOwner & IAffixOwner> implements LevenshteinUtils.ICallback<T> {
    /**
     * 变更的列表，使用unit中的charIndex作为下标，适用于字符串中定位(= from 与 < to)
     */
    public final List<Change.Index> changes;
    private Change.Index mLastChange; // 上一次使用的变更
    private int changeCount;

    protected BaseCallback(List<Change.Index> changes) {
        this.changes = changes;
    }

    @Override
    public void onStart() {
        // 不需处理
    }

    @Override
    public void onElementSame(T source, T target) {
        handleAffixUnit(source, target);
    }

    @Override
    public void onElementAdd(int addPos, T source, T target) {
        int pointIndex = addPos < 0 ? source.getCharStartIndex() : source.getAffixCharEndIndex();
        saveChange(Change.ADD, pointIndex, pointIndex, target.getCharStartIndex(), target.getAffixCharEndIndex());
    }

    @Override
    public void onElementModify(T source, T target) {
        handleAffixUnit(source, target);
    }

    @Override
    public void onElementDelete(int delPos, T source, T target) {
        int pointIndex = delPos < 0 ? target.getCharStartIndex() : target.getAffixCharEndIndex();
        saveChange(Change.DELETE, source.getCharStartIndex(), source.getAffixCharEndIndex(), pointIndex, pointIndex);
    }

    @Override
    public void onFinal(int distance) {
        // 不需处理
    }

    /**
     * 获得变更的数目(单元)
     */
    public int getChangeCount() {
        return changeCount;
    }

    // ****************************************子类方法****************************************

    /**
     * 保存变更
     */
    protected void saveChange(byte changeType, int sCharStartIndex, int sCharEndIndex, int tCharStartIndex, int tCharEndIndex) {
        Change.Index change = getSuitableChange(changeType, sCharStartIndex, tCharStartIndex);
        change.source.to = sCharEndIndex;
        change.target.to = tCharEndIndex;
        changeCount++;
    }

    protected void handleAffixUnit(IAffixOwner source, IAffixOwner target) {
        AffixUnit sourceAffix = source.getAffix();
        AffixUnit targetAffix = target.getAffix();
        // 两者均为null，相等，不作额外处理
        if (sourceAffix == null && targetAffix == null) {
            return;
        }
        int pointIndex;
        // 删除
        if (targetAffix == null) {
            pointIndex = target.getAffixCharStartIndex();
            saveChange(Change.DELETE, sourceAffix.charIndex, sourceAffix.charEndIndex, pointIndex, pointIndex);
        }
        // 新增
        else if (sourceAffix == null) {
            pointIndex = source.getAffixCharStartIndex();
            saveChange(Change.ADD, pointIndex, pointIndex, targetAffix.charIndex, targetAffix.charEndIndex);
        }
        // 修改
        else if (!sourceAffix.equals(targetAffix)) {
            // todo 可进一步的找出不同（子类重写该步的实现，再进行对比）
            saveChange(Change.MODIFY, sourceAffix.charIndex, sourceAffix.charEndIndex, targetAffix.charIndex, targetAffix.charEndIndex);
        }
    }

    // ****************************************内部方法****************************************

    /**
     * 获得适当的变更(重用/新建)
     */
    private Change.Index getSuitableChange(byte changeType, int sourceCharIndex, int targetCharIndex) {
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
