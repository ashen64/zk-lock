package com.zk.lock.test;

import com.zk.lock.inter.LockInter;
import com.zk.lock.inter.impl.LockImpl;
import com.zk.lock.inter.impl.RecommendLockImpl;
import com.zk.lock.service.BuyTickets;

import java.util.concurrent.CountDownLatch;
/**
 * 购票客户端（测试方法二）
 */
public class BuyTicketsClient2 {
    //并发数量
    private static final int THREAD_NUM = 100;
    private CountDownLatch cdl = new CountDownLatch(THREAD_NUM);
    BuyTickets buyTicketsService=new BuyTickets();
    //private LockInter lock = new LockImpl();
    private LockInter lock = new RecommendLockImpl();


    @org.junit.Test
    public void fun() {
        for (int i = 0; i < THREAD_NUM; i++) {
            Thread t = new Thread(() -> {
                try {
                    // 减一
                    cdl.countDown();
                    // 等待
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try{
                    lock.getLock();
                    String s = buyTicketsService.getNumber();
                    System.out.println("取得票号是："+s);
                }finally {
                    if(lock!=null){
                        lock.unLock();
                    }
                }



            });
            t.start();
        }
        try {
            // 子线程创建完以后主线程退出，并没有等待子线程作业，所以先等待子线程作业。
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
