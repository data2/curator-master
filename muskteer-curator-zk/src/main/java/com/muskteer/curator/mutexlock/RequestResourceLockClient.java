package com.muskteer.curator.mutexlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
/**
 * 可重入锁Shared Reentrant Lock, 通过InterProcessMutex实现
 * 此处的Reentrant和JDK的ReentrantLock类似， 意味着同一个客户端在拥有锁的同时，可以多次获取，不会被阻塞。
 * 如果非可重入锁 Shared Lock, 则使用InterProcessSemaphoreMutex.
 */
import java.util.concurrent.TimeUnit;

/**
 * Created by wanglei on 2018/1/31.
 */
public class RequestResourceLockClient {

    private final InterProcessMutex lock;
    private static final java.lang.String path = "/udp/mutex";
    private final Resource resource;
    private final String clinetid;

    public RequestResourceLockClient(CuratorFramework client, Resource resource, String clinetid) {
        this.lock = new InterProcessMutex(client, path);
        this.resource = resource;
        this.clinetid = clinetid;
    }

    public void execute(){
        try {
            boolean res = lock.acquire(10, TimeUnit.SECONDS);
            if (!res) {
                System.out.println(this.clinetid + " get lcok fail.");
                return;
            }
            System.out.println(this.clinetid + " get lcok okay.");
            resource.use();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
