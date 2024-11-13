package edu.cs4730.gpsDemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Map;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.cs4730.gpsDemo.databinding.ActivityMainBinding;

/**
 * this is a demo to show how to get location (ie gps) information from the device.
 * shows a listener changes and then just get the current (last known) information at the bottom.
 * <p/>
 * if using the emulator, set the location in the emulator before running this code, otherwise
 * you may get errors or just really odd results.
 */

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    ActivityMainBinding binding;
    LocationManager myL;

    private String[] REQUIRED_PERMISSIONS;
    ActivityResultLauncher<String[]> rpl;

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
        //full set of permissions, both coarse and fine
        REQUIRED_PERMISSIONS = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
        //based on which permissions, then call the right one.
        rpl = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> isGranted) {
                    boolean fine = false, coarse = false;
                    for (Map.Entry<String, Boolean> x : isGranted.entrySet()) {
                        logthis(x.getKey() + " is " + x.getValue());
                        if (x.getKey().equals("android.permission.ACCESS_FINE_LOCATION") && x.getValue()) {
                            fine = true;

                        } else if (x.getKey().equals("android.permission.ACCESS_COARSE_LOCATION") && x.getValue()) {
                            coarse = true;
                        }
                    }
                    if (fine)
                        startGPSDemo();
                    else if (coarse)
                        startCoarseDemo();
                }
            }
        );

        logthis("\nNOTE, if you haven't told the Sim a location, there will be errors!\n");
        myL = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        startDemo();

    }

    public void startDemo() {
        boolean fine = false, coarse = false;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                if (permission.equals("android.permission.ACCESS_FINE_LOCATION")) {
                    fine = true;

                } else if (permission.equals("android.permission.ACCESS_COARSE_LOCATION")) {
                    coarse = true;
                }
            }
        }
        if (fine)
            startGPSDemo();
        else if (coarse)
            startCoarseDemo();
        else
            rpl.launch(REQUIRED_PERMISSIONS);
    }


    //use the GPS location for this device.
    public void startGPSDemo() {

        //first check to see if I have permissions to.  This should not be necessary.  just in case.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            rpl.launch(REQUIRED_PERMISSIONS);
        } else {
            logthis("GPS/Fine I have permissions");
            myL.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {
                            logthis("Fine onLocationChanged called");
//                          location.getAltitude();
//					        location.getLatitude();
//	    			        location.getLongitude();
//	    			        location.getTime();
//	    			        location.getAccuracy();
//	    			        location.getSpeed();
//	    			        location.getProvider();

                            logthis(location.getLatitude() + " " + location.getLongitude());

                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        logthis("Fine Provider is disabled");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        logthis("Find Provider is enabled");
                    }

                });

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            List<String> mylist = myL.getProviders(true);
            Location loc = null;
            String networkstr;
            for (int i = 0; i < mylist.size() && loc == null; i++) {
                networkstr = mylist.get(i);
                logthis("Attempting: " + networkstr);
                loc = myL.getLastKnownLocation(networkstr);
            }
            if (loc != null) {
                double sLatitude = loc.getLatitude();
                double sLongitude = loc.getLongitude();
                String location = " " + sLatitude + "," + sLongitude;
                logthis(location);
            } else {
                logthis("\nNo location can be found.\n");
            }
        }
    }


    //uses the Network location which is wifi and cell locations, instead of GPS.
    public void startCoarseDemo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            rpl.launch(REQUIRED_PERMISSIONS);
        } else {
            logthis("Coarse I have permissions");
            myL.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {
                            logthis("Coarse onLocationChanged called");
                            logthis(location.getLatitude() + " " + location.getLongitude());
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        logthis("Coarse Provider is disabled");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        logthis("Coarse Provider is enabled");

                    }
                });

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            List<String> mylist = myL.getProviders(true);
            Location loc = null;
            String networkstr;
            for (int i = 0; i < mylist.size() && loc == null; i++) {
                networkstr = mylist.get(i);
                logthis("Attempting: " + networkstr);
                loc = myL.getLastKnownLocation(networkstr);
            }
            if (loc != null) {
                double sLatitude = loc.getLatitude();
                double sLongitude = loc.getLongitude();
                String location = " " + sLatitude + "," + sLongitude;
                logthis(location);
            } else {
                logthis("\nNo location can be found.\n");
            }

        }
    }

    //simple log function to log to both a textivew and the logcat.
    void logthis(String item) {
        binding.logger.append(item + "\n");
        Log.d(TAG, item);
    }
}
