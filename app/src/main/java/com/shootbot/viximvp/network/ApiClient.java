package com.shootbot.viximvp.network;

// import com.shootbot.viximvp.utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    // .baseUrl(Constants.PUSHY_API_URL)
                    .baseUrl("http://10.0.2.2:9080/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
