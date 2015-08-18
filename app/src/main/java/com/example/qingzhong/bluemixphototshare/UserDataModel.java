package com.example.qingzhong.bluemixphototshare;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by qingzhong on 24/7/15.
 */
public class UserDataModel {

    public String userName;
    public Date date;
    public Bitmap photo;


    public UserDataModel(String name, Date date, Bitmap photoname){
        userName=name;
        this.date=date;
        this.photo=photoname;
    }
}
