package com.muskteer.curator.serverid;

import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Created by wanglei on 2018/2/8.
 */
public class SequenceConcurrentTest {
    public static void main(String[] args) {
        String id = null;
        try {
            id = ServerIdRegistFactory.tryServerId("/udp/master", 2,
                    new CuratorConfig("10.143.130.62:2182",
                            new ExponentialBackoffRetry(1000, 3), 15000, 3600000));
            System.out.println(id);
            System.out.println("start to sleep.");
            Thread.sleep(60*1000*5);
            System.out.println("stop sleep.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
