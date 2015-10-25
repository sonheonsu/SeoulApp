package com.example.heonsu.soeulbob;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.heonsu.login.User;
import com.example.heonsu.splash.SplashActivity;

public class MainActivity extends Activity {

    public static User loginedUser;//스테틱으로 유저정보 보관


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView t = (TextView)findViewById(R.id.textview1);
        t.setText(loginedUser.getU_id()+"\t"+loginedUser.getName()+"\t"+loginedUser.getEmail());
    }
}
