package com.example.myapplication;


import org.json.JSONException;
import org.json.JSONObject;

public class Msg {

    private byte motorDirection=1;
    private byte motorSpeed=1;
    private byte rudder=50;
    private boolean sharpTurn_L=false;
    private boolean sharpTurn_R=false;
    public interface OnChangeListener {	// 创建interface类
        void onChange();    // 值改变
    }
    private static OnChangeListener onChangeListener;	// 声明interface接口
    public static void setOnChangeListener(OnChangeListener onChange){	// 创建setListener方法
        onChangeListener = onChange;
    }


    public byte getMotorDirection() {
        return motorDirection;
    }

    public void setMotorDirection(byte motorDirection) {
        this.motorDirection = motorDirection;
        onChangeListener.onChange();
    }

    public int getMotorSpeed() {
        return motorSpeed;
    }

    public void setMotorSpeed(byte motorSpeed) {
        this.motorSpeed = motorSpeed;
        onChangeListener.onChange();
    }

    public int getRudder() {
        return rudder;
    }

    public void setRudder(byte rudder) {
        this.rudder = rudder;
        onChangeListener.onChange();
    }

    public boolean getSharpTurn_L() {
        return sharpTurn_L;
    }

    public void setSharpTurn_L(boolean sharpTurn_L) {
        this.sharpTurn_L = sharpTurn_L;
        onChangeListener.onChange();
    }

    public boolean getSharpTurn_R() {
        return sharpTurn_R;
    }

    public void setSharpTurn_R(boolean sharpTurn_R) {
        this.sharpTurn_R = sharpTurn_R;
        onChangeListener.onChange();
    }
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("motorDirection", motorDirection);
        jsonObject.put("motorSpeed", motorSpeed);
        jsonObject.put("rudder", rudder);
        jsonObject.put("sharpTurn_L", sharpTurn_L);
        jsonObject.put("sharpTurn_R", sharpTurn_R);
        return jsonObject;
    }
}

