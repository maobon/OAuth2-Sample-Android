package com.oauth.sample;

import android.app.Application;
import android.util.Base64;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by Mathias Seguy - Android2EE on 04/01/2017.
 */
public class MyApplication extends Application {

    public static MyApplication instance;
    private String codeVerifier;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        codeVerifier = UUID.randomUUID().toString().replace("-", "");
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }
}
