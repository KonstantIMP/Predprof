package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.davemorrissey.labs.subscaleview.ImageSource;

import org.akred.predprof.R;
import org.akred.predprof.databinding.ActivityMainBinding;
import org.akred.predprof.models.DataViewModel;
import org.akred.predprof.serialization.Anomaly;
import org.akred.predprof.serialization.Radio;
import org.akred.predprof.serialization.Swan;
import org.akred.predprof.serialization.Shtuka;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataViewModel dataViewModel;
    private ActivityMainBinding binding;
    private Double startx = null, starty = null, endx = null, endy = null;
    private Bitmap imgRes = null;
    private Shtuka shtuka;
    private ArrayList<String> anomalies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        dataViewModel.getDataFromServer(this);

        dataViewModel.getAnomalies().observe(this, anomalies -> {
            if (anomalies.size() == 0) return;
            updateImgRes(anomalies);
        });

        imgRes = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        binding.imageView.setImage(ImageSource.bitmap(imgRes));

        binding.start.setOnClickListener(view -> showStartDialog());

        binding.end.setOnClickListener(view -> showEndDialog());
    }

    private void updateImgRes(List<Radio> anomalies) {
        Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        tmp = tmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tmp);
        Bitmap anomalyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radio);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.argb(100, 255, 0, 0));

        for(Radio anomaly : anomalies) {
            for (Swan swan : anomaly.swans) {
                canvas.drawCircle(
                        Double.valueOf(Math.max(0, 50 * anomaly.coords.get(0))).floatValue(),
                        Double.valueOf(Math.max(0, 50 * anomaly.coords.get(1))).floatValue(),
                        Double.valueOf(swan.rate * 50).floatValue(),
                        p
                );
            }

            canvas.drawBitmap(anomalyBitmap,
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(0) - 32)).floatValue(),
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(1) - 32)).floatValue(),
                    null);
        }

        if (startx != null && starty != null && endx != null && endy != null) {
            p.setColor(Color.argb(255, 255, 255, 255));
            p.setStrokeWidth(6.5f);
            canvas.drawLine(startx.floatValue() * 50, starty.floatValue() * 50, endx.floatValue() * 50, endy.floatValue() * 50, p);
        }
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showShtukaDalog();

            }
        });

        binding.createAnomaly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCreateAnomalyDialog();

            }
        });


    }

    private void showCreateAnomalyDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.create_anomaly, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Создание фномалии");

        final Button create = dialogView.findViewById(R.id.create);
        final Button close = dialogView.findViewById(R.id.close);
        final EditText name = dialogView.findViewById(R.id.name);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sname = name.getText().toString().trim();

                if(!TextUtils.isEmpty(sname)){

                    anomalies.add(sname);
                    b.dismiss();

                } else {

                    name.setError("ведите название аномалии");

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

    private void showShtukaDalog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.shtuka_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Введите штуку");

        final Button add = dialogView.findViewById(R.id.add);
        final Button close = dialogView.findViewById(R.id.close);
        final Spinner anomaly = dialogView.findViewById(R.id.anomaly);
        final Spinner sensor = dialogView.findViewById(R.id.sensor);
        final EditText rank = dialogView.findViewById(R.id.rank);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        ArrayList<String> sensors = new ArrayList<>();
        sensors.add("cccc");
        sensors.add("ddd");

        ArrayAdapter<String> sensor_adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sensors);
        sensor.setAdapter(sensor_adapter);
        anomalies.add("aaaa");
        anomalies.add("bbb");

        ArrayAdapter<String> anomaly_adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, anomalies);
        anomaly.setAdapter(anomaly_adapter);




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String srank = rank.getText().toString().trim();

            if (!TextUtils.isEmpty(srank)){

                if (Integer.parseInt(srank) > 0){

                    Log.d("TAGG", "Create shtuka");
                    b.dismiss();

                } else {

                    rank.setError("Числа не входят в диапазон");

                }

            } else {

                rank.setError("Введите ранк");

            }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();
                updateImgRes(dataViewModel.getAnomalies().getValue());
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();
                updateImgRes(dataViewModel.getAnomalies().getValue());
            }
        });

    }

}