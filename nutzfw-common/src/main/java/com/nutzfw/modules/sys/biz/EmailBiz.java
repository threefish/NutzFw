/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.sys.entity.MailBody;
import org.apache.commons.mail.EmailException;
import org.nutz.dao.Cnd;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：
 */
public interface EmailBiz {

    LayuiTableDataListVO query(Cnd cnd, int pageNum, int pageSize);

    boolean send(String to, String subject, String html) throws Throwable;

    boolean send(List<String> to, List<String> cc, List<String> bcc, String subject, String html) throws EmailException, Throwable;

    AjaxResult resend(String id);

    AjaxResult sendEmail(MailBody mailBody);
}
