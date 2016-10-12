package com.example.codeprojectandroid.orientationsensorsample;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class OrientationSensorActivity extends Activity implements SensorEventListener {

    TextView txtOrientationTextView;
    private SensorManager sensorManager;

    private float[] accelValues = new float[3];
    private float[] geoMagData = new float[3];
    private float[] orientationData = new float[3];
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_sensor);
        txtOrientationTextView = (TextView)findViewById(R.id.txtOrientationTextView);

        // Get the SensorManager instance
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
   }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for(int i=0; i<3; i++){
                    accelValues[i] =  event.values[i];
                }
                if(geoMagData[0] != 0)
                    isReady = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i=0; i<3; i++){
                    geoMagData[i] =  event.values[i];
                }
                if(accelValues[2] != 0)
                    isReady = true;
                break;
            default:
                return;
        }

        if(!isReady) return;

        float R[] = new float[9];
        float I[] = new float[9];

        if (SensorManager.getRotationMatrix(R, I, accelValues, geoMagData)) {
            SensorManager.getOrientation(R, orientationData);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Azimuth: "+ Math.toDegrees(orientationData[0]));
            stringBuilder.append("\nPitch: "+ Math.toDegrees(orientationData[1]));
            stringBuilder.append("\nRoll: "+ Math.toDegrees(orientationData[2]));
            txtOrientationTextView.setText(stringBuilder.toString());
        }
        else {
            txtOrientationTextView.setText("Failed to get rotation Matrix");
        }
    }
}
