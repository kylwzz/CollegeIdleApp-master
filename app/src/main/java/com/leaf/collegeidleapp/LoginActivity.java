package com.leaf.collegeidleapp;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leaf.collegeidleapp.bean.Student;
import com.leaf.collegeidleapp.bean.User;
import com.leaf.collegeidleapp.tools.NetUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

// 登录界面Activity类

public class LoginActivity extends AppCompatActivity {

    EditText EtStuNumber,EtStuPwd;
    private String username;

    //LinkedList<User> users = new LinkedList<>();
//    Student student = new Student();
//    User user = new User();
//    String id = null;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvRegister = findViewById(R.id.tv_register);
        //跳转到注册界面
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        EtStuNumber = findViewById(R.id.et_username);
        EtStuPwd = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CheckInput()) {
                    new Thread() {
                        @Override
                        public void run() {
                            String num= init();
                            if(num != null){   //判断id是否大于1，>1就是查询到有数据 ，可以进入主界面
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                Bundle bundle = new Bundle();
                                username = EtStuNumber.getText().toString();
                                bundle.putString("username",username);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }else {
                                Looper.prepare();
                                Toast.makeText (getApplicationContext(),"用户或密码错误", Toast.LENGTH_LONG ).show();
                                Looper.loop();
                            }
                        }
                    }.start();
                }
            }
        });
    }

    //检查输入是否符合要求
    public boolean CheckInput() {
        String StuNumber = EtStuNumber.getText().toString();
        String StuPwd = EtStuPwd.getText().toString();
        if(StuNumber.trim().equals("")) {
            Toast.makeText(LoginActivity.this,"学号不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuPwd.trim().equals("")) {
            Toast.makeText(LoginActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String init() {
//        家里的ip
        final String path = "http://192.168.31.140:8080/login";
        //广州的ip
//        final String path = "http://192.168.0.102:8080/login";
        String id;
        id = null;
        try {

            String username = EtStuNumber.getText().toString().trim();
            String password = EtStuPwd.getText().toString().trim();

            URL url = new URL(path);
            //打开URL连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);

            //json方式传数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            //json串转String类型
            String content = String.valueOf(jsonObject);
            connection.setRequestProperty("ser-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();

            int code = connection.getResponseCode();
            System.out.println(code);
            if (code == 200) {
                //用输入流读取返回的数据
                InputStream is = connection.getInputStream();
                //调用工具类，将流转换成String类型
                String json = NetUtils.readString(is);

                Gson gson = new Gson();
                id = gson.fromJson(json, String.class);

                return id;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}

