package com.nutzfw.core.common.util.excel.dto;

import org.nutz.lang.util.NutMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/25  15:02
 * 描述此类：
 */
public class ReportDataDTO {
    /**
     * 0 单行数据 1大数据
     */
    private int dataType = 0;
    /**
     * 0
     **/
    private NutMap aloneData = new NutMap();
    /**
     * 1
     **/
    private List<Path> bigListData = new ArrayList<>();
    /**
     * 数据条数
     */
    private int dataCount = 0;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public List<Path> getBigListData() {
        return bigListData;
    }

    public void setBigListData(List<Path> bigListData) {
        this.bigListData = bigListData;
    }

    public NutMap getAloneData() {
        return aloneData;
    }

    public void setAloneData(NutMap aloneData) {
        this.aloneData = aloneData;
    }

    @Override
    public String toString() {
        return "ReportDataDTO{" +
                "dataType=" + dataType +
                ", aloneData=" + aloneData +
                ", bigListData=" + bigListData +
                ", dataCount=" + dataCount +
                '}';
    }
}
