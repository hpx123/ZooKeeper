package com.hc.zookeeper;

public class NodeBlocklessLock implements ZooKeeperLock{

    /**尝试获取锁**/
    public boolean lock(String guidNodeName, String clientGuid) {

        if(exists(guidNodeName) == false){

        }
        return false;
    }

    public boolean release(String guidNodeName, String clientGuid) {
        return false;
    }

    public boolean exists(String guidNodeName) {
        return false;
    }
}
