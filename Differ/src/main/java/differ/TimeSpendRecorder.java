package differ;

/**
 * <br>Created by Soybeany on 2019/9/25.
 */
public class TimeSpendRecorder {

    public static final TimeSpendRecorder INSTANCE = new TimeSpendRecorder();

    private Long startTimestamp;
    private Long lastTimestamp;

    public void start() {
        startTimestamp = lastTimestamp = System.currentTimeMillis();
        System.out.println("开始计时");
    }

    public void stop() {
        System.out.println("结束计时,总耗时:" + getSpendText(System.currentTimeMillis(), startTimestamp));
        startTimestamp = null;
        lastTimestamp = null;
    }

    public void record(String desc) {
        if (null == lastTimestamp) {
            throw new RuntimeException("请先调用start方法");
        }
        long curTimestamp = System.currentTimeMillis();
        System.out.println(desc + "耗时:" + getSpendText(curTimestamp, lastTimestamp));
        lastTimestamp = curTimestamp;
    }

    private String getSpendText(long curTimeStamp, long lastTimestamp) {
        long spend = curTimeStamp - lastTimestamp;
        return spend + "ms";
    }
}
