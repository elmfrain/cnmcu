package com.elmfer.cnmcu.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HTTPSFetcher {
    private FetcherWorker worker;
    private byte[] contentData = null;
    private String stringData = null;

    public HTTPSFetcher(String url) {
        try {
            worker = new FetcherWorker(this, new URL(url));
            worker.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean fetchComplete() {
        return worker.complete;
    }

    public String stringContent() {
        if (worker.complete) {
            if (stringData == null)
                stringData = new String(contentData, StandardCharsets.UTF_8);
            return stringData;
        }

        return worker.status;
    }
    
    public JsonObject jsonContent() {
        if(worker.complete) {
            try {
                return JsonParser.parseString(stringContent()).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return new JsonObject();
    }

    public byte[] byteContent() {
        return contentData;
    }

    public boolean hasFailed() {
        return worker.statusCode != HttpURLConnection.HTTP_OK;
    }

    public int statusCode() {
        return worker.statusCode;
    }

    /** Internal fetcher worker. **/
    public static class FetcherWorker extends Thread {
        URL url;
        boolean complete = false;
        int statusCode = 200;
        String status = "Connecting";
        HTTPSFetcher parent;

        FetcherWorker(HTTPSFetcher parent, URL url) {
            this.url = url;
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                status = "Connected, Recieving Data";
                connection.setRequestMethod("GET");

                InputStream is = connection.getInputStream();
                statusCode = connection.getResponseCode();
                status = String.format("Recieved Data: %d", statusCode);

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                byte[] chunk = new byte[4096];
                int bytesRead = 0;
                while (0 < (bytesRead = is.read(chunk))) {
                    os.write(chunk, 0, bytesRead);
                }

                parent.contentData = os.toByteArray();
                is.read(parent.contentData);

                is.close();
                connection.disconnect();

                complete = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                statusCode = 0;
                status = String.format("Fetch Failed: %s", e.toString());
            }
        }
    }
}