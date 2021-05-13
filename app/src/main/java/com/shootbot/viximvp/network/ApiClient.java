package com.shootbot.viximvp.network;

import com.shootbot.viximvp.utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    private static Retrofit retrofit = null;
//    private static Retrofit pub = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.PUSHY_API_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

//    public static Retrofit getPub() {
//        if (pub == null) {
//            pub = new Retrofit.Builder()
//                    .baseUrl("http://10.0.2.2:9080/pub/?id=ch1")
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .build();
//        }
//        return pub;
//    }
}
