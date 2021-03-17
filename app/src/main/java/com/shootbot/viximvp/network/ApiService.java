package com.shootbot.viximvp.network;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import static com.shootbot.viximvp.utilities.Constants.SECRET_API_KEY;

public interface ApiService {

    @POST("push?api_key=" + SECRET_API_KEY)
    Call<String> sendRemoteMessage(
            @HeaderMap Map<String, String> headers,
            @Body String remoteBody
    );
}
