package com.zk.lock.config;

import org.I0Itec.zkclient.ZkClient;

public class ZookeeperAbstractLock {
    // zk连接地址
    private static final String CONNECTSTRING = "192.168.120.159:2181,192.168.120.160:2181,192.168.120.162:2181";
    // 创建zk连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING);
    protected static final String PATH = "/zk-lock";
    protected static final String PATH2 = "/zk-lock2";
}
