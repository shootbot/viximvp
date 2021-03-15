package com.shootbot.viximvp;

import android.app.Application;

import com.parse.Parse;
import com.shootbot.viximvp.utilities.PropertyReader;

import java.net.URL;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PropertyReader pr = new PropertyReader(getApplicationContext(), "app.properties");

        String parseServer = pr.getProperty("parse_server");

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("vixi-caller")
                .server("http://10.0.2.2:1337/parse/")
                .build()
        );
    }
}
