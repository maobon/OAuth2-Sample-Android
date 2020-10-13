package com.oauth.sample.retrofit.interceptors;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.oauth.sample.MyApplication;
import com.oauth.sample.model.OAuthToken;
import com.oauth.sample.view.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mathias Seguy - Android2EE on 05/01/2017.
 * This class aims to add automaticly in the Header the OAuth token
 */
public class OAuthInterceptor implements Interceptor {

    private static final String TAG = "OAuthInterceptor";

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        // find the token
        OAuthToken oauthToken = OAuthToken.Factory.create();

        assert oauthToken != null;
        String accessToken = oauthToken.getAccessToken();
        String accessTokenType = oauthToken.getTokenType();

        //add it to the request
        Request.Builder builder = chain.request().newBuilder();
        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(accessTokenType)) {
            Log.e(TAG, "In the interceptor adding the header authorization with : " + accessTokenType + " " + accessToken);
            builder.header("Authorization", accessTokenType + " " + accessToken);

        } else {
            Log.e(TAG, "In the interceptor there is a fuck with : " + accessTokenType + " " + accessToken);
            //you should launch the loginActivity to fix that:
            Intent i = new Intent(MyApplication.instance, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            MyApplication.instance.startActivity(i);
        }

        //proceed to the call
        return chain.proceed(builder.build());
    }
}
