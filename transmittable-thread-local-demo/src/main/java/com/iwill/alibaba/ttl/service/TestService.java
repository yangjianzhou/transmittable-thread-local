package com.iwill.alibaba.ttl.service;

import com.iwill.alibaba.ttl.bean.ThreadContext;
import com.iwill.alibaba.ttl.util.ThreadContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private OtherService otherService ;

    @Autowired
    private ThreadContextHolder contextHolder ;

    public void testInSyncThread(){
        ThreadContext context = new ThreadContext( Thread.currentThread().getName(),Thread.currentThread().getId());
        contextHolder.set(context);
        otherService.testInSyncThread();
    }

    public void testInAsyncThread() {
        for (int i = 0; i < 1000; i++) {
            ThreadContext context = new ThreadContext(Thread.currentThread().getName(), Thread.currentThread().getId());
            contextHolder.set(context);
            System.out.println("main:" + context);
            otherService.testInAsyncThread();
        }
    }

}
