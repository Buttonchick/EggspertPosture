package com.example.eggspertposture;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class MyWorker extends Worker {
    private SensorEventListener bakugan;
    private Sensor s;
    private SensorManager sm;
    private int ang;






    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context,workerParams);
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) s = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }


    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Result doWork() {
        backgroundSensor();
        while(true){
            sm.registerListener(bakugan, s, SensorManager.SENSOR_DELAY_UI);
            showNotification("Title", "Message" + ang, ang);
        }
    }




    private void backgroundSensor(){
        bakugan = new SensorEventListener(){

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix);

                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }
                ang = (int) orientations[1];

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    private void showNotification(String title, String message,int ang) {
        int type;
        if(-40 <= ang & ang < -10) type = R.drawable.ic_notificator_sutuliy; //sutuliy
        else if(-60 <= ang & ang < -40) type = R.drawable.ic_notificator_pochti_rowni; //pochti rovnay spina
        else if(-110 <= ang & ang < -60) type = R.drawable.ic_notificator_rowni; //rovnay spina
        else type = R.drawable.ic_notificator_else; //skoree vsego telefon lejit

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // If on Oreo then notification required a notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setSmallIcon(type);
        notificationManager.notify(1, notification.build());

    }

}
