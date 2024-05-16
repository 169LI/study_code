package com.example.myapplication;

import static android.os.Build.HOST;
import static android.provider.Telephony.Carriers.PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import android.widget.Toast;
import android.widget.VideoView;



import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    private  final byte MotorDirect_FORWARD=2;
    private  final byte MotorDirect_FREE=1;
    private  final byte MotorDirect_BACKWARD=0;


    private ImageView upIV;
    private ImageView downIV;
    private SeekBar speedSB;
    private VideoView videoView;
    private SeekBar orientationSB;
    private Button turnSharpLeftBTN;
    private Button turnSharpRightBTN;
    private Animation animation;
    private Context context=null;
    private final String RESPONSE_TOPIC = "skyline/L1iang/#";  //需要订阅的主题
    private static final String PUBLISH_TOPIC = "skyline/L1iang/attributeSet";  //需要发布的主题
    private final String host = "tcp://broker.emqx.io:1883";     // TCP协议
    private final String userName = "name";
    private final String passWord = "pwd";
    private final String CLIENT_ID = "skyline/L1iang/phone"+System.currentTimeMillis();
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mMqttConnectOptions;
    private boolean sendFlag=false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

        Msg msg=new Msg();


        upIV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:view.startAnimation(animation);
                        if(!msg.getSharpTurn_R()&!msg.getSharpTurn_L())
                            msg.setMotorDirection(MotorDirect_FORWARD);
                        else Toast.makeText(context,"前进之前释放急转",Toast.LENGTH_SHORT).show();
                    //改变发送为前进
                        break;
                    case MotionEvent.ACTION_UP:view.clearAnimation();
                        msg.setMotorDirection(MotorDirect_FREE);
                    //改变发送为自由
                        break;
                }
                return true;
            }
        });

        downIV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:view.startAnimation(animation);
                        if(!msg.getSharpTurn_R()&!msg.getSharpTurn_L())
                            msg.setMotorDirection(MotorDirect_BACKWARD);
                        else Toast.makeText(context,"后退之前释放急转",Toast.LENGTH_SHORT).show();
                        //改变发送为后退
                        break;
                    case MotionEvent.ACTION_UP:view.clearAnimation();
                        msg.setMotorDirection(MotorDirect_FREE);
                    //改变发送为自由
                        break;
                }
                return true;
            }
        });

        turnSharpLeftBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:view.startAnimation(animation);
                        //改变发送为左电机向正转右电机反转
                        if(msg.getMotorDirection()==MotorDirect_FREE&& !msg.getSharpTurn_R()) {
                            msg.setSharpTurn_L(true);
                        }else Toast.makeText(context,"急转前释放其他按钮",Toast.LENGTH_SHORT).show();
                        break;
                    case MotionEvent.ACTION_UP:view.clearAnimation();
                        msg.setSharpTurn_L(false);
                    //改变发送为自由
                        break;
                }
                return true;
            }
        });

        turnSharpRightBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:view.startAnimation(animation);
                        //改变发送为右电机向正转左电机反转
                        if(msg.getMotorDirection()==MotorDirect_FREE&& !msg.getSharpTurn_L()) {
                            msg.setSharpTurn_R(true);
                        }else Toast.makeText(context,"急转前释放其他按钮",Toast.LENGTH_SHORT).show();
                        break;
                    case MotionEvent.ACTION_UP:view.clearAnimation();
                        msg.setSharpTurn_R(false);
                    //改变发送为自由
                        break;
                }
                return true;
            }
        });

        orientationSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                msg.setRudder((byte) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(50);
            }
        });

        speedSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                msg.setMotorSpeed((byte) seekBar.getProgress());
            }
        });


        msg.setOnChangeListener(new Msg.OnChangeListener() {
            @Override
            public void onChange() {
                sendFlag=true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(sendFlag) {
                        try {
                            publish(msg.toJSONObject().toString());
                            sendFlag=false;
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        InitMQTTConnect();
    }

    private void Init(){
        context=this.getApplicationContext();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        upIV = findViewById(R.id.motor_upIV);
        downIV = findViewById(R.id.motor_downIV);
        speedSB = findViewById(R.id.speed);
        videoView = findViewById(R.id.videoView);
        orientationSB = findViewById(R.id.orientation);
        turnSharpLeftBTN = findViewById(R.id.sharpTurn_L);
        turnSharpRightBTN = findViewById(R.id.sharpTurn_R);

        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_scale);
    }
    public void InitMQTTConnect() {

        mqttAndroidClient = new MqttAndroidClient(this, host, CLIENT_ID);
        mMqttConnectOptions = new MqttConnectOptions();
        // 在重新启动和重新连接时记住状态
        mMqttConnectOptions.setCleanSession(true);
        // 设置连接的用户名
        mMqttConnectOptions.setUserName(userName);
        // 设置密码
        mMqttConnectOptions.setPassword(passWord.toCharArray());
        // 设置超时时间，单位：秒
        mMqttConnectOptions.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20);
        //设置服务质量
        MqttMessage message = new MqttMessage("PayLoad".getBytes());
        message.setQos(1);

        mqttAndroidClient.setCallback(mqttCallback);// （需要实现接口回调）
        connectionMQTTServer();


    }
    private void connectionMQTTServer() { // 连接操作

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //进行服务器连接
                    /***
                     * mMqttConnectOptions MQTT设置
                     * iMqttActionListener MQTT连接监听
                     */

                    mqttAndroidClient.connect(mMqttConnectOptions,context, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) { // 连接成功

                            try {
                                Toast.makeText(context, "连接成功！", Toast.LENGTH_SHORT).show();
                                mqttAndroidClient.subscribe(PUBLISH_TOPIC, 1);//订阅主题，参数：主题、服务质量
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //连接失败

                            Toast.makeText(context, "连接失败！正在重新连接！", Toast.LENGTH_SHORT).show();
                            exception.printStackTrace();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connectionMQTTServer(); // ReConnection
                                }
                            }, 5000);   //延时5秒重新连接MQTT服务器

                        }
                    });

                } catch (MqttException e) {
                    e.fillInStackTrace();


                }
            }
        }).run();

    }


    private MqttCallback mqttCallback = new MqttCallbackExtended() {  //回传
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            /**
             *与服务器的连接成功完成时调用。
             * @param reconnect如果为true，则连接是自动重新连接的结果。
             * @param serverURI建立连接的服务器URI。
             **/

        }

        @Override
        public void connectionLost(Throwable cause) {

            Log.i("myMQTTconnect", "连接断开 ");
            connectionMQTTServer(); // ReConnection
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {  // 接收的消息
            /*
            String s = new String(message.getPayload(), "GB2312");
            Log.e("myMQTTconnect", topic + s);  //接收的消息
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            response("GetInfo");
            */
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }


    };


    public static void publish(String message) {
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(PUBLISH_TOPIC, message.getBytes(), 1, retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*
    public void response(String message) {
        String topic = RESPONSE_TOPIC;
        Integer qos = 1;
        Boolean retained = false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    protected void onStop() {
        super.onStop();
        try {
            publish("{\"motorDirection\":1,\"motorSpeed\":1,\"rudder\":50,\"sharpTurn_L\":false,\"sharpTurn_R\":false}");
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}