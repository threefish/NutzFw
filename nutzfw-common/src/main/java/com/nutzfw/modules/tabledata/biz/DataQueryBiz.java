/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz;

import com.nutzfw.modules.tabledata.vo.TableColsVO;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/30
 * 描述此类：
 */
public interface DataQueryBiz {
    /**
     * 计算SQL
     *
     * @return
     */
    String getSql();

    /**
     * 取得前端表格控件表头
     *
     * @return
     */
    TableColsVO[] getCols();

    /**
     * 取得数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    List getData(int pageNum, int pageSize);

    /**
     * 取得共有多少条数据
     *
     * @return
     */
    int getCount();

    /**
     * 取得错误信息
     *
     * @return
     */
    String getErrorMsg();


    /**
     * 导出数据到excle
     *
     * @return
     */
    File exportDataToExcle(int type, int pageNum, int pageSize) throws IOException;
}
