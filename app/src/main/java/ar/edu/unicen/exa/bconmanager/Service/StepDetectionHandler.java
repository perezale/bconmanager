package ar.edu.unicen.exa.bconmanager.Service;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class StepDetectionHandler extends AppCompatActivity implements
        SensorEventListener {
    private String  TAG = "PDRActivity";
    SensorManager sm;
    Sensor sensor;

    private StepDetectionListener mStepDetectionListener;

    int step = 0;

    public StepDetectionHandler(SensorManager sm) {
        super();
        this.sm = sm;
        sensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void start() {
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

    }

    public void stop() {
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        float y;
        if (e.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            y = e.values[1];
            //threshold from which it is considered that it is a step
            if (y > 1 && mStepDetectionListener != null) {
                onNewStepDetected();

            }
        }
    }

    public void onNewStepDetected() {
        Log.d(TAG,"ON NEW STEP DETECTED");
        float distanceStep = 0.2f;
        step++;
        mStepDetectionListener.newStep(distanceStep);
    }

    public void setStepListener(StepDetectionListener listener) {
        mStepDetectionListener = listener;
    }

    public interface StepDetectionListener {
        public void newStep(float stepSize);

    }

}