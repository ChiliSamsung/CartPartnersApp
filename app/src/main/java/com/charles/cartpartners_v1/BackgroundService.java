package com.charles.cartpartners_v1;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.widget.Toast;

public class BackgroundService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {

        //TODO: here can update and pull information from the server, and maybe make a notification

        Toast.makeText(this.getApplicationContext(), "job started", Toast.LENGTH_SHORT).show();
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Toast.makeText(this.getApplicationContext(), "job ended", Toast.LENGTH_SHORT).show();


        return true;
    }

}
