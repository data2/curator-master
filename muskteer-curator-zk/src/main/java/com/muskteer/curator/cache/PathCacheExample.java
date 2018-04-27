package com.muskteer.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

/**
 * Created by wanglei on 2018/1/31.
 */
public class PathCacheExample {
    /**
     * Path Cache用来监控一个ZNode的子节点. 当一个子节点增加， 更新，删除时，
     * Path Cache会改变它的状态， 会包含最新的子节点， 子节点的数据和状态。
     */
    private final static java.lang.String path = "/pathcache";

    public static void main(String[] args) {
        CuratorFramework client = null;
        try {
            client = CuratorFrameworkFactory.newClient("10.143.130.62:2182",
                    6000, 6000, new ExponentialBackoffRetry(1000, 3));
            client.start();
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);

            // create node.
            if (client.checkExists().forPath(path) == null){
                client.create().forPath(path, "path".getBytes());
                System.out.println(path  + " is not exist , already created.");
            }
            // add listener.
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework curatorFramework,
                                       PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                    System.out.println(type);
                    if (type.equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                        System.out.println("child node added.");
                    }
                    if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                        System.out.println("child node update.");
                    }
                }
            });
            pathChildrenCache.start();

            client.create().creatingParentsIfNeeded().forPath(path+"/node1", "node1".getBytes());
            client.setData().forPath(path+"/node1", "node111".getBytes());
            client.setData().forPath(path+"/node1", "node132311".getBytes());

            CloseableUtils.closeQuietly(pathChildrenCache);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
