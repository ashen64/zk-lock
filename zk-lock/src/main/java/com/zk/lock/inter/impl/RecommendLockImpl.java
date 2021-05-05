package com.zk.lock.inter.impl;

import com.zk.lock.config.ZookeeperAbstractLock;
import com.zk.lock.inter.LockInter;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecommendLockImpl extends ZookeeperAbstractLock implements LockInter {
    private CountDownLatch countDownLatch = null;
    //当前请求的节点前一个节点
    private String beforePath;
    //当前请求的节点
    private String currentPath;

    public RecommendLockImpl() {
        //如果不存在该节点则创建
        if (!this.zkClient.exists(PATH2)) {
            this.zkClient.createPersistent(PATH2);
        }
    }

    @Override
    public boolean getLock() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            //创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(PATH2 +"/", "lock");
        }
        //获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.zkClient.getChildren(PATH2);
        Collections.sort(childrens);
        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) {//如果当前节点在所有节点中排名第一则获取锁成功
            return true;
        } else {//如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens,
                    currentPath.substring(10));
            beforePath = PATH2 + '/' + childrens.get(wz - 1);
        }
        return false;
    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                if(countDownLatch!=null){
                    countDownLatch.countDown();
                }
            }
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }
        };
        //给排在前面的的节点增加数据删除的watcher,本质是启动另外一个线程去监听前置节点
        this.zkClient.subscribeDataChanges(beforePath, listener);
        if(this.zkClient.exists(beforePath)){
            countDownLatch=new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.zkClient.unsubscribeDataChanges(beforePath, listener);
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

    public static void main(String[] args) throws InterruptedException {
        RecommendLockImpl recommendLock = new RecommendLockImpl();
        String currentPath = recommendLock.zkClient.createEphemeralSequential(PATH2 +"/", "1234");

        System.out.println("创建节点完毕");
        List<String> childrens = recommendLock.zkClient.getChildren(PATH2);
        Collections.sort(childrens);
        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) {//如果当前节点在所有节点中排名第一则获取锁成功
        } else {//如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens,
                    currentPath.substring(10));
            String beforePath = PATH2 + '/' + childrens.get(wz - 1);
            System.out.println(beforePath);
        }
    }



}
