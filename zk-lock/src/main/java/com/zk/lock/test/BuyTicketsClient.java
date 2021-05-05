package com.zk.lock.test;

import com.zk.lock.inter.LockInter;
import com.zk.lock.inter.impl.LockImpl;
import com.zk.lock.inter.impl.RecommendLockImpl;
import com.zk.lock.service.BuyTickets;

/**
 * 购票客户端（测试方法一）
 */
public class BuyTicketsClient implements Runnable {
    private BuyTickets buyTickets = new BuyTickets();
    //private LockInter lock = new LockImpl();
    private LockInter lock = new RecommendLockImpl();

    public void run() {
        getNumber();
    }
    public void getNumber() {
        try {
            lock.getLock();
            String number = buyTickets.getNumber();
            System.out.println(Thread.currentThread().getName() + ",生成票号ID:" + number);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unLock();
        }
    }
    public static void main(String[] args) {
        //50个线程
        for (int i = 0; i < 50; i++) {
            new Thread( new BuyTicketsClient()).start();
        }
    }
}