package com.alibaba.ttl.threadpool;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.spi.TtlWrapper;
import com.alibaba.ttl.threadpool.agent.TtlAgent;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.concurrent.*;

/**
 * Util methods for TTL wrapper of jdk executors.
 *
 * <ol>
 *     <li>Factory methods to get TTL wrapper from jdk executors.</li>
 *     <li>unwrap/check methods for TTL wrapper of jdk executors.</li>
 *     <li>wrap/unwrap/check methods to disable Inheritable for {@link ThreadFactory}.</li>
 * </ol>
 * <p>
 * <b><i>Note:</i></b>
 * <ul>
 * <li>all method is {@code null}-safe, when input {@code executor} parameter is {@code null}, return {@code null}.</li>
 * <li>skip wrap/decoration thread pool/{@code executor}(aka. just return input {@code executor})
 * when ttl agent is loaded, Or when input {@code executor} is already wrapped/decorated.</li>
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Executor
 * @see ExecutorService
 * @see ThreadPoolExecutor
 * @see ScheduledThreadPoolExecutor
 * @see Executors
 * @see CompletionService
 * @see ExecutorCompletionService
 * @see ThreadFactory
 * @see Executors#defaultThreadFactory()
 * @since 0.9.0
 */
public final class TtlExecutors {
    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link Executor},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable}
     * to the execution time of {@link Runnable}.
     * <p>
     * NOTE: sine v2.12.0 the idempotency of return wrapped Executor is changed to true,
     * so the wrapped Executor can be cooperated with the usage of "Decorate Runnable and Callable".
     * <p>
     * About idempotency: if is idempotent,
     * it's allowed to submit the {@link com.alibaba.ttl.TtlRunnable}/{@link com.alibaba.ttl.TtlCallable} to the wrapped Executor;
     * otherwise throw {@link IllegalStateException}.
     *
     * @param executor input Executor
     * @return wrapped Executor
     * @see com.alibaba.ttl.TtlRunnable#get(Runnable, boolean, boolean)
     * @see com.alibaba.ttl.TtlCallable#get(Callable, boolean, boolean)
     */
    @Nullable
    public static Executor getTtlExecutor(@Nullable Executor executor) {
        if (TtlAgent.isTtlAgentLoaded() || null == executor || executor instanceof TtlEnhanced) {
            return executor;
        }
        return new ExecutorTtlWrapper(executor, true);
    }

    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link ExecutorService},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable} or {@link Callable}
     * to the execution time of {@link Runnable} or {@link Callable}.
     * <p>
     * NOTE: sine v2.12.0 the idempotency of return wrapped ExecutorService is changed to true,
     * so the wrapped ExecutorService can be cooperated with the usage of "Decorate Runnable and Callable".
     * <p>
     * About idempotency: if is idempotent,
     * it's allowed to submit the {@link com.alibaba.ttl.TtlRunnable}/{@link com.alibaba.ttl.TtlCallable} to the wrapped ExecutorService;
     * otherwise throw {@link IllegalStateException}.
     *
     * @param executorService input ExecutorService
     * @return wrapped ExecutorService
     * @see com.alibaba.ttl.TtlRunnable#get(Runnable, boolean, boolean)
     * @see com.alibaba.ttl.TtlCallable#get(Callable, boolean, boolean)
     */
    @Nullable
    public static ExecutorService getTtlExecutorService(@Nullable ExecutorService executorService) {
        if (TtlAgent.isTtlAgentLoaded() || executorService == null || executorService instanceof TtlEnhanced) {
            return executorService;
        }
        return new ExecutorServiceTtlWrapper(executorService, true);
    }


    /**
     * {@link TransmittableThreadLocal} Wrapper of {@link ScheduledExecutorService},
     * transmit the {@link TransmittableThreadLocal} from the task submit time of {@link Runnable} or {@link Callable}
     * to the execution time of {@link Runnable} or {@link Callable}.
     * <p>
     * NOTE: sine v2.12.0 the idempotency of return wrapped ScheduledExecutorService is changed to true,
     * so the wrapped ScheduledExecutorService can be cooperated with the usage of "Decorate Runnable and Callable".
     * <p>
     * About idempotency: if is idempotent,
     * it's allowed to submit the {@link com.alibaba.ttl.TtlRunnable}/{@link com.alibaba.ttl.TtlCallable} to the wrapped ScheduledExecutorService;
     * otherwise throw {@link IllegalStateException}.
     *
     * @param scheduledExecutorService input scheduledExecutorService
     * @return wrapped scheduledExecutorService
     * @see com.alibaba.ttl.TtlRunnable#get(Runnable, boolean, boolean)
     * @see com.alibaba.ttl.TtlCallable#get(Callable, boolean, boolean)
     */
    @Nullable
    public static ScheduledExecutorService getTtlScheduledExecutorService(@Nullable ScheduledExecutorService scheduledExecutorService) {
        if (TtlAgent.isTtlAgentLoaded() || scheduledExecutorService == null || scheduledExecutorService instanceof TtlEnhanced) {
            return scheduledExecutorService;
        }
        return new ScheduledExecutorServiceTtlWrapper(scheduledExecutorService, true);
    }

    /**
     * check the executor is a TTL executor wrapper or not.
     * <p>
     * if the parameter executor is TTL wrapper, return {@code true}, otherwise {@code false}.
     * <p>
     * NOTE: if input executor is {@code null}, return {@code false}.
     *
     * @param executor input executor
     * @param <T>      Executor type
     * @see #getTtlExecutor(Executor)
     * @see #getTtlExecutorService(ExecutorService)
     * @see #getTtlScheduledExecutorService(ScheduledExecutorService)
     * @see #unwrap(Executor)
     * @since 2.8.0
     */
    public static <T extends Executor> boolean isTtlWrapper(@Nullable T executor) {
        return executor instanceof TtlWrapper;
    }

    /**
     * Unwrap TTL executor wrapper to the original/underneath one.
     * <p>
     * if the parameter executor is TTL wrapper, return the original/underneath executor;
     * otherwise, just return the input parameter executor.
     * <p>
     * NOTE: if input executor is {@code null}, return {@code null}.
     *
     * @param executor input executor
     * @param <T>      Executor type
     * @see #getTtlExecutor(Executor)
     * @see #getTtlExecutorService(ExecutorService)
     * @see #getTtlScheduledExecutorService(ScheduledExecutorService)
     * @see #isTtlWrapper(Executor)
     * @see com.alibaba.ttl.TtlUnwrap#unwrap(Object)
     * @since 2.8.0
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Executor> T unwrap(@Nullable T executor) {
        if (!isTtlWrapper(executor)){
            return executor;
        }

        return (T) ((ExecutorTtlWrapper) executor).unwrap();
    }

    /**
     * Wrapper of {@link ThreadFactory}, disable inheritable.
     *
     * @param threadFactory input thread factory
     * @see DisableInheritableThreadFactory
     * @since 2.10.0
     */
    @Nullable
    public static ThreadFactory getDisableInheritableThreadFactory(@Nullable ThreadFactory threadFactory) {
        if (threadFactory == null || isDisableInheritableThreadFactory(threadFactory)){
            return threadFactory;
        }

        return new DisableInheritableThreadFactoryWrapper(threadFactory);
    }

    /**
     * Wrapper of {@link Executors#defaultThreadFactory()}, disable inheritable.
     *
     * @see #getDisableInheritableThreadFactory(ThreadFactory)
     * @since 2.10.0
     */
    @NonNull
    public static ThreadFactory getDefaultDisableInheritableThreadFactory() {
        return getDisableInheritableThreadFactory(Executors.defaultThreadFactory());
    }

    /**
     * check the {@link ThreadFactory} is {@link DisableInheritableThreadFactory} or not.
     *
     * @see DisableInheritableThreadFactory
     * @since 2.10.0
     */
    public static boolean isDisableInheritableThreadFactory(@Nullable ThreadFactory threadFactory) {
        return threadFactory instanceof DisableInheritableThreadFactory;
    }

    /**
     * Unwrap {@link DisableInheritableThreadFactory} to the original/underneath one.
     *
     * @see com.alibaba.ttl.TtlUnwrap#unwrap(Object)
     * @see DisableInheritableThreadFactory
     * @since 2.10.0
     */
    @Nullable
    public static ThreadFactory unwrap(@Nullable ThreadFactory threadFactory) {
        if (!isDisableInheritableThreadFactory(threadFactory)){
            return threadFactory;
        }

        return ((DisableInheritableThreadFactory) threadFactory).unwrap();
    }

    private TtlExecutors() {
    }
}
