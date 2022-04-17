package org.akred.predprof.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

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
}