package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.davemorrissey.labs.subscaleview.ImageSource;

import org.akred.predprof.R;
import org.akred.predprof.databinding.ActivityMainBinding;
import org.akred.predprof.models.DataViewModel;
import org.akred.predprof.serialization.Anomaly;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataViewModel dataViewModel;
    private ActivityMainBinding binding;
    private double startx, starty, endx, endy;
    private Bitmap imgRes = null;

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

        //imgRes = BitmapFactory

        binding.imageView.setImage(ImageSource.resource(R.drawable.map));

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showStartDialog();

            }
        });

        binding.end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEndDialog();

            }
        });


    }

    private void showEndDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.coordinates_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Введите конец");

        final Button set = dialogView.findViewById(R.id.set);
        final Button close = dialogView.findViewById(R.id.close);
        final EditText x = dialogView.findViewById(R.id.x);
        final EditText y = dialogView.findViewById(R.id.y);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(x.getText().toString())){

                    if(!TextUtils.isEmpty(y.getText().toString())){

                        endx = Double.parseDouble(x.getText().toString());
                        endy = Double.parseDouble(y.getText().toString());
                        b.dismiss();

                    } else{

                        y.setError("Введите Y");
                        y.requestFocus();

                    }

                } else {

                    x.setError("Введите X");
                    x.requestFocus();

                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

    }

    private void showStartDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.coordinates_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Введите начало");

        final Button set = dialogView.findViewById(R.id.set);
        final Button close = dialogView.findViewById(R.id.close);
        final EditText x = dialogView.findViewById(R.id.x);
        final EditText y = dialogView.findViewById(R.id.y);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(x.getText().toString())){

                    if(!TextUtils.isEmpty(y.getText().toString())){

                        startx = Double.parseDouble(x.getText().toString());
                        starty = Double.parseDouble(y.getText().toString());
                        b.dismiss();

                    } else{

                        y.setError("Введите Y");
                        y.requestFocus();

                    }

                } else {

                    x.setError("Введите X");
                    x.requestFocus();

                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

    }

}