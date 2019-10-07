/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.util;

import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.lang.util.NutMap;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/29
 */
public class DataUtil {

    public static NutMap coverInsertData(NutMap nutMap, UserAccount userAccount) {
        nutMap.put("create_by_date", new Date());
        nutMap.put("create_by_name", userAccount.getRealName());
        nutMap.put("create_by_userid", userAccount.getUserid());
        nutMap.put("update_version", 0);
        return nutMap;
    }

    public static NutMap coverUpdateData(NutMap nutMap, UserAccount userAccount, int oldVersion) {
        nutMap.put("update_by_date", new Date());
        nutMap.put("update_by_name", userAccount.getRealName());
        nutMap.put("update_by_userid", userAccount.getUserid());
        nutMap.put("update_version", oldVersion + 1);
        return nutMap;
    }
}
