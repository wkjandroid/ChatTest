package com.example.wkj.chattest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {
    private List<Msg> msgList=MyApplication.msgList;
    private EditText inputText;
    private Button send;
    private WebSocket baseWebSocket;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        getWebSocket("ws://119.29.154.229/chat/live");
    }
    private void initListener(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString().trim();
                initMsgs(content);
            }
        });
    }

    private void initView(){
        inputText=(EditText)findViewById(R.id.input_text);
        send=(Button)findViewById(R.id.send);
        msgRecyclerView=(RecyclerView)findViewById(R.id.msg_recycler_view);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(manager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
    }

    private  void initMsgs(String content) {
        if (!TextUtils.isEmpty(content)) {
            Msg msg = new Msg(content, Msg.TYPE_SEND);
            msgList.add(msg);
            adapter.notifyItemInserted(msgList.size() - 1);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
            inputText.setText("");
            baseWebSocket.send(""+content);
        }
    }
    public void getWebSocket(String address){
        System.out.println("WebSocketUtils client start");
        Request request=new Request.Builder().url(address)
                .build();
        new OkHttpClient.Builder().build().newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                baseWebSocket=webSocket;
                sendPingToServer();
                System.out.println("WebSocketUtils client onOpen\"+\"client request header:\" + response.request().headers()\n" +
                        "                +\"client response header:\" + response.headers()+\"client response:\" + response");
            }
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("----------"+text);

                if (!TextUtils.isEmpty(text)) {
                    Msg msg = new Msg(text, Msg.TYPE_RECEIVEID);
                    msgList.add(msg);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           adapter.notifyItemInserted(msgList.size() - 1);
                           msgRecyclerView.scrollToPosition(msgList.size() - 1);
                       }
                   });
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                baseWebSocket=null;
                System.out.println("WebSocketUtils"+"client onClosing"+"code:" + code + " reason:" + reason);
            }
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                baseWebSocket=null;
                System.out.println("webSocketUtils"+"client onClosed"+"code:" + code + " reason:" + reason);
            }
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                baseWebSocket=null;
                //出现异常会进入此回调
                System.out.println("WebSocketUtils"+"client onFailure"+"throwable:" + t);
            }
        });
    };
    //设置心跳防止websocket断线
    public void sendPingToServer(){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (null!=baseWebSocket){
                    baseWebSocket.send("");
                }
            }
        },0,3000);
    }
}
