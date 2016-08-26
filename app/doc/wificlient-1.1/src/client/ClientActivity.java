package com.ziv.wificlient.client;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ziv.wificlient.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientActivity extends AppCompatActivity {
    private EditText ipEdit;
    private EditText portEdit;
    private EditText buffEdit;
    private Button startButton;
    private Button sendButton;
    private TextView receiveView;
    private Socket s = null;
    private byte[] receiveBuffer = new byte[1024];
    private String sendBuffer = new String();
    private int cmdCount = 0;

    private Handler handler = new Handler(){//线程与UI交互更新界面
        public void handleMessage(Message msg){
            receiveView.setText(new String(receiveBuffer).trim());
            Arrays.fill(receiveBuffer, (byte)0);//清空
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        setContentView(R.layout.activity_client);

        init();
        /*开启socket通信*/
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(s == null){//这里要设置验证!!!!!!!!!
                    /*设定ip和port*/
                    String ip = ipEdit.getText().toString();
                    int port = Integer.parseInt(portEdit.getText().toString().trim());
                    /*开启socket线程*/
                    new Thread(new SocketClientControl(ip,port)).start();
                }
                Toast.makeText(ClientActivity.this, "服务器连接成功", Toast.LENGTH_SHORT).show();
            }
        });
        /*发送数据*/
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(s != null)
                    sendBuffer = buffEdit.getText().toString();
                Toast.makeText(ClientActivity.this, "send -> "+sendBuffer, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init(){
        startButton = (Button) findViewById(R.id.client_start_btn);
        sendButton  = (Button) findViewById(R.id.client_send_btn);
        ipEdit      = (EditText) findViewById(R.id.client_ip_number);
        portEdit    = (EditText) findViewById(R.id.client_port_number);
        ipEdit.setText("172.20.5.12");
        portEdit.setText("8080");
        buffEdit    = (EditText) findViewById(R.id.client_buff_text);
        receiveView = (TextView) findViewById(R.id.client_receive_text);
    }
    private class SocketClientControl implements Runnable{
        private InputStream in  = null;
        private OutputStream out = null;
        private String ip = null;
        private int port;
        private SocketClientControl(){ }
        public SocketClientControl(String ip,int port){
            this.ip = ip;
            this.port = port;
        }
        public void run(){
            try {
                s = new Socket(ip,port);//获得链接
                in  = s.getInputStream();//获得输入流
                out = s.getOutputStream();//获得输出流
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);//要是出问题，就线程退出
            }

            new Thread(new WriteThread()).start();//开启“写”线程
            new Thread(new ReadThread()).start();//开启“读”线程
        }
        private class ReadThread implements Runnable{
            public void run() {
                while(true){
                    try {
                        if(in.read(receiveBuffer) > 0){//等待命令的输入
                            cmdCount ++;
                            handler.sendEmptyMessage(0);//发送信息，更新UI
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        private class WriteThread implements Runnable{

            public void run(){
                while(true){
                    if(!sendBuffer.equals("")){
                        try {
                            out.write(sendBuffer.getBytes());//输出
                            out.flush();//输出刷新缓冲
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendBuffer = "";
                    }
                }
            }

        }

    }
}
