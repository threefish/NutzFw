package com.nutzfw.core.plugin.flowable.context;

import java.util.Optional;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 */
public class ProcessContextHolder {

    private static final ThreadLocal<ProcessContext> PROCESS_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static final ProcessContext get() {
        return PROCESS_CONTEXT_THREAD_LOCAL.get();
    }

    public static final Optional<ProcessContext> getOptional() {
        return Optional.ofNullable(PROCESS_CONTEXT_THREAD_LOCAL.get());
    }

    public static final void set(ProcessContext context) {
        PROCESS_CONTEXT_THREAD_LOCAL.set(context);
    }
    public static final void remove() {
        PROCESS_CONTEXT_THREAD_LOCAL.remove();
    }
}
