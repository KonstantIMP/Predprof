package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.davemorrissey.labs.subscaleview.ImageSource;

import org.akred.predprof.R;
import org.akred.predprof.databinding.ActivityMainBinding;
import org.akred.predprof.models.DataViewModel;
import org.akred.predprof.serialization.Anomaly;
import org.akred.predprof.serialization.Radio;
import org.akred.predprof.serialization.Swan;

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