package com.leaf.collegeidleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leaf.collegeidleapp.adapter.ReviewAdapter;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.bean.Review;
import com.leaf.collegeidleapp.tools.NetUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//商品信息评论/留言类

public class ReviewCommodityActivity extends AppCompatActivity {

    TextView title,description,price,phone;
    ImageView ivCommodity;
    ListView lvReview;

    LinkedList<Review> reviews = new LinkedList<>();

    EditText etComment;
    int position;
    byte[] picture;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_commodity);
        ivCommodity = findViewById(R.id.iv_commodity);
        title = findViewById(R.id.tv_title);
        description = findViewById(R.id.tv_description);
        price = findViewById(R.id.tv_price);
        phone = findViewById(R.id.tv_phone);
        final Bundle b = getIntent().getExtras();
        if( b != null) {
            picture = b.getByteArray("picture");
            Bitmap img = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            ivCommodity.setImageBitmap(img);
            title.setText(b.getString("title"));
            description.setText(b.getString("description"));
            price.setText(String.valueOf(b.getFloat("price"))+"元");
            phone.setText(b.getString("phone"));
            position = b.getInt("position");
            index = b.getInt("index");
        }

        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //点击收藏按钮
        ImageButton ibMyLove = findViewById(R.id.ib_my_love);
        ibMyLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection collection = new Collection();
                collection.setTitle(title.getText().toString());
                String price1 = price.getText().toString().substring(0,price.getText().toString().length()-1);
                collection.setPrice(Float.parseFloat(price1));
                collection.setPhone(phone.getText().toString());
                collection.setDescription(description.getText().toString());
                collection.setPicture(picture.toString());

                String stuId = getIntent().getStringExtra("stuId");

                collection.setUsername(stuId);

                addCollection(collection);
                Toast.makeText(getApplicationContext(),"已添加至我的收藏!",Toast.LENGTH_SHORT).show();
            }
        });

        etComment = findViewById(R.id.et_comment);
        lvReview = findViewById(R.id.list_comment);
        //提交评论点击事件
        Button btnReview = findViewById(R.id.btn_submit);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查是否为空
                if(CheckInput()) { ;
                    Review review = new Review();
                    //获取评论内容
                    review.setContent(etComment.getText().toString());

                    //获取当前时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                    Date date = new Date(System.currentTimeMillis());
                    review.setCurrentTime(simpleDateFormat.format(date));

                    String stuId = getIntent().getStringExtra("stuId");
                    review.setStuId(stuId);

                    review.setPosition(index);

                    System.out.println("添加评论：" + index);
                    init(review);

                    //评论置为空
                    etComment.setText("");
                    Toast.makeText(getApplicationContext(),"评论成功!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());

        GetReview(index);

        adapter.setData(reviews);


        //设置适配器
        lvReview.setAdapter(adapter);
        //刷新页面
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviews.clear();
                refresh(index);
                adapter.setData(reviews);
                adapter.notifyDataSetChanged();
                lvReview.setAdapter(adapter);
            }
        });
    }

    private void addCollection(final Collection collection) {

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/addCollection";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/addCollection";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", collection.getUsername());
                    jsonObject.put("picture", collection.getPicture());
                    jsonObject.put("title", collection.getTitle());
                    jsonObject.put("description", collection.getDescription());
                    jsonObject.put("price", collection.getPrice());
                    jsonObject.put("phone", collection.getPhone());


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

//  检查输入评论是否为空

    public boolean CheckInput() {
        String comment = etComment.getText().toString();
        if (comment.trim().equals("")) {
            Toast.makeText(this,"评论内容不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void init(final Review review){
        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/addReview";
                //广州的ip
                //final String path = "http://192.168.0.102:8080/addReview";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stuId", review.getStuId());
                    jsonObject.put("currentTime", review.getCurrentTime());
                    jsonObject.put("content", review.getContent());
                    jsonObject.put("position", review.getPosition());

                    System.out.println("添加评论里的" + index);

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
//                        students = gson.fromJson(json, Student.class);

//                        Message msg = new Message();
//                        msg.what = 2;
//                        msg.obj = new String(String.valueOf(students));
//                        handler.sendMessage(msg);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public LinkedList<Review> GetReview(final int index){

        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/GetReviews";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/GetReviews";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("position", index);

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
                        Type type = new TypeToken<List<Review>>() {}.getType();
                        List<Review> list = gson.fromJson(json, type);

                        reviews.addAll(list);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        return reviews;
    }

    public LinkedList<Review> refresh(final Integer index){
        new Thread(){
            @Override
            public void run() {
                //家里的ip
                final String path = "http://192.168.31.140:8080/RefreshReviews";
                //广州的ip
//                final String path = "http://192.168.0.102:8080/RefreshReviews";

                try{

                    URL url = new URL(path);
                    //打开URL连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);

                    //json方式传数据
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("position", index);

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
                        Type type = new TypeToken<List<Review>>() {}.getType();
                        List<Review> list = gson.fromJson(json, type);
                        reviews.addAll(list);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        return reviews;
    }

}
