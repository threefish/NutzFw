package com.nutzfw.modules.sys.vo;

import com.nutzfw.modules.sys.entity.Dict;
import lombok.Builder;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/23
 * 描述此类：
 */
@Data
@Builder
public class DictVO {

    Integer id;

    Integer pid;

    String sysCode;

    String lable;

    String val;

    Integer showType;

    Boolean grouping;

    Boolean edit;

    Boolean internal;

    String mark;

    Integer shortNo;

    Boolean defaultVal;

    Boolean chkDisabled;

    Boolean isParent;

    String extra1;
    String extra2;
    String extra3;
    String extra4;
    String extra5;
    String extra6;
    String extra7;
    String extra8;
    String extra9;
    String extra10;

    public DictVO() {

    }

    public DictVO(Integer id, Integer pid, String sysCode, String lable, String val, Integer showType, Boolean grouping, Boolean internal, Boolean edit, String mark, Integer shortNo, Boolean defaultVal, Boolean chkDisabled, Boolean isParent, String extra1, String extra2, String extra3, String extra4, String extra5, String extra6, String extra7, String extra8, String extra9, String extra10) {
        this.id = id;
        this.pid = pid;
        this.sysCode = sysCode;
        this.lable = lable;
        this.val = val;
        this.showType = showType;
        this.grouping = grouping;
        this.internal = internal;
        this.mark = mark;
        this.shortNo = shortNo;
        this.edit = edit;
        this.defaultVal = defaultVal;
        this.chkDisabled = chkDisabled;
        this.isParent = isParent;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
        this.extra4 = extra4;
        this.extra5 = extra5;
        this.extra6 = extra6;
        this.extra7 = extra7;
        this.extra8 = extra8;
        this.extra9 = extra9;
        this.extra10 = extra10;
    }

    public static DictVO create(Dict dict, boolean hasChilds) {
        return new DictVO(
                dict.getId(),
                dict.getPid(),
                dict.getSysCode(),
                dict.getLable(),
                dict.getVal(),
                dict.getShowType(),
                dict.isGrouping(),
                dict.isInternal(),
                dict.isEdit(),
                dict.getMark(),
                dict.getShortNo(),
                dict.isDefaultVal(),
                false,
                hasChilds,
                dict.getExtra1(),
                dict.getExtra2(),
                dict.getExtra3(),
                dict.getExtra4(),
                dict.getExtra5(),
                dict.getExtra6(),
                dict.getExtra7(),
                dict.getExtra8(),
                dict.getExtra9(),
                dict.getExtra10()
        );
    }

}
