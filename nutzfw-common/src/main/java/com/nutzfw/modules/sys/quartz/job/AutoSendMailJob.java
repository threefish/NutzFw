/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.quartz.job;

import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.sys.biz.EmailBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：自动发送邮件
 */
@IocBean(args = {"refer:$ioc"})
@DisallowConcurrentExecution
public class AutoSendMailJob extends BaseJob {

    /**
     * 失败标记
     */
    private final static int ERROR  = -1;
    /**
     * 成功标记
     */
    private final static int SUCESS = 1;
    @Inject
    EmailBiz        emailBiz;
    @Inject
    MailBodyService mailBodyService;

    public AutoSendMailJob(Ioc ioc) {
        super(ioc);
    }

    @Override
    public void run(JobDataMap data) {
        //查询所有失败的和待发
        List<MailBody> mailBodyList = mailBodyService.query(
                Cnd.where("delFlag", "=", false)
                        .and(Cnd.exps("status", "=", 0).or("status", "=", -1))
        );
        Timestamp nowTimes = new Timestamp(System.currentTimeMillis());
        mailBodyList.forEach(mailBody -> {
            if (mailBody.getStatus() == 0 && canSend(mailBody, nowTimes)) {
                //待发
                send(mailBody);
            }
            if (mailBody.getStatus() == -1 && mailBody.getSendNum() <= mailBody.getMaxSendNum()) {
                //发送失败的
                send(mailBody);
            }
        });

    }

    private boolean send(MailBody mailBody) {
        boolean b;
        try {
            b = emailBiz.send(coverList(mailBody.getTo()), coverList(mailBody.getCc()), coverList(mailBody.getBcc()), mailBody.getSubject(), mailBody.getHtmlMsg());
            mailBody.setStatus(SUCESS);
        } catch (Throwable throwable) {
            log.error("邮件发送失败", throwable);
            mailBody.setErrorMsg(StringUtil.throwableToString(throwable));
            mailBody.setStatus(ERROR);
            mailBody.setSendNum(mailBody.getSendNum() + 1);
            b = false;
        } finally {
            mailBodyService.update(mailBody);
        }
        return b;
    }

    private List<String> coverList(String mails) {
        return Arrays.asList(Strings.splitIgnoreBlank(Strings.sNull(mails), ","));
    }


    /**
     * 是否满足发送条件
     *
     * @param mailBody
     * @param nowTimes
     * @return
     */
    private boolean canSend(MailBody mailBody, Timestamp nowTimes) {
        if (null != mailBody.getTaskTime() && mailBody.getTaskTime().getTime() <= nowTimes.getTime()) {
            // (1)必须 要到了这一天才发送
            // (2)万一这个 发送时间 , 就在 定时任务 执行的这 15 秒中.
            long sendTimestamp = mailBody.getTaskTime().getTime();
            if (nowTimes.equals(mailBody.getTaskTime()) || Math.abs(nowTimes.getTime() - sendTimestamp) <= 15000) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
