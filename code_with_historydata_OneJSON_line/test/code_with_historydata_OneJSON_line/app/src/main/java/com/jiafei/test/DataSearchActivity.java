package com.jiafei.test;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.jiafei.test.LogUtil.e;
public class DataSearchActivity extends AppCompatActivity {
    private static final String Accept = "application/json, text/plain, */*";
    private static  String Authorization ;
    private Calendar startDateData=Calendar.getInstance();
    private NewDatePickerDialog datePickerDialog_start;//自定义控件，日期选择
    private TextView startDate,data_name;
    private String time1,selectedDate,time2,selectedDate2;
    RunTimeCurvesView RTCurvesView;
    private  ListView listView;//新控件，用于列表下拉显示多个数据
    private Button BtnDataSearch;
    private final String[] mDisplayMonths = {"1", "2", "3","4", "5", "6","7", "8", "9","10", "11", "12"};
    public static final int UPDATE_TEXT=1;
    boolean isFirstRun = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_search);
        startDate= findViewById(R.id.start_date_S);
        data_name= findViewById(R.id.data_name);
        RTCurvesView = findViewById(R.id.mRTCurve);//动态图像绘制
        RTCurvesView.setCoordinator("时间", "数据",10,15,"int","int");
        BtnDataSearch=findViewById(R.id.button1);
        InitDate1();
        InitDate();//初始化函数
        Get();//主函数，默认打开该界面自动调用一次，获取以当天为起始的所有数据
        BtnDataSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataSearchActivity.this, MainActivity.class);//返回按钮定义
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        startDate.setOnClickListener(this::onClick);//监听返回按键的点击
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_date_S://监听日期选择确定按钮
                RTCurvesView.ClearOn(true);
                getStartDate();//以选定的初始日期重新获取并刷新显示的主函数在这里被调用
                break;
        }
    }
    public void Get(){//主函数，默认打开该界面自动调用一次，获取以当天为起始的所有数据 --相关定义
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    Authorization = Token.token();
                    Log.d("Token", Authorization);
                    //接受温度
                    // 新的URL和参数
                    String url = "https://iot-api.heclouds.com/thingmodel/query-device-property-history?product_id=0w6evrQh6e&device_name=Arduino&identifier=Tem&start_time="+time1+"&end_time="+time2 ;

                    OkHttpClient client = new OkHttpClient();//--新建OKHttp通信客户端服务
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", Authorization)
                            .addHeader("Accept", Accept)
                            .build();
                    client.newCall(request).enqueue(new Callback() {            //--向OneNET发送GET请求获取设备数据点，涉及到产品IDproduct_id 设备名称device_name和数据流名称datastream_id 还需要前面赋值的鉴权信息--区别于MainActivity，这里多了time1和limit,表示获取数据的起始时间以及限制数量
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code" + response);
                            } else {
                                String responseBody = response.body().string();           //定义字符串类型变量并将服务器回复内容以字符串形式赋值给responseBody
                                System.out.println(responseBody);//输出响应内容
                                showList(responseBody);
                            }
                        }

                    });
                } catch (NoSuchAlgorithmException | InvalidKeyException |
                        UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void InitDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 创建一个 Calendar 对象，并设置为当前时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
// 清除时间部分，只保留日期
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
// 获取当天0点的时间戳
            long timestamp = calendar.getTimeInMillis();
            time1 = String.valueOf(timestamp);

            // 增加一天的时间戳
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long timestamp2 = calendar.getTimeInMillis();
            time2 = String.valueOf(timestamp2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void InitDate1() {//初始化函数定义
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
        data_name.setText("数据");
        startDate.setText(date1[0]+"年"+date1[1]+"月"+date1[2]+"日");//显示格式定义
        Values.startDateInfor=date+"T"+ systemTime.getNowTime();//把手机系统时间赋值给values，作为默认进入该界面第一次发送GET请求的起始时间    systemTime定义在与MainActivity相同目录下的systemTime
        e("时间初始化","现在的时间是："+date);
    }

    private void getStartDate() {          //下面是根据日期选择反馈的日期重新把日期提供给GET请求，重新获取数据并刷新显示
        Calendar calendar=Calendar.getInstance();
        datePickerDialog_start=new NewDatePickerDialog(DataSearchActivity.this,
                AlertDialog.THEME_HOLO_LIGHT,
                (datePicker, i, i1, i2) -> {
                    if ((i2+1)<=10){
                        String desc1=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        selectedDate = desc1.substring(0,desc1.length()-9);// 分割字符串
                        try {
                            // 创建一个 Calendar 对象，并设置为当前时间
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            calendar1.set(i,i1,i2, 0, 0, 0); // 设置时间为当天0点
                            calendar1.set(Calendar.MILLISECOND, 0); // 清除毫秒部分

                            // 获取当天0点的时间戳
                            long timestamp3 = calendar1.getTimeInMillis();
                            selectedDate = String.valueOf(timestamp3);
                            // 增加一天的时间戳
                            calendar1.add(Calendar.DAY_OF_MONTH, 1);
                            long timestamp2 = calendar1.getTimeInMillis();
                            selectedDate2 = String.valueOf(timestamp2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void run() {
                                try {
                                    Authorization = Token.token();
                                    Log.d("Token", Authorization);
                                } catch (NoSuchAlgorithmException | InvalidKeyException |
                                        UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                // 新的URL和参数
                                String url = "https://iot-api.heclouds.com/thingmodel/query-device-property-history?product_id=0w6evrQh6e&device_name=Arduino&identifier=Tem&start_time="+selectedDate+"&end_time="+selectedDate2 ;

                                OkHttpClient client = new OkHttpClient();//--新建OKHttp通信客户端服务
                                Request request = new Request.Builder()
                                        .url(url)
                                        .addHeader("Authorization", Authorization)
                                        .addHeader("Accept", Accept)
                                        .build();
                                client.newCall(request).enqueue(new Callback() {            //--向OneNET发送GET请求获取设备数据点，涉及到产品IDproduct_id 设备名称device_name和数据流名称datastream_id 还需要前面赋值的鉴权信息--区别于MainActivity，这里多了time1和limit,表示获取数据的起始时间以及限制数量
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (!response.isSuccessful()) {
                                            throw new IOException("Unexpected code" + response);
                                        } else {
                                            String responseBody = response.body().string();           //定义字符串类型变量并将服务器回复内容以字符串形式赋值给responseBody
                                            System.out.println(responseBody);//输出响应内容
                                            showList(responseBody);
                                        }
                                    }

                                });
                            }
                        }).start();
                    }else {      //嵌套了一个else循环是因为获取的日期  if ((i2+1)<10) 月份可能是1位数和两位数，需要单独处理了
                        String desc1=String.format("%d-0%d-%dT00:00:00",i,i1+1,i2);
                        selectedDate = desc1.substring(0,desc1.length()-9);// 分割字符串
                        try {
                            // 创建一个 Calendar 对象，并设置为当前时间
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            calendar1.set(i,i1,i2, 0, 0, 0); // 设置时间为当天0点
                            calendar1.set(Calendar.MILLISECOND, 0); // 清除毫秒部分

                            // 获取当天0点的时间戳
                            long timestamp3 = calendar1.getTimeInMillis();
                            selectedDate = String.valueOf(timestamp3);
                            // 增加一天的时间戳
                            calendar1.add(Calendar.DAY_OF_MONTH, 1);
                            long timestamp2 = calendar1.getTimeInMillis();
                            selectedDate2 = String.valueOf(timestamp2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void run() {
                                try {
                                    Authorization = Token.token();
                                    Log.d("Token", Authorization);
                                } catch (NoSuchAlgorithmException | InvalidKeyException |
                                        UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                // 新的URL和参数
                                String url = "https://iot-api.heclouds.com/thingmodel/query-device-property-history?product_id=0w6evrQh6e&device_name=Arduino&identifier=Tem&start_time="+selectedDate+"&end_time="+selectedDate2 ;

                                OkHttpClient client = new OkHttpClient();//--新建OKHttp通信客户端服务
                                Request request = new Request.Builder()
                                        .url(url)
                                        .addHeader("Authorization", Authorization)
                                        .addHeader("Accept", Accept)
                                        .build();
                                client.newCall(request).enqueue(new Callback() {            //--向OneNET发送GET请求获取设备数据点，涉及到产品IDproduct_id 设备名称device_name和数据流名称datastream_id 还需要前面赋值的鉴权信息--区别于MainActivity，这里多了time1和limit,表示获取数据的起始时间以及限制数量
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (!response.isSuccessful()) {
                                            throw new IOException("Unexpected code" + response);
                                        } else {
                                            String responseBody = response.body().string();           //定义字符串类型变量并将服务器回复内容以字符串形式赋值给responseBody
                                            System.out.println(responseBody);//输出响应内容
                                            showList(responseBody);
                                        }
                                    }

                                });
                            }
                        }).start();
                    }                    //日期选择控件弹窗显示的设置，以及选定新日期后刷新相关内容的UI
                    String desc=String.format("%d年%d月%d日",i,i1+1,i2);
                    startDate.setText(desc);
                    startDateData.set(i,i1,i2);
                    if ((i1+1)<10){
                        Values.startDateInfor=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);//也是根据1~9月和10-12月单独处理
                    }else {
                        Values.startDateInfor=String.format("%d-0%d-%dT00:00:00",i,i1+1,i2);
                    }
                    e("日期转变","开始日期为"+ Values.startDateInfor);
                    time1 = Values.startDateInfor;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog_start.setButton(DialogInterface.BUTTON_POSITIVE,"确定",datePickerDialog_start);//日期选择空间布局显示设置
        datePickerDialog_start.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",datePickerDialog_start);
        datePickerDialog_start.show();

        DatePicker dp = findDatePicker( (ViewGroup) datePickerDialog_start.getWindow().getDecorView());
        if (dp != null) {
            ((NumberPicker) ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(1)).setDisplayedValues(mDisplayMonths);
        }

    }
    private DatePicker findDatePicker(ViewGroup group) {     //日期选择控件 的定义 滚轮和反馈数值
        if  (group  !=   null ) {
            for  ( int  i  =   0 , j  =  group.getChildCount(); i  <  j; i ++ ) {
                View child  =  group.getChildAt(i);
                if  (child  instanceof DatePicker) {
                    return  (DatePicker) child;
                }  else   if  (child  instanceof ViewGroup) {
                    DatePicker result  =  findDatePicker((ViewGroup) child);
                    if  (result  !=   null )
                        return  result;
                }
            }
        }
        return   null ;
    }
    private void showList(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            JSONArray listArray = jsonObject.getJSONObject("data").getJSONArray("list");

            if (code == 0 && "succ".equals(msg) && listArray.length() > 0) {
                List<String> resultList = new ArrayList<>();
                int curve1 = RTCurvesView.createCurve(R.color.colorPrimaryDark, false);
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject item = listArray.getJSONObject(i);
                    long timestamp = item.getLong("time");
                    String value = item.getString("value");
                    float data2;
                    // Convert timestamp to HH:mm:ss format
                    String formattedTime = convertTimestamp(timestamp);
//                    resultList.add(formattedTime + "              " + "数据" + value);
                    data2 = Float.valueOf(value);
                    RTCurvesView.push2Curve(curve1, data2);
                    RTCurvesView.gridOn(true);
                    RTCurvesView.setXDivNum(10);
                    RTCurvesView.postInvalidate(); // 刷新曲线视图
                    try {
                        Thread.sleep(100);
                        RTCurvesView.setPathEffect(new DashPathEffect(new float[]{20, 10}, 1));//虚线绘制
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("Data Parsing", "Error: Code-" + code + ", Message-" + msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

// Convert timestamp to HH:mm:ss format with timezone consideration
    private String convertTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为亚洲/上海
        return sdf.format(date);
    }


}