package com.zk.lock.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 购票后端业务
 */
public class BuyTickets {
    //全局票量(假设有20张票)
    public  static int COUNT = 0;

    //生成订单ID
    public   String getNumber() {
        SimpleDateFormat simpt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String s=simpt.format(new Date()) + "-" + ++COUNT;
        if(COUNT>20){
            return "没有票了。";
        }
        return s;
    }
    public static void main(String[] args) {
        for (int i=0; i<100;i++){
            String number = new BuyTickets().getNumber();
            System.out.println(number);
        }

    }
}