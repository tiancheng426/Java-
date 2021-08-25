package com.example.myapplication3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class gameActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 隐藏状态栏，
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //锁定横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        DeskView deskView=new DeskView(this);
        setContentView(deskView);
        //隐藏标题栏
        getSupportActionBar().hide();

        /*
        new Thread(){
            @Override
            public void run() {
                Log.d("线程","已启动");
                OutputStream output;
                String severContent="hello hehe";
                try{
                   //指明服务器端的端口号
                    ServerSocket sereverSocket=new ServerSocket(12356);
                    while(true){
                        Log.d("循环","已启动，正在接收消息");
                        Message msg=new Message();
                        msg.what=1;
                        try{
                            Socket socket=sereverSocket.accept();
                            output=socket.getOutputStream();
                            output.write(severContent.getBytes("utf-8"));
                            output.flush();
                            socket.shutdownOutput();
                            BufferedReader bff=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String result="";
                            String buffer="";
                            while((buffer=bff.readLine())!=null){
                                result=result+buffer;
                            }
                            msg.obj=result;
                            //handler.sendMessage(msg);
                            Log.d("消息","已收到");
                            socket.shutdownInput();
                            bff.close();;
                            output.close();
                            socket.close();
                            sereverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }
}