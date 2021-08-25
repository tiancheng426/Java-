package com.example.myapplication3;

import android.graphics.Rect;

public class Person{
    private final Cards mCards=Cards.getInstance();
    public int [] person1=new int[17];
    public int [] person2=new int[17];
    public int [] person3=new int[17];
    //余下三张属于地主的
    public int[]threePukes=new int[3];  //底牌
    public Person(){
        personHold(mCards.pukes);
    }
    /**分牌*/
    private void personHold(int[] pukes){
        int k=0;
        for(int i=0;i<3;i++){
            if(i==0){
                for(int j=0;j<17;j++){
                    person1[j]=pukes[k++];
                }
//将其排序
                sort(person1);
            }
            if(i==1){
                for(int j=0;j<17;j++){
                    person2[j]=pukes[k++];
                }
//将其排序
                sort(person2);
            }
            if(i==2){
                for(int j=0;j<17;j++){
                    person3[j]=pukes[k++];
                }
//将其排序
                sort(person3);
            }
        }
        threePukes[0]=pukes[51];
        threePukes[1]=pukes[52];
        threePukes[2]=pukes[53];
    }
    /**对每个玩家手里的牌排序:使用冒泡排序*/
    private void sort(int[]ary){
        for(int i=0;i<ary.length;i++){
            for(int j=0;j<ary.length-i-1;j++){
                if(ary[j]>ary[j+1]){
                    int temp=ary[j];
                    ary[j]=ary[j+1];
                    ary[j+1]=temp;
                }
            }
        }
    }
    /**
     *对应扑克所在图片上的位置
     *159…………53
     *2610…………54
     *3711
     *4812
     */
    public Rect cardRect(int cardValue, int width, int height){
        int x,y;
        if(cardValue%4==0){
            x=cardValue/4-1;
            y=4;
        }else{
            x=cardValue/4;
            y=cardValue%4;
        }
        int left=x*width;
        int top=(y-1)*height;
        int right=(x+1)*width;
        int bottom=(y)*height;
        return new Rect(left,top,right,bottom);
    }
}
