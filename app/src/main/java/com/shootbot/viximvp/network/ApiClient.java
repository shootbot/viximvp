package com.shootbot.viximvp.network;


import android.content.Context;

import com.shootbot.viximvp.utilities.PropertyReader;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            PropertyReader pr = new PropertyReader(context, "app.properties");
            String pubServer = pr.getProperty("publication_server");

            retrofit = new Retrofit.Builder()
                    .baseUrl(pubServer)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
