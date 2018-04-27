package com.muskteer.curator.leaderselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;

/**
 * Created by wanglei on 2018/1/31.
 */
public class LeaderSelectorListenerAdapterExample extends LeaderSelectorListenerAdapter implements Closeable {

    private LeaderSelector leaderSelector;
    private final static String path = "/udp/latch";


    public LeaderSelectorListenerAdapterExample() {
    }

    public void start(CuratorFramework curatorFramework) {
        leaderSelector = new LeaderSelector(curatorFramework, path, this);
        leaderSelector.autoRequeue();//成为leader后，触发takeLeadership，执行完后，还具有下一次选举的资格
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

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client =
                CuratorFrameworkFactory.newClient("10.143.130.62:2182", 60000, 15000,
                        new ExponentialBackoffRetry(1000,3));
        client.start();
        LeaderSelectorListenerAdapterExample listen = new LeaderSelectorListenerAdapterExample();
        listen.start(client);
        Thread.sleep(10000);
        listen.close();
    }
}
