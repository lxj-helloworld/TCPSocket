package com.example.xiaojin20135.tcplibrary;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaojin20135 on 2017-11-22.
 */

public enum TCPUtil {
    TCP_UTIL;
    private static final String TAG = "TCPReceiver";
    /**
     * 接收数据的服务端Socket
     */
    ServerSocket serverSocket;//创建ServerSocket对象
    Socket clicksSocket;//连接通道，创建Socket对象
    InputStream inputstream;//创建输入数据流
    OutputStream outputStream;//创建输出数据流
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler;
    public static final int RECEIVESUCCESS = 100;
    public static final int SENDSUCCESS = 101;
    public static final int STARTSUCCESS = 102;
    public static final int STARTFAILED = 103;
    TCPUtil() {
    }
    public void init(int serverPort,Handler handler){
        this.handler = handler;
        initSocket(serverPort);
        initReceiverMessage();
    }
    private void initSocket(int serverPort) {
        try {
            // 创建一个ServerSocket对象，并设置监听端口
            serverSocket = new ServerSocket(serverPort);
            Log.i(TAG, "isBound=" + serverSocket.isBound() + "  isClosed=" + serverSocket.isClosed());
            sendMessage(STARTSUCCESS,"");//启动成功
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(STARTFAILED,"");//启动失败
        }
    }
    private void initReceiverMessage() {
        executorService.execute(runnableReceiverMsg);
    }

    private Runnable runnableReceiverMsg = new Runnable() {
        @Override
        public void run() {
            try {
                // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
                clicksSocket = serverSocket.accept();
                Log.d(TAG,"getPort = " + clicksSocket.getPort());
                Log.d(TAG," = " + clicksSocket.getRemoteSocketAddress());
                // 从Socket当中得到InputStream对象
                inputstream = clicksSocket.getInputStream();
                byte buffer[] = new byte[20];
                int temp = 0;
                // 从InputStream当中读取客户端所发送的数据
                while ((temp = inputstream.read(buffer)) != -1) {
                    Log.i(TAG, new String(buffer, 0, temp));  //打印接收到的信息
                    if(temp > 0){
                        StringBuilder receiveArr = new StringBuilder("");
                        for(int i=0;i<temp;i++){
                            receiveArr.append(Integer.toHexString(buffer[i]) + " ");
                        }
                        Log.d(TAG,"receiveArr = " + receiveArr);
                        TCPDatas.TCP_DATAS.addDatas(buffer);
                        sendMessage(RECEIVESUCCESS,"");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public boolean sendData(byte[] datas){
        boolean result = true;
        try{
            //获取输出流
            outputStream = clicksSocket.getOutputStream();
            //发送数据
            outputStream.write(datas);
            Log.d(TAG,"send");
            sendMessage(SENDSUCCESS,"");
        }
        catch (Exception e){
            result = false;
            e.printStackTrace();
        }
        return result;
    }
    private void sendMessage(int what,String obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        if(handler != null){
            handler.sendMessage(message);
        }
    }

}
