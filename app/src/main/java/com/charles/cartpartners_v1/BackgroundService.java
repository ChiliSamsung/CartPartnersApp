package com.charles.cartpartners_v1;

import android.app.job.JobParameters;
import android.app.job.JobService;
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


        //returning true means it holds in wakelock. We want it to stop and then execute later in 15 mins
        return false;
    }


    //only called it the job has to be ended prematurely by the system itself, as opposed to being cancelled programmatically
    @Override
    public boolean onStopJob(JobParameters params) {

        Toast.makeText(this.getApplicationContext(), "job ended", Toast.LENGTH_SHORT).show();

        System.out.println("JOB ENDED");
        System.out.println("JOB ENDED");
        System.out.println("JOB ENDED");
        System.out.println("JOB ENDED");
        System.out.println("JOB ENDED");
        System.out.println("JOB ENDED");

        //returning true means to tell JobManager you'd like to reschedule the app based on the retry criteria provided at creation-time
        return true;
    }

}
