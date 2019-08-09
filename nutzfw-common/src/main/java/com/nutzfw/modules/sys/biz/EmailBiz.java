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
