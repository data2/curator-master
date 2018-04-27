package com.muskteer.curator.serverid;

import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Created by wanglei on 2018/2/1.
 */
public class ServerIdExample {

    public static void main(String[] args) throws IOException {
        String id = ServerIdRegistFactory.tryServerId("/udp/master", 2,
                new CuratorConfig("10.143.130.62:2182",
                        new ExponentialBackoffRetry(1000, 3), 15000, 3600000));
        System.out.println(id);
    }
}
