package com.hc.zookeeper;

import org.apache.jute.Index;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private void ensureRoot(){
        try {
            if(zk.exists(lockName, true) == null){
                zk.create(lockName, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**获取锁**/

    private void getLock(){
        ensureRoot();
        String path = null;
        try {
            path = zk.create(lockName + "/mylock_","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            lockNode = path;
            List<String> minPath = zk.getChildren(lockName, false);
            Collections.sort(minPath);
            /**判断当前节点是否是最小节点，如果是则直接获取该锁**/
            if(!path.trim().isEmpty() && !minPath.get(0).trim().isEmpty() && path.equals(lockName + "/" +minPath.get(0))){
                System.out.println(Thread.currentThread().getName() + " get lock......");
                return;
            }
            String preNode = null;
            /**获取比当前节点的上一个节点(就是比当前节点小的）**/
            for(int i = minPath.size() - 1; i >= 0; i--){
                if(minPath.get(i).compareTo(path.substring(path.lastIndexOf('/') + 1)) < 0){
                    preNode = minPath.get(i);
                    break;
                }
            }

            /**监听上一个节点是否释放锁**/
            if(preNode != null){
                final String watchNode = preNode;
                final Thread thread = Thread.currentThread();
                Stat stat = zk.exists(lockName + "/" + watchNode, new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                            thread.interrupt();
                        }
                        try {
                            zk.exists(lockName + "/" + watchNode, true);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if(stat != null){
                    System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + lockName + "/" + watchNode);
                }
                try {
                    Thread.sleep(1000000000);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " notify......");
                    System.out.println(Thread.currentThread().getName() + "get lock ......");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**释放锁**/
    private void unlock(){
        try {
            System.out.println(Thread.currentThread().getName() + "  release lock......");
            zk.delete(lockNode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0;i<4;i++){
            service.execute(()-> {
                LockTest test = new LockTest();
                try {
                    test.getLock();
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                test.unlock();
            });
        }
        service.shutdown();
    }
}
