/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:29:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
