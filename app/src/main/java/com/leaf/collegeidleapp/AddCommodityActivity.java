package com.leaf.collegeidleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.tools.NetUtils;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// 物品发布界面Activity类

public class AddCommodityActivity extends AppCompatActivity {

    TextView tvStuId;
    ImageButton ivPhoto;
    EditText etTitle,etPrice,etPhone,etDescription;
    Spinner spType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);
        //取出学号
        tvStuId = findViewById(R.id.tv_student_id);
        tvStuId.setText(this.getIntent().getStringExtra("user_id"));
        final String username = this.getIntent().getStringExtra("user_id");
        Button btnBack = findViewById(R.id.btn_back);
        //返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivPhoto = findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,1);
            }
        });
        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etPhone = findViewById(R.id.et_phone);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);
        Button btnPublish = findViewById(R.id.btn_publish);
        //发布按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查合法性
                if(CheckInput()) {
                    //把图片先转化成bitmap格式
                    BitmapDrawable drawable = (BitmapDrawable) ivPhoto.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    //二进制数组输出流
                    ByteArrayOutputStream byStream = new ByteArrayOutputStream();
                    //将图片压缩成质量为100的PNG格式图片
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byStream);
                    //把输出流转换为二进制数组
                    final byte[] byteArray2 = byStream.toByteArray();
                    final String title2 = etTitle.getText().toString();
                    final String username2 = username;
                    final String phone2 = etPhone.getText().toString();
                    final float Price2 = Float.parseFloat(etPrice.getText().toString());
                    final String Category2 = spType.getSelectedItem().toString();
                    final String description2 = etDescription.getText().toString();
                    System.out.println(byteArray2);
                    final String S2 = byteArray2.toString();
                    System.out.println(S2);

                    new Thread() {
                        @Override
                        public void run() {

                            String num = init(username2, S2,title2,phone2,Category2,Price2,description2);
                            if(num!=null){
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "商品信息发布成功!", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "商品信息发布失败!", Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                            Toast.makeText(getApplicationContext(), "商品信息发布成功!", Toast.LENGTH_SHORT).show();
                            finish();


                        }
                    }.start();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            //从相册返回的数据
            if (data != null) {
                //得到图片的全路径
                Uri uri = data.getData();
                ivPhoto.setImageURI(uri);
            }
        }
    }

//检查输入是否合法
    public boolean CheckInput() {
        String title = etTitle.getText().toString();
        String price = etPrice.getText().toString();
        String type = spType.getSelectedItem().toString();
        String phone = etPhone.getText().toString();
        String description = etDescription.getText().toString();
        if (title.trim().equals("")) {
            Toast.makeText(this,"商品标题不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (price.trim().equals("")) {
            Toast.makeText(this,"商品价格不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (type.trim().equals("请选择类别")) {
            Toast.makeText(this,"商品类别未选择!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.trim().equals("")) {
            Toast.makeText(this,"手机号码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.trim().equals("")) {
            Toast.makeText(this,"商品描述不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String init(String username2, String S2, String title2, String phone2, String Category2, float Price2, String description2){
        //家里的ip
        final String path = "http://192.168.31.140:8080/addCommodity";
        //广州的ip
//        final String path = "http://192.168.0.102:8080/addCommodity";
        String id = null;
        try {
            URL url = new URL(path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);


            /* String data = "username=" + URLEncoder.encode(R1, "utf-8") + "&password=" + URLEncoder.encode(R2,"utf-8");*/

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username2);
//            jsonObject.put("picture", byteArray2);
            jsonObject.put("picture", S2);
            jsonObject.put("title", title2);
            jsonObject.put("phone", phone2);
            jsonObject.put("category", Category2);
            jsonObject.put("price", Price2);
            jsonObject.put("description", description2);

            String content = String.valueOf(jsonObject);

            System.out.println(content);
            connection.setRequestProperty("ser-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();

            int responseCode = connection.getResponseCode();
            /*byte arr[] = new byte[1024];*/
            System.out.println(responseCode);
            if (responseCode == 200) {

                InputStream is = connection.getInputStream();
                //调用工具类，将流转换成String类型
                String json = NetUtils.readString(is);

                Gson gson = new Gson();
                Commodity commodity = gson.fromJson(json, Commodity.class);


                id = ("1");
                System.out.println(id);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

}
