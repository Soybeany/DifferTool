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

    public Range setup(int from, int to) {
        this.from = from;
        this.to = to;
        return this;
    }

    /**
     * 设置只有1偏移量的范围
     */
    public void setupWith1Offset(int from) {
        setup(from, from + 1);
    }

    public int length() {
        return to - from;
    }
}
