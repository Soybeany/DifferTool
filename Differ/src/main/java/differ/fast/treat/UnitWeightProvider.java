package differ.fast.treat;

import differ.fast.model.Unit;
import differ.fast.utils.IWeightProvider;

/**
 * <br>Created by Soybeany on 2019/10/12.
 */
public class UnitWeightProvider implements IWeightProvider<Unit> {
    private static final UnitWeightProvider mInstance = new UnitWeightProvider();

    public static UnitWeightProvider get() {
        return mInstance;
    }

    @Override
    public int getAddAction() {
        return 1;
    }

    @Override
    public int getDeleteAction() {
        return 1;
    }

    @Override
    public int getModifyAction() {
        return 1;
    }

    @Override
    public int getAddElement(Unit ele) {
        return ele.priority;
    }

    @Override
    public int getDeleteElement(Unit ele) {
        return ele.priority;
    }

    @Override
    public int getModifyElement(Unit ele1, Unit ele2) {
        if (ele1.priority > ele2.priority) {
            return 2 * ele1.priority - ele2.priority;
        }
        return 2 * ele2.priority - ele1.priority;
//        return Math.max(ele1.priority, ele2.priority) + Math.abs(ele1.priority - ele2.priority);
    }
}
