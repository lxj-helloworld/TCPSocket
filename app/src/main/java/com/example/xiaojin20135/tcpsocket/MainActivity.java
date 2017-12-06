package com.example.xiaojin20135.tcpsocket;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.xiaojin20135.tcplibrary.TCPDatas;
import com.example.xiaojin20135.tcplibrary.TCPUtil;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private Button start_btn;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn = (Button)findViewById(R.id.start_btn);
        initHandler();
    }
    private void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == TCPUtil.RECEIVESUCCESS){
                    byte[] temp = TCPDatas.TCP_DATAS.getFirstData();
                    for(int i=0;i<10;i++){
                        boolean sendResult = TCPUtil.TCP_UTIL.sendData(temp);
                        if(sendResult){
                            TCPDatas.TCP_DATAS.removeFirstData();
                        }
                    }
                }else if(msg.what == TCPUtil.STARTSUCCESS){
                    Log.d(TAG,"TCP server 启动成功");
                }else if(msg.what == TCPUtil.STARTFAILED){
                    Log.d(TAG,"TCP server 启动失败");
                }
            }
        };
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_btn){
            TCPUtil.TCP_UTIL.init(8080,handler,100,true);
        }
    }
    /**
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
}
