import com.alibaba.druid.filter.config.ConfigTools;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018/12/22 0022
 */
public class MySqlPass {
    public static void main(String[] args) {
        try {
            String password = "123456";
            String[] arr = ConfigTools.genKeyPair(512);
            System.out.println("privateKey:" + arr[0]);
            System.out.println("publicKey:" + arr[1]);
            System.out.println("password:" + ConfigTools.encrypt(arr[0], password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
