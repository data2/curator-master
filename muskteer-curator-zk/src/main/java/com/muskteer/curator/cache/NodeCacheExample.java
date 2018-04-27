package com.muskteer.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

/**
 * Created by wanglei on 2018/1/31.
 */
public class NodeCacheExample {
    /**
     * Path Cache用来监控一个ZNode. 当节点的数据修改或者删除时，Node Cache能更新它的状态包含最新的改变。
     */
    private final static java.lang.String path = "/cachenode";

    public static void main(String[] args) {
        CuratorFramework client = null;
        try {
            client = CuratorFrameworkFactory.newClient("10.143.130.62:2182",
                    6000, 6000, new ExponentialBackoffRetry(1000, 3));
            client.start();
            final NodeCache nodeCache = new NodeCache(client, path);

            // create node.
            if (client.checkExists().forPath(path) == null){
                client.create().forPath(path, "55".getBytes());
                System.out.println(path  + " is not exist , already created.");
            }
            // add listener.
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    System.out.println("================== catch node data change ==================");
                    ChildData childData = nodeCache.getCurrentData();
                    if(childData == null){
                        System.out.println("===delete, path=" + path + ", childData=null");
                    }else{
                        System.out.println("===update or add, path=" + path + ", childData="
                                + new String(childData.getData()));
                    }
                }
            });
            nodeCache.start();

            client.setData().forPath(path,"5679".getBytes());
            client.setData().forPath(path,"8679".getBytes());

            CloseableUtils.closeQuietly(nodeCache);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
