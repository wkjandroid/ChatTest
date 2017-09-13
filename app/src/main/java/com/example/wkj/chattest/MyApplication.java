package com.example.wkj.chattest;

import android.app.Application;

import com.example.wkj.chattest.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkj_pc on 2017/3/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public  static List<Msg> msgList=new ArrayList<>();
}
