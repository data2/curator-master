package com.muskteer.curator.serverid;

import org.apache.curator.RetryPolicy;

/**
 * Created by wanglei on 2018/2/1.
 */
public class CuratorConfig {

    private static final int DEFALUT_CONNECTIONTIMEOUTMS = 6 * 1000;
    private static final int DEFALUT_SESSIONTIMEOUTMS = 6 * 1000;

    private String connectString;
    private RetryPolicy retryPolicy;
    private int connectionTimeoutMs;
    private int sessionTimeoutMs;

    public CuratorConfig(String connectString, RetryPolicy retryPolicy,
                         int connectionTimeoutMs, int sessionTimeoutMs) {
        this.connectString = connectString;
        this.retryPolicy = retryPolicy;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public CuratorConfig(String connectString, RetryPolicy retryPolicy) {
        this.connectString = connectString;
        this.retryPolicy = retryPolicy;
        this.connectionTimeoutMs = DEFALUT_CONNECTIONTIMEOUTMS;
        this.sessionTimeoutMs = DEFALUT_SESSIONTIMEOUTMS;
    }

    public String getConnectString() {
        return connectString;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }
}
