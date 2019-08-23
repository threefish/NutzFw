package com.nutzfw.core.mvc;

import com.nutzfw.core.common.annotation.TryCatchMsg;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.error.PreventDuplicateSubmitError;
import com.nutzfw.core.common.util.ElUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.ErrorLogHistory;
import com.nutzfw.modules.sys.service.ErrorLogHistoryService;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.ViewProcessor;

import java.sql.Timestamp;

/**
 * 覆盖nutz默认错误执行
 *
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBean
public class FailProcessor extends ViewProcessor {

    private static final Log log = Logs.get();

    @Inject
    ErrorLogHistoryService errorLogHistoryService;

    @Override
    public void init(NutConfig config, ActionInfo ai) {
        view = evalView(config, ai, ai.getFailView());
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (log.isWarnEnabled()) {
            String uri = Mvcs.getRequestPath(ac.getRequest());
            log.warn(String.format("执行期间错误 %s :", uri), ac.getError());
        }
        String errorMsg = this.getErrorMsg(ac);
        if (!"".equals(errorMsg)) {
            Mvcs.getReq().setAttribute("error_msg", errorMsg);
        }
        if (NutShiro.isAjax(ac.getRequest())) {
            ac.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
            Mvcs.write(ac.getResponse(), NutMap.NEW().setv("ok", false).setv("msg", errorMsg), JsonFormat.full());
        } else {
            //继续执行原始逻辑
            super.process(ac);
            if (!BeetlViewMaker.isDev) {
                this.asyncInsertErrorLogHistory(ac);
            }
        }
    }

    private void asyncInsertErrorLogHistory(ActionContext ac) {
        try {
            ErrorLogHistory errorLog = new ErrorLogHistory();
            UserAccount user = (UserAccount) Mvcs.getSessionAttrSafe(Cons.SESSION_USER_KEY);
            if (user != null) {
                errorLog.setUserid(user.getId());
                errorLog.setUserDesc(user.getRealName());
            }
            errorLog.setCt(new Timestamp(System.currentTimeMillis()));
            errorLog.setPath(Mvcs.getRequestPath(ac.getRequest()));
            if (ac.getError() != null) {
                errorLog.setErrorMsg(ac.getError().getMessage());
                errorLog.setErrorMsgInfo(StringUtil.throwableToString(ac.getError()));
                if (Strings.sNull(errorLog.getErrorMsg()).indexOf("您的主机中的软件中止了一个已建立的连接") == -1 &&
                        Strings.sNull(errorLog.getErrorMsg()).indexOf("远程主机强迫关闭了一个现有的连接") == -1 &&
                        Strings.sNull(errorLog.getErrorMsg()).indexOf("java.net.SocketTimeoutException") == -1
                ) {
                    errorLogHistoryService.async(errorLog);
                }
            }
        } catch (Exception e) {
        }

    }

    private String getErrorMsg(ActionContext ac) {
        Throwable throwable = ac.getError();
        String errorMsg = ac.getError().getMessage();
        if (throwable instanceof PreventDuplicateSubmitError) {
            errorMsg = ((PreventDuplicateSubmitError) throwable).getDetailMessage();
        } else if (ac.getError().getCause() != null) {
            errorMsg = ac.getError().getCause().getMessage();
        }
        TryCatchMsg tryCatchMsg = null;
        if (ac.getMethod() != null) {
            tryCatchMsg = ac.getMethod().getAnnotation(TryCatchMsg.class);
        }
        if (tryCatchMsg != null) {
            return ElUtil.render(tryCatchMsg.value(), Lang.context().set("errorMsg", errorMsg));
        }
        if (throwable instanceof NullPointerException) {
            return "空指针异常！";
        }
        return errorMsg;
    }
}