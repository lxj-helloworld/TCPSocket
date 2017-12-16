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
    private static final String TAG = "TCPUtil";
    /**
     * 接收数据的服务端Socket
     */
    ServerSocket serverSocket;//创建ServerSocket对象
    Socket clicksSocket;//连接通道，创建Socket对象
    InputStream inputstream;//创建输入数据流
    OutputStream outputStream;//创建输出数据流
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler;
    private int byteLen = 100; //接受报文长度，默认为100
    private boolean showLog = true; //接收报文是否显示日志
    public static final int RECEIVESUCCESS = 100;
    public static final int SENDSUCCESS = 101;
    public static final int STARTSUCCESS = 102;
    public static final int STARTFAILED = 103;
    public static final int CONNECTED = 104;//客户端连接成功
    private boolean isRunging = true;

    TCPUtil() {
    }
    public void init(int serverPort,Handler handler,int byteLen,boolean showLog){
        if(handler != null){
            this.handler = handler;
        }
        initSocket(serverPort);
        initReceiverMessage();
        this.byteLen = byteLen;
        this.showLog = showLog;
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

    public boolean closeServer(){
        boolean result = true;
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
            if(inputstream != null){
                inputstream.close();
            }
            if(clicksSocket != null){
                clicksSocket.close();
            }

            isRunging = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }finally {
            serverSocket = null;
            clicksSocket = null;
        }
        return result;
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
                Log.d(TAG,"getRemoteSocketAddress = " + clicksSocket.getRemoteSocketAddress());
                sendMessage(CONNECTED,clicksSocket.getRemoteSocketAddress() + " connected." );
                // 从Socket当中得到InputStream对象
                inputstream = clicksSocket.getInputStream();
                byte buffer[] = new byte[byteLen];
                int temp = 0;
                // 从InputStream当中读取客户端所发送的数据
                int length = 0;
                while (isRunging && inputstream != null && (temp = inputstream.read(buffer)) != -1) {
                    Log.d(TAG,"in ");
                    if(temp > 0){
                        Log.d(TAG,"temp = " + temp);
                        if(showLog){
                            StringBuilder receiveArr = new StringBuilder("");
                            for(int i=0;i<temp;i++){
                                receiveArr.append(Integer.toHexString(buffer[i] & 0xFF) + " ");
                            }
                            Log.d(TAG,"receiveArr = " + receiveArr);
                        }
                        byte[] tempArr = new byte[temp];
                        System.arraycopy(buffer,0,tempArr,0,temp);
                        TCPDatas.TCP_DATAS.addDatas(tempArr);
//                        sendMessage(RECEIVESUCCESS,"");
                    }else{
                        Log.d(TAG,"未接收到数据.");
                    }
                    Thread.sleep(1000);
                }
                Log.d(TAG,"end.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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
