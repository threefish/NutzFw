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
