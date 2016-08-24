package com.ziv.wificlient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ziv.wificlient.service.LocalService;
import com.ziv.wificlient.threads.SendDataThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends Activity {
    WifiManager wifi=null;
    Button open=null;
    Button gets=null;
    Button sends=null;
    Button openget=null;
    //Button bind=null;
    boolean flag=false;
    LocalService myservice;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifi=(WifiManager)getSystemService(WIFI_SERVICE);
        open=(Button)findViewById(R.id.openheat);
        gets=(Button)findViewById(R.id.getphone);
        sends=(Button)findViewById(R.id.startsocket);
        //openget=(Button)findViewById(R.id.openget);
        //bind=(Button)findViewById(R.id.bindService);
		/*bind.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("aaa", "开始绑定服务");

			}
		});*/
        handler=new Handler()
        {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(MainActivity.this, msg.obj.toString(), 5000).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, msg.obj.toString(), 5000).show();
                        break;
                    default:
                        break;
                }
            }

        };


        sc=new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("aaa", "绑定成功");
                LocalService.LocalBinder binder = (LocalService.LocalBinder)service; //通过IBinder获取Service
                myservice=binder.getService();
                myservice.startWaitDataThread(handler);//完成绑定后打开另外一条线程等待消息接收
            }
        };
        open.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flag=!flag;
                setWifiApEnabled(flag);//开启热点
            }
        });
        gets.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//获取连接到消息发送的手机IP地址
                ArrayList<String> connectedIP = getConnectedIP();
                StringBuilder resultList = new StringBuilder();

                for (String ip : connectedIP) {
                    resultList.append(ip);
                    resultList.append("\n");
                }
                Toast.makeText(getApplicationContext(), "连接到手机上的Ip是："+resultList.toString(), 3000).show();
            }
        });

        sends.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<String> connectedIP = getConnectedIP();
                for (String ip : connectedIP) {
                    if (ip.contains(".")) {
                        new SendDataThread(ip).start();//消息发送方启动线程发送消息
                    }
                }
            }
        });
		/*openget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});*/
        connection();//绑定等待发送方消息的service
    }
    private ArrayList<String> getConnectedIP() {//获取连接到本机热点上的手机ip
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifi.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "scorewizard";
            String password="password";
            //配置热点的密码
            apConfig.preSharedKey =password;
            apConfig.hiddenSSID = true;
            apConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            apConfig.status = WifiConfiguration.Status.ENABLED;
            //通过反射调用设置热点
            Method method = wifi.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            Toast.makeText(MainActivity.this, "连接账号："+apConfig.SSID+",密码是："+apConfig.preSharedKey, 5000).show();//提示信息接收方要连接的热点账号和密码
            //返回热点打开状态
            return (Boolean) method.invoke(wifi, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }
    private ServiceConnection sc=null;
    private void connection() {
        Intent intent = new Intent("com.deng.bindService");
        bindService(intent, sc, this.BIND_AUTO_CREATE);
    }
    @Override
    public void onDestroy()
    {
        unbindService(sc);
        super.onDestroy();
    }
}