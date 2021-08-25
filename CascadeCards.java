package com.example.myapplication3;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class CascadeCards extends ViewGroup {
    public CascadeCards(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec ){
        /* 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);  //推荐宽度
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec); //推荐高度
        measureChildren(widthMeasureSpec,heightMeasureSpec); //对所有子元素进行测量
        int width=0;
        int height=0;
        int c_count=getChildCount();
        if(c_count==0)
            setMeasuredDimension(0,0);
        else{
            View childview=getChildAt(0);
            height=childview.getMeasuredHeight();
            width=(childview.getMeasuredWidth())*c_count/2;
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);

    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int c_count=getChildCount();
        int left=0;
        int ct=0;
        int cb;
        for(int i=0;i!=c_count;i++){
            View childview=getChildAt(i);
            int cwidth=childview.getMeasuredWidth();
            int cheight=childview.getMeasuredHeight();
            cb=ct+cheight;
            childview.layout(left,ct,left+cwidth,cb);
            ct+=30;
            // Log.d("Gameview2","layout已执行");
        }
    }
}
