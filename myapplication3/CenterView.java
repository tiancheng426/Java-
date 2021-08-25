package com.example.myapplication3;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class CenterView extends ViewGroup {
    public CenterView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec ){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);  //推荐宽度
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec); //推荐高度
        setMeasuredDimension(sizeWidth,sizeHeight);//这里可以保证测量模式为EXACTLY,所以直接用推荐高度和推荐宽度
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int c_count=getChildCount();
        int left=0,top=0,btm,right;
        for(int i=0;i!=c_count;i++){
            if((i+1)%10==0){
                top+=185;
                left=0;
            }
            View view=getChildAt(i);
            int cwidth=view.getMeasuredWidth();
            right=left+cwidth;
            btm=top+view.getMeasuredHeight();
            view.layout(left,top,right,btm);
            left+=cwidth;
        }
    }
}
