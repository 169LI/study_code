package com.jiafei.test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import  okhttp3.Request;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.jiafei.test.LogUtil.e;

public class MainActivity extends AppCompatActivity {

    private static final String Accept = "application/json, text/plain, */*";//用于鉴权信息的验证OneNET API的鉴权参数作为header的authorization参数存在
    private static  String Authorization ;//用于鉴权信息的验证
    Button GET;                                             //这部分基本都是布局文件的控件初始化
    TextView data1,data2,data3,data5;                                  //预先定义的全局变量
    String value1,time,time1;
    private int Flag=1;     //用于判定数据获取和暂停
    private boolean shouldGetData = false;   //用于判定数据获取和暂停
//    RunTimeCurvesView RTCurvesView;
    // 声明一个标志来控制是否需要调用 Plotline() 方法
    private boolean plotlineCalled = false;
    @RequiresApi(api = Build.VERSION_CODES.M)               //Token里面相关健全信息的声明
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GET = findViewById(R.id.get);//匹配layout刷新数据按键
        data1 = findViewById(R.id.temp);//实现匹配layout温度显示区域
        data2 = findViewById(R.id.hum2);//实现匹配layout温度显示区域
        data3 = findViewById(R.id.pm);//实现匹配layout温度显示区域
        data5 = findViewById(R.id.tim);//实现匹配layout时间显示区域
//        RTCurvesView = findViewById(R.id.mRTCurve);//动态图像绘制
//        RTCurvesView.setCoordinator("时间", "数据",10,15,"int","int");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.INTERNET"}, 1);//获取网络权限
        }
        InitDate();//初始化函数
