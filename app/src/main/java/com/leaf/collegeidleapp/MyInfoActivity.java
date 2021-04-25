package com.leaf.collegeidleapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.leaf.collegeidleapp.bean.Student;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.StudentDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 我的个人信息活动类

public class MyInfoActivity extends AppCompatActivity {

    TextView tvStuName,tvStuMajor,tvStuPhone,tvStuQq,tvStuAddress;
      Student students = null;
      Student freshstudents = new Student();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 2){
                System.out.println(msg.what);
                tvStuName.setText(students.getStuName());
                tvStuMajor.setText(students.getStuMajor());
                tvStuPhone.setText(students.getStuPhone());
                tvStuQq.setText(students.getStuQq());
                tvStuAddress.setText(students.getStuAddress());
            }else if (msg.what == 1){
                System.out.println(msg.what);
                tvStuName.setText(freshstudents.getStuName());
                tvStuMajor.setText(freshstudents.getStuMajor());
                tvStuPhone.setText(freshstudents.getStuPhone());
                tvStuQq.setText(freshstudents.getStuQq());
                tvStuAddress.setText(freshstudents.getStuAddress());
            }else {
                tvStuName.setText("暂未填写");
                tvStuMajor.setText("暂未填写");
                tvStuPhone.setText("暂未填写");
                tvStuQq.setText("暂未填写");
                tvStuAddress.setText("暂未填写");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Button btnBack = findViewById(R.id.btn_back);
        //返回点击事件,销毁当前界面
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //从bundle中获取用户账号/学号
        final TextView tvUserNumber = findViewById(R.id.tv_stu_number);
        tvUserNumber.setText(this.getIntent().getStringExtra("stu_number1"));
        final String stuNumber = this.getIntent().getStringExtra("stu_number1");

        tvStuName = findViewById(R.id.tv_stu_name);
        tvStuMajor = findViewById(R.id.tv_stu_major);
        tvStuPhone = findViewById(R.id.tv_stu_phone);
        tvStuQq = findViewById(R.id.tv_stu_qq);
        tvStuAddress = findViewById(R.id.tv_stu_address);

        //从数据库中读取infos
        init(this.getIntent().getStringExtra("stu_number1"));

        Button btnModifyInfo = findViewById(R.id.btn_modify_info);
        //跳转到修改用户信息界面
        btnModifyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ModifyInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("stu_number2",tvUserNumber.getText().toString());

                //将信息封装进ArrayList 传给下个intent
                List<String> list = new ArrayList<String>();
                list.add(tvStuName.getText().toString());
                list.add(tvStuMajor.getText().toString());
                list.add(tvStuPhone.getText().toString());
                list.add(tvStuQq.getText().toString());
                list.add(tvStuAddress.getText().toString());

                bundle.putStringArrayList("infos", (ArrayList<String>) list);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //刷新按钮点击事件
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentDbHelper dbHelper1 = new StudentDbHelper(getApplicationContext(),StudentDbHelper.DB_NAME,null,1);
                LinkedList<Student> students = dbHelper1.readStudents(tvUserNumber.getText().toString());

//                Student freshstudent = new Student();
                freshstudents.setStuNumber(stuNumber);
                refresh(freshstudents);

                if(students != null) {
                    for(Student student : students) {
                        tvStuName.setText(student.getStuName());
                        tvStuMajor.setText(student.getStuMajor());
                        tvStuPhone.setText(student.getStuPhone());
                        tvStuQq.setText(student.getStuQq());
                        tvStuAddress.setText(student.getStuAddress());
                    }
                }
            }
        });

    }

    public void init(final String stuNumber){

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/findStudentInfos";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/findStudentInfos";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stuNumber", stuNumber);

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
                        students = gson.fromJson(json, Student.class);

                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = new String(String.valueOf(students));
                        handler.sendMessage(msg);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void refresh(final Student freshstudent){

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/refreshMyInfos";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/refreshMyInfos";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stuNumber", freshstudent.getStuNumber());

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
                        freshstudents = gson.fromJson(json, Student.class);

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = new String(String.valueOf(students));
                        handler.sendMessage(msg);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
