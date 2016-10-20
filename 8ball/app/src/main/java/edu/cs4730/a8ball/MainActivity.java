package edu.cs4730.a8ball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    EightBall myEightBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myEightBall = (EightBall) findViewById(R.id.ball);
    }


    //setup listeners for the accerlator.
    //call the ball change text when the ball is "shaken".
    /*
        short x, y, z;

    System.out.println("onData Called");
    x = data.getLastXAcceleration();
    y = data.getLastYAcceleration();
    z = data.getLastZAcceleration();
    //try this...
    double totalForce = 0.0f;
    double t;
    t = x*1.0d/AccelerometerSensor.G_FORCE_VALUE;
    totalForce += t*t;

    t = y*1.0d/AccelerometerSensor.G_FORCE_VALUE;
    totalForce += t*t;

    t = z*1.0d/AccelerometerSensor.G_FORCE_VALUE;
    totalForce += t*t;
    totalForce = Math.sqrt(totalForce);

    label1.setText("x: "+x+" y: "+y+" z: "+z+ " TF: "+totalForce);
    if (totalForce > best) {
        best = totalForce;
        label2.setText("Best: "+ best);
    }
    if (totalForce > threshold) {

        shake++;
        label4.setText("shake " +shake);
        if (shake >1) {
          channel.removeAccelerometerListener();
          int i =   generator.nextInt(Answers.length);
          label3.setText(Answers[i]);
         // shake = 0;
          label4.setText("shake " +shake);
        }
    }
     */

}
