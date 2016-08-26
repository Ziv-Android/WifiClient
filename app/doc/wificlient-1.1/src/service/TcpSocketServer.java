package com.ziv.wificlient.service;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sensetime on 16-8-25.
 */
public class TcpSocketServer {
    private ServerSocket ss =null;
    private Socket s = null;
    private OutputStream out = null;
    private InputStream in  = null;
    private String receiveBuffer = null;
    public TcpSocketServer(int port){
        //新建ServerSocket对象,端口为传进来的port;
        try {
            //ss= new ServerSocket(1821);
            Log.e("ziv","TcpSocketServer statue = no");
            ss = new ServerSocket(port);
            Log.e("ziv","TcpSocketServer statue = yes");
            s = ss.accept();
            out = s.getOutputStream();
            in  = s.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String buffer)throws Exception{
        //新建Socket通信对象，接受客户端发来的请求accept();
        //Socket s = ss.accept();
        //创建输入流对象InputStream
        InputStream bais = new ByteArrayInputStream(buffer.getBytes());
        byte[] buff = new byte[1024];
        bais.read(buff);
        out.write(buff);
        out.flush();
    }
    public String getMessage(){
        byte[] temp = new byte[1024];
        try{
            if(in.read(temp) > 0)
            {
                return receiveBuffer = new String(temp).trim();
            }} catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String receiveMessage(){
        return null;
    }
}