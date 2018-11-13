package edu.cs4730.gpsDemo;

import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * this is a demo to show how to get location (ie gps) information from the device.
 * shows a listener changes and then just get the current (last known) information at the bottom.
 * <p/>
 * if using the emulator, set the location in the emulator before running this code, otherwise
 * you may get errors or just really odd results.
 */
public class MainFragment extends Fragment {
    TextView output;
    static public String TAG = "MainFragment";
    LocationManager myL;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_main, container, false);

        output = myView.findViewById(R.id.TextView01);
        output.append("\nNOTE, if you haven't told the Sim a location, there will be errors!\n");
        myL = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        //in the activity use:

        startGPSDemo();

        return myView;
    }

    //use the GPS location for this device.
    public void startGPSDemo() {
        //LocationManager myL = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        //In other places may need to use (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        //add a location listener, here building on the fly.

        //first check to see if I have permissions (marshmallow) if I don't then ask, otherwise start up the demo.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //I'm on not explaining why, just asking for permission.
            Log.v(TAG, "asking for permissions");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MainActivity.REQUEST_FINE_ACCESS);

        } else {
            Log.v(TAG, "I have permissions");
            myL.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {
                            output.append("\n onLocationChanged called");
                    /*	        location.getAltitude();
					        location.getLatitude();
	    			        location.getLongitude();
	    			        location.getTime();
	    			        location.getAccuracy();
	    			        location.getSpeed();
	    			        location.getProvider();
					 */
                            output.append("\n" + location.getLatitude() + " " + location.getLongitude());

                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {


                    }
                });

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            List<String> mylist = myL.getProviders(true);
            Location loc = null;
            String networkstr;
            for (int i = 0; i < mylist.size() && loc == null; i++) {
                networkstr = mylist.get(i);
                output.append("\n Attempting: " + networkstr);
                loc = myL.getLastKnownLocation(networkstr);
            }
            if (loc != null) {
                double sLatitude = loc.getLatitude();
                double sLongitude = loc.getLongitude();
                String location = " " + sLatitude + "," + sLongitude;
                output.append(location);
            } else {
                output.append("\nNo location can be found.\n");
            }

        }
    }


    //uses the Network location which is wifi and cell locations, instead of GPS.
    public void startCourseDemo() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "asking for permission course");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MainActivity.REQUEST_COARSE_ACCESS);
        } else {
            myL.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {
                            output.append("\n onLocationChanged called");
                            output.append("\n" + location.getLatitude() + " " + location.getLongitude());
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {


                    }
                });

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            List<String> mylist = myL.getProviders(true);
            Location loc = null;
            String networkstr;
            for (int i = 0; i < mylist.size() && loc == null; i++) {
                networkstr = mylist.get(i);
                output.append("\n Attempting: " + networkstr);
                loc = myL.getLastKnownLocation(networkstr);
            }
            if (loc != null) {
                double sLatitude = loc.getLatitude();
                double sLongitude = loc.getLongitude();
                String location = " " + sLatitude + "," + sLongitude;
                output.append(location);
            } else {
                output.append("\nNo location can be found.\n");
            }

        }
    }

}