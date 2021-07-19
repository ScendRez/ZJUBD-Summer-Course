package com.bytedance.practice5.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import static com.bytedance.practice5.Constants.BASE_URL;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;

    //head请求内容
    private static String content = "HEAD / HTTP/1.1\r\nHost:www.zju.edu.cn\r\n\r\n";


    @Override
    public void run() {
        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示

        try{
            Log.i("socccc","start");
            Socket socket = new Socket("www.zju.edu.cn",80);
            socket.setSoTimeout(10000);
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            double n=1;
            byte[] data= new byte[1024*5];
            int len=-1;
            if(socket.isConnected())
            {
                bos.write(content.getBytes(StandardCharsets.UTF_8));
                bos.flush();
                int receiveLen=bis.read(data);
                String receive= new String(data,0,receiveLen);
                Log.i("socccc",receive);
                callback.onResponse(receive);
            }
            else
                Log.i("socccc","not connected");
            bos.flush();
            bos.close();
            socket.close();
        }
        catch (Exception e)
        {
            Log.i("socccc",e.toString());
            e.printStackTrace();
        }
    }
}