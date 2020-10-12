package com.oauth.sample.retrofit;

import com.oauth.sample.transverse.model.OAuthToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Mathias Seguy - Android2EE on 04/01/2017.
 */
public interface OAuthServerIntf {

    /**
     * The call to request a access token
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    Call<String> requestTokenForm(
            @Field("grant_type") String grant_type,

            @Field("client_id") String client_id,
            @Field("client_secret")String client_secret, // Is not relevant for Android application // need PKCE

            @Field("redirect_uri") String redirect_uri,

            @Field("code") String code,
            @Field("code_verifier") String codeVerifier
    );


    /**
     * The call to refresh a token
     */
    @FormUrlEncoded
    @POST("oauth2/v4/token")
    Call<OAuthToken> refreshTokenForm(
            @Field("refresh_token") String refresh_token,
            @Field("client_id") String client_id,
//            @Field("client_secret")String client_secret, //Is not relevant for Android application
            @Field("grant_type") String grant_type);

}
