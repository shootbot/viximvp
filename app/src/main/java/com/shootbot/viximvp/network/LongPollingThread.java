package com.shootbot.viximvp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LongPollingThread implements Runnable {

    public LongPollingThread() {
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        String line = null;

        URL serverAddress = null;

        String lastModified = null;
        String etag = null;

        while (true) {
            try {
                serverAddress = new URL("http://localhost:9080/sub/ch1");
                // set up out communications stuff
                connection = null;

                // Set up the initial connection
                connection = (HttpURLConnection) serverAddress.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setReadTimeout(600000);

                if (lastModified != null) {
                    connection.setRequestProperty("If-Modified-Since", lastModified);
                }

                if (etag != null) {
                    connection.setRequestProperty("If-None-Match", etag);
                }

                connection.connect();

                // read the result from the server
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    sb.append(line + '\n');
                }


                lastModified = connection.getHeaderField("Last-Modified");
                etag = connection.getHeaderField("Etag");

                System.out.println(sb.toString());
                System.out.println(String.format("Last-Modified: %s Etag: %s", lastModified, etag));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // close the connection, set all objects to null
                connection.disconnect();
                rd = null;
                sb = null;
                connection = null;
            }
        }
    }
}
