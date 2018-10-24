package com.muskteer.curator.serverid.leader;

import com.google.common.base.Preconditions;
import com.muskteer.curator.serverid.CuratorConfig;
import com.muskteer.curator.serverid.ServerIdRegistFactory;
import com.muskteer.curator.serverid.utils.ZooUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Created by wanglei on 2018/2/1.
 */
public class ServerSequenceListenerClient extends LeaderSelectorListenerAdapter implements Closeable {
    private static final String SERVERID_PATH = "/udp/serverid";
    private final CuratorFramework curatorFramework;
    private final LeaderSelector select;
    private final String path;
    private final int idLength;
    private final Logger logger = LoggerFactory.getLogger("ServerSequenceListenerClient");
    private String result;
    private volatile boolean isClosed = false;

    public ServerSequenceListenerClient(String path, int idLength, CuratorConfig config) {
        this.path = path;
        this.idLength = idLength;
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(config.getConnectString())
                .retryPolicy(config.getRetryPolicy())
                .connectionTimeoutMs(config.getConnectionTimeoutMs())
                .sessionTimeoutMs(config.getSessionTimeoutMs())
                .build();
        this.curatorFramework.start();
        while (true) {
            if (this.curatorFramework.getState() == CuratorFrameworkState.STARTED) {
                this.select = new LeaderSelector(this.curatorFramework, this.path, this);
//                this.select.autoRequeue();
                this.select.start();
                logger.info(" client started.");
                break;
            }
        }
    }

    @Override
    public void close() {
        try {
            if (this.curatorFramework.getState() == CuratorFrameworkState.STARTED) {
                this.select.close();
                logger.info("select closed.");
                if (this.select.hasLeadership()) {
                    logger.info("select closed,but not release lock.");
                }
//            this.curatorFramework.close();
            }
            this.isClosed = true;
            logger.info("client closed.");
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) {
        try {
            logger.info("client get the lock. -- thread id " + Thread.currentThread().getId());
            List<Integer> sequences = ZooUtils.getChildren(curatorFramework, SERVERID_PATH);
            if (sequences.size() == 0) {
                logger.info("there are none childnode.");
                createNodeByClient(curatorFramework, 0);
                this.setResult(fullStr(0));
                logger.info("client release the lock. -- thread id " + Thread.currentThread().getId());
                return;
            }
            ZooUtils.sort(sequences);
            int res = ZooUtils.getCanUseServerId(sequences);
            Preconditions.checkState(res != -1, "get can use serverid is null");
            createNodeByClient(curatorFramework, res);
            this.setResult(fullStr(res));
            logger.info("client release the lock. -- thread id " + Thread.currentThread().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNodeByClient(CuratorFramework curatorFramework, int res) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(SERVERID_PATH).append("/").append(res);
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(sb.toString(), (res + "").getBytes("UTF-8"));
        logger.info("EPHEMERAL node : " + sb.toString() + " created.");


    }

    private String fullStr(int i) {
        StringBuffer idToStr = new StringBuffer();
        idToStr.append(i);
        while (idToStr.length() < idLength) {
            idToStr.insert(0, "0");
        }
        return idToStr.toString();
    }

    public String result() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean getCloseState() {
        return isClosed;
    }
}
