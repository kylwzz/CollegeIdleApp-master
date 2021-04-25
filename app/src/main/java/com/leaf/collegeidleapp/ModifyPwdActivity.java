package com.leaf.collegeidleapp;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.LinkedList;

// 修改密码活动类

public class ModifyPwdActivity extends AppCompatActivity {

    TextView tvStuNumber;
    EditText etOriginPwd,etNewPwd,etConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        //取消事件
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuNumber = findViewById(R.id.tv_stu_number);
        tvStuNumber.setText(this.getIntent().getStringExtra("stu_number"));
        etOriginPwd = findViewById(R.id.et_original_pwd);
        etNewPwd = findViewById(R.id.et_new_pwd);
        etConfirmPwd = findViewById(R.id.et_confirm_new_pwd);
        Button btnModify = findViewById(R.id.btn_modify_pwd);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //首先保证输入合法
                if(CheckInput()) {

                    final String stuNumber = tvStuNumber.getText().toString();
                    final String pwd = etNewPwd.getText().toString();
                    final String pwdOriginPwd = etOriginPwd.getText().toString();
                    new Thread() {
                        @Override
                        public void run() {
                            System.out.println(stuNumber);
                            System.out.println(pwdOriginPwd);
                            String num = init(stuNumber,pwdOriginPwd);

                            System.out.println("num"+num);
                            if(num!=null){
                                String num1 =init1(stuNumber,pwd);
                                if (num1!=null){
                                    Looper.prepare();
                                    Toast.makeText (getApplicationContext(),"修改成功", Toast.LENGTH_LONG ).show();
                                    Looper.loop();
                                }else{
                                    Looper.prepare();
                                    Toast.makeText (getApplicationContext(),"修改失败", Toast.LENGTH_LONG ).show();
                                    Looper.loop();
                                }

                            }else{
                                Looper.prepare();
                                Toast.makeText (getApplicationContext(),"原始密码输入错误，无法修改密码", Toast.LENGTH_LONG ).show();
                                Looper.loop();
                            }
                        }
                    }.start();

                }
            }
        });
    }

    //判断输入的合法性
    public boolean CheckInput() {
        String OriginalPwd = etOriginPwd.getText().toString();
        String NewPwd = etNewPwd.getText().toString();
        String NewConfirmPwd = etConfirmPwd.getText().toString();
        if(OriginalPwd.trim().equals("")) {
            Toast.makeText(this,"原始密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(NewPwd.trim().equals("")) {
            Toast.makeText(this,"新密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(NewConfirmPwd.trim().equals("")) {
            Toast.makeText(this,"确认新密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NewPwd.trim().equals(NewConfirmPwd.trim())) {
            Toast.makeText(this,"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //查询密码
    public String init(String stuNumber, String pwdOriginPwd){
        final String path ="http://192.168.31.140:8080/registerexist";

        String id = null;
        try {
            /* String path="http://192.168.1.13:8080/chapter17/loginByName?username="+URLEncoder.encode("wwww","UTF-8")+"&password="+URLEncoder.encode("123456","UTF-8");*/
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", stuNumber);
            jsonObject.put("password", pwdOriginPwd);


            String content = String.valueOf(jsonObject);
            connection.setRequestProperty("ser-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            /* os.write(data.getBytes());*/
            os.close();

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == 200) {

                InputStream is = connection.getInputStream();
                //调用工具类，将流转换成String类型
                String json = NetUtils.readString(is);

                Gson gson = new Gson();
                User user = gson.fromJson(json, User.class);


                id = user.getUsername();
                System.out.println(id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    //修改密码
    public String init1(String stuNumber,String pwd){
        final String path ="http://192.168.31.140:8080/updateUser";

        String usertest =("");
        try {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", stuNumber);
            jsonObject.put("password", pwd);

            String content = String.valueOf(jsonObject);
            connection.setRequestProperty("ser-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");


            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {

                InputStream is = connection.getInputStream();

                String json = NetUtils.readString(is);

                Gson gson = new Gson();
                User user = gson.fromJson(json, User.class);



                usertest = user.getUsername();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usertest;
    }
}
