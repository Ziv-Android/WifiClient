package com.ziv.wificlient.threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenThread extends Thread {
	ServerSocket socket=null;
	public ListenThread(int port, Handler handler)
	{
		try {
			port=12345;
			socket=new ServerSocket(port);//监听本机的12345端口
			this.handler=handler;
		} catch (IOException e) {
			Log.d("aaa", "ListenThread ServerSocket init() has exception");
		}

	}
	Handler handler;
	@Override
	public void run() {
		while (true) {
			try {

				Message msg=new Message();
				 /*msg.what=2;
				 msg.obj="ServerSocket 正在等待数据传输";
				 this.handler.sendMessage(msg);*/
				final Socket soc=socket.accept();//等待消息
				InputStream is=soc.getInputStream();//获取消息
				/*msg.what=2;
				 msg.obj="ServerSocket 传输成功";
				 this.handler.sendMessage(msg);*/
				if (is!=null) {
					BufferedReader in=  new BufferedReader(new InputStreamReader(is,"UTF-8"));
					PrintWriter out = new PrintWriter(soc.getOutputStream());//输出信息
					String str="";
					str=in.readLine();

					msg.what=1;
					msg.obj=str;
					this.handler.sendMessage(msg);
					soc.close();
				}else
				{
					Log.d("aaa", "没有接收到数据");
				}

			} catch (IOException e) {
				Log.d("aaa", "ListenThread.run() -->final Socket soc=socket.accept();has exception");
			}

		}
	}
	/*public String getLocalIpAddress() {  
        String ipaddress="";
        
    try {  
        for (Enumeration<NetworkInterface> en = NetworkInterface  
                .getNetworkInterfaces(); en.hasMoreElements();) {  
            NetworkInterface intf = en.nextElement();  
            for (Enumeration<InetAddress> enumIpAddr = intf  
                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                InetAddress inetAddress = enumIpAddr.nextElement();  
                if (!inetAddress.isLoopbackAddress()) {  
                        ipaddress= inetAddress.getHostAddress().toString();  
                }  
            }  
        }  
    } catch (SocketException ex) {  
        Log.e("WifiPreference IpAddress", ex.toString());  
    }  
    return ipaddress; 
    }*/
}
