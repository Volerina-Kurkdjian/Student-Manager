package com.example.studentmanager.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class HttpManager implements Callable<String> {

    private URL url;
    private HttpURLConnection connection;

    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private final String urlAddress;

    public HttpManager(String urlAddress){
        this.urlAddress=urlAddress;
    }


    @Override
    public String call() throws Exception {
        try{
            return getResultFromHttp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection();
        }


        return null;
    }

    public String getResultFromHttp() throws IOException {
        url=new URL(urlAddress);
        connection= (HttpURLConnection) url.openConnection();
        inputStream=connection.getInputStream();
        inputStreamReader=new InputStreamReader(inputStream);
        bufferedReader=new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder=new StringBuilder();
        String line;
        while((line=bufferedReader.readLine())!=null)
        {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }


    private void closeConnection()
    {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
    }















}
