package com.jiafei.test;

public class Datapoints {
    private String at;
    private String value;
    private String identifier; // 添加标识符属性
    public void setValue(String value){this.value=value;}
    public String getAt(){return at;}
    public String getValue(){return value;}
    public String getIdentifier() { // 添加获取标识符的方法
        return identifier;
    }
}
