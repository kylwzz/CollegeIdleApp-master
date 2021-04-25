package com.leaf.collegeidleapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leaf.collegeidleapp.adapter.MyCollectionAdapter;
import com.leaf.collegeidleapp.adapter.MyCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.CommodityDbHelper;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// 我的发布物品Activity类

public class MyCommodityActivity extends AppCompatActivity {

    ListView lvMyCommodity;
    List<Commodity> myCommodities = new ArrayList<>();
    TextView tvStuId;

    MyCommodityAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commodity);
        TextView tvBack = findViewById(R.id.tv_back);
        //点击返回销毁当前界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuId = findViewById(R.id.tv_stu_id);
        tvStuId.setText(this.getIntent().getStringExtra("stu_id"));
        lvMyCommodity = findViewById(R.id.lv_my_commodity);

        adapter = new MyCommodityAdapter(getApplicationContext());

        final String username = this.getIntent().getStringExtra("stu_id");

        GetMyCommodity(this.getIntent().getStringExtra("stu_id"));
        adapter.setData(myCommodities);
        lvMyCommodity.setAdapter(adapter);

        //长按点击事件
        lvMyCommodity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //注意,这里的content不能写getApplicationContent();
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommodityActivity.this);
                builder.setTitle("提示:").setMessage("确认删除/完成交易").setIcon(R.drawable.icon_user).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //根据商品名称,商品描述和价格执行删除操作
                        Commodity commodity = (Commodity) adapter.getItem(position);

                        DeleteCommodity(commodity);

                        Toast.makeText(MyCommodityActivity.this,"操作成功!",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return false;
            }
        });
        //刷新界面点击事件
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new MyCommodityAdapter(MyCommodityActivity.this);
                refresh(username);
                adapter.setData(myCommodities);
                lvMyCommodity.setAdapter(adapter);
            }
        });
    }

    private void refresh(final String username) {
        myCommodities.clear();

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/GetMyCommodity";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/GetMyCommodity";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);

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
                        Type type = new TypeToken<List<Commodity>>() {}.getType();
                        List<Commodity> list = gson.fromJson(json, type);
                        myCommodities.addAll(list);


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void DeleteCommodity(final Commodity commodity) {

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/DeleteCommodity";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/DeleteCommodity";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", commodity.getTitle());
                    jsonObject.put("price", commodity.getPrice());
                    jsonObject.put("description", commodity.getDescription());
                    jsonObject.put("username", commodity.getUsername());

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

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private List<Commodity> GetMyCommodity(final String username) {

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/GetMyCommodity";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/GetMyCommodity";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);

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
                        Type type = new TypeToken<List<Commodity>>() {}.getType();
                        List<Commodity> list = gson.fromJson(json, type);
                        myCommodities.addAll(list);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        return myCommodities;
    }
}
