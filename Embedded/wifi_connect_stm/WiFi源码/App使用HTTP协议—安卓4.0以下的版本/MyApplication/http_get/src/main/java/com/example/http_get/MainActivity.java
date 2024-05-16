package com.example.http_get;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.StandardConstants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String Device_Name="mqtt";
    private String Pe_ID="9hu56lk2XL";
    private String ok_key="version=2020-05-29&res=userid%2F360841&et=1956499200&method=sha1&sign=TQ3e%2FaG%2FmWVHsiAvmwiZGWvDp6s%3D";
    String path="https://iot-api.heclouds.com/thingmodel/query-device-property?product_id=9hu56lk2XL&device_name=mqtt";//获取属性

    private Handler handler;

    String shidu,wendu;
    boolean switchs;
    private EditText wendu_et,shidu_et;
    private Button no_off;

    private Runnable task = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            handler.postDelayed(this,50);//设置循环时间，此处是5秒
            run_data();//查询云端数据
            if(switchs){
                no_off.setText("关");
            }else{
                no_off.setText("开");
            }
            wendu_et.setText(wendu);
            shidu_et.setText(shidu);

            //需要执行的代码
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //token key = new token();  //获取token密钥
        wendu_et = findViewById(R.id.wendu);
        shidu_et=findViewById(R.id.shidu);
        no_off = findViewById(R.id.switchs);

        handler = new Handler();
        handler.postDelayed(task,1000);//延时
        handler.post(task);//执行

        no_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doHttpRequest();//上报数据
                        android.util.Log.e("tag","执行下发开关命令");
                    }
                }).start();
            }
        });



    }
    private void doHttpRequest() {
        try {
            URL url = new URL("https://iot-api.heclouds.com/thingmodel/set-device-property");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Connection","keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("authorization", ok_key);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            String json = getJsonContent();
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            int responseCode = conn.getResponseCode();
            android.util.Log.e("tag", "responseCode = " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream input = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb.append((char) ss);
                }
                android.util.Log.e("tag", "请求结果 = " + sb.toString());
                input.close();
            }
            conn.disconnect();
        } catch (Exception e) {
            android.util.Log.e("tag", "出现异常: " + e.toString());
            e.printStackTrace();
        }
    }

    private String getJsonContent() {
        JSONObject jsonObject = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            jsonObject.put("product_id", "9hu56lk2XL");
            jsonObject.put("device_name", "mqtt");
            jsonObject.put("params", params);
            params.put("switchs", !switchs);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }



    public void run_data() {
        URL url;
        HttpsURLConnection connection;
        try {
            url = new URL(path);
            connection = (HttpsURLConnection) url.openConnection();
            // 下面使一些自由的定制，比如设置连接超时，读取超时的毫秒数，以及服务器希望得到的一些消息头
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authorization", ok_key);
            //获取状态码，200为成功；
            if (connection.getResponseCode() == 200) {
                //Toast.makeText(MainActivity.this, "第二", Toast.LENGTH_LONG).show();
                // 接下来利用输入流对数据进行读取
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                // 读取数据完毕，接下来将数据传送到Handler进行显示
                //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                json_data(response.toString());
                //a.setText(shidu+wendu+switchs);
                // 最后关闭HTTP连接
                connection.disconnect();
            } else {
                Toast.makeText(MainActivity.this, "第三", Toast.LENGTH_LONG).show();
            }

        } catch (ProtocolException e) {
            Toast.makeText(MainActivity.this, "协找到", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this, "协议未找到", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "您输入的网址有误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    void json_data(String JSON) throws JSONException {
        JSONObject jsonObject = new JSONObject(JSON);
        JSONArray data = jsonObject.optJSONArray("data");//获取JSON的数组结构【】
        for (int i = 0; i < data.length(); i++) {
            JSONObject value= data.getJSONObject(i);
            if(value.optString("identifier").equals("shidu")){
                shidu=value.optString("value");
            }
            if(value.optString("identifier").equals("wendu")){
                wendu=value.optString("value");
            }
            if(value.optString("identifier").equals("switchs")){
                switchs=value.optBoolean("value");
            }
        }

    }
}


