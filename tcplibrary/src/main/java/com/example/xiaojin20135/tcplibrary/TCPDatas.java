package com.example.xiaojin20135.tcplibrary;

import java.util.ArrayList;

/**
 * Created by xiaojin20135 on 2017-11-23.
 */
public enum TCPDatas {
    TCP_DATAS;
    private ArrayList<byte[]> receivedDatas = new ArrayList<>();
    TCPDatas(){
        receivedDatas.clear();
    }
    /**
     * 增加到最后
     * @param data
     */
    public void addDatas(byte[] data){
        receivedDatas.add(data);
    }
    /**
     * 取出最旧的数据
     * @return
     */
    public byte[] getFirstData(){
        if(receivedDatas.size() > 0){
            byte[] data =  receivedDatas.get(0);
//            receivedDatas.remove(0);
            return data;
        }else{
            return null;
        }
    }
    /**
     * 移除数组的第一组数据
     * @return
     */
    public boolean removeFirstData(){
        if(receivedDatas.size() > 0){
            if(receivedDatas.remove(0) != null){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    /**
     * 清理现有数据
     */
    public void clearDatas(){
        receivedDatas.clear();
    }
    public ArrayList<byte[]> getReceivedDatas() {
        return receivedDatas;
    }
    public void setReceivedDatas(ArrayList<byte[]> receivedDatas) {
        this.receivedDatas = receivedDatas;
    }
}
