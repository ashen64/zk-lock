package com.zk.lock.inter.impl;

import com.zk.lock.config.ZookeeperAbstractLock;
import com.zk.lock.inter.LockInter;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

public class LockImpl extends ZookeeperAbstractLock implements LockInter {
    private CountDownLatch countDownLatch = null;
    @Override
    public boolean getLock() {
        try {
            //获取锁
            zkClient.createEphemeral(PATH);
            return true;
        } catch (Exception e) {
            //没有获取锁
            return false;
        }
    }

    @Override
    public void waitLock() {
        IZkDataListener izkDataListener = new IZkDataListener() {
            //节点被删除则触发该方法
            public void handleDataDeleted(String path) throws Exception {
                // 一旦节点被删除则唤醒线程
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
            //节点被改变则触发该方法
            public void handleDataChange(String path, Object data) throws Exception {
            }
        };
        // 注册事件
        zkClient.subscribeDataChanges(PATH, izkDataListener);

        //如果节点存
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                //等待，一直等到接受到事件通知
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 删除监听
        zkClient.unsubscribeDataChanges(PATH, izkDataListener);
    }

    @Override
    public void unLock() {
        //释放锁
        if (zkClient != null) {
            try{
                zkClient.delete(PATH);
            }finally {
                zkClient.close();
            }
        }
    }
}