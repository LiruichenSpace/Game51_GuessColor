package org.li.guesscolor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.li.guesscolor.GameSys.Game;

public class MainActivity extends AppCompatActivity {
    private Button nosame;
    private Button same;
    private Button help;
    long prevpress=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar()!=null)getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        nosame=findViewById(R.id.main_nosame);
        same=findViewById(R.id.main_same);
        help=findViewById(R.id.help);
        nosame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameActivity.game=new Game(false);
                Intent intent=new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });
        same.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameActivity.game=new Game(true);
                Intent intent=new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long curr=System.currentTimeMillis();
        if(curr-prevpress<1000){
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(),"再次点击退出",Toast.LENGTH_SHORT).show();
            prevpress=curr;
        }

    }
}
