package org.akred.predprof.network;

import com.google.gson.GsonBuilder;

import org.akred.predprof.serialization.Message;
import org.akred.predprof.serialization.Radio;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataClient {
    public static final String SERVER_ADDRESS = "https://dt.miet.ru/ppo_it_final";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String ACCESS_TOKEN = "hjsmanxb";
    private OkHttpClient client = null;

    public DataClient() {
        client = new OkHttpClient.Builder()
                .build();
    }

    public List<Radio> getData() {
        Request request = new Request.Builder()
                .url(SERVER_ADDRESS)
                .addHeader("X-Auth-Token", ACCESS_TOKEN)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();

            Message data = new GsonBuilder()
                    .create()
                    .fromJson(response.body().string(), Message.class);

            return data.message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
