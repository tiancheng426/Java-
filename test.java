package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class test extends AppCompatActivity {
    //TextView result;
    Button send;
    EditText input;
    EditText ipaddress;
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
               // result.append("server:" + msg.obj.toString() + "\n");
                Log.d("111","0000");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        //result=(TextView)findViewById(R.id.textView2);
        send = (Button) findViewById(R.id.send);
        input = (EditText) findViewById(R.id.message);//port端口
        ipaddress=(EditText)findViewById(R.id.ipaddress);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContent = input.getText().toString();
                String temptress=ipaddress.getText().toString();
               // result.append("client:" + inputContent + "\n");
                //启动线程 向服务器发送和接收信息
                new MyThread(inputContent,temptress).start();
            }
        });

    }
    class MyThread extends Thread {

        public String content;
        public String impress1;

        public MyThread(String str,String inaptness1) {
            content = str;
            this.impress1 =inaptness1;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 1;
            try {
                //连接服务器 并设置连接超时为5秒
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(impress1,12356), 1000);

                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                //获取输出输出流
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //向服务器发送信息
                ou.write(content.getBytes("utf-8"));
                ou.flush();
                socket.shutdownOutput();
                Log.d("fffff","ff");

                //读取发来服务器信息
                String result = "";
                String buffer = "";
                while ((buffer = bff.readLine()) != null) {
                    result = result + buffer;
                    Log.d("not null","while");
                }
                msg.obj = result;
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                msg.obj =  "服务器连接失败！请检查网络是否打开";
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
