package com.example.wkj.chattest;

import java.io.Serializable;

/**
 * Created by wkj on 2017/2/27.
 */

public class Msg implements Serializable{
    public static final int TYPE_RECEIVEID=0;
    public static final int TYPE_SEND=1;
    private String content;
    private int type;
    public Msg(String content,int type){
        this.content=content;
        this.type=type;
    }
    public String getContent() {
        return content;
    }
    public int getType(){
        return type;
    }
}
