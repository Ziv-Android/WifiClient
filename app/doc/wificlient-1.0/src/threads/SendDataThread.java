package com.ziv.wificlient.threads;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SendDataThread extends Thread{
	Socket socket;
	public SendDataThread(String address)
	{
		this.address=address;
	}String address;
	@Override
	public void run() {
		try {
			/*InetAddress localMachine=null;
	        try {
	                localMachine=InetAddress.getLocalHost();
	                //获取ip地址
	        } catch (UnknownHostException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        }
	        Log.d("aaa", localMachine.toString());*/
			socket=new Socket(address, 12345);//发送到本机下某个Ip的端口上
		} catch (UnknownHostException e) {
			Log.d("aaa", "SendDataThread.init() has UnknownHostException"+e.getMessage() );
		} catch (IOException e) {
			Log.d("aaa", "SendDataThread.init().IOException:"+e.getMessage());
		}
		if (socket!=null) {
			try {
				PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
				out.println("我是一只小小鸭，咿呀咿呀哟");  //发送数据
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	/*@Override
	protected String doInBackground(String... params) {
		if (socket!=null) {
			try {
				OutputStream os=socket.getOutputStream();
				String data="我是一只小小鸭，咿呀咿呀哟";
				os.write(data.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "yes";
	}
	*/
}
