/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.NutActionChain;
import org.nutz.mvc.impl.NutActionChainMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/14
 */
public class NutzFwNutActionChainMaker extends NutActionChainMaker {

    private static final Log log = Logs.get();

    /**
     * !com.nutzfw.core.mvc.GlobalsSettingProcessor 前面加!号表示如果不存在可以忽略
     * <p>
     * ioc:fristLoginProcessor 表示在ioc中取得
     */
    List<String> processors = Arrays.asList(
            "com.nutzfw.core.mvc.HttpHostHeaderFilterProcessor",
            "com.nutzfw.core.mvc.GlobalsSettingProcessor",
            "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
            "org.nutz.mvc.impl.processor.EncodingProcessor",
            "com.nutzfw.core.mvc.FristLoginProcessor",
            "org.nutz.mvc.impl.processor.ModuleProcessor",
            "org.nutz.integration.shiro.NutShiroProcessor",
            "com.nutzfw.core.mvc.XssSqlFilterProcessor",
            "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
            "org.nutz.mvc.impl.processor.AdaptorProcessor",
            "org.nutz.plugins.validation.ValidationProcessor",
            "com.nutzfw.core.mvc.PreventDuplicateSubmitProcessor",
            "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
            "org.nutz.mvc.impl.processor.ViewProcessor"
    );

    public NutzFwNutActionChainMaker(String... args) {
    }

    @Override
    public ActionChain eval(NutConfig config, ActionInfo ai) {
        try {
            List<Processor> list = new ArrayList<>();
            for (String name : processors) {
                Processor processor = getProcessorByName(config, name);
                if (processor != null) {
                    processor.init(config, ai);
                    list.add(processor);
                }
            }
            Processor errorProcessor = config.getIoc().get(FailProcessor.class);
            errorProcessor.init(config, ai);
            return new NutActionChain(list, errorProcessor, ai);
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debugf("Eval FAIL!! : %s", ai.getMethod(), e);
            }
            throw Lang.wrapThrow(e);
        }
    }
}
