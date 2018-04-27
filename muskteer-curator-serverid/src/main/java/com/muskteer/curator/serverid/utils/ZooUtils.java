package com.muskteer.curator.serverid.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wanglei on 2018/2/2.
 */
public class ZooUtils {

    public static List<Integer> getChildren(CuratorFramework client, String path) throws Exception {
        List<String> children = client.getChildren().forPath(path);
        List<Integer> sequences = new ArrayList<>();
        for (String childNode : children) {
            if (StringUtils.isNotEmpty(childNode) && NumberUtils.isCreatable(childNode)) {
                System.out.println(path + " include childNode : " + childNode);
                sequences.add(NumberUtils.toInt(childNode));
            } else {
                System.out.println(childNode + " is null or not number, excluded.");
            }
        }
        return sequences;
    }

    public static void sort(List<Integer> sequences) {
        Collections.sort(sequences, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return NumberUtils.compare(o1.intValue(), o2.intValue());
            }
        });
    }

    public static int getCanUseServerId(List<Integer> sequences) {
        // sequence start from 0.
        for (int i = 0; i < sequences.size(); i++) {
            int comparedRes = NumberUtils.compare(i, sequences.get(i));
            if (comparedRes == 0) {
                continue;
            } else if (comparedRes == -1) {
                return i;
            }
        }
        return sequences.get(sequences.size() - 1) + 1;
    }
}
