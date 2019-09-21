/**
 * <br>Created by Soybeany on 2019/9/19.
 */
public class RentUtils {

    public static void main(String[] args) {
        Thing tv = new Thing("电视机", 600, 0, 5 * 12);
        Thing table = new Thing("木沙发", 1500, 0, 10 * 12);
        Thing ac = new Thing("空调", 1800, 0, 10 * 12);
    }

    /**
     * 获得出租金额
     */
    private static int getRentAmount(Landlord landlord, Tenant tenant, Thing... things) {
        return 0;
    }

    /**
     * 租客
     */
    private static class Tenant {
        /**
         * 人数
         */
        int num;

        /**
         * 租的时间
         */
        int rentMonth;
    }

    private static class Landlord {
        /**
         * 无家具的底价
         */
        int basePrise;

        /**
         * 回本速率(0 ~ 1  0：不回本  1：最短的1个月回本)
         */
        float paybackRate = 0.6f;
    }

    /**
     * 额外购买的东西
     */
    private static class Thing {

        // ****************************************商品信息****************************************

        /**
         * 描述
         */
        String desc;

        // ****************************************商品价格****************************************

        /**
         * 价格(单位:元)
         */
        int price;

        /**
         * 折旧后的最低价格(单位:元)
         */
        int minPrice;

        // ****************************************商品寿命****************************************

        /**
         * 已使用的时间(单位:月)
         */
        int usedTime;

        /**
         * 使用寿命(单位:月)
         */
        int lifeTime;

        Thing(String desc, int price, int usedTime, int lifeTime) {
            this(desc, price, 0.1f, usedTime, lifeTime);
        }

        Thing(String desc, int price, float minDepreciate, int usedTime, int lifeTime) {
            this.desc = desc;
            this.price = price;
            this.minPrice = (int) (minDepreciate * price);
            this.usedTime = usedTime;
            this.lifeTime = lifeTime;
        }

        /**
         * 获得每个月
         *
         * @param month
         * @return
         */
        float getPricePerInMonth(int month) {
            return 0;
        }
    }
}
