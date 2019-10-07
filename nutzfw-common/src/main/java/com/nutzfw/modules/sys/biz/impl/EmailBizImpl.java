/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz.impl;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.sys.biz.EmailBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.apache.commons.mail.ImageHtmlEmail;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：
 */
@IocBean(name = "emailBiz")
public class EmailBizImpl implements EmailBiz {

    private static final Log           LOG           = Logs.get();
    static               AtomicInteger atomicInteger = new AtomicInteger(0);
    @Inject("refer:$ioc")
    Ioc             ioc;
    @Inject
    MailBodyService mailBodyService;
    @Inject("java:$conf.get('mail.count')")
    private int    count;
    @Inject("java:$conf.get('mail.charset')")
    private String charset;

    private ImageHtmlEmail getImageHtmlEmail() {
        if (count == 1) {
            return ioc.get(ImageHtmlEmail.class, "htmlEmail0");
        } else if (count > 1) {
            return ioc.get(ImageHtmlEmail.class, "htmlEmail".concat(String.valueOf(getInt())));
        }
        throw new RuntimeException("邮箱配置信息错误！");
    }

    private int getInt() {
        int incrementAndGet = atomicInteger.incrementAndGet();
        if (incrementAndGet == count) {
            incrementAndGet = 0;
            atomicInteger.set(incrementAndGet);
        }
        return incrementAndGet;
    }

    @Override
    public LayuiTableDataListVO query(Cnd cnd, int pageNum, int pageSize) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT b.*,u.realName userName FROM sys_mail_body b LEFT JOIN sys_user_account u ON u.id = b.opBy ");
        sb.append("WHERE b.delFlag = false $condition ORDER BY b.opAt desc");
        Sql sql = Sqls.create(sb.toString());
        sql.setCondition(cnd);
        sql.setCallback(Sqls.callback.records());
        return mailBodyService.listPage(pageNum, pageSize, sql);
    }

    /**
     * 不采用直接注入的方法是为了避免启动时没有配置正确的mail.properties文件内容
     *
     * @Inject("htmlEmail") ImageHtmlEmail htmlEmail;
     */
    @Override
    public boolean send(String to, String subject, String html) throws Throwable {
        ImageHtmlEmail htmlEmail = getImageHtmlEmail();
        htmlEmail.setSubject(subject);
        htmlEmail.setHtmlMsg(html);
        htmlEmail.setCharset(charset);
        htmlEmail.addTo(to);
        htmlEmail.send();
        return true;
    }

    @Override
    public boolean send(List<String> to, List<String> cc, List<String> bcc, String subject, String html) throws Throwable {
        ImageHtmlEmail htmlEmail = getImageHtmlEmail();
        htmlEmail.setSubject(subject);
        htmlEmail.setHtmlMsg(html);
        htmlEmail.setCharset(charset);
        if (to.size() > 0) {
            htmlEmail.addTo(to.toArray(new String[0]));
        }
        if (cc.size() > 0) {
            //抄送
            htmlEmail.addCc(cc.toArray(new String[0]));
        }
        if (bcc.size() > 0) {
            //密送
            htmlEmail.addBcc(bcc.toArray(new String[0]));
        }
        htmlEmail.send();
        return true;

    }

    /**
     * 重新发送
     */
    @Override
    public AjaxResult resend(String id) {
        MailBody mailBody = mailBodyService.fetch(id);
        mailBody.setStatus(0);
        mailBody.setReSend(true);
        mailBody.setMaxSendNum(1);
        Boolean flag = mailBodyService.update(mailBody) > 0;
        if (flag) {
            return AjaxResult.sucessMsg("操作成功");
        }
        return AjaxResult.error("操作失败");
    }

    /**
     * 发送邮件
     */
    @Override
    public AjaxResult sendEmail(MailBody mailBody) {
        if (mailBody == null) {
            return AjaxResult.error("失败");
        }
        mailBodyService.insert(MailBody.builder()
                .subject(mailBody.getSubject())
                .to(mailBody.getTo())
                .htmlMsg(mailBody.getHtmlMsg())
                .cc(mailBody.getCc())
                .bcc(mailBody.getBcc())
                .taskTime(mailBody.getTaskTime())
                .status(0)
                .reSend(true)
                .maxSendNum(1)
                .sendNum(0).build());
        return AjaxResult.sucessMsg("发送成功");
    }
}
