package com.hc.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestZookeeper {

    private String connectString = "192.168,43.109:2181";
    /**设置最大连接时间**/
    private int sessionTimeout = 10000;
    private ZooKeeper zkClient;
    /**初始化，链接到zookeeper集群**/
    @Before
    public void init() throws IOException {
        /**watcher是监听器**/
         zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    /**创建根节点**/
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String path = zkClient.create("/hututu", "huchengzuimei".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(path);
    }
}
