package com.oauth.sample.retrofit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.oauth.sample.retrofit.converters.StringConverterFactory;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Mathias Seguy - Android2EE on 05/01/2017.
 */
public class RetrofitBuilder {

    /***********************************************************
     *  Constants
     **********************************************************/
    /**
     * Root URL
     * (always ends with a /)
     */
    public static final String BASE_URL = "https://github.com/login/";

    /***********************************************************
     * Getting OAuthServerIntf instance using Retrofit creation
     **********************************************************/
    /**
     * A basic client to make unauthenticated calls
     *
     * @param ctx
     * @return OAuthServerIntf instance
     */
    public static OAuthServerIntf getSimpleClient(Context ctx) {

        // Using Default HttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .client(getSimpleOkHttpClient(ctx))
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        OAuthServerIntf webServer = retrofit.create(OAuthServerIntf.class);

        return webServer;
    }

    /**
     * An autenticated client to make authenticated calls
     * The token is automaticly added in the Header of the request
     *
     * @param ctx
     * @return OAuthServerIntf instance
     */
    public static OAuthServerIntf getOAuthClient(Context ctx) {
        // now it's using the cach
        // Using my HttpClient
        Retrofit raCustom = new Retrofit.Builder()
                .client(getOAuthOkHttpClient(ctx))
                .baseUrl(BASE_URL)
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        OAuthServerIntf webServer = raCustom.create(OAuthServerIntf.class);
        return webServer;
    }

    /***********************************************************
     * OkHttp Clients
     **********************************************************/

    /**
     * Return a simple OkHttpClient v:
     * have a cache
     * have a HttpLogger
     */
    @NonNull
    public static OkHttpClient getSimpleOkHttpClient(Context ctx) {
        // Define the OkHttp Client with its cache!
        // Assigning a CacheDirectory
        File myCacheDir = new File(ctx.getCacheDir(), "OkHttpCache");

        // You should create it...
        int cacheSize = 1024 * 1024;
        Cache cacheDir = new Cache(myCacheDir, cacheSize);

        HttpLoggingInterceptor httpLogInterceptor = new HttpLoggingInterceptor();
        httpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                //add a cache
                .cache(cacheDir)
                .addInterceptor(httpLogInterceptor)
                .build();
    }

    /**
     * Return a OAuth OkHttpClient v:
     * have a cache
     * have a HttpLogger
     * add automaticly the token in the header of each request because of the oAuthInterceptor
     *
     * @param ctx
     * @return
     */
    @NonNull
    public static OkHttpClient getOAuthOkHttpClient(Context ctx) {
        // Define the OkHttp Client with its cache!
        // Assigning a CacheDirectory
        File myCacheDir = new File(ctx.getCacheDir(), "OkHttpCache");

        // You should create it...
        int cacheSize = 1024 * 1024;
        Cache cacheDir = new Cache(myCacheDir, cacheSize);

        //Interceptor oAuthInterceptor = new OAuthInterceptor();
        HttpLoggingInterceptor httpLogInterceptor = new HttpLoggingInterceptor();
        httpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .cache(cacheDir)
                //.addInterceptor(oAuthInterceptor)
                .addInterceptor(httpLogInterceptor)
                .build();
    }
}
