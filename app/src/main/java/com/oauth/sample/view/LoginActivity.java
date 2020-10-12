package com.oauth.sample.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oauth.sample.MyApplication;
import com.oauth.sample.retrofit.OAuthServerIntf;
import com.oauth.sample.retrofit.RetrofitBuilder;
import com.oauth.sample.model.OAuthToken;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.Objects;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private static final String CLIENT_ID = "a2d0a6faaaf91bce7c23";
    private static final String CLIENT_SECRET = "3f052b9e96f4d4f33b6b10e1e5b901f7ac7670a5";

    private static final String REDIRECT_URI = "com.oauth.sample://oauth2redirect";
    private static final String REDIRECT_URI_ROOT = "com.oauth.sample";

    private static final String CODE = "code";
    private static final String ERROR_CODE = "error";

    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private String code;
    private String error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //You can be created either because your user start your application
        //        <intent-filter>
        //            <action android:name="android.intent.action.MAIN" />
        //            <category android:name="android.intent.category.LAUNCHER" />
        //        </intent-filter>

        //either because the callBack of the Authorisation is called :
        //        <intent-filter>
        //            <action android:name="android.intent.action.VIEW" />
        //            <category android:name="android.intent.category.BROWSABLE" />
        //            <category android:name="android.intent.category.DEFAULT" />
        //            <data android:scheme="com.renaultnissan.githubsample" />
        //        </intent-filter>

        //Manage the callback case:
        Uri data = getIntent().getData();
        if (data != null && !TextUtils.isEmpty(data.getScheme())) {
            if (REDIRECT_URI_ROOT.equals(data.getScheme())) {

                error = data.getQueryParameter(ERROR_CODE);
                if (!TextUtils.isEmpty(error)) {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onCreate: handle result of authorization with error :" + error);
                    finish();
                }

                // code 授权服务器返回的授权码
                code = data.getQueryParameter(CODE);
                Log.e(TAG, "onCreate: handle result of authorization with code :" + code);

                if (!TextUtils.isEmpty(code)) {
                    getTokenFormUrl();
                }
            }

        } else {
            //Manage the start application case:
            //If you don't have a token yet or if your token has expired , ask for it

            OAuthToken oauthToken = OAuthToken.Factory.create();
            if (oauthToken == null || oauthToken.getAccessToken() == null) {

                //first case==first token request
                if (oauthToken == null || oauthToken.getRefreshToken() == null) {
                    Log.e(TAG, "onCreate: Launching authorization (first step)");
                    //first step of OAUth: the authorization step
                    makeAuthorizationRequest();

                } else {
                    Log.e(TAG, "onCreate: refreshing the token :" + oauthToken);
                    //refresh token case
                    refreshTokenFormUrl(oauthToken);
                }
            }

            // else just launch your MainActivity
            else {
                Log.e(TAG, "onCreate: Token available, just launch MainActivity");
                startMainActivity(false);
            }
        }
    }

    /***********************************************************
     *  Managing Authotization and Token process
     *
     *  https://authorization-server.com/authorize?
     *   response_type=code
     *   &client_id=Un1yjfYovuZU6OChsGya6nIQ
     *   &redirect_uri=https://www.oauth.com/playground/authorization-code.html
     *   &scope=photo+offline_access
     *   &state=WZojwh9jxVry6hvZ
     **********************************************************/

    /**
     * Make the Authorization request
     */
    private void makeAuthorizationRequest() {
        String codeChallenge;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(MyApplication.instance.getCodeVerifier().getBytes());
            codeChallenge = Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_PADDING);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HttpUrl authorizeUrl =
                Objects.requireNonNull(HttpUrl.parse("https://github.com/login/oauth/authorize")).newBuilder()
                        .addQueryParameter("response_type", "code")
                        .addQueryParameter("client_id", CLIENT_ID)
                        .addQueryParameter("redirect_uri", REDIRECT_URI)
                        .addQueryParameter("scope", "user public_repo")

                        .addQueryParameter("state", "WZojwh9jxVry6hvZ") // 随机

                        .addQueryParameter("code_challenge", codeChallenge)
                        .addQueryParameter("code_challenge_method", "S256")

                        .build();

        Intent i = new Intent(Intent.ACTION_VIEW);

        Log.e(TAG, "the url is : " + authorizeUrl.url());
        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));

        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
        finish();
    }

    /**
     * Refresh the OAuth token
     */
    private void refreshTokenFormUrl(OAuthToken oauthToken) {

        OAuthServerIntf oAuthServer = RetrofitBuilder.getSimpleClient(this);

        Call<OAuthToken> refreshTokenFormCall = oAuthServer.refreshTokenForm(
                oauthToken.getRefreshToken(),
                CLIENT_ID,
                GRANT_TYPE_REFRESH_TOKEN
        );

        refreshTokenFormCall.enqueue(new Callback<OAuthToken>() {
            @Override
            public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
                Log.e(TAG, "===============New Call==========================");
                Log.e(TAG, "The call refreshTokenFormUrl succeed with code=" + response.code() + " and has body = " + response.body());
                //ok we have the token
                response.body().save();
                // startMainActivity(true);
            }

            @Override
            public void onFailure(Call<OAuthToken> call, Throwable t) {
                Log.e(TAG, "===============New Call==========================");
                Log.e(TAG, "The call refreshTokenFormCall failed", t);

            }
        });
    }

    /**
     * Retrieve the OAuth token
     * 与资源服务器通讯 获取AccessToken
     */
    private void getTokenFormUrl() {

        OAuthServerIntf oAuthServer = RetrofitBuilder.getSimpleClient(this);

        Call<String> getRequestTokenFormCall = oAuthServer.requestTokenForm(
                GRANT_TYPE_AUTHORIZATION_CODE,
                CLIENT_ID,
                CLIENT_SECRET,
                REDIRECT_URI,
                code,
                MyApplication.instance.getCodeVerifier()
        );

        getRequestTokenFormCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                Log.e(TAG, "===============New Call==========================");

                Log.v(TAG, "get accessToken response:" + response.body());

                String serverResponse = response.body();
                assert serverResponse != null;
                String accessToken = serverResponse.split("&")[0];

                Toast.makeText(LoginActivity.this, "accessToken=" + accessToken, Toast.LENGTH_SHORT).show();

                //Log.e(TAG, "The call getRequestTokenFormCall succeed with code=" + response.code() + " and has body = " + response.body());

                //ok we have the token
                // response.body().save();

                // startMainActivity(true);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "===============New Call==========================");
                Log.e(TAG, "The call getRequestTokenFormCall failed", t);

            }
        });
    }

    /***********************************************************
     *  Others business methods
     **********************************************************/

    /**
     * Start the next activity
     */
    private void startMainActivity(boolean newtask) {
        Intent i = new Intent(this, MainActivity.class);
        if (newtask) {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(i);
        //you can die so
        finish();
    }

}
