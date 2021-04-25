package com.leaf.collegeidleapp;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leaf.collegeidleapp.bean.Student;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.StudentDbHelper;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//修改个人信息Activity类

public class ModifyInfoActivity extends AppCompatActivity {

    EditText etStuName,etMajor,etPhone,etQq,etAddress;
    List<String> list = null;
//    Student students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        Button btnBack = findViewById(R.id.btn_back);
        //返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //利用bundle传递学号
        final TextView tvStuNumber = findViewById(R.id.tv_stu_number);
        tvStuNumber.setText(this.getIntent().getStringExtra("stu_number2"));
        final String stuNumber = this.getIntent().getStringExtra("stu_number2");
        list = this.getIntent().getStringArrayListExtra("infos");

        etStuName = findViewById(R.id.et_stu_name);
        etMajor = findViewById(R.id.et_stu_major);
        etPhone = findViewById(R.id.et_stu_phone);
        etQq = findViewById(R.id.et_stu_qq);
        etAddress = findViewById(R.id.et_stu_address);

        //如果查找到的学生信息不为空
        if(list != null) {
            etStuName.setText(list.get(0));
            etMajor.setText(list.get(1));
            etPhone.setText(list.get(2));
            etQq.setText(list.get(3));
            etAddress.setText(list.get(4));

        }



        Button btnSaveInfo = findViewById(R.id.btn_save_info);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断输入不为空
                if(CheckInput()) {

                    Student students = new Student();
                    students.setStuNumber(stuNumber);
                    students.setStuName(etStuName.getText().toString().trim());
                    students.setStuMajor(etMajor.getText().toString().trim());
                    students.setStuPhone(etPhone.getText().toString().trim());
                    students.setStuQq(etQq.getText().toString().trim());
                    students.setStuAddress(etAddress.getText().toString().trim());
//
//                    System.out.println(students.getStuNumber());
//                    System.out.println(students.getStuName());
//                    System.out.println(students.getStuMajor());
//                    System.out.println(students.getStuPhone());
//                    System.out.println(students.getStuQq());
//                    System.out.println(students.getStuAddress());

                    init(students);

                    Toast.makeText(getApplicationContext(),"用户信息保存成功!",Toast.LENGTH_SHORT).show();
                    //销毁当前界面
                    finish();
                }
            }
        });
    }

    //检查输入是否为空
    public boolean CheckInput() {
        String StuName = etStuName.getText().toString();
        String StuMajor = etMajor.getText().toString();
        String StuPhone = etPhone.getText().toString();
        String StuQq = etQq.getText().toString();
        String StuAddress = etAddress.getText().toString();
        if(StuName.trim().equals("")) {
            Toast.makeText(this,"姓名不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuMajor.trim().equals("")) {
            Toast.makeText(this,"专业不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuPhone.trim().equals("")) {
            Toast.makeText(this,"联系方式不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuQq.trim().equals("")) {
            Toast.makeText(this,"QQ号不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuAddress.trim().equals("")) {
            Toast.makeText(this,"地址不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void init(final Student students){
        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/updateInfos";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/updateInfos";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stuNumber", students.getStuNumber());
                    jsonObject.put("stuName", students.getStuName());
                    jsonObject.put("stuMajor", students.getStuMajor());
                    jsonObject.put("stuPhone", students.getStuPhone());
                    jsonObject.put("stuQq", students.getStuQq());
                    jsonObject.put("stuAddress", students.getStuAddress());

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

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
