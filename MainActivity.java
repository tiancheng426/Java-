package com.example.myapplication3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button serever_button;
    private Button client_button;
    private Button alert_button;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serever_button=(Button)findViewById(R.id.button);
        client_button=(Button)findViewById(R.id.button2);
        alert_button=(Button)findViewById(R.id.alertbutton);
        initString();
        serever_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ChooseActivity.class);  //单击按钮开启chooseactivity
                startActivity(intent);
            }
        });
        client_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,inputip.class);  //单击按钮开启input界面
                startActivity(intent);
            }
        });
        alert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(message);
                builder.setPositiveButton("确定",null);
                final AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

    }
    private void initString(){
        message=
                "(1)根据《中华人民共和国刑法》、《中华人民共和国治安管理处罚法》和《中华人民共和国网络安全法》:严禁开展任何形式的聚众赌博和网络赌博，违者追究刑事责任。" +
                        "因此该软件不接入任何网络支付接口，不调用手机任何支付权限，不访问手机其他应用，仅向手机申请网络使用权限、个人热点和WIFI权限，且游戏界面不设任何倍率，押注显示" +'\n'+
                        "(2)游戏规则：斗地主需满三人才能开始，其中一人以服务端开始游戏，并打开手机个人热点，其余两人以客户端方式启动，打开手机WIFI输入服务器玩家的IP地址连入服务端玩家的局域网下即可开始游戏" + '\n'+
                        "(3)游戏代码简陋，UI设计离谱，如果出现BUG在所难免。按照著名IT神话人物罗永浩先生的话来说，当程序出现BUG或死机时，请大家不要慌张，这个时候应该高喊“理解万岁”"+'\n'+
                        "(4)远离赌博，此软件仅供娱乐使用，最终解释权归开发者所有"+'\n'+
                        "(5)产品信息:Built by the Android Studio(Powered by the Intellij Platform),External reference to Google Glide framework"+'\n'+'\n'+
                        "版本:开放测试版1.2"
        ;
    }

}
