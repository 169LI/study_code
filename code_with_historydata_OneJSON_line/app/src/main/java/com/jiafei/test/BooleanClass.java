package com.jiafei.test;
import android.app.usage.UsageEvents;
public class BooleanClass {
    private BooleanListener booleanListener;
    public void doEat(){
        booleanListener.doEat(new Event(this));
    }
    //注册监听器，该类没有监听器对象啊，那么就传递进来吧。
    public void registerLister(BooleanListener personListener) {
        this.booleanListener = personListener;
    }
}
