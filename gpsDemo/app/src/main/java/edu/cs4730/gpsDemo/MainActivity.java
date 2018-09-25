package edu.cs4730.gpsDemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

//Nothing to see here, go the MainFragment.

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    public static final int REQUEST_FINE_ACCESS = 0;
    public static final int REQUEST_COARSE_ACCESS = 1;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.v(TAG, "onRequest result called.");
        switch (requestCode) {
            case REQUEST_FINE_ACCESS:
                //received result for GPS access
                Log.v(TAG, "Received response for gps permission request.");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.v(TAG, permissions[0] + " permission has now been granted. Showing preview.");
                    Toast.makeText(this, "GPS access granted",
                            Toast.LENGTH_SHORT).show();
                    myFrag.startGPSDemo();  //call the method again, so the gps demo will start up.
                } else {
                    // permission denied,    Disable this feature or close the app.
                    Log.v(TAG, "GPS permission was NOT granted.");
                    Toast.makeText(this, "GPS access NOT granted", Toast.LENGTH_SHORT).show();
                }

                return;
            case REQUEST_COARSE_ACCESS:
                //received result for cell/wifi location (IE coarse location access)
                Log.v(TAG, "Received response for coarse access permissions request.");
               if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   Log.v(TAG, permissions[0] + " permission has now been granted. Showing preview.");
                   // permission was granted
                    Toast.makeText(this, "cell/wifi location access granted", Toast.LENGTH_SHORT).show();
                   myFrag.startCourseDemo();  //call the method again, so the course demo will start up.
                } else {
                   // permission denied
                    Log.v(TAG, "coarse location permissions were NOT granted.");
                    Toast.makeText(this, "cell/wifi location access granted", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
