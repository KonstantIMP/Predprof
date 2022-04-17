package org.akred.predprof.models;

import android.app.Activity;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.akred.predprof.R;
import org.akred.predprof.network.DataClient;
import org.akred.predprof.serialization.Anomaly;
import org.akred.predprof.serialization.Radio;
import org.akred.predprof.serialization.Swan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataViewModel extends ViewModel {
    private MutableLiveData<List<Radio>> anomalies = new MutableLiveData<>(new ArrayList<>());

    private MutableLiveData<HashMap<String, Anomaly>> ranks = new MutableLiveData<>(new HashMap<>());

    public void getDataFromServer(Activity act) {
        Runnable requestTask = () -> {
            while (act != null) {
                DataClient client = new DataClient();

                List<Radio> tmp = client.getData();

                if (tmp != null) {
                    anomalies.postValue(tmp);

                    HashMap<String, Anomaly> ttt = new HashMap<>();

                    for (Radio r: tmp) {

                        for(Swan sp: r.swans) {
                            if (ttt.containsKey(sp.id) == false) ttt.put(sp.id, new Anomaly(sp.id));
                            Anomaly.AnomalyData ad = new Anomaly.AnomalyData();
                            ad.x = r.coords.get(0); ad.y = r.coords.get(1);
                            ad.rank = sp.rate;
                            ttt.get(sp.id).ranks.add(ad);
                        }

                    }

                    ranks.postValue(ttt);

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

    public LiveData<List<Radio>> getAnomalies() {
        return anomalies;
    }

    public LiveData<HashMap<String, Anomaly>> getRanks() {
        return ranks;
    }
}
