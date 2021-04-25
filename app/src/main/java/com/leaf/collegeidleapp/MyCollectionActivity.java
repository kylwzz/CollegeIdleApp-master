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
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.tools.NetUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//我的收藏Activity类

public class MyCollectionActivity extends AppCompatActivity {

    ListView lvMyCollection;
    List<Collection> myCollections = new ArrayList<>();
    TextView tvStuId;

//    MyCollectionDbHelper dbHelper;
    //CommodityDbHelper commodityDbHelper;
    MyCollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuId = findViewById(R.id.tv_stuId);
        tvStuId.setText(this.getIntent().getStringExtra("stuId"));
        final String username = this.getIntent().getStringExtra("stuId");
        lvMyCollection = findViewById(R.id.lv_my_collection);

        adapter = new MyCollectionAdapter(getApplicationContext());

        getCollection(username);

        adapter.setData(myCollections);
        lvMyCollection.setAdapter(adapter);

        //设置长按删除事件
        lvMyCollection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCollectionActivity.this);
                builder.setTitle("提示:").setMessage("确定删除此收藏商品吗?").setIcon(R.drawable.icon_user).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Collection collection = (Collection) adapter.getItem(position);
                        deleteCollection(collection);
                        //删除收藏商品项
//                        dbHelper.deleteMyCollection(collection.getTitle(), collection.getDescription(), collection.getPrice());
                        Toast.makeText(MyCollectionActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return false;
            }
        });
        //页面刷新
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                refresh(username);
                myCollections.clear();
                getCollection(username);
                adapter.setData(myCollections);
                lvMyCollection.setAdapter(adapter);
            }
        });
    }

    private void deleteCollection(final Collection collection) {

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/deleteCollection";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/deleteCollection";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", collection.getTitle());
                    jsonObject.put("price", collection.getPrice());
                    jsonObject.put("description", collection.getDescription());
                    jsonObject.put("username", collection.getUsername());

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

    private List<Collection> getCollection(final String username) {

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/getCollection";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/getCollection";

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
                        Type type = new TypeToken<List<Collection>>() {}.getType();
                        List<Collection> list = gson.fromJson(json, type);
                        myCollections.addAll(list);

//                        System.out.println(myCollections.get(0).getUsername());
//                        System.out.println(myCollections.get(0).getTitle());
//                        System.out.println(myCollections.get(0).getPicture());

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        return myCollections;
    }
}
