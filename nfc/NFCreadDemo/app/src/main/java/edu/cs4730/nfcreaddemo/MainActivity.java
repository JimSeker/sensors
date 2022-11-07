package edu.cs4730.nfcreaddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;


/**
 * This example is a simple generic nfc reader.  It should read most everything the nfc chip can read on the phone.
 * it will log it and display it on the screen.  Note, this is a singletask set for this activity so
 * hopefully even if the app is not visible,  it will display correctly.
 *
 * Writing is not covered in this example, only reading.  The companion app is writeDemo.  read and write should be on different phones.
 */


public class MainActivity extends AppCompatActivity {

    TextView logger;
    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger = findViewById(R.id.logger);
    }

    /**
     * When a nfc tag is detected, we get it here.  We attempt to read all the data from the tag here.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];

                }
                // Process the messages array.
                for (NdefMessage message : messages) {
                    //there always going to be at least one record, so msg[0] will always exist, but could be more.
                    NdefRecord[] msg = message.getRecords();
                    for (int j = 0; j < msg.length; j++) {
                        String body = new String(msg[j].getPayload());
                        logthis("record " + j + " is " + body);
                    }
                }
            }
        }
    }

    /**
     * helper method to write the screen and the logcat.
     */
    void logthis( String item) {

        logger.append(item + "\n");
        Log.d(TAG, item);
   }

}