package com.example.myapplication3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

public class CardView extends androidx.appcompat.widget.AppCompatImageView {
    boolean chooseflag;
    int cardvalue;
    int index;
    //Bitmap background;//背面
   // Bitmap frontground;//正面
    int background;
    int frontground;
    Context context;
    public CardView(Context context) {
        super(context);
        chooseflag=false;
        cardvalue=-1;
        // this.setBackgroundColor(Color.BLACK);
        //background=null;
        //frontground=null;
        this.context=context;
    }
    public CardView(Context context,int value,Bitmap bitmap){
        super(context);
        cardvalue=value;
        chooseflag=false;
        //frontground=bitmap;
        //this.setImageBitmap(this.frontground);
        this.setScaleType(ScaleType.FIT_XY);//将图片设置为缩放到与imageview一样大
        this.context=context;
    }
    public CardView(Context context,int value){
        super(context);
        this.setBackgroundColor(Color.BLACK);
        cardvalue=value;
        this.context=context;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);  //推荐宽度
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec); //推荐高度
        setMeasuredDimension(sizeWidth,sizeHeight);//这里可以保证测量模式为EXACTLY,所以直接用推荐高度和推荐宽度
    }
    protected void reMeasure(int width,int height){
        setMeasuredDimension(width,height);
        this.requestLayout();
    }
    protected void setFrontBitmap(int drawable){
        frontground=drawable;
    }
    protected void setBackBitmap(int drawable){
        background=drawable;
    }
    protected void showBack(){//
        Glide.with(context).load(background).into(this);//Glide框架，图片加载器
        //this.setImageBitmap(background);
        this.setScaleType(ScaleType.FIT_XY);
    }
    protected void showFront(){//
        Glide.with(context).load(frontground).into(this);
        //this.setImageBitmap(frontground);
        this.setScaleType(ScaleType.FIT_XY);
    }
    public void setIndex(int index){
        this.index=index;
    }
}

