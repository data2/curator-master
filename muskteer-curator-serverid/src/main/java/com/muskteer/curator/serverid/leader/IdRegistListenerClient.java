package com.muskteer.curator.serverid.leader;

import com.google.common.base.Preconditions;
import com.muskteer.curator.serverid.CuratorConfig;
import com.muskteer.curator.serverid.utils.ZooUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wanglei on 2018/2/1.
 */
public class IdRegistListenerClient implements Closeable, LeaderSelectorListener {
    private final CuratorFramework curatorFramework;
    private final LeaderSelector select;
    private final String path;
    private final int idLength;
    private final Logger logger = LoggerFactory.getLogger("serverid");
    private String result;
    private volatile boolean isClosed = false;

    public IdRegistListenerClient(String path, int idLength, CuratorConfig config) {
        this.path = path;
        this.idLength = idLength;
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(config.getConnectString())
                .retryPolicy(config.getRetryPolicy())
                .connectionTimeoutMs(config.getConnectionTimeoutMs())
                .sessionTimeoutMs(config.getSessionTimeoutMs())
                .build();
        this.curatorFramework.start();
        // must after started then can we listen.
        while (true){
            if (this.curatorFramework.getState() == CuratorFrameworkState.STARTED) {
                this.select = new LeaderSelector(this.curatorFramework, this.path, this);
//                this.select.autoRequeue();
                this.select.start();
                logger.info(" client started.");
                break;
            }
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (this.curatorFramework.getState() == CuratorFrameworkState.STARTED){
            this.select.close();
            logger.info("select closed.");
            if (this.select.hasLeadership()){
                logger.info("select closed,but not release lock.");
            }
//            this.curatorFramework.close();
        }
        this.isClosed = true;
        logger.info("client closed.");

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void takeLeadership(CuratorFramework curatorFramework) {
        try {
            logger.info("client get the lock. -- thread id " + Thread.currentThread().getId());
            List<Integer> sequences = ZooUtils.getChildren(curatorFramework, this.path);
            if (sequences.size() == 0){
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
        sb.append(this.path).append("/").append(res);
        curatorFramework.delete().forPath(this.path + "/BIANJIE");
        logger.info("delete  node : " + this.path + "/BIANJIE.");

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(sb.toString(), (res + "").getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(this.path + "/BIANJIE", "".getBytes());
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

    /**
     * Called when there is a state change in the connection
     *
     * @param client   the client
     * @param newState the new state
     */
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
            logger.info("state changed, now is " + newState );
    }

    public boolean getCloseState() {
        return isClosed;
    }
}
