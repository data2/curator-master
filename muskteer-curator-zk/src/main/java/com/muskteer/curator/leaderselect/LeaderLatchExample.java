package com.muskteer.curator.leaderselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by wanglei on 2018/1/31.
 */
public class LeaderLatchExample {

    public static void main(String[] args) throws Exception {

        String PATH = "/udp/latch";
        CuratorFramework client1 = CuratorFrameworkFactory
                .newClient("10.143.130.62:2182", 60000, 15000, new ExponentialBackoffRetry(1000, 3));
        CuratorFramework client2 = CuratorFrameworkFactory
                .newClient("10.143.130.62:2182", 60000, 15000, new ExponentialBackoffRetry(1000, 3));
        client1.start();
        client2.start();

        final LeaderLatch latche1 = new LeaderLatch(client1, PATH, "latch1");
        final LeaderLatch latche2 = new LeaderLatch(client2, PATH, "latch2");

        latche1.start();
        latche2.start();

        Thread.sleep(5000);

        while (true) {
            Thread.sleep(2000);
            if (latche1.hasLeadership() ) {
                System.out.println( "latche1 is leader now.");
                latche1.close();
            }
            if (latche2.hasLeadership() ) {
                System.out.println( "latche2 is leader now.");
                latche2.close();
            }
        }

    }


}
