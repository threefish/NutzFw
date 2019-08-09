package com.nutzfw.module;

import com.nutzfw.core.MainModule;
import org.junit.runners.model.InitializationError;
import org.nutz.mock.NutTestRunner;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/14
 * 描述此类：
 */
public class TestRunner extends NutTestRunner {

    public TestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Class<?> getMainModule() {
        return MainModule.class;
    }
}
