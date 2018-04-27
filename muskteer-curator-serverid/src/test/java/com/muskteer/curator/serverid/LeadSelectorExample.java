package com.muskteer.curator.serverid;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by wanglei on 2018/2/2.
 */
public class LeadSelectorExample {

    static int i = 0;

    public static void setI(int ii) {
        i = ii;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("start.");

        CuratorFramework client = CuratorFrameworkFactory
                .newClient("10.143.130.62:2182", 10000, 10000,
                        new ExponentialBackoffRetry(1000, 3));
        client.start();
        LeaderSelector leaderSelector = null;
        if (client.getState() == CuratorFrameworkState.STARTED) {
            leaderSelector = new LeaderSelector(client,
                    "/udp/testserverid", new LeaderSelectorListener() {

                @Override
                public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    System.out.println("state change." + newState);
                }

                @Override
                public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                    System.out.println("deal lock ");
                    setI(1);
                    System.out.println("set i = 1");
                    System.out.println("deal done.");
                }
            });
        }
        leaderSelector.autoRequeue();
        leaderSelector.start();
        try {
            System.out.println("leader" + leaderSelector.getLeader());
            Thread.sleep(10000);
            System.out.println("sleep 10000 ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (leaderSelector.hasLeadership()) {
            System.out.println("has leadership");
            leaderSelector.close();
            System.out.println("i :" + i);
            System.out.println("has leadership done.");
        }
        client.close();

    }
}
