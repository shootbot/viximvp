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
        String lastModified = null;
        String etag = null;

        while (true) {
            try {
                URL serverAddress = new URL("http://10.0.2.2:9080/sub/ch1");
                connection = null;

                connection = (HttpURLConnection) serverAddress.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(600000);

                if (lastModified != null) {
                    connection.setRequestProperty("If-Modified-Since", lastModified);
                }

                if (etag != null) {
                    connection.setRequestProperty("If-None-Match", etag);
                }

                connection.connect();

                System.out.println("response code: " + connection.getResponseCode());
                // System.out.println("response message: " + connection.getResponseMessage());
                // Map<String, List<String>> map = connection.getHeaderFields();
                // for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                //     String k = entry.getKey();
                //     List<String> v = entry.getValue();
                //     System.out.println("key=" + k + ", value=" + v.toString());
                // }

                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line + '\n');
                }

                lastModified = connection.getHeaderField("Last-Modified");
                etag = connection.getHeaderField("Etag");

                System.out.println("message: " + sb.toString());
                System.out.println(String.format("Last-Modified: %s Etag: %s", lastModified, etag));

            } catch (ProtocolException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            }
        }
    }
}
