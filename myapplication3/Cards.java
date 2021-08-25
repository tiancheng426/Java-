package com.example.myapplication3;

import java.util.Random;

public class Cards{
    //声明一副扑克牌
    public int[]pukes=new int[54];
    private static Cards cardsInstance=null;
    private Cards(){
        setPuke();
        shuffle();
    }
    public static Cards getInstance(){
        if(cardsInstance==null){
            cardsInstance=new Cards();
        }
        return cardsInstance;
    }
    /**给54张扑克牌赋值：1~54*/
    private void setPuke(){
        for(int i=0;i<54;i++){
            pukes[i]=i+1;
        }
    }
    /**洗牌*/
    private void shuffle(){
        Random rdm=new Random();
        for(int i=0;i<54;i++){
//random.nextInt();是个前闭后开的方法：0~53
            int rdmNo=rdm.nextInt(54);
            int temp=pukes[i];
            pukes[i]=pukes[rdmNo];
            pukes[rdmNo]=temp;
        }
    }
}
