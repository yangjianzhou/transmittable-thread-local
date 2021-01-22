package com.alibaba.ttl.integration.vertx4.agent.transformlet;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import com.alibaba.ttl.threadpool.agent.transformlet.helper.AbstractExecutorTtlTransformlet;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link com.alibaba.ttl.threadpool.agent.transformlet.TtlTransformlet}
 * for {@link io.netty.util.concurrent.SingleThreadEventExecutor}.
 *
 * @see io.netty.util.concurrent.SingleThreadEventExecutor
 * @see io.vertx.core.eventbus.EventBus
 * @see io.vertx.core.impl.EventLoopContext
 * @see io.vertx.core.eventbus.Message
 */
public final class NettySingleThreadEventExecutorTtlTransformlet extends AbstractExecutorTtlTransformlet {
    private static final Set<String> EXECUTOR_CLASS_NAMES = new HashSet<>();

    static {
        EXECUTOR_CLASS_NAMES.add("io.netty.util.concurrent.SingleThreadEventExecutor");
    }

    public NettySingleThreadEventExecutorTtlTransformlet() {
        super(EXECUTOR_CLASS_NAMES, TtlAgent.isDisableInheritableForThreadPool());
    }
}