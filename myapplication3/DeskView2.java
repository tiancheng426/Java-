package com.example.myapplication3;
//客户端用

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
public class DeskView2 extends ViewGroup {
    String ip;
    Integer playernumber;//玩家号码，用于标记出牌顺序
    int tagnum;
    boolean play;//是否该此对象出牌
    boolean receive;//是否可接受数据？
    boolean first;//地主玩家标记
    boolean gameover;//游戏结束标记
    boolean break1;// 循环1可结束标识
    Button button1;
    Button button2;
    Button button_f;//地主按钮
    Button button_n;
    Handler mhandler;
    CenterView centerview;
    Gameview2 buttomview;
    CascadeCards CView1;
    CascadeCards CView2;
    myThread1 pukesthread;//发牌线程
    ArrayList<Integer> index17;
    ArrayList<CardView> pukes;//54 张牌
    socketThread socketthread;//socket线程
    ArrayList<Integer> playingcards;//此容器用于装载正在出的牌
    ArrayList<Integer> receivecards;//用于记录从通信中接受到的牌
    CardView topview1;
    CardView topview2;
    CardView topview3;
    int current_tag;
    AlertDialog.Builder builder;//弹窗对象
    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DeskView2(final Context context) {
        super(context);
        play=false;
        first=false;
        receive=true;
        break1=false;
        mhandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        for(int i=0;i!=buttomview.getChildCount();i++){
                            CardView view=(CardView) buttomview.getChildAt(i);
                            if(view.chooseflag){
                                buttomview.reLayout(view,i);  //为被选中的view重新设置layout
                            }
                        }
                        break;
                    case 2:
                        for(int i=0;i!=buttomview.getChildCount();i++){
                            CardView view=(CardView) buttomview.getChildAt(i);
                            if(view.cardvalue==msg.arg1){
                                view.chooseflag=false;
                                buttomview.reLayout(view,i);
                                break;
                            }
                        }
                        break;
                    case 3://准备就绪,开始发牌
                        socketthread=new socketThread();
                        socketthread.start();
                        while(true){
                            if(pukesthread.flag==true){
                                pukes=pukesthread.returnpukes54();
                            break;}
                        }
                        while(buttomview.getChildCount()==0){
                            if(socketthread.indexflag==true){
                                for(int i=0;i!=index17.size();i++){
                                    buttomview.addView(pukes.get(index17.get(i)),100,154);
                                }
                            }
                        }
                        button1.setVisibility(View.INVISIBLE);// 设置该按钮不可见
                        centerview.setVisibility(View.VISIBLE);  //设置中心view可见
                        DeskView2.this.addView(button2,200,120);
                        break;
                    case 4://出牌
                        for(int i=0;i<buttomview.getChildCount();i++){
                            CardView view=(CardView)buttomview.getChildAt(i);
                            if(view.chooseflag){
                                buttomview.removeView(view);//先从父级移除，再添加
                                centerview.addView(view);
                                playingcards.add(view.cardvalue);//将牌装入记录，用于发送
                            }
                        }
                        playingcards.add(playernumber);//最后装入玩家号码
                        play=true;//出牌完毕，置好标记码,用于通信线程检测
                        receive=true;
                        break;
                    case 5://收到数据更新UI
                        for(int i=0;i<pukes.size();i++){
                            if(receivecards.contains(pukes.get(i).cardvalue)){
                                CardView view= pukes.get(i);
                                centerview.addView(view,100,154);
                            }
                        }
                        break;
                }
                refreshUI(buttomview);
            }
        };
        //this.setBackground(getResources().getDrawable(R.drawable.gameback2));
        this.setBackgroundColor(Color.GREEN);
        thisaddview(context); //添加view

        pukesthread=new myThread1(context);
        pukesthread.start();

    }//构造函数结束
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("HandlerLeak")
    public DeskView2(final Context context, String ip) {
        super(context);
        this.ip=ip;
        new Thread(new Runnable() {
            @Override
            public void run() {
                play=false;
                receive=true;
                gameover=false;
                tagnum=-1;
                current_tag=1;
                builder=new AlertDialog.Builder(context);
                builder.setMessage("不为你的出牌次序");
                builder.setPositiveButton("确定",null);
            }
        }).start();
        mhandler=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        for(int i=0;i!=buttomview.getChildCount();i++){
                            CardView view=(CardView) buttomview.getChildAt(i);
                            if(view.chooseflag){
                                buttomview.reLayout(view,i);  //为被选中的view重新设置layout
                            }
                        }
                        break;
                    case 2:
                        for(int i=0;i!=buttomview.getChildCount();i++){
                            CardView view=(CardView) buttomview.getChildAt(i);
                            if(view.cardvalue==msg.arg1){
                                view.chooseflag=false;
                                buttomview.reLayout(view,i);
                                break;
                            }
                        }
                        break;
                    case 3://准备就绪,开始发牌
                        socketthread=new socketThread();
                        socketthread.start();
                        pukes=pukesthread.returnpukes54();
                        button1.setVisibility(View.INVISIBLE);// 设置该按钮不可见
                        centerview.setVisibility(View.VISIBLE);  //设置中心view可见
                        DeskView2.this.addView(button2,200,120);
                        DeskView2.this.addView(button_f,200,120);
                        DeskView2.this.addView(button_n,200,120);
                        break;
                    case 4:// 出牌
                        if(current_tag!=1){
                            final AlertDialog dialog=builder.create();
                            dialog.show();
                            break;
                        }
                        centerview.removeAllViews();
                        for(int i=0;i<buttomview.getChildCount();i++){
                            CardView view=(CardView)buttomview.getChildAt(i);
                            if(view.chooseflag){
                                buttomview.removeView(view);//先从父级移除，再添加
                                playingcards.add(view.cardvalue);//将牌装入记录，用于发送
                                i--;
                            }
                        }
                        playingcards.add(tagnum);//最后装入玩家号码
                        play=true;//出牌完毕，置好标记码,用于通信线程检测
                        current_tag=current_tag%3+1;
                        break;
                    case 5://收到服务器数据后更新UI
                       case5_func();
                        break;
                    case 6:
                        Log.d("tag","case 6`执行");
                        DeskView2.this.removeView(topview1);
                        DeskView2.this.removeView(topview2);
                        DeskView2.this.removeView(topview3);
                        buttomview.addView(topview1);
                        buttomview.addView(topview2);
                        buttomview.addView(topview3);
                        Sort.sort(buttomview);
                        topview3.showFront();
                        topview2.showFront();
                        topview1.showFront();
                        first=true;
                        current_tag=1;
                        break1=true;
                        button_f.setText("不出");
                        button_f.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mhandler.sendMessage(mhandler.obtainMessage(11));
                            }
                        });
                        //button_f.setVisibility(View.INVISIBLE);
                        button_n.setVisibility(View.INVISIBLE);
                        break;
                    case 7://收到数据更新buttomview
                        if(playernumber==2){
                            for(int i=17;i!=34;i++){
                                pukes.get(index17.get(i)).showFront();
                                buttomview.addView(pukes.get(index17.get(i)),145,185);
                            }
                        }
                        else if(playernumber==3){
                            for(int i=34;i!=51;i++){
                                pukes.get(index17.get(i)).showFront();
                                buttomview.addView(pukes.get(index17.get(i)),145,185);
                            }
                        }
                        Sort.sort(buttomview);// 整理排序
                        topview1=pukes.get(index17.get(51));
                        topview2=pukes.get(index17.get(52));
                        topview3=pukes.get(index17.get(53));
                        DeskView2.this.addView(topview1,145,185);
                        DeskView2.this.addView(topview2,145,185);
                        DeskView2.this.addView(topview3,145,185);
                        topview3.showBack();
                        topview2.showBack();
                        topview1.showBack();
                        addListener(buttomview);
                        break;
                    case 8:
                        Log.d("tag","case 8执行");
                        topview3.showFront();
                        topview2.showFront();
                        topview1.showFront();
                        first=false;
                        break1=true;
                        button_f.setText("不出");
                        button_f.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mhandler.sendMessage(mhandler.obtainMessage(11));
                            }
                        });
                       // button_f.setVisibility(View.INVISIBLE);
                        button_n.setVisibility(View.INVISIBLE);

                        break;
                    case 9:
                        if(playernumber==2){
                            for(int i=0;i!=17;i++){
                                pukes.get(index17.get(i)).showBack();
                                CView1.addView(pukes.get(index17.get(i)),154,120);
                            }
                            for(int i=34;i!=51;i++){
                                pukes.get(index17.get(i)).showBack();
                                CView2.addView(pukes.get(index17.get(i)),154,120);
                            }
                        }else if(playernumber==3){
                            for(int i=17;i!=34;i++){
                                pukes.get(index17.get(i)).showBack();
                                CView1.addView(pukes.get(index17.get(i)),154,120);
                            }
                            for(int i=0;i!=17;i++){
                                pukes.get(index17.get(i)).showBack();
                                CView2.addView(pukes.get(index17.get(i)),154,120);
                            }
                        }
                        break;
                    case 11:
                        centerview.removeAllViews();
                        synchronized (playingcards){
                            playingcards.add(105);
                            playingcards.add(tagnum);
                        }
                        play=true;
                        break;
                    default:
                        break;
                }
            }
        };
        this.setBackground(getResources().getDrawable(R.drawable.gameback3));
        //this.setBackgroundColor(Color.GREEN);
        thisaddview(context); //添加view
        pukesthread=new myThread1(context);
        pukesthread.start();

    }//构造函数结束

    public void addListener(Gameview2 pukeview){
        int c_count=pukeview.getChildCount();
        for(int i=0;i!=c_count;i++){
            final CardView childview= (CardView) pukeview.getChildAt(i);
            childview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(childview.chooseflag==false){
                        childview.chooseflag=true;
                        mhandler.sendMessage(mhandler.obtainMessage(1));
                    }
                    else{
                        mhandler.sendMessage(mhandler.obtainMessage(2,childview.cardvalue,0));
                    }
                }
            });
        }
        topview1.setOnClickListener(v -> {
            if(!topview1.chooseflag){
                topview1.chooseflag=true;
                mhandler.sendMessage(mhandler.obtainMessage(1));
            }
            else{
                mhandler.sendMessage(mhandler.obtainMessage(2,topview1.cardvalue,0));
            }
        });
        topview2.setOnClickListener(v -> {
            if(!topview2.chooseflag){
                topview2.chooseflag=true;
                mhandler.sendMessage(mhandler.obtainMessage(1));
            }
            else{
                mhandler.sendMessage(mhandler.obtainMessage(2,topview2.cardvalue,0));
            }
        });
        topview3.setOnClickListener(v -> {
            if(!topview3.chooseflag){
                topview3.chooseflag=true;
                mhandler.sendMessage(mhandler.obtainMessage(1));
            }
            else{
                mhandler.sendMessage(mhandler.obtainMessage(2,topview3.cardvalue,0));
            }
        });
    }

    private void refreshUI(Gameview2 view){//刷新UI界面
        view.invalidate();
        // this.requestLayout();
        this.invalidate();
    }
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec ){
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width=800;
        int height=400;
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (heightMode == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }
    private void case5_func(){
        centerview.removeAllViews();
        int tag3=receivecards.get(receivecards.size()-1);//容器的最后一位是标记码，标记哪位玩家出的牌
        if(playernumber==2){
            if(tag3==102){//服务器出的牌
                current_tag=3;
                if(!first)
                    toolfunc();
                for(int i=0;i<CView1.getChildCount();i++){
                    CardView childview =(CardView) CView1.getChildAt(i);
                    if(receivecards.contains(childview.cardvalue)){
                        CView1.removeView(childview);
                        centerview.addView(childview);
                        childview.showFront();
                        childview.reMeasure(145,185);
                        i--;//remove会重新排序index,所以自减
                    }
                }
                current_tag=current_tag%3+1;
            }
            else if(tag3==tagnum){//自己的牌
                for(int i=0;i<pukes.size();i++){
                    if(receivecards.contains(pukes.get(i).cardvalue)){
                        CardView view=(CardView)pukes.get(i);
                        centerview.addView(view,120,154);
                    }
                }}
            else{//另一位客户端出的牌
                current_tag=2;
                if(!first)
                    toolfunc();
                for(int i=0;i<CView2.getChildCount();i++){
                    CardView childview =(CardView) CView2.getChildAt(i);
                    if(receivecards.contains(childview.cardvalue)){
                        CView2.removeView(childview);
                        centerview.addView(childview);
                        childview.showFront();
                        childview.reMeasure(145,185);
                        i--;
                    }
                }
                current_tag=current_tag%3+1;
            }
        }else if(playernumber==3){
            if(tag3==102){//服务器出的牌
                current_tag=2;
                if(!first)
                    toolfunc();
                for(int i=0;i<CView2.getChildCount();i++){
                    CardView childview =(CardView) CView2.getChildAt(i);
                    if(receivecards.contains(childview.cardvalue)){
                        CView2.removeView(childview);
                        centerview.addView(childview);
                        childview.showFront();
                        childview.reMeasure(145,185);
                        i--;
                    }
                }
                current_tag=current_tag%3+1;
            }
            else if(tag3==tagnum){//自己的牌
                for(int i=0;i<pukes.size();i++){
                    if(receivecards.contains(pukes.get(i).cardvalue)){
                        CardView view=(CardView)pukes.get(i);
                        centerview.addView(view,120,154);
                    }
                }}
            else{//另一位客户端出的牌
                current_tag=3;
                if(!first)
                    toolfunc();
                for(int i=0;i<CView1.getChildCount();i++){
                    CardView childview =(CardView) CView1.getChildAt(i);
                    if(receivecards.contains(childview.cardvalue)){
                        CView1.removeView(childview);
                        centerview.addView(childview);
                        childview.showFront();
                        childview.reMeasure(145,185);
                        i--;
                    }
                }
                current_tag=current_tag%3+1;
            }
        }

        receivecards.clear();
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int c_count=getChildCount();
        if(c_count==0){
            return;
        }
        int cheight=0;
        int cwidth=0;
        for(int i=0;i!=c_count;i++){
            View childview=getChildAt(i);
            cheight=childview.getMeasuredHeight() ;
            cwidth=childview.getMeasuredWidth();
            int cl=0,cr=0,ct=0,cb=0;
            switch (i){
                case 0:
                    cl=40;
                    ct=85;
                    break;
                case 1:
                    cl = getWidth() - cwidth -35;
                    ct = 85;
                    break;
                case 2:
                    cl=300;
                    ct=800;
                    break;
                case 3: //按钮1
                    cl=450;
                    ct=200;
                    break;
                case 4:// centerview
                    cl=400;
                    ct=200;
                    break;
                case 5://按钮2
                    cl=780;
                    ct=660;
                    break;
                case 6://地主按钮
                    cl=990;
                    ct=660;
                    break;
                case 7:
                    cl=1210;
                    ct=660;
                    break;
                case 8:
                    cl=400;
                    ct=5;
                    break;
                case 9:
                    cl=550;
                    ct=5;
                    break;
                case 10:
                    cl=700;
                    ct=5;
                    break;
            }
            cr=cl+cwidth;
            cb=ct+cheight;
            childview.layout(cl,ct,cr,cb);

        }
    }

    protected void toolfunc(){
        if(receivecards.contains(topview1.cardvalue)){
            DeskView2.this.removeView(topview1);
            centerview.addView(topview1);
        }
        if(receivecards.contains(topview2.cardvalue)){
            DeskView2.this.removeView(topview2);
            centerview.addView(topview2);
        }
        if(receivecards.contains(topview3.cardvalue)){
            DeskView2.this.removeView(topview3);
            centerview.addView(topview3);
        }
    }
    private void thisaddview(Context context){
        CView1=new CascadeCards(context);
        CView2=new CascadeCards(context);
        centerview=new CenterView(context);

        button1=new Button(context);
        button1.setText("准备");
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mhandler.sendMessage(mhandler.obtainMessage(3));
            }
        });
        button2=new Button(context);
        button2.setText(" 出牌");
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mhandler.sendMessage(mhandler.obtainMessage(4));}// 出牌消息
        });
        button_f=new Button(context);
        button_f.setText("地主");
        button_f.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mhandler.sendMessage(mhandler.obtainMessage(6)); }
        });
        button_n=new Button(context);
        button_n.setText("不抢");
        button_n.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mhandler.sendMessage(mhandler.obtainMessage(8)); }
        });
        buttomview=new Gameview2(context);
        this.addView(CView1,154,650);
        this.addView(CView2,154,650);
        //Log.d("add view1,view2","已添加");
        this.addView(buttomview,1550,210);
        this.addView(button1,200,120);
        this.addView(centerview,750,370);
        centerview.setVisibility(View.INVISIBLE);//设置不可见
        //Log.d("pukesview","已添加");
    }

    class socketThread extends Thread {
        public boolean indexflag;//标记码

        public socketThread() {
            indexflag=false;
        }
        @Override
        public void run() {
            //定义消息
            playingcards=new ArrayList<>();
            receivecards=new ArrayList<>();
            Message msg = new Message();
            index17=new ArrayList<>();
            try {
                //连接服务器 并设置连接超时为5秒
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 3306));

                //获取输入输出流
                OutputStream output = socket.getOutputStream();
                //获取输出输出流
                InputStream input = socket.getInputStream();
                //向服务器发送信息
                output.write(101);
                Integer num = input.read();
                if (num == 100) {
                    playernumber=input.read();//读入服务器分配的玩家号码
                    if(playernumber==2)
                        tagnum=103;
                    else if(playernumber==3)
                        tagnum=104;
                    for (int i = 0; i != 54; i++)
                        index17.add(input.read());
                    mhandler.sendMessage(mhandler.obtainMessage(7));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(true){
                                if(buttomview.getChildCount()==0){
                                    gameover=true;
                                    break;
                                }
                                if(CView1.getChildCount()==0){
                                    gameover=true;
                                    break;
                                }
                                if(CView2.getChildCount()==0){
                                    gameover=true;
                                    break;
                                }
                            }
                        }
                    }).start();
                boolean tag1 = true;
                while (tag1) { //循环1
                    if (break1) {
                        if (first) {//检查是不是地主玩家
                            output.write(playernumber);//如果是，写入自己的玩家号码告知服务端
                            receive = false;//自己是地主不用接受数据
                            input.read();//跳过106数据
                        } else {
                            output.write(103);//不为地主就发送提示码，防止服务端引起read（）阻塞
                            receive = true;
                        }
                        tag1 = false;
                        sleep(500);//睡眠适度减少程序开销
                    }
                    if(input.available()!=0){
                        Log.d("111","111111");
                        int temp=input.read();
                        if(temp==106){
                            mhandler.sendMessage(mhandler.obtainMessage(8));
                            tag1=false;
                        }
                    }
                }
                output.flush();
                while(input.available()!=0){
                    input.read();
                }
                Log.d("客户端stream","已调过所有可读字节");
                mhandler.sendMessage(mhandler.obtainMessage(9));
                this.sleep(500);
                //mhandler.sendMessage(mhandler.obtainMessage(10));
                indexflag=true;
                while(true){
                   // Log.d("socket","游戏循环启动");
                    if(play){
                        int size=playingcards.size();
                        output.write(size);//先写入可读字节数,便于接收方处理
                        for(int i=0;i!=size;i++){
                            output.write(playingcards.get(i));
                        }
                        playingcards.clear();// 数据发送后置空容器以便后面继续写入
                        play=false;
                        receive=true;
                    }
                    if(receive && input.available()!=0){
                        int bytesize=input.read();
                        for(int i=0;i!=bytesize;i++)
                            receivecards.add(input.read());
                        mhandler.sendMessage(mhandler.obtainMessage(5));

                    }
                    this.sleep(100);//线程睡眠0.2秒
                }

                }
                socket.shutdownOutput();
                socket.shutdownInput();
                input.close();
                output.close();

            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                //发送消息 修改UI线程中的组件
                mhandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class myThread1 extends Thread{
    boolean flag;  //线程完成标志;
    Context context;
    private ArrayList<CardView> puke54;
    myThread1(Context cont){
        context=cont;
        puke54=new ArrayList<>();
        flag=false;
    }
    @Override
    public void run(){
        puke54=initBitmap(context);
        flag=true;
    }
    public ArrayList<CardView> returnpukes54(){ return puke54;}
    /*public ArrayList<Bitmap> returnBitmaps(Context context){
        ArrayList<Bitmap> bitmaps=new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.a1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.a2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.a3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.a4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.two1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.two2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.two3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.two4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.three1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.three2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.three3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.three4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.four1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.four2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.four3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.four4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.five1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.five2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.five3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.five4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.six1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.six2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.six3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.six4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.seven1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.seven2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.seven3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.seven4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.eight1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.eight2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.eight3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.eight4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.nine1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.nine2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.nine3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.nine4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.ten1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.ten2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.ten3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.ten4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.j1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.j2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.j3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.j4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.q1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.q2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.q3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.q4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.k1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.k2));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.k3));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.k4));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.joker1));
        bitmaps.add(BitmapFactory.decodeResource(context.getResources(),R.drawable.joker2));
        return bitmaps;
    }*/
    public ArrayList<Integer> returnDrawable(Context context){
        ArrayList<Integer> bitmaps=new ArrayList<>();
        bitmaps.add(R.drawable.a1);
        bitmaps.add(R.drawable.a2);
        bitmaps.add(R.drawable.a3);
        bitmaps.add(R.drawable.a4);
        bitmaps.add(R.drawable.two1);
        bitmaps.add(R.drawable.two2);
        bitmaps.add(R.drawable.two3);
        bitmaps.add(R.drawable.two4);
        bitmaps.add(R.drawable.three1);
        bitmaps.add(R.drawable.three2);
        bitmaps.add(R.drawable.three3);
        bitmaps.add(R.drawable.three4);
        bitmaps.add(R.drawable.four1);
        bitmaps.add(R.drawable.four2);
        bitmaps.add(R.drawable.four3);
        bitmaps.add(R.drawable.four4);
        bitmaps.add(R.drawable.five1);
        bitmaps.add(R.drawable.five2);
        bitmaps.add(R.drawable.five3);
        bitmaps.add(R.drawable.five4);
        bitmaps.add(R.drawable.six1);
        bitmaps.add(R.drawable.six2);
        bitmaps.add(R.drawable.six3);
        bitmaps.add(R.drawable.six4);
        bitmaps.add(R.drawable.seven1);
        bitmaps.add(R.drawable.seven2);
        bitmaps.add(R.drawable.seven3);
        bitmaps.add(R.drawable.seven4);
        bitmaps.add(R.drawable.eight1);
        bitmaps.add(R.drawable.eight2);
        bitmaps.add(R.drawable.eight3);
        bitmaps.add(R.drawable.eight4);
        bitmaps.add(R.drawable.nine1);
        bitmaps.add(R.drawable.nine2);
        bitmaps.add(R.drawable.nine3);
        bitmaps.add(R.drawable.nine4);
        bitmaps.add(R.drawable.ten1);
        bitmaps.add(R.drawable.ten2);
        bitmaps.add(R.drawable.ten3);
        bitmaps.add(R.drawable.ten4);
        bitmaps.add(R.drawable.j1);
        bitmaps.add(R.drawable.j2);
        bitmaps.add(R.drawable.j3);
        bitmaps.add(R.drawable.j4);
        bitmaps.add(R.drawable.q1);
        bitmaps.add(R.drawable.q2);
        bitmaps.add(R.drawable.q3);
        bitmaps.add(R.drawable.q4);
        bitmaps.add(R.drawable.k1);
        bitmaps.add(R.drawable.k2);
        bitmaps.add(R.drawable.k3);
        bitmaps.add(R.drawable.k4);
        bitmaps.add(R.drawable.joker1);
        bitmaps.add(R.drawable.joker2);
        return bitmaps;
    }
    public ArrayList<CardView> returnCards(Context context){
        ArrayList<CardView> cards=new ArrayList<CardView>();
        for(int i=0;i!=54;i++){
            cards.add(new CardView(context,i+1));
        }
        return cards;
    }
    public  ArrayList<CardView> initBitmap(Context context){//初始化图片
        ArrayList<CardView> cards=returnCards(context);
        ArrayList<Integer> bitmaps=returnDrawable(context);
        for(int i=0;i!=bitmaps.size();i++){
            cards.get(i).setFrontBitmap(bitmaps.get(i));
            //cards.get(i).showFront();
            //cards.get(i).setBackBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.pukebackbround));
            cards.get(i).setBackBitmap(R.drawable.pukebackbround);
        }
        return cards;
    }


}
