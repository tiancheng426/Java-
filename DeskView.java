/*
* 定义：
* 红桃1，方块2，黑桃3，梅花4
* */
package com.example.myapplication3;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;

public class DeskView extends ViewGroup {
    int playernumber;//玩家号码，用于标记出牌顺序
    boolean first;//标记是否最先出牌
    boolean play;//是否该此对象出牌
    boolean receive;//是否可接受数据？
    boolean gameover;
    boolean break1;//循环1可结束标识
    boolean tag2; //游戏循环标识之一,是否可向客户端写数据
    Integer tag3;//记录两个线程是否都已经发送了数据
    boolean tag5;
    boolean tag7;
    boolean send;
    int tag6;
    Integer current_player;// 当前出牌的玩家
    Button button1;
    Button button2;
    Button button_f;//地主按钮
    Button button_n;//不抢
    CenterView centerview;
    Handler mhandler;
    Gameview2 buttomview;
    CascadeCards CView1;
    CascadeCards CView2;
    myThread pukesthread;
    ArrayList<CardView> pukes54;
    ArrayList<Integer> playingcards;//此容器用于装载正在出的牌
    ArrayList<Integer> receivecards;//用于记录从通信中接受到的牌
    CardView topview1;
    CardView topview2;
    CardView topview3;
    AlertDialog.Builder builder;//弹窗对象

    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DeskView(Context context) {
        super(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                playernumber=1;//服务器端玩家永远被置为1，其他两个玩家分别为2，3，该号码对游戏无影响，仅用于标记
                current_player=0;
                play=false;
                first=false;
                gameover=false;
                break1=false;
                tag2=false;
                tag5=false;
                tag6=0;
                tag7=false;
                send=false;
                playingcards=new ArrayList<>();
                builder=new AlertDialog.Builder(context);
                //builder.setMessage("不为你的出牌次序");
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
                                buttomview.reLayout(view,i);
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
                    case 3:// 准备
                        case3_func();
                        break;
                    case 4://出牌
                        if(current_player!=1){
                            builder.setMessage("不为你的出牌次序");
                            final AlertDialog dialog=builder.create();
                            dialog.show();
                            break;
                        }
                        centerview.removeAllViews();
                        synchronized (playingcards){
                            for(int i=0;i<buttomview.getChildCount();i++){ //用不等号会异常, 用小于号正常
                                CardView view=(CardView)buttomview.getChildAt(i);
                                if(view.chooseflag){
                                    buttomview.removeView(view);
                                    centerview.addView(view);
                                    view.setClickable(false);//出牌之后取消卡牌的点击事件监听
                                    playingcards.add(view.cardvalue);//将牌装入记录，用于发送
                                    i--;//由于remove之后child的index会变化，而i却是在无差别增大的，所以会跳过下一个数据,i自减1消除该漏洞
                                }
                            }
                            playingcards.add(102);//最后装入玩家号码
                        }
                        play=true;
                        break;
                    case 5:
                        //centerview.removeAllViews();
                        synchronized (receivecards){
                            Integer x=receivecards.size();
                            Log.d("tag case 5 size",x.toString());
                            if(!first)
                                toolfunc();
                            if(current_player==2){
                                for(int i=0;i<CView2.getChildCount();i++){
                                    CardView childview =(CardView) CView2.getChildAt(i);
                                    if(receivecards.contains(childview.cardvalue)){
                                        CView2.removeView(childview);
                                        centerview.addView(childview);
                                        childview.showFront();
                                        childview.reMeasure(145,185);
                                        i--;
                                    } } }
                            else if(current_player==3){
                                for(int i=0;i<CView1.getChildCount();i++){
                                    CardView childview =(CardView) CView1.getChildAt(i);
                                    if(receivecards.contains(childview.cardvalue)){
                                        CView1.removeView(childview);
                                        centerview.addView(childview);
                                        childview.showFront();
                                        childview.reMeasure(145,185);
                                        i--;
                                    } } } }
                        break;
                    case 6:  //响应地主按钮
                        addListener(buttomview);
                        case6_func();
                        first=true;
                        break1=true;
                        button_f.setText("不出");
                        button_f.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mhandler.sendMessage(mhandler.obtainMessage(10));
                            }
                        });
                        //button_f.setVisibility(View.INVISIBLE);
                        button_n.setVisibility(View.INVISIBLE);//点击之后隐藏两个按钮
                        /*
                        button_n.setText("整理");
                        button_n.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mhandler.sendMessage(mhandler.obtainMessage(11));
                            }
                        });*/
                        break;
                    case 7:
                        addListener(buttomview);
                        case7_func();
                        first=false;
                        //break1=true;
                        //button_f.setVisibility(View.INVISIBLE);
                        button_f.setText("不出");
                        button_f.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mhandler.sendMessage(mhandler.obtainMessage(10));
                            }
                        });
                        button_n.setVisibility(View.INVISIBLE);
                        break;
                    case 8:
                        for(int i=34;i!=51;i++){
                            pukes54.get(i).showBack();
                            CView1.addView(pukes54.get(i),154,120);
                        }
                        break;
                    case 9:
                        for(int i=17;i!=34;i++){
                            pukes54.get(i).showBack();
                            CView2.addView(pukes54.get(i),154,120);
                        }
                        break;
                    case 10:
                        Log.d("button","buuchu");
                        centerview.removeAllViews();
                        synchronized (playingcards){
                            playingcards.add(105);
                            playingcards.add(102);
                        }
                        play=true;
                }
                //refreshUI(buttomview);
            }
        };
        this.setBackground(getResources().getDrawable(R.drawable.gameback3));
        pukesthread=new myThread(context);
        pukesthread.start();
        //this.setBackgroundColor(Color.GREEN);
        thisaddview(context);

    }//构造函数结束
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void case6_func(){
        DeskView.this.removeView(topview1);
        DeskView.this.removeView(topview2);
        DeskView.this.removeView(topview3);
        buttomview.addView(topview1);
        buttomview.addView(topview2);
        buttomview.addView(topview3);
        topview3.showFront();
        topview2.showFront();
        topview1.showFront();
        Sort.sort(buttomview);
    }
    private void case7_func(){
        topview3.showFront();
        topview2.showFront();
        topview1.showFront();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void case3_func(){
        new seversocketThread().start();
        pukes54=pukesthread.returnpukes54();
        for(int i=0;i!=17;i++){
            pukes54.get(i).showFront();
            buttomview.addView(pukes54.get(i),145,185);
        }
        Sort.sort(buttomview);//排序
        topview1=pukes54.get(51);
        topview2=pukes54.get(52);
        topview3=pukes54.get(53);
        button1.setVisibility(View.INVISIBLE);// 设置该按钮不可见
        centerview.setVisibility(View.VISIBLE);  //设置中心view可见
        DeskView.this.addView(button2,200,120);
        DeskView.this.addView(button_f,200,120);
        DeskView.this.addView(button_n,200,120);
        DeskView.this.addView(topview1,145,185);
        DeskView.this.addView(topview2,145,185);
        DeskView.this.addView(topview3,145,185);
        topview3.showBack();
        topview2.showBack();
        topview1.showBack();
    }
    public void addListener(Gameview2 pukeview){
        int c_count=pukeview.getChildCount();
        for(int i=0;i!=c_count;i++){
            CardView childview= (CardView) pukeview.getChildAt(i);
            childview.setOnClickListener(v -> {
                if(!childview.chooseflag){
                    childview.chooseflag=true;
                    mhandler.sendMessage(mhandler.obtainMessage(1));
                }
                else{
                    mhandler.sendMessage(mhandler.obtainMessage(2,childview.cardvalue,0));
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

    private void refreshUI(Gameview2 view){
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
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }
    protected void toolfunc(){//case5调用
        if(receivecards.contains(topview1.cardvalue)){
            DeskView.this.removeView(topview1);
            centerview.addView(topview1);
        }
        if(receivecards.contains(topview2.cardvalue)){
            DeskView.this.removeView(topview2);
            centerview.addView(topview2);
        }
        if(receivecards.contains(topview3.cardvalue)){
            DeskView.this.removeView(topview3);
            centerview.addView(topview3);
        }
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
                case 11:
                    cl=1000;
                    ct=660;
                    break;
            }
            cr=cl+cwidth;
            cb=ct+cheight;
            childview.layout(cl,ct,cr,cb);
            //Log.d("layout","layout已执行");
        }
    }

    private void thisaddview(Context context){
        CView1=new CascadeCards(context);
        CView2=new CascadeCards(context);
        button1=new Button(context);
        button1.setText("准备");
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mhandler.sendMessage(mhandler.obtainMessage(3));
            }
        });
        button2=new Button(context);
        button2.setText("发牌");
        button2.setOnClickListener(v -> mhandler.sendMessage(mhandler.obtainMessage(4)));
        button_f=new Button(context);
        button_f.setText("地主");
        button_f.setOnClickListener(v -> mhandler.sendMessage(mhandler.obtainMessage(6)));
        button_n=new Button(context);
        button_n.setText("不抢");
        button_n.setOnClickListener(v -> mhandler.sendMessage(mhandler.obtainMessage(7)));
        centerview=new CenterView(context);
        //topview=new CenterView(context);
        buttomview=new Gameview2(context);
        this.addView(CView1,154,650);
        this.addView(CView2,154,650);
        //Log.d("add view1,view2","已添加");
        this.addView(buttomview,1550,210);
        this.addView(button1,200,120);
        this.addView(centerview,1305,370);
        centerview.setVisibility(View.INVISIBLE);//设置不可见
        //Log.d("pukesview","已添加");
    }
/**状态码定义
 *  100 服务器已准备
 *  101 客户端已准备
 *  102 服务器标识
 *  103 客户端1
 *  104 客户端2
 *  105 “不出”标记
 *  106 抢地主标记
 */
    class seversocketThread extends Thread {
        @Override
        public void run(){
            int index=2;
            ServerSocket seversocket= null;
            try {
                seversocket = new ServerSocket(3306);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true){//处理多客户端应用,
             try {
                 Socket clientsocket=seversocket.accept();  //接受一个socket连接请求之后，新建线程单独处理该socket
                 new acceptThread(clientsocket,index).start();//accept()方法会一直阻塞直到有新的socket连接请求
                 index++;
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
        }
    }

    class acceptThread extends Thread{
        Socket clientsocket;
        Integer index;//对应的玩家号码
        boolean flag;
        acceptThread(Socket socket,int i){
            clientsocket=socket;
            index=i;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run(){
            playingcards=new ArrayList<>();
            receivecards=new ArrayList<>();
            try {
                InputStream input=clientsocket.getInputStream();
                OutputStream output=clientsocket.getOutputStream();
                ArrayList<Integer> temp_index=pukesthread.returnindex();
                output.write(100);
                if(input.read()==101){ //检测状态码客户端是否已经准备，就绪后向客户端发送扑克信息
                    output.write(index);//先将玩家号码发送给客户端
                    if(index == 2) {
                        mhandler.sendMessage(mhandler.obtainMessage(9));
                    }
                    else if(index == 3){
                        mhandler.sendMessage(mhandler.obtainMessage(8));
                    }
                    for(int i=0;i!=temp_index.size();i++){
                        output.write(temp_index.get(i));
                    }
                    boolean tag1=true;
                    tag3=0;
                    while(tag1) { //循环1
                        if(break1){
                            if (first) {//检查服务器端是不是地主玩家
                                current_player = 1;
                                //receive = false;//自己是地主不用接受数据
                                output.write(106);
                                //input.read();//跳过103数据
                            } else {
                                int temp = input.read();
                                if (temp != 103) {//103为非地主玩家标识
                                    //first_player = temp;
                                    current_player=temp;
                                }
                                output.write(106);
                            }
                            tag1=false;
                            receive = true;
                        }
                        if(input.available()!=0){
                            int temp=input.read();
                            if(temp==106){
                                mhandler.sendMessage(mhandler.obtainMessage(7));
                                tag1=false;
                            }
                            else if(temp==103){}
                            else if(temp==2||temp==3){
                                Log.d("222","2222");
                                current_player=temp;
                                mhandler.sendMessage(mhandler.obtainMessage(7));
                               // output.write(106);
                                tag7=true;
                                receive=true;
                            }
                        }
                        if(tag7){
                            output.write(106);
                            tag1=false;
                        }
                        sleep(500);//睡眠适度减少程序开销
                    }
                    output.flush();//刷新缓冲
                    while(input.available()!=0){
                        input.read();
                    }
                    Log.d("服务端端stream","已调过所有可读字节");

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
                    boolean tag4=false;
                    while(true){//游戏循环
                        if(tag6==2){
                            synchronized (receivecards){
                                receivecards.clear();
                                tag6=0;
                                Log.d("tag","清除执行");
                            }
                        }
                        if(play){
                            synchronized (playingcards){
                                int size=playingcards.size();
                                output.write(size);  //先写入可读字节数,便于接收方处理
                                for(int i=0;i!=size;i++){
                                    output.write(playingcards.get(i));
                                }
                                tag3+=1;
                                tag4=true;
                            }
                            }
                        if(receive && current_player==index){
                            int bytesize=input.read();   //read()操作会引起程序阻塞
                            for(int i=0;i!=bytesize;i++)
                                receivecards.add(input.read());
                            tag2=true;
                        }
                        if(tag2){//数据接受完毕后才可以写入流
                            synchronized (receivecards){
                                Integer bytesize=receivecards.size();
                                output.write(bytesize);     //先写入可读字节数
                                Log.d("tag 写入时size",bytesize.toString());
                                for(int i=0;i!=bytesize;i++)// 服务器收到数据后立即发送给客户端用于更新UI
                                    output.write(receivecards.get(i));
                                tag6++;
                                if(!send){
                                    Integer x=receivecards.size();
                                    Log.d("tag 发送时size",x.toString());
                                    mhandler.sendMessage(mhandler.obtainMessage(5));//因为有两个线程对该步进行操作，但执行一次就够了，不然引起topview的重复移除
                                    send=true;
                                }

                                Log.d("size",bytesize.toString());
                            }
                                tag3 += 1;
                                tag4=true;
                        }
                        while(tag3==1 && tag4){
                            //阻塞
                            //如果有一个线程先执行玩了发数据的操作，且时间差距过大，则该线程就在此阻塞等待另一个执行完write()操作
                            this.sleep(100);//线程睡眠0.5秒
                        }

                        synchronized (tag3){
                            if(tag3==2){//tag3等于2说明两个线程都已经发送了数据
                                tag2=false;//????
                                play=false;
                                tag4=false;
                                send=false;
                                //receivecards.clear();
                                synchronized (playingcards){
                                    playingcards.clear();
                                }
                                tag3=0;
                                current_player=current_player%3+1;//余法计算下一位出牌的玩家，1-3中循环
                            }
                        }
                        if(gameover)
                            break;//游戏结束，跳出循环
                       // Log.d("while",index.toString()+" 正在循环");
                    }
                    clientsocket.shutdownInput();
                    clientsocket.shutdownOutput();
                    input.close();
                    output.close();
                }
                } catch (IOException | InterruptedException ex) {
                builder.setMessage("检测到玩家异常退出，游戏已中断");
                final AlertDialog dialog=builder.create();
                dialog.show();
            }

        }
    }


}

class myThread extends Thread{
    boolean flag;  //线程完成标志;
    Context context;
    ArrayList<Integer> index;
    private ArrayList<Integer> index1;
    private ArrayList<Integer> index2;
    private ArrayList<Integer> index3;
    private ArrayList<Integer> indexthree;
    private ArrayList<CardView> pukes54;
    ArrayList<CardView> puke1;
    ArrayList<CardView> threepukes;//三张底牌
    myThread(Context cont){
        context=cont;
        indexthree=new ArrayList<>();
        index=new ArrayList<>();
        index1=new ArrayList<>();
        index2=new ArrayList<>();//玩家2
        index3=new ArrayList<>();//玩家3
        puke1=new ArrayList<>();
        pukes54=new ArrayList<>();
        threepukes=new ArrayList<>();
        flag=false;
    }
    @Override
    public void run(){
        pukes54=sortpukes(initBitmap(this.context));
        dealcards(pukes54);
        indexinit();
        flag=true;
    }
    public ArrayList<Integer> returnindex(){ return index;}
    public ArrayList<CardView> returnpuke1(){
        return puke1;
    }
    public ArrayList<Integer> returnindex2(){ return index2;}
    public ArrayList<Integer> returnindex3(){ return index3;}
    public ArrayList<Integer> returnthree(){ return indexthree;}
    public ArrayList<CardView> returnpukes54(){ return pukes54;}


   /* public ArrayList<Bitmap> returnBitmaps(Context context){
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
    private  ArrayList<CardView> initBitmap(Context context){//初始化图片
        ArrayList<CardView> cards=returnCards(context);
        ArrayList<Integer> bitmaps=returnDrawable(context);
        for(int i=0;i!=bitmaps.size();i++){
            cards.get(i).setFrontBitmap(bitmaps.get(i));//设置牌的正反面图片
            //cards.get(i).setBackBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.pukebackbround2));
            cards.get(i).setBackBitmap(R.drawable.pukebackbround2);
            //cards.get(i).showFront();
        }
        return cards;
    }
    public ArrayList<CardView> sortpukes(ArrayList<CardView> pukes){//洗牌
        ArrayList<CardView> newpukes=new ArrayList<>();
        ArrayList<Integer> index=new ArrayList<>();
        Random random=new Random();
        while(index.size()!=54){//生成随机不重复的54个下标
            int i=random.nextInt(54);
            if(!index.contains(i)){
                index.add(i);
            } }
        this.index=index;
        for(int i=0;i!=54;i++){
            newpukes.add(pukes.get(index.get(i)));
        }
       /* for(int i=17;i!=newpukes.size();i++){
            newpukes.get(i).showBack();
        }*/
        return newpukes;
    }
    public void dealcards(ArrayList<CardView> pukes){//发牌
        int i=0;
           for(int j=0;j!=17;j++){
               puke1.add(pukes.get(i));
               i++;
           }
       }
       private void indexinit(){
        for(int i=0;i!=17;i++){
            index1.add(index.get(i));
        }
        for(int i=17;i!=34;i++){
            index2.add(index.get(i));
        }
        for(int i=34;i!=51;i++){
            index3.add(index.get(i));
        }
        for(int i=51;i!=54;i++){
            indexthree.add(index.get(i));
        }
       }
}
