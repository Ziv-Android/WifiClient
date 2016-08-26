package com.ziv.wificlient.service;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ziv.wificlient.R;

public class ServiceActivity extends AppCompatActivity {
    private Button start = null;
    private EditText bufferText = null;
    private Button send = null;
    private ServerThread serverThread = null;
    private String sendBuffer = null;
    private String receiveBuffer = null;
    private TcpSocketServer tss = null;
    private TextView receiveView = null;

    private Handler handler = new Handler(){//线程与UI交互更新界面
        public void handleMessage(Message msg){
            receiveView.setText(receiveBuffer);
            Toast.makeText(ServiceActivity.this, receiveBuffer, Toast.LENGTH_SHORT).show();
        }
    };
    private String intToIp(int i) {
        return (i & 0xFF ) + "" +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        TextView tv=(TextView) findViewById(R.id.service_ip_number);
        tv.setText("本机IP："+ip);
        receiveView = (TextView)this.findViewById(R.id.service_receive_text);
        start = (Button) this.findViewById(R.id.service_start_btn);

        //监听服务器开启
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(serverThread == null){
                    EditText portEditText = (EditText)ServiceActivity.this.findViewById(R.id.service_port_number);
                    String port = portEditText.getText().toString().trim();
                    serverThread = new ServerThread(port);
                    serverThread .start();
                    Toast.makeText(ServiceActivity.this, port, Toast.LENGTH_SHORT).show();
                }
            }});


        send = (Button)this.findViewById(R.id.service_send_btn);
        bufferText = (EditText)this.findViewById(R.id.service_buffer_text);

        //监听发送信息
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                sendBuffer = bufferText.getText().toString().trim();
                if(sendBuffer != null)//为了避免线程把它弄为buffer = null;
                    Toast.makeText(ServiceActivity.this, sendBuffer, Toast.LENGTH_SHORT).show();
            }
        });
    }
    class ServerThread extends Thread{
        private int port;
        public ServerThread (String port){
            this.port = Integer.parseInt(port);
        }
        public void run(){
            //建立服务端
            if(tss == null)
                tss = new TcpSocketServer(this.port);
            new Thread(new WriteThread()).start();//开启“写”线程
            new Thread(new ReadThread()).start();//开启“读”线程
        }
        private class ReadThread implements Runnable{

            public void run(){
                while(true){
                    if((receiveBuffer = tss.getMessage()) != null){//收到不为null的信息就发送出去
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }
        private class WriteThread implements Runnable{
            public void run(){
                while(true){
                    try {
                        //发送数据
                        if(sendBuffer != null){
                            //tss.sendMessage(1821,buffer);
                            tss.sendMessage(sendBuffer);
                            sendBuffer = null;//清空，不让它连续发
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
