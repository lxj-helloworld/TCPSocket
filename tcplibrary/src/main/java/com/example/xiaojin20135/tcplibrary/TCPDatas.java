package com.example.xiaojin20135.tcplibrary;

import java.util.ArrayList;

/**
 * Created by xiaojin20135 on 2017-11-23.
 */

public enum TCPDatas {
    TCP_DATAS;
    private ArrayList<byte[]> datas = new ArrayList<>();

    TCPDatas(){
        datas.clear();
    }

    /**
     * 增加到最后
     * @param data
     */
    public void addDatas(byte[] data){
        datas.add(data);
    }

    /**
     * 取出最旧的数据
     * @return
     */
    public byte[] getFirstData(){
        if(datas.size() > 0){
            byte[] data =  datas.get(0);
            datas.remove(0);
            return data;
        }else{
            return null;
        }
    }

    /**
     * 清理现有数据
     */
    public void clearDatas(){
        datas.clear();
    }


    public ArrayList<byte[]> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<byte[]> datas) {
        this.datas = datas;
    }
}
