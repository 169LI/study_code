//package com.example.myapplication;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//
//import java.io.EOFException;
//import java.io.IOException;
//import java.util.List;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String DeviceID="";
//    private static final String Device_NAME="test";
//    private static final String ProductID="9hu56lk2XL";
//    //private static final String APIkey="dDlMU3VMd1g2SElsR2lSY0ptSU4zb1ZiZzhzcDVMNzU=";
//    private static final String APIkey="version=2018-10-31&res=products%2F9hu56lk2XL%2Fdevices%2Fmqtt&et=1956499200&method=sha1&sign=JDU7tMPZxOISrTtu20WR4zksYto%3D";
//    private static final String wendu="wendu";
//    private static final String shidu="shidu";
//    TextView data1,data2;
//    String value1,Value2;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        data1=findViewById(R.id.wendu);
//        data2=findViewById(R.id.shidu);
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//            requestPermissions(new String[]{"android.permission.INTERNET"},1);
//        }
//        try {
//            OkHttpClient client=new OkHttpClient();
//            Request request=new Request.Builder().url("https://iot-api.heclouds.com/thingmodel/query-device-property?product_id="+ProductID+"&device_name="+"mqtt").addHeader("api-key",APIkey).build();
//            Response response=client.newCall(request).execute();
//            String responseData =response.body().string();
//            Toast.makeText(MainActivity.this,responseData,Toast.LENGTH_LONG).show();
//            parseJSONWithGSON(responseData);
//
//            JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
//            List<Datastreams> streams=app.getData().getDatastreams();
//            List<Datapoints> points=streams.get(0).getDatapoints();
//            value1=points.get(0).getValue();
//            data1.post(new Runnable() {
//                @Override
//                public void run() {
//                    data1.setText(value1);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Get();
//    }
//    public void Get(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//
//                }
//            }
//        });
//
//    }
//
//    private void parseJSONWithGSON(String jsonData){
//        JsonRootBean app =new Gson().fromJson(jsonData,JsonRootBean.class);
//        List<Datastreams> streams =app.getData().getDatastreams();
//        List<Datapoints> points =streams.get(0).getDatapoints();
//        int count=app.getData().getCount();
//        for (int i=0;i< points.size();i++){
//            String time=points.get(i).getAt();
//            String value =points.get(i).getValue();
//            Log.w("www","time=" +time);
//            Log.w("www","value=" +value);
//        }
//    }
//}
