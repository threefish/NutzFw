package com.nutzfw.core.mvc;

import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: 黄川
 * Date Time: 2017/2/2117:33
 * To change this template use File | Settings | File Templates.
 * 设置基础全局基础参数
 */
public class GlobalsSettingProcessor extends AbstractProcessor {
    @Override
    public void process(ActionContext ac) throws Throwable {
        String lang = ac.getRequest().getParameter("lang");
        if (!Strings.isEmpty(lang)) {
            Mvcs.setLocalizationKey(lang);
        } else {
            lang = Mvcs.getDefaultLocalizationKey();
        }
        ac.getRequest().setAttribute("lang", lang);
        //可以预设变量
        ac.getRequest().setAttribute("base", Mvcs.getReq().getContextPath());
        doNext(ac);
    }
}
