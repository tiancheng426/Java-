package com.example.myapplication3;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;

public class Gameview2 extends ViewGroup {
    int cardsnum;
    public Gameview2(Context context) {
        super(context);
    }
    public Gameview2(Context context,int num){
        super(context);
        cardsnum=num;
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
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int c_count=getChildCount();
        int left=0;
        for(int i=0;i!=c_count;i++){
            int ct = 0, cb = 0;
            View childview=getChildAt(i);
            int cwidth=childview.getMeasuredWidth();
            ct=30;
            cb=ct+childview.getMeasuredHeight();
            childview.layout(left,ct,left+cwidth,cb);
            left=left+cwidth/2;
           // Log.d("Gameview2","layout已执行");
        }
    }
    @Override
    public void onViewAdded(View view){
        super.onViewAdded(view);
    }
    public void reLayout(CardView view, int i){ // 为被选中的view重新设置 layout
        if(view.chooseflag==true){
            int cwidth=view.getMeasuredWidth();
            int top=10;
            int left=(cwidth/2)*i;
            view.layout(left,top,left+cwidth,top+view.getMeasuredHeight());
        }else{
            int cwidth=view.getMeasuredWidth();
            int top=30;
            int left=(cwidth/2)*i;
            view.layout(left,top,left+cwidth,top+view.getMeasuredHeight());
        }
    }
}
