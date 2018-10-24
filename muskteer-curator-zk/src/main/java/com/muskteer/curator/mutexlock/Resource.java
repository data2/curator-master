package com.muskteer.curator.mutexlock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wanglei on 2018/1/31.
 */
public class Resource {
    private final AtomicBoolean inUse = new AtomicBoolean(false);

    public void use() throws InterruptedException {
        if (!inUse.compareAndSet(false, true)) {
            throw new IllegalStateException("Needs to be used by one client at a time");
        }
        try {
            Thread.sleep((long) (3 * Math.random()));
        } finally {
            inUse.set(false);
        }
    }
}
