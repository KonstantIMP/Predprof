package org.akred.predprof.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;

import org.akred.predprof.serialization.Anomaly;
import org.akred.predprof.serialization.Message;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class DataClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String ACCESS_TOKEN = "hjsmanxb";

    public static final String SERVER_ADDRESS = "https://dt.miet.ru/ppo_it_final";

    private OkHttpClient client = null;

    public DataClient() {
        client = new OkHttpClient.Builder()
                .build();
    }

    public List<Anomaly> getData() {
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
