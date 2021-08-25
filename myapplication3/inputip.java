package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class inputip extends AppCompatActivity {
    private Button enter_button;
    private Button back_button;
    private EditText ipinput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputip);
        enter_button=(Button)findViewById(R.id.enter);
        back_button=(Button)findViewById(R.id.back);
        ipinput=(EditText)findViewById(R.id.ipinput);

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipadress=ipinput.getText().toString();
                Intent intent=new Intent(inputip.this,gameActivity2.class);  //单击按钮开启gameactivity
                intent.putExtra("ip",ipadress);
                startActivity(intent);
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}