package com.jiafei.test;
import java.util.Date;
public class JsonRootBean {

    private int code;
    private Data data;
    private String msg;
    private String request_id;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
    public String getRequest_id() {
        return request_id;
    }

}