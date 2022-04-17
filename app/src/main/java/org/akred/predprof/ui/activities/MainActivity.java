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
import android.util.Pair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private DataViewModel dataViewModel;
    private ActivityMainBinding binding;
    private Double startx = null, starty = null, endx = null, endy = null;
    private Bitmap imgRes = null;
    private ArrayList<Shtuka> shtukas = new ArrayList<>();
    private Set<String> anomalies = new TreeSet<>();

    private static final Double EPSILON = 0.5;

    private HashMap<String, Pair<Double, Double>> td = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        dataViewModel.getDataFromServer(this);

        dataViewModel.getAnomalies().observe(this, anomaliess -> {
            if (anomaliess.size() == 0) return;

            Set<String> ans = new TreeSet<>();

            for(Radio r: anomaliess) {
                for (Swan sw: r.swans) {
                    ans.add(sw.id);
                }
            }

            anomalies.addAll(ans);

            updateImgRes(anomaliess);
        });

        imgRes = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        binding.imageView.setImage(ImageSource.bitmap(imgRes));

        binding.start.setOnClickListener(view -> showStartDialog());
        binding.end.setOnClickListener(view -> showEndDialog());
        binding.add.setOnClickListener(view -> showShtukaDalog());
        binding.createAnomaly.setOnClickListener(view -> showCreateAnomalyDialog());

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
                        Double.valueOf(Math.sqrt(swan.rate) * 50).floatValue(),
                        p
                );
            }

            canvas.drawBitmap(anomalyBitmap,
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(0) - 32)).floatValue(),
                    Double.valueOf(Math.max(0, 50 * anomaly.coords.get(1) - 32)).floatValue(),
                    null);
        }

        HashMap<String, List<Pair<Double, Pair<Double, Double>>>> temp = new HashMap<>();

        for (Radio radio: anomalies) {
            for (Swan swan: radio.swans) {
                if (temp.containsKey(swan.id) == false) temp.put(swan.id, new ArrayList<>());
                temp.get(swan.id).add(new Pair<>(swan.rate, new Pair<>(radio.coords.get(0), radio.coords.get(1))));
            }
        }

        for(Shtuka sh: shtukas) {
            List<Radio> rs = anomalies.stream().filter(x -> String.valueOf(x.id).equals(sh.sensor)).collect(Collectors.toList());
            if (rs.size() == 0) continue;

            if (temp.containsKey(sh.anomaly) == false) temp.put(sh.anomaly, new ArrayList<>());
            temp.get(sh.anomaly).add(new Pair<>(sh.rank, new Pair<>(rs.get(0).coords.get(0), rs.get(0).coords.get(1))));
        }

        HashMap<String, Pair<Double, Double>> dots = new HashMap<>();

        for (String k: temp.keySet()) {
            if (temp.get(k).size() < 3) continue;

            for (float tx = 20.0f; tx >= 0.0f; tx -= 0.5f) {
                for (float ty = 15.0f; ty >= 0.0f; ty -= 0.5f) {
                    Double r1 = temp.get(k).get(0).first * (Math.pow(ty - temp.get(k).get(0).second.second, 2) + Math.pow(tx - temp.get(k).get(0).second.first, 2));
                    Double r2 = temp.get(k).get(1).first * (Math.pow(ty - temp.get(k).get(0).second.second, 2) + Math.pow(tx - temp.get(k).get(0).second.first, 2));
                    Double r3 = temp.get(k).get(2).first * (Math.pow(ty - temp.get(k).get(0).second.second, 2) + Math.pow(tx - temp.get(k).get(0).second.first, 2));

                    if (r3 - r2 < EPSILON && r3 - r1 < EPSILON) {
                        dots.put(k, new Pair<>(new Double(tx), new Double(ty)));
                        break;
                    }
                }
                if (dots.containsKey(k)) break;
            }
        }

        td = dots;

        class AnRoma {
            public Double rank, x, y;

            public AnRoma(Double a, Double b, Double c) {
                rank = a; x = b; y = c;
            }
        }

        ArrayList<AnRoma> ar = new ArrayList<>();

        for (String k: dots.keySet()) {
            ar.add(new AnRoma(temp.get(k).get(0).first, dots.get(k).first, dots.get(k).second);
        }

        Bitmap ab = BitmapFactory.decodeResource(getResources(), R.drawable.anomaly).copy(Bitmap.Config.ARGB_8888, true);

        for (Pair<Double,Double> dts: dots.values()) {
            canvas.drawBitmap(ab,
                    Double.valueOf(Math.max(0, 50 * dts.first - 32)).floatValue(),
                    Double.valueOf(Math.max(0, 50 * dts.second - 32)).floatValue(),
                    null);
        }

        if (startx != null && starty != null && endx != null && endy != null) {
            p.setColor(Color.argb(255, 255, 255, 255));
            p.setStrokeWidth(6.5f);
            canvas.drawLine(startx.floatValue() * 50, starty.floatValue() * 50, endx.floatValue() * 50, endy.floatValue() * 50, p);
        }

        imgRes = tmp;
        binding.imageView.setImage(ImageSource.bitmap(imgRes));
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

        close.setOnClickListener(view -> b.dismiss());


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

        for (Radio radio: dataViewModel.getAnomalies().getValue()) {
            sensors.add(String.valueOf(radio.id));
        }

        ArrayAdapter<String> sensor_adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sensors);
        sensor.setAdapter(sensor_adapter);

        ArrayAdapter<String> anomaly_adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, anomalies.stream().collect(Collectors.toList()));
        anomaly.setAdapter(anomaly_adapter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String srank = rank.getText().toString().trim();

                if (!TextUtils.isEmpty(srank)){

                    if (Integer.parseInt(srank) > 0){

                        shtukas.add(new Shtuka(sensor.getSelectedItem().toString(), anomaly.getSelectedItem().toString(), Integer.parseInt(srank)));
                        b.dismiss();

                    } else {

                        rank.setError("Число не входит в диапозон");

                    }

                } else {

                    rank.setError("Введите ранк");

                }

            }
        });


        close.setOnClickListener(view -> b.dismiss());
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