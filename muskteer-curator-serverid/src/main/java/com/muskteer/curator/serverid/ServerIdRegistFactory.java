package com.muskteer.curator.serverid;

import com.muskteer.curator.serverid.leader.CuratorSequenceInstance;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by wanglei on 2018/2/1.
 */
public class ServerIdRegistFactory {

    private static final String DEFAULT_PATH = "/udp/serverid";
    private static final int DEFAULT_ID_LENGTH = 2;

    public static String tryServerId(CuratorConfig config) throws IOException {
        return ServerIdRegistFactory.tryServerId(DEFAULT_PATH, DEFAULT_ID_LENGTH, config);
    }

    public static String tryServerId(String path, int idLength, CuratorConfig config) throws IOException {
        return CuratorSequenceInstance.getInstance().tryId(
                (StringUtils.isEmpty(path)) ? DEFAULT_PATH : path, idLength, config);

    }
}
