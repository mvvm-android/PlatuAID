package com.mcdenny.coronavirusapp.view.ui.splashscreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.mcdenny.coronavirusapp.R;
import com.mcdenny.coronavirusapp.data.workers.GlobalCoronaWorker;
import com.mcdenny.coronavirusapp.data.workers.HospitalWorker;
import com.mcdenny.coronavirusapp.utils.Config;
import com.mcdenny.coronavirusapp.view.ui.HomeActivity;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
    private WorkManager globalWorkManager;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        builder = new AlertDialog.Builder(this);

        globalWorkManager = WorkManager.getInstance(this);

        if (Config.isNetworkAvailable(this)) {
            //global stats periodic work request
            PeriodicWorkRequest globalStatsRequest = new PeriodicWorkRequest.Builder(GlobalCoronaWorker.class, 15, TimeUnit.MINUTES)
                    .build();
            globalWorkManager.enqueue(globalStatsRequest);


            //hospitals work request
            OneTimeWorkRequest hospitalRequest = new OneTimeWorkRequest.Builder(HospitalWorker.class).build();
            globalWorkManager.enqueue(hospitalRequest);

            new Handler().postDelayed(() -> {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }, 4000);

        } else {
            builder.setMessage("No internet connection!")
                    .setPositiveButton("Try Again", (dialog, which) -> startActivity(new Intent(this, SplashActivity.class)));
            builder.setCancelable(false);
            builder.show();
        }
    }
}
