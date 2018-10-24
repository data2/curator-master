package com.muskteer.curator.client;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.Map;

/**
 * Created by wanglei on 2018/9/12.
 */
public class UdpStormOffsetMove {
    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
//                .namespace("udp")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectString("10.20.42.41:2181,10.20.42.42:2181,10.20.42.43:2181,10.20.42.44:2181,10.20.42.45:2181")
                .sessionTimeoutMs(3000)
                .connectionTimeoutMs(3000)
                .build();

        client.start();
        try {
            for (int i = 0 ; i <= 15; i++){
                String  json = new String(client.getData().forPath("/hqmtest/partition_"+ i));
                Map m = JSONObject.parseObject(json, Map.class);
                m.put("offset",((int)m.get("offset") + 2));
                String newJson = JSONObject.toJSONString(m);
                Stat l = client.setData().forPath("/hqmtest/partition_" + i, newJson.getBytes());
                System.out.println("partition:" + i + "\n old json \n" + json + ", newJson \n" + newJson);
//                Stat stat = new Stat();
//                client.getData().storingStatIn(stat).forPath("/hqmtest/partition_" + i);
//                Stat l = client.setData().withVersion(stat.getVersion()).forPath("/hqmtest/partition_" + i);
//

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        client.setData().forPath(path, "testVlaue2".getBytes());
//        System.out.println(new String(client.getData().forPath(path)));
//
//        ExistsBuilder existsBuilder = client.checkExists();
//        System.out.println(existsBuilder.forPath(path));
//        client.getChildren();

        client.close();
    }
}
