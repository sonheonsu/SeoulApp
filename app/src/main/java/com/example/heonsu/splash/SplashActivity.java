package com.example.heonsu.splash;

/**
 * Created by Heonsu on 2015-10-25.
 */


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.heonsu.soeulbob.R;

public class SplashActivity extends Activity {

    int splashSceneNumber;

    RelativeLayout splashLayout;

    ImageView imageView;

    Handler mHandler;

    boolean isClick;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // TODO Auto-generated method stub

        // xml 소스 참조
        splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);



        // 처음화면 0
        splashSceneNumber = 0;

        // 클릭 이벤트가 있었는지 확인
        isClick = true;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (splashSceneNumber) {
                    case 0:
                        // 두번째 화면
                        splashSceneNumber = 1;

                        mHandler.sendEmptyMessage(splashSceneNumber);
                        break;

                    case 1:
                        splashSceneNumber = 2;
                        mHandler.sendEmptyMessageDelayed(splashSceneNumber, 1000);
                        break;

                    case 2:
                        // 엑티비티 종료
                        SplashActivity.this.finish();
                        break;

                    case 3:
                        // 딜레이이벤트 클리기 없을경우 바로 0 이벤트로 보낸다..
                        if (isClick && splashSceneNumber == 0) {
                            splashSceneNumber = 0;
                            mHandler.sendEmptyMessage(splashSceneNumber);
                        }
                        break;
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(3, 2000);
    }

    public void hn_splashOnclick(View v) {

                splashSceneNumber = 2;
                mHandler.sendEmptyMessage(splashSceneNumber);

    }
}