//        Plotline();
        GET.setOnClickListener(new View.OnClickListener() {      //刷新数据按键的点击事件设置定义
            @Override
            public void onClick(View v) {
                Flag = -Flag;//用于判定数据获取和暂停
                if (Flag > 0) {
//                    RTCurvesView.ClearOn(true);
                    shouldGetData = true; // 开始数据获取
                    startDataFetchingLoop(); // 重新启动数据获取循环
//                    Plotline();
                    GET.setText("暂停获取"); // 设置按钮文本为"暂停数据获取"
                    Toast.makeText(MainActivity.this, "继续获取数据", Toast.LENGTH_SHORT).show();

                } else {
                    shouldGetData = false; // 停止数据获取

                    GET.setText("继续获取"); // 设置按钮文本为"暂停数据获取"
                    Toast.makeText(MainActivity.this, "暂停数据获取", Toast.LENGTH_SHORT).show();
                }
            }
        });
        data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //--点击温度跳转到历史数据获取界面的定义
                Intent intent = new Intent(MainActivity.this, DataSearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        data2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //--点击温度跳转到历史数据获取界面的定义
                Intent intent = new Intent(MainActivity.this, HumActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        data3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //--点击温度跳转到历史数据获取界面的定义
                Intent intent = new Intent(MainActivity.this, PmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    //当你的Activity在前台时（即可见时），startDataFetchingLoop()方法会启动循环获取数据。
    // 当Activity不再可见时，shouldGetData标志会被设置为false，循环获取数据会停止。
    // 这样就确保了在UI加载完成后才开始获取数据。
        @Override
        protected void onResume() {
            super.onResume();
            shouldGetData = true;
            startDataFetchingLoop();
        }

        @Override
        protected void onPause() {
            super.onPause();
            shouldGetData = false;
        }

        private void startDataFetchingLoop() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (shouldGetData) {
                        Get();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    private void InitDate() {//初始化函数定义
    //  RTCurvesView.ClearOn(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//定义时间的格式
        String date = sdf.format(new Date());
        String [] date1;
        date1 = date.split("-");// 分割字符串
        if(date1[1].startsWith("0")){   //去掉月日可能出现的0
            date1[1]=date1[1].substring(1);
        }
        if(date1[2].startsWith("0")){
            date1[2]=date1[2].substring(1);
        }
        Values.startDateInfor=date+"T"+ systemTime.getNowTime();//把手机系统时间赋值给values，作为默认进入该界面第一次发送GET请求的起始时间    systemTime定义在与MainActivity相同目录下的systemTime
        e("时间初始化","现在的时间是："+date);
        time1 = date+"T"+"00:00:00";
    }

    public void Get() { //获取数据流信息的主函数
        new Thread(new Runnable() { //新建一个子线程进行处理
            @RequiresApi(api = Build.VERSION_CODES.O) //--用于鉴权信息的验证，声明全局变量
            @Override
            public void run() {
                //鉴权值获取
                try {
                    Authorization = Token.token();//--把Token类中生成的鉴权信息赋值给全局变量，用于后续GET请求的验证
                    Log.d("Token", Authorization);//日志打印
                } catch (NoSuchAlgorithmException | InvalidKeyException |//异常状态捕获
                        UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //接受温度
                // 新的URL和参数
                String url = "https://iot-api.heclouds.com/thingmodel/query-device-property?product_id=0w6evrQh6e&device_name=Arduino";

                OkHttpClient client = new OkHttpClient();//--新建OKHttp通信客户端服务
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", Authorization)
                        .addHeader("Accept", Accept)
                        .build();

                client.newCall(request).enqueue(new Callback() {    //--向OneNET发送GET请求获取设备数据点，涉及到产品IDproduct_id 设备名称device_name和数据流名称datastream_id 还需要前面赋值的鉴权信息
                                                                    //--GET请求格式在OneNET官方开发文档中提供了案例
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }//OKHttp通信的响应体，用于打包发送
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {       //捕获到服务器回复GET请求的所返回的相关内容
                        if(!response.isSuccessful()) {
                            throw new IOException("Unexpected code" +response);                     //如果异常则抛出并记录异常代码和反馈
                        }else {
                            String responseBody = response.body().string();           //定义字符串类型变量并将服务器回复内容以字符串形式赋值给responseBody
                            System.out.println(responseBody);//输出响应内容
                            parseData(responseBody);
                        }
                    }
                });
            }
        }).start();
    }
    private void parseData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            JSONArray dataArray = jsonObject.getJSONArray("data");

            if ("succ".equals(msg) && dataArray.length() > 0) {
                JSONObject temperatureData = dataArray.getJSONObject(dataArray.length() - 1); // 获取最后一个元素，即温度数据
                JSONObject HUmData = dataArray.getJSONObject(dataArray.length() - 3); // 获取最后er个元素，即湿度数据
                JSONObject PmData = dataArray.getJSONObject(dataArray.length() - 2); // 获取最后三个元素，即PM2.5数据

                String temperatureValue = temperatureData.getString("value");
                String HUmValue = HUmData.getString("value");
                String PmValue = PmData.getString("value");

                long timeMillis = temperatureData.getLong("time"); // 获取温度数据的时间（假设是以秒为单位的时间戳）
                String formattedTime = formatTime(timeMillis); // 格式化时间

                showTime(formattedTime);
                showTemperature(temperatureValue);
                showPm(PmValue);
                showHum(HUmValue);
                // 在这里对时间进行处理，例如显示在界面上或者其他操作

                Log.d("Data Parsing", "Temperature Time: " + time);
            } else {
                // 处理错误情况，例如输出错误消息或者进行其他处理
                Log.e("Data Parsing", "Error: Code-" + code + ", Message-" + msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为亚洲/上海
        Date date = new Date(timeMillis); // Already in milliseconds
        return sdf.format(date);
    }

    private void showTemperature(final String temperatureValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里处理温度值，例如显示在界面上或进行其他操作
                Log.d("Temperature", "Temperature Value: " + temperatureValue);
                data1.setText(String.format("温度:      %s ℃", temperatureValue));   // 把温度值显示在具体控件上
            }
        });
    }
    private void showHum(final String HUmData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里处理温度值，例如显示在界面上或进行其他操作
                Log.d("HUm", "hum Value: " + HUmData);
                data2.setText(String.format("湿度:      %s %%", HUmData));   // 把温度值显示在具体控件上
            }
        });
    }
    private void showPm(final String PmData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里处理温度值，例如显示在界面上或进行其他操作
                Log.d("Pm", "hum Value: " + PmData);
                data3.setText(String.format("PM2.5:   %s ug/m3", PmData));   // 把温度值显示在具体控件上
            }
        });
    }
    private void showTime(final String formattedTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里处理温度值，例如显示在界面上或进行其他操作
                data5.setText(String.format("日期:%s", formattedTime));   // 把温度值显示在具体控件上
            }
        });
    }
}