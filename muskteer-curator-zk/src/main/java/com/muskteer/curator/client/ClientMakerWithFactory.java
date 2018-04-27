package com.muskteer.curator.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;

import java.io.File;

/**
 * Created by wanglei on 2018/1/31.
 */
public class ClientMakerWithFactory {
    
    private static String path = "/udptest";
    
    public static void main(String[] args) throws Exception {
//        TestingServer server = new TestingServer(2182);
//        server.start();

        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "10.143.130.62:2182", 5000, 5000, new RetryOneTime(5));
        client.start();

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
