package com.nutzfw.core.common.threadpool;


import com.zaxxer.hikari.util.DefaultThreadFactory;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class BusinessCommonTaskExecutorContextHolder {


    public static final String BEAN_NAME = "businessCommonThreadPool";

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * IO密集型：核心线程数 = CPU核数 * 2 + 1
     */
    private static final int CORE_POOL_SIZE = (PROCESSORS * 2) + 1;

    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;

    /**
     * 队列最大长度
     */
    private static final int QUEUE_CAPACITY = 1024;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final int KEEP_ALIVE_SECONDS = Math.toIntExact(Duration.ofMinutes(1).getSeconds());

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(QUEUE_CAPACITY),
                new DefaultThreadFactory("BusinessCommonThreadPool", false),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static void execute(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return THREAD_POOL_EXECUTOR.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return THREAD_POOL_EXECUTOR.submit(callable);
    }

}
