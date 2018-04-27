package com.muskteer.curator.serverid;

import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Created by wanglei on 2018/2/8.
 */
public class SequenceInstanceTest {

    public static void main(String[] args) throws IOException {
        for (int i = 0 ; i < 3; i ++){
            TestThread t = new TestThread();
            t.start();
        }

    }

    static class TestThread extends Thread{

        @Override
        public void run() {
            String id = null;
            try {
                id = ServerIdRegistFactory.tryServerId("/udp/master", 2,
                        new CuratorConfig("10.143.130.62:2182",
                                new ExponentialBackoffRetry(1000, 3), 15000, 3600000));
                System.out.println(id);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
