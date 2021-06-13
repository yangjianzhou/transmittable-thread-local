package com.iwill.alibaba.ttl.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.iwill.alibaba.ttl.bean.ThreadContext;
import org.springframework.stereotype.Component;

@Component
public class ThreadContextHolder {

    private TransmittableThreadLocal<ThreadContext> holder = new TransmittableThreadLocal<ThreadContext>();

    public void set(ThreadContext context){
        holder.set(context);
    }

    public ThreadContext get(){
        return holder.get();
    }
}
