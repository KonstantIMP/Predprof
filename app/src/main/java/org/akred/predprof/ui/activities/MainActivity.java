package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
import org.akred.predprof.network.DataClient;
import org.akred.predprof.serialization.Anomaly;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataViewModel dataViewModel;
    private ActivityMainBinding binding;
    private Double startx = null, starty = null, endx = null, endy = null;
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
                if (anomalies.size() == 0) return;
                updateImgRes(anomalies);
            }
        });

        imgRes = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        binding.imageView.setImage(ImageSource.bitmap(imgRes));

        binding.start.setOnClickListener(view -> showStartDialog());

        binding.end.setOnClickListener(view -> showEndDialog());
    }

    private void updateImgRes(List<Anomaly> anomalies) {
        Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        tmp = tmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tmp);
        Bitmap anomalyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anomaly);

        for(Anomaly anomaly : anomalies) {
            canvas.drawBitmap(anomalyBitmap,
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(0) - 32)).floatValue(),
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(1) - 32)).floatValue(),
                    null);
        }

        imgRes = tmp;
        binding.imageView.setImage(ImageSource.bitmap(imgRes));
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

        set.setOnClickListener(view -> {

                String sx = x.getText().toString().trim(), sy = y.getText().toString().trim();

                if(!TextUtils.isEmpty(sx)){

                    if(!TextUtils.isEmpty(sy)){

                        if (Double.parseDouble(sx) > 0.0 && Double.parseDouble(sx) < 40.0 && Double.parseDouble(sy) > 0.0 && Double.parseDouble(sy) < 30.0){

                            endx = Double.parseDouble(sx);
                            endy = Double.parseDouble(sy);
                            b.dismiss();

                        } else {

                            x.setError("Числа не входят в диапазон");
                            x.requestFocus();

                        }

                    } else{

                    y.setError("Введите Y");
                    y.requestFocus();

                    }

            } else {

                x.setError("Введите X");
                x.requestFocus();

            }
        });

        close.setOnClickListener(view -> b.dismiss());

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

        set.setOnClickListener(view -> {

                String sx = x.getText().toString().trim(), sy = y.getText().toString().trim();

                if(!TextUtils.isEmpty(sx)){

                    if(!TextUtils.isEmpty(sy)){

                        if (Double.parseDouble(sx) > 0.0 && Double.parseDouble(sx) < 40.0 && Double.parseDouble(sy) > 0.0 && Double.parseDouble(sy) < 30.0){

                            startx = Double.parseDouble(sx);
                            starty = Double.parseDouble(sy);
                            b.dismiss();

                        }  else {

                            x.setError("Числа не входят в диапазон");
                            x.requestFocus();

                        }


                    } else{

                        y.setError("Введите Y");
                        y.requestFocus();

                    }

                } else {

                    x.setError("Введите X");
                    x.requestFocus();

                }

        });

        close.setOnClickListener(view -> b.dismiss());

    }

}