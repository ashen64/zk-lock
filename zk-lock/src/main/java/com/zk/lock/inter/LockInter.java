package com.zk.lock.inter;

public interface LockInter {
    //获取锁
    boolean getLock();
    //等待锁
    void waitLock();
    //释放锁
    void unLock();
}
