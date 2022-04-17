package org.akred.predprof.models;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.akred.predprof.R;
import org.akred.predprof.network.DataClient;
import org.akred.predprof.serialization.Anomaly;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataViewModel extends ViewModel {
    private MutableLiveData<List<Anomaly>> anomalies = new MutableLiveData<>(new ArrayList<>());

    public void getDataFromServer(Activity act) {
        Runnable requestTask = () -> {
            while (act != null) {
                DataClient client = new DataClient();

                List<Anomaly> tmp = client.getData();

                if (tmp != null) {
                    anomalies.postValue(tmp);
                    break;
                }

                act.runOnUiThread(() -> Toast.makeText(act, act.getApplicationContext().getResources().getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show());

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread requestThread = new Thread(requestTask);
        requestThread.start();
    }

    public LiveData<List<Anomaly>> getAnomalies() {
        return anomalies;
    }
}
