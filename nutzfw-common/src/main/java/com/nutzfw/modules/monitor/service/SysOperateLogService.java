/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.monitor.entity.SysOperateLog;
import org.nutz.dao.Cnd;
import org.nutz.el.El;
import org.nutz.lang.segment.CharSegment;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月12日 15时55分14秒
 */
public interface SysOperateLogService extends BaseService<SysOperateLog> {

    void async(SysOperateLog operateLog);

    void sync(SysOperateLog operateLog);

    void sync(List<SysOperateLog> operateLogList);

    void log(String type, String tag, String source, CharSegment seg,
             Map<String, El> els, boolean param, boolean result, String consuming,
             Object[] args, Object re, Method method, Object obj,
             Throwable e);

    LayuiTableDataListVO listPage(int pageNum, int pageSize, Cnd cnd);
}
