package com.charles.cartpartners_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BackgroundService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "job started", Toast.LENGTH_SHORT).show();
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("BITCHES");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");
        System.out.println("JOB STARTED");

    }

}
