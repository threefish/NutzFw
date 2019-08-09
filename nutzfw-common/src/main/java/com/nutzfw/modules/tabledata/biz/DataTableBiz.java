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
