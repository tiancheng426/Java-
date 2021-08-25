package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ChooseActivity extends AppCompatActivity {
    public static TextView IP,client_content;
    private Button nextbutton;
    private String serverip="";
    private String intetype="";//网络类型
    private String port="3306"; //端口号

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                client_content.append("client"+msg.obj.toString()+"\n");
                //client_content.setText(port);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);  //设置主activity

        IP = (TextView) findViewById(R.id.ip);
        client_content = (TextView) findViewById(R.id.client_content);
        nextbutton=(Button)findViewById(R.id.button3);
        serverip = Getaddress.getIPAddress(this).first;

        if(serverip!=null){   //IP不为空，开始进行下一步, 添加按钮监听
            nextbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ChooseActivity.this,gameActivity.class);  //单击按钮开启gameactivity
                    startActivity(intent);
                }
            });
            nextbutton.setText("获取成功，单击进行下一步");
        }else
            nextbutton.setText("获取失败!!!");

        intetype=Getaddress.getIPAddress(this).second;
        IP.setText(serverip+"   type: "+intetype);
        client_content.setText(port);
        //Log.d("dd","ddddddddddd");

    }


    /*
   服务器端接收数据
   需要注意以下一点：
   服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上；
   */



}