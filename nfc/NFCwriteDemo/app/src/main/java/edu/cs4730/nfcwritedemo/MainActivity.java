package edu.cs4730.nfcwritedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;


/**
 * This app is a companion to the NFCreadDemo.  Note, the write should be one one device and the read on a separate device.  Otherwise it confuses android.
 * <p>
 * This take data provided by the user in edittext box and will wrote to a device/tag (works best with a nfc tag).
 *
 *  About half of this example is deeprecated in api 29.  Googles doc's says stupid things like use bluetooth instead.
 *  https://developer.android.com/reference/android/nfc/NfcAdapter.CreateNdefMessageCallback#createNdefMessage(android.nfc.NfcEvent)
 */


public class MainActivity extends AppCompatActivity {

    TextView logger;
    EditText editText;
    Button btn_write;
    final static String TAG = "MainActivity";
    boolean writemode = false, writeDone = false, notPaused = true;

    //used for writing to the device/tag.
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger = findViewById(R.id.logger);
        editText = findViewById(R.id.writeTag);
        btn_write = findViewById(R.id.btn_wrt);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writemode) {
                    //it's own, so turn it off
                    disableTagWriteMode();
                    editText.setEnabled(true);
                    btn_write.setText("Write Tag");
                    writemode = false;
                } else {
                    //turn on write mode.  disable edit text and then turn on writing.
                    if (editText.getText().length() <= 0) {
                        logthis("There must be something to write");
                        return;
                    }
                    //turn off the read mode for the device tag, we just want to write.
                    disableNdefExchangeMode();
                    enableTagWriteMode();
                    editText.setEnabled(true);
                    btn_write.setText("Disable Writing");
                    writemode = true;
                }
            }
        });

        //so we can write a tag.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Handle all of our received NFC intents in this activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        } else {
            mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }


    /**
     * if we have finished writing the tag, but the app was paused (Likely), then this will disable the write mode.
     */
    @Override
    protected void onResume() {
        super.onResume();
        notPaused = true;
        if (writeDone) {
            disableTagWriteMode();
        }
    }

    /**
     * making sure that read is turned off and we need to know when the app is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        notPaused = false;
        //turn off the reading of tags.  we are only writing to them.
        mNfcAdapter.disableForegroundNdefPush(this); //deprecated

    }

    /**
     * This will turn off reading of devices/tags.
     */
    private void disableNdefExchangeMode() {
        //This turns off read of nfc devices/tags.  We are only writing to tags in this example.
        mNfcAdapter.disableForegroundNdefPush(this);  //deprecated.
        mNfcAdapter.setNdefPushMessage(null, MainActivity.this);  //turn it off.
        mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * turns on the wrote mode, so when we receive a tag/device.
     */
    private void enableTagWriteMode() {
        logthis("Enabling writing to Tag now");
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[]{tagDetected};
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    /**
     * When a nfc tag is detected, we get it here.  if writemode is on, then we can proceed to write
     * out the data to the device/tag.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (!writemode) {
            logthis("Received a tag, but we writing is disabled.");
            return;
        }

        logthis("Received a tag, about to try and write to it.");
        byte[] textBytes = editText.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte[]{}, textBytes);
        NdefMessage Ndefmsg = new NdefMessage(new NdefRecord[]{textRecord});

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        logthis("Tag info is " + detectedTag.toString());
        writeDone = writeTag(Ndefmsg, detectedTag);
        if (writeDone) {
            editText.setEnabled(true);
            btn_write.setText("Write Tag");
            writemode = false;

            if (notPaused)  //if the app is paused, we can't change the hardware settings until we not not paused.
                disableTagWriteMode();
        }

    }

    /**
     * Figures out which type of tag we have and proceeds to write to it.  if it can.
     */
    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    logthis("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    logthis("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                        + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                logthis("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        logthis("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        logthis("Failed to format tag.");
                        return false;
                    }
                } else {
                    logthis("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            logthis("Failed to write tag");
        }

        return false;
    }


    /**
     * Turns off the write mode.  We still get new TAGS because of the intent filter.  but the write is off.
     */
    private void disableTagWriteMode() {
        logthis("Disabling writing to tag");
        mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * helper method to write the screen and the logcat.
     */
    void logthis(String item) {
        logger.append(item + "\n");
        Log.d(TAG, item);
    }

}