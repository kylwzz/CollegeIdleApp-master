package com.leaf.collegeidleapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leaf.collegeidleapp.bean.User;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.UserDbHelper;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// 注册界面Activity类

public class RegisterActivity extends AppCompatActivity {

    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnCancel = findViewById(R.id.btn_cancel);
        //返回到登录界面
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuNumber = findViewById(R.id.et_username);
        tvStuPwd = findViewById(R.id.et_password);
        tvStuConfirmPwd = findViewById(R.id.et_confirm_password);
        //注册点击事件
        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //首先确保不为空
                if(CheckInput()) {

                    new Thread() {
                        @Override
                        public void run() {
                            int num = ifexist();
                            if(num!=0){   //判断id是否大于1，>1就是查询到有数据 ，可以进入主界面
                                Looper.prepare();
                                Toast.makeText (getApplicationContext(),"用户已被注册", Toast.LENGTH_LONG ).show();
                                Looper.loop();
                            }else {
                                init();
                                Looper.prepare();
                                Toast.makeText (getApplicationContext(),"用户注册成功", Toast.LENGTH_LONG ).show();
                                Looper.loop();
                            }
                        }
                    }.start();

                }
            }
        });
    }

    //判断输入是否符合规范
    public boolean CheckInput() {
        String username = tvStuNumber.getText().toString();
        String password = tvStuPwd.getText().toString();
        String confirm_password = tvStuConfirmPwd.getText().toString();
        if(username.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"确认密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public int ifexist(){
        //家里的ip
        final String path = "http://192.168.31.140:8080/registerexist";
        //广州的ip
//        final String path = "http://192.168.0.102:8080/registerexist";
        int id = 0;
        try{

            String username = tvStuNumber.getText().toString().trim();
            String password = tvStuPwd.getText().toString().trim();

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
            connection.setRequestProperty("Content-Type","application/json");

            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();

            int code = connection.getResponseCode();
            System.out.println(code);
            if (code == 200){
//                        Message msg = Message.obtain();
//                        msg.what = 1;
//                        msg.obj = "恭喜你注册成功!";
//                        handler.sendMessage(msg);

                InputStream is = connection.getInputStream();
                //调用工具类，将流转换成String类型
                String json = NetUtils.readString(is);

                Gson gson = new Gson();
                User user = gson.fromJson(json, User.class);


                id = Integer.parseInt(user.getUsername());

//                Message msg = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putInt("id", id);
//                msg.setData(bundle);
//                handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
        return id;
            }

    public void init(){
                //家里的ip
                final String path = "http://192.168.31.140:8080/register";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/register";
                int id = 0;
                try{

                    String username = tvStuNumber.getText().toString().trim();
                    String password = tvStuPwd.getText().toString().trim();

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
                    connection.setRequestProperty("Content-Type","application/json");

                    OutputStream os = connection.getOutputStream();
                    os.write(content.getBytes());
                    os.close();

                    int code = connection.getResponseCode();
                    System.out.println(code);
                    if (code == 200){

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }

