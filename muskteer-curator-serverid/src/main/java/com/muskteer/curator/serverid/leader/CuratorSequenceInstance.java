package com.muskteer.curator.serverid.leader;

import com.muskteer.curator.serverid.CuratorConfig;

import java.io.IOException;

/**
 * Created by wanglei on 2018/2/1.
 */
public class CuratorSequenceInstance {
    private static final long LITTLE_WAIT_MS = 100;
    private static final int DEFAULT_TRY_COUNT = 10 * 60 * 5;//5min to wait.
    private static ServerSequenceListenerClient client;

    private CuratorSequenceInstance() {
    }

    public static CuratorSequenceInstance getInstance() {
        return CuratorServerInner.instance;
    }

    public static String tryId(String path, int idLength, CuratorConfig config) throws IOException {
        initClient(path, idLength, config);
        int tryCount = 0;
        while (checkIsNull(client.result())) {
            try {
                if (tryCount >= DEFAULT_TRY_COUNT) {
                    break;
                }
                Thread.sleep(LITTLE_WAIT_MS);
                if (tryCount * LITTLE_WAIT_MS % 1000 == 0) {
                    System.out.println("alread wait " + tryCount * LITTLE_WAIT_MS / 1000 + " s");
                }
                tryCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            if (!client.getCloseState()) {
                client.close();
            }
        }catch (Exception e){}

        return client.result();
    }

    private static boolean checkIsNull(String result) {
        if (result == null || result.isEmpty()) {
            return true;
        }
        return false;
    }

    private static synchronized void initClient(String path, int idLength, CuratorConfig config) {
        if (client == null) {
            client = new ServerSequenceListenerClient(path, idLength, config);
        }
    }

    private static class CuratorServerInner {
        private static CuratorSequenceInstance instance = new CuratorSequenceInstance();
    }


}
