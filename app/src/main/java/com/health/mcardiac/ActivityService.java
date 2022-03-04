package com.health.mcardiac;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityService extends Service implements SensorEventListener {
    private SensorManager mgnSensor;
    private Sensor   senAcc;
    DBHandler DBase;
    boolean isRegistered = false;
    private  String valActivity,valPosition;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public ActivityService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        valActivity = intent.getStringExtra("activity");
        valPosition = intent.getStringExtra("position");
        // connect to database
        DBase = new DBHandler(this);
        //get sensor service
        mgnSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // register for Accelerometer
        senAcc = mgnSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mgnSensor.registerListener(ActivityService.this,senAcc, SensorManager.SENSOR_DELAY_GAME);
        isRegistered = true;
        return START_NOT_STICKY;

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop recording sensor data
        if (isRegistered) {
            mgnSensor.unregisterListener(ActivityService.this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String Curtimestamp = simpleDateFormat.format(new Date());
            Long tsLong = System.currentTimeMillis()/1000;
            DBase.insertData(valActivity,valPosition, Float.toString(sensorEvent.values[0]),Float.toString(sensorEvent.values[1]),Float.toString(sensorEvent.values[2]),tsLong,Curtimestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
