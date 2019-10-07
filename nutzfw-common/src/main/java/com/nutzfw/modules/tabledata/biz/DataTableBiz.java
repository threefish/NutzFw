/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz;

import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/7
 * 描述此类：
 */
public interface DataTableBiz {


    String synchronize(int id, int synchronizeType);

    void save(DataTable newTable, DataTable oldTable, TableFields[] list);
}
