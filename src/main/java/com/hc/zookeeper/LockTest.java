package com.hc.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;

public class LockTest {
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 6000;
    private String lockName = "/mylock";
    private String lockNode = null;
    private ZooKeeper zk;
    public LockTest(){
        try {
            zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("Receive event "+watchedEvent);
                    if(Event.KeeperState.SyncConnected == watchedEvent.getState())
                        System.out.println("connection is established...");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureRoot() throws InterruptedException {
        try {
            if(zk.exists(lockName, true) == null){
                zk.create(lockName, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**获取锁**/
    //private
}
