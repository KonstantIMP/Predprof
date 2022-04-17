package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import org.akred.predprof.databinding.ActivityMainBinding;
import org.akred.predprof.models.DataViewModel;
import org.akred.predprof.network.DataClient;
import org.akred.predprof.serialization.Anomaly;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataViewModel dataViewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        dataViewModel.getDataFromServer(this);

        dataViewModel.getAnomalies().observe(this, new Observer<List<Anomaly>>() {
            @Override
            public void onChanged(List<Anomaly> anomalies) {
                Log.e("Woof", String.valueOf(anomalies.size()));
            }
        });
    }
}