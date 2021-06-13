package com.iwill.alibaba.ttl.bean;

public class ThreadContext {

    private String name ;

    private Long threadId ;

    public ThreadContext(String name, Long threadId) {
        this.name = name;
        this.threadId = threadId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }
}
