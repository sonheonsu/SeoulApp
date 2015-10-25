package com.example.heonsu.login;

/**
 * Created by Heonsu on 2015-10-25.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heonsu.soeulbob.MainActivity;
import com.example.heonsu.soeulbob.R;
import com.example.heonsu.splash.SplashActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private static final  String TAG = "mLog";      //로그 태그
    private static int count = 0;                   //태그 카운터

    private final String LOGIN_PAGE_URL = "http://pander7night.cafe24.com/seoul/login/login.jsp";   //로그인 URL
    private final String JOIN_PAGE_URL = "http://pander7night.cafe24.com/seoul/login/join.jsp";     //회원가입 URL

    Button btnJoin;     //회원가입 버튼
    Button btnLogin;    //로그인 버튼

    //회원가입 팝업 Component
    EditText email; //이메일
    EditText name;  //이름
    EditText pw1;   //비밀번호
    EditText pw2;   //비밀번호확인

    EditText et, pass;

    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    CheckBox CheckID, CheckPW;
    Boolean idcheck, pwcheck;
    String id,pw;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SplashActivity.class));
        setContentView(R.layout.login);

        SharedPreferences LoginSetting = getSharedPreferences("LoginSetting", Activity.MODE_PRIVATE);

        btnJoin = (Button)findViewById(R.id.btnJoin);   //회원가입 버튼

        btnLogin = (Button) findViewById(R.id.btnLogin);
        et = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        final CheckBox CheckID = (CheckBox)findViewById(R.id.saveID);
        final CheckBox CheckPW = (CheckBox)findViewById(R.id.savePW);
        et.setText("");
        pass.setText("");

        CheckID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (CheckID.isChecked() == false) {
                    CheckPW.setChecked(false);
                }
            }
        });



        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();

            }
        });

        //회원가입 팝업창 띄우기
        btnJoin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getJoinPopup();//회원가입창
            }
        });


    }

    //회원가입 팝업 메소드
    private void getJoinPopup(){

        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.join, (ViewGroup) findViewById(R.id.layout_root));

        pw1 = (EditText)layout.findViewById(R.id.password1);
        pw2 = (EditText)layout.findViewById(R.id.password2);
        name = (EditText)layout.findViewById(R.id.joinName);
        email = (EditText)layout.findViewById(R.id.joinEmail);


        AlertDialog.Builder aDialog = new AlertDialog.Builder(LoginActivity.this);
        aDialog.setTitle("회원가입");
        aDialog.setView(layout);


        aDialog.setPositiveButton("가입하기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //패스워드 중복체크
                String pwStr1 = "", pwStr2 = "", emailStr = "", nameStr = "";

                try {
                    emailStr = email.getText().toString();
                    nameStr = name.getText().toString();
                    pwStr1 = pw1.getText().toString();
                    pwStr2 = pw2.getText().toString();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                //빈칸이 있을때
                if (emailStr.equals("") || nameStr.equals("")
                        || pwStr1.equals("") || pwStr2.equals("")) {
                    Toast.makeText(getApplicationContext(), "모든 정보를 입력하시오.", Toast.LENGTH_LONG).show();
                } else if (!pwStr1.equals(pwStr2)) {
                    //비밀번호가 틀릴 경우
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                } else {
                    //중복메일 체크은 웹에서
                    Log.d(TAG,(count++)+"");
                    new Thread(new Runnable() {
                        public void run() {
                            join();
                        }
                    }).start();
                }
            }
        });
        aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog ad = aDialog.create();
        ad.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Toast.makeText(getApplicationContext(),"onResume",Toast.LENGTH_SHORT).show();
        SharedPreferences LoginSetting = getSharedPreferences("LoginSetting", Activity.MODE_PRIVATE);
        EditText SaveID = (EditText)findViewById(R.id.username);
        EditText SavePW = (EditText)findViewById(R.id.password);
        CheckBox CheckID = (CheckBox)findViewById(R.id.saveID);
        CheckBox CheckPW = (CheckBox)findViewById(R.id.savePW);

        id = LoginSetting.getString("SaveID", "");
        pw = LoginSetting.getString("SavePW", "");

        idcheck = LoginSetting.getBoolean("CheckID", false);
        pwcheck = LoginSetting.getBoolean("CheckPW", false);

        CheckID.setChecked(idcheck);
        CheckPW.setChecked(pwcheck);


        //Toast.makeText(getApplicationContext(),"id: "+String.valueOf(LoginSetting.getBoolean("CheckID", false))+" / pw: "+String.valueOf(LoginSetting.getBoolean("CheckPW", false)),Toast.LENGTH_SHORT).show();
        if(idcheck) {
            et.setText(id);
        }else{
            et.setText("");
        }
        if(pwcheck){
            pass.setText(pw);
        }else{
            pass.setText("");
        }

    }

    //로그인
    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(LOGIN_PAGE_URL); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("user_email", et.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("user_pw", pass.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response = httpclient.execute(httppost);
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });

            JSONArray jArray = new JSONArray(response);
            String result = "";
            MainActivity.loginedUser = new User();  //사용자 객체 생성
            MainActivity.loginedUser.setEmail(et.getText().toString().trim());    //이메일 저장

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject json = jArray.getJSONObject(i);
                    result = json.getString("result");
                    MainActivity.loginedUser.setU_id(json.getInt("u_id"));
                    MainActivity.loginedUser.setName(json.getString("name"));     //이름 저장
                } catch (Exception e) {

                }
            }

            if (result.equals("ok_login")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainActivity.loginedUser = null;//사용자 객체 초기화
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            dialog.dismiss();
            Log.d(TAG, e.getMessage());
        }
    }


    //회원가입
    void join(){
        Log.d(TAG,(count++)+"");
        try {
            Log.d(TAG,(count++)+"");
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(JOIN_PAGE_URL); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("user_email", email.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("user_pw", pw1.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("user_name", name.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            Log.d(TAG, (count++) + "전" + name.getText().toString().trim());
            Log.d(TAG, (count++) + "후");
            JSONArray jArray = new JSONArray(response);
            String result = "";

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject json = jArray.getJSONObject(i);
                    result = json.getString("result");
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            Log.d(TAG, "ok_joi전");
            Log.d(TAG, "result:"+result);
            if (result.equals("ok_join")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LoginActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }



        } catch (Exception e) {
            dialog.dismiss();
            Log.d(TAG, e.getMessage());
        }
    }


}