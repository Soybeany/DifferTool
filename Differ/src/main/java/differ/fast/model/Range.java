package differ.fast.model;

/**
 * 区间范围
 */
public class Range {
    /**
     * 开始的下标(包含)
     */
    public int from = -1;

    /**
     * 结束的下标(不包含)
     */
    public int to = -1;

    /**
     * 范围偏移指定距离
     *
     * @param maxTo 允许最大偏移到的下标
     */
    public void shift(int offset, int maxTo) {
        from = Math.min(from + offset, maxTo);
        to = Math.min(to + offset, maxTo);
    }

    public Range setup(int from, int to) {
        this.from = from;
        this.to = to;
        return this;
    }

    public Range to(int to) {
        this.to = to;
        return this;
    }

    public int length() {
        return to - from;
    }
}
