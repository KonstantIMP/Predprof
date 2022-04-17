package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.akred.predprof.databinding.ActivityMainBinding;
import org.akred.predprof.network.DataClient;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataClient cl = new DataClient();
                cl.getData();
            }
        });
        thread.start();
    }
}