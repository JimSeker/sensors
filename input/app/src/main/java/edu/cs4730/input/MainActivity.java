package edu.cs4730.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.cs4730.input.databinding.ActivityMainBinding;

/**
 * An example of how to access the touch, tap, double, and Gestures on the screen.
 * Also how to get keys (hardware or software keyboard)
 * <p>
 * The orientation sensor is also used, and yea, it's is depreciated.
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private SensorManager myManager;
    private Sensor accSensor;
    private List<Sensor> sensors;
    SensorEventListener mySensorListener;
    GestureDetector mGestureDetector = null;
    View.OnTouchListener mGestureListener = null;
    char[] chars = {'a', 'b', 'c', 'd', 'e'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        binding.iv.setOnKeyListener(new myKeyListener());

        binding.iv.setOnClickListener(new myClickListener());
        binding.iv.setOnLongClickListener(new myLongClickListener());

        mGestureListener = new myTouchListener();
        mGestureDetector = new GestureDetector(this, new MyGestureDetector());
        binding.iv.setOnTouchListener(mGestureListener);  //lint is wrong, it is handled.

    }

    @Override
    public void onPause() {
        unregisterAccelerometer();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerAccelerometer();
        super.onResume();
    }

    class myKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //
            char key = event.getMatch(chars);
            if (key != '\0') {
                binding.label1.setText("Key: " + key);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                binding.label1.setText("Key: menu");
                return false;  //allow android to still use the key as well.
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                binding.label1.setText("Key: back");
                return true;  //don't allow android, since it kills the program.
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                binding.label2.setText("Navigation: DOWN");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                binding.label2.setText("Navigation: UP");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                binding.label2.setText("Navigation: LEFT");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                binding.label2.setText("Navigation: RIGHT");
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                binding.label2.setText("Navigation: CENTER");
                return true;
            }
            binding.label1.setText("Key: ");
            return false;
        }

    }

    class myClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Click!", Toast.LENGTH_SHORT).show();
        }

    }

    class myLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(getApplicationContext(), "Long Click!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    class myTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector.onTouchEvent(event))
                return true;

            //Either not a gesture or not supposed to use them anyway.
            int action = event.getAction();
            // Retrieve the new x and y touch positions
            int x = (int) event.getX();
            int y = (int) event.getY();
            binding.label3.setText("Touch: x=" + x + " y=" + y);
            switch (action) {
                case MotionEvent.ACTION_UP:  //removes a lint warning.
                    v.performClick();
                    break;
                case MotionEvent.ACTION_MOVE:
                    binding.label2.setText("Navigation: touch move");
                    return true;
            }

            return false;
        }


    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 100; //150
        private static final int SWIPE_MAX_OFF_PATH = 100;
        private static final int SWIPE_THRESHOLD_VELOCITY = 75; //100

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dX = e2.getX() - e1.getX();
            float dY = e1.getY() - e2.getY();

            if (Math.abs(dY) < SWIPE_MAX_OFF_PATH &&
                Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY &&
                Math.abs(dX) >= SWIPE_MIN_DISTANCE) {
                if (dX > 0) {
                    Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH &&
                Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&
                Math.abs(dY) >= SWIPE_MIN_DISTANCE) {
                if (dY > 0) {
                    Toast.makeText(MainActivity.this, "Up Swipe", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Down Swipe", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }
    }

    void registerAccelerometer() {
        myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensors = myManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
//        if(sensors.size() > 0)
//          accSensor = sensors.get(0);
        accSensor = myManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mySensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // I have no desire to deal with the accuracy events
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    int Sensorhold = 45;  //about the angle the phone is held at.
                    int SensorThreshold = 15;  // how far the phone needs be pitched/rolled for an effect.
                    float sy = event.values[1];  //pitch rotation, on y axis between -180 and 180
                    float sx = event.values[2];  //roll rotation on the x axis  between -180 and 180
                    //Toast.makeText(requireActivity().getApplicationContext(), "Orientation: x:"+sx+" y:"+sy, Toast.LENGTH_SHORT).show();
                    if (sx >= SensorThreshold || sx <= -SensorThreshold || //x value is valid
                        sy >= SensorThreshold + Sensorhold || sy <= -SensorThreshold + Sensorhold) {  //y value is valid
                        /*
                         * valid value based on thresholding, now figure out
                         * which direction to take is x ">" then y including negative direction
                         * note -x is left is lifted, +x is right is lifted  (based on default orientation)
                         *      +y is top is lifted and -y is bottom is lifted.
                         *      But we need to reverse, because with want it goes "down", instead of lifted.
                         */
                        if (Math.abs(sx) >= Math.abs(sy)) {  //sx value is the one to take
                            if (sx > 0) //go right lifted, so go left
                                binding.label4.setText("Sensor: x:" + sx + " y:" + sy + "  Rightside up");
                            else
                                binding.label4.setText("Sensor: x:" + sx + " y:" + sy + "  leftside up");
                        } else {  //up or down, sy value is bigger.
                            if (sy > 0)
                                binding.label4.setText("Sensor: x:" + sx + " y:" + sy + "  Bottom  up");
                            else {//TOP is lifted, so go down
                                binding.label4.setText("Sensor: x:" + sx + " y:" + sy + " top up");
                            }
                        }

                    }
                }
            }
        };
        myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    void unregisterAccelerometer() {
        if (myManager != null && mySensorListener != null) {
            myManager.unregisterListener(mySensorListener);
        }
        myManager = null;
        mySensorListener = null;
    }
}
