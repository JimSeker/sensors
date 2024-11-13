package edu.cs4730.gpsdemo_kt

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.gpsdemo_kt.databinding.ActivityMainBinding


/**
 * this is a demo to show how to get location (ie gps) information from the device.
 * shows a listener changes and then just get the current (last known) information at the bottom.
 *
 * if using the emulator, set the location in the emulator before running this code, otherwise
 * you may get errors or just really odd results.
 */
class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    var myL: LocationManager? = null
    private lateinit var REQUIRED_PERMISSIONS: Array<String>
    private lateinit var rpl: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        //full set of permissions, both coarse and fine
        REQUIRED_PERMISSIONS = arrayOf(
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"
        )
        //based on which permissions, then call the right one.
        rpl = registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            var fine = false
            var coarse = false
            for ((key, value) in isGranted) {
                logthis("$key is $value")
                if (key == "android.permission.ACCESS_FINE_LOCATION" && value) {
                    fine = true
                } else if (key == "android.permission.ACCESS_COARSE_LOCATION" && value) {
                    coarse = true
                }
            }
            if (fine) startGPSDemo() else if (coarse) startCoarseDemo()
        }
        logthis("\nNOTE, if you haven't told the Sim a location, there will be errors!\n")
        myL = getSystemService(LOCATION_SERVICE) as LocationManager
        startDemo()
    }

    fun startDemo() {
        var fine = false
        var coarse = false
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (permission == "android.permission.ACCESS_FINE_LOCATION") {
                    fine = true
                } else if (permission == "android.permission.ACCESS_COARSE_LOCATION") {
                    coarse = true
                }
            }
        }
        if (fine) startGPSDemo() else if (coarse) startCoarseDemo() else rpl!!.launch(
            REQUIRED_PERMISSIONS
        )
    }

    //use the GPS location for this device.
    fun startGPSDemo() {

        //first check to see if I have permissions to.  This should not be necessary.  just in case.
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            rpl.launch(REQUIRED_PERMISSIONS)
        } else {
            logthis("GPS/Fine I have permissions")
            myL!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {  //in java this can and is null, kotlin asserts it's not.  odd.
                            logthis("Fine onLocationChanged called")
//                            location.altitude;
//                            location.latitude;
//                            location.longitude;
//                            location.time;
//                            location.accuracy;
//                            location.speed;
//                            location.provider;
                            logthis(location.latitude.toString() + " " + location.longitude)
                        }
                    }

                    override fun onProviderDisabled(provider: String) {
                        logthis("Fine Provider is disabled")
                    }

                    override fun onProviderEnabled(provider: String) {
                        logthis("Find Provider is enabled")
                    }

                })

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            val mylist = myL!!.getProviders(true)
            var loc: Location? = null
            var networkstr: String
            var i = 0
            while (i < mylist.size && loc == null) {
                networkstr = mylist[i]
                logthis("Attempting: $networkstr")
                loc = myL!!.getLastKnownLocation(networkstr)
                i++
            }
            if (loc != null) {
                val sLatitude = loc.latitude
                val sLongitude = loc.longitude
                val location = " $sLatitude,$sLongitude"
                logthis(location)
            } else {
                logthis("\nNo location can be found.\n")
            }
        }
    }

    //uses the Network location which is wifi and cell locations, instead of GPS.

    fun startCoarseDemo() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            rpl.launch(REQUIRED_PERMISSIONS)
        } else {
            logthis("Coarse I have permissions")
            myL!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        //if we have location information, update the screen here.  just lat and lot, others
                        //are shown if you may need them.
                        if (location != null) {
                            logthis("Coarse onLocationChanged called")
                            logthis(location.latitude.toString() + " " + location.longitude)
                        }
                    }

                    override fun onProviderDisabled(provider: String) {
                        logthis("Coarse Provider is disabled")
                    }

                    override fun onProviderEnabled(provider: String) {
                        logthis("Coarse Provider is enabled")
                    }

                })

            //Get a list of providers
            //could also use  String = getBestProvider(Criteria  criteria, boolean enabledOnly)
            val mylist = myL!!.getProviders(true)
            var loc: Location? = null
            var networkstr: String
            var i = 0
            while (i < mylist.size && loc == null) {
                networkstr = mylist[i]
                logthis("Attempting: $networkstr")
                loc = myL!!.getLastKnownLocation(networkstr)
                i++
            }
            if (loc != null) {
                val sLatitude = loc.latitude
                val sLongitude = loc.longitude
                val location = " $sLatitude,$sLongitude"
                logthis(location)
            } else {
                logthis("\nNo location can be found.\n")
            }
        }
    }

    //simple log function to log to both a textivew and the logcat.
    fun logthis(item: String) {
        binding.logger.append(item + "\n");
        Log.d(TAG, item)
    }
}
