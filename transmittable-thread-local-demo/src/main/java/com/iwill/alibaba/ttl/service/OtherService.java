package com.iwill.alibaba.ttl.service;

import com.iwill.alibaba.ttl.bean.ThreadContext;
import com.iwill.alibaba.ttl.util.ThreadContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class OtherService {

    @Autowired
    private ThreadContextHolder contextHolder ;

    private ExecutorService executorService = new ThreadPoolExecutor(10, 10,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());

    public void testInSyncThread(){
        ThreadContext context = contextHolder.get();
        System.out.println(context);
    }

    public void testInAsyncThread(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ThreadContext context = contextHolder.get();
                System.out.println(context);
            }
        });
    }
}
