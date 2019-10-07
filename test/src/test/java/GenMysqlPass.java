/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:29:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/29
 * 描述此类：
 */
public class GenMysqlPass {
    public static void main(String[] args) throws Exception {
        String password = "123456";
        String[] arr = ConfigTools.genKeyPair(512);
        String encryptPass = ConfigTools.encrypt(arr[0], password);
        System.out.println("******************************请替换jdbc.properties中对应的数据************************************");
        System.out.println(arr[0]);
        System.out.println("db.connectionProperties=config.decrypt=true;config.decrypt.key=" + arr[1]);
        System.out.println("db.password=" + encryptPass);
        System.out.println("******************************************************************");
        System.out.println(ConfigTools.decrypt(arr[1], encryptPass));
    }

}
