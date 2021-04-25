package com.leaf.collegeidleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

//不同类型商品信息的活动类

public class CommodityTypeActivity extends AppCompatActivity {

    TextView tvCommodityType;
    ListView lvCommodityType;
    List<Commodity> commodities = new LinkedList<>();

    AllCommodityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_type);
        //返回事件
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCommodityType = findViewById(R.id.tv_type);
        lvCommodityType = findViewById(R.id.list_commodity);
        adapter = new AllCommodityAdapter(getApplicationContext());
        //根据不同的状态显示不同的界面
        int status = this.getIntent().getIntExtra("status",0);
        System.out.println(status);
        if(status == 1) {
            tvCommodityType.setText("学习用品");
        }else if(status == 2) {
            tvCommodityType.setText("电子用品");
        }else if(status == 3) {
            tvCommodityType.setText("生活用品");
        }else if(status == 4) {
            tvCommodityType.setText("体育用品");
        }

        String category = tvCommodityType.getText().toString();
        System.out.println(category);
        GetReview(category);

//        System.out.println(commodities.get(0).getId());
//        System.out.println(commodities.get(0).getCategory());
//        System.out.println(commodities.get(0).getTitle());

        adapter.setData(commodities);
        lvCommodityType.setAdapter(adapter);
    }

    public List<Commodity> GetReview(final String category){

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/showCommodityBycategory";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/showCommodityBycategory";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("category", category);

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

                        System.out.println(list.get(0).getTitle());

                        commodities.addAll(list);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        return commodities;
    }
}
