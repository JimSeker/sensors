package edu.cs4730.gpsDemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

//Nothing to see here, go the MainFragment.

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    private static final int REQUEST_FINE_ACCESS = 0;
    private static final int REQUEST_COURSE_ACCESS = 1;
    MainFragment myFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFrag = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, myFrag).commit();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.v(TAG, "onRequest result called?.");
        if (requestCode == REQUEST_FINE_ACCESS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.v(TAG, "Received response for gps permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.v(TAG, "GPS permission has now been granted. Showing preview.");
                Toast.makeText(this, "GPS access granted",
                        Toast.LENGTH_SHORT).show();

                myFrag.startDemo();

            } else {
                Log.v(TAG, "GPS permission was NOT granted.");
                Toast.makeText(this, "GPS access granted",
                        Toast.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_COURSE_ACCESS) {
            Log.v(TAG, "Received response for course access permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // All required permissions have been granted, display contacts fragment.
                Toast.makeText(this, "cell wifi access granted",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.v(TAG, "cell wifi permissions were NOT granted.");
                Toast.makeText(this, "cell wifi access granted",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
