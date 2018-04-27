package com.muskteer.curator.serverid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;

/**
 * Created by wanglei on 2018/1/31.
 */
public class Test extends LeaderSelectorListenerAdapter implements Closeable {

    private final static String path = "/udp/testserverid";
    private LeaderSelector leaderSelector;


    public Test() {
    }

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client =
                CuratorFrameworkFactory.newClient("10.143.130.62:2182", 60000, 15000, new ExponentialBackoffRetry(1000, 3));
        client.start();
        Test listen = new Test();
        client.getConnectionStateListenable().addListener(listen);
        listen.start(client);
        Thread.sleep(10000);
        System.out.println(client.getState());
        listen.close();
        System.out.println(client.getState());
        client.close();

    }

    public void start(CuratorFramework curatorFramework) {
        leaderSelector = new LeaderSelector(curatorFramework, path, this);
        leaderSelector.start();
    }

    public void close() {
        leaderSelector.close();
    }

    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        //do the 占位 job
        System.out.println("get the lock.");
        System.out.println("release the lock.");
    }
}
