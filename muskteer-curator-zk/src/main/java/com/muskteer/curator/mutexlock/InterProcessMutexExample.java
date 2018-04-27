package com.muskteer.curator.mutexlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanglei on 2018/1/31.
 */
public class InterProcessMutexExample {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(10);
        final Resource resource = new Resource();

        for (int i = 0 ; i < 10; i++){

            final int finalI = i;
            es.submit(new Runnable() {

                public void run() {
                    CuratorFramework client = CuratorFrameworkFactory
                            .newClient("10.143.130.62:2182", new ExponentialBackoffRetry(1000,3));
                    client.start();
                    RequestResourceLockClient requestClient =
                            new RequestResourceLockClient(client, resource, "mutex"+ finalI);
                    requestClient.execute();
                }
            });
        }

        es.shutdown();
        es.awaitTermination(10, TimeUnit.SECONDS);

//        server.close();

    }
}
