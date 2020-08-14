package com.hc.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestZookeeper {

    private String connectString = "127.0.0.1:2181";
    /**设置最大连接时间**/
    private int sessionTimeout = 10000;
    private ZooKeeper zkClient;
    /**初始化，链接到zookeeper集群**/
    @Before
    public void init() throws IOException {
        /**watcher是监听器**/
         zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    System.out.println("-------start----------");
                    List<String> children = zkClient.getChildren("/", true);
                    for(String child : children){
                        System.out.println(child);
                    }
                    System.out.println("-------end---------");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**创建根节点**/
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String path = zkClient.create("/hututu", "huchengzuimei".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(path);
    }

    /**获取子节点并监听节点的变化**/
    @Test
    public void getChildren(){
        List<String> children = null;
        try {
            children = zkClient.getChildren("/", true);
            for(String child : children){
                System.out.println(child);
            }
            Thread.sleep(Long.MAX_VALUE);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**判断节点是否存在**/
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/hututu", false);
        System.out.println(stat == null? "noexist" : stat);
    }
}
