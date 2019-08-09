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
