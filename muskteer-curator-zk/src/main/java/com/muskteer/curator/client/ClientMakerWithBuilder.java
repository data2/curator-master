package com.muskteer.curator.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

/**
 * Created by wanglei on 2018/1/31.
 */
public class ClientMakerWithBuilder {
    private static String path = "/udptest2";

    public static void main(String[] args) throws Exception {
//        TestingServer server = new TestingServer(2182);
//        server.start();

        CuratorFramework client = CuratorFrameworkFactory.builder()
//                .namespace("udp")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectString("10.143.130.62:2182")
                .sessionTimeoutMs(3000)
                .connectionTimeoutMs(3000)
                .build();

        client.start();
        System.out.println(client.getState());

        client.create().forPath(path, "testValue".getBytes());
        System.out.println(new String(client.getData().forPath(path)));
        client.setData().forPath(path, "testVlaue2".getBytes());
        System.out.println(new String(client.getData().forPath(path)));
        client.delete().forPath(path);
        ExistsBuilder existsBuilder = client.checkExists();
        System.out.println(existsBuilder.forPath(path));
        client.getChildren();

        client.close();
//        server.close();
    }
}
