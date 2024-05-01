package edu.cs4730.nfcreadwritedemo;

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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

/**
 * This has both the read and the write for nfc devices/tags together.  Note when writing a tag, reading tags is turned off.
 * <p>
 * Also about half of this example is @Deprecated in 29.  removing the enable and disable stops
 * this example from working in the foreground.  It will read in the background without them.
 *
 * this example is broken in api 34 as one main function is now removed.
 * <p>
 * Googles doc's says stupid things like use bluetooth instead.
 * https://developer.android.com/reference/android/nfc/NfcAdapter.CreateNdefMessageCallback#createNdefMessage(android.nfc.NfcEvent)
 */

public class MainActivity extends AppCompatActivity {

    TextView logger;
    final static String TAG = "MainActivity";
    Button btn_write;
    EditText editText;

    boolean writemode = false, writeDone = false, notPaused = true;

    //used for writing to the device/tag.
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;
    NfcAdapter mNfcAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger = findViewById(R.id.logger);
        editText = findViewById(R.id.et_write);
        btn_write = findViewById(R.id.btn_write);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writemode) {
                    //it's own, so turn it off
                    disableTagWriteMode();
                    editText.setEnabled(true);
                    btn_write.setText("Write Tag");
                    writemode = false;
                    writeDone = true;  //just making sure.
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
                    writeDone = false;
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
        // Intent filters for reading devices/tags.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.d(TAG, "Failed to addDataType");
        }
        mNdefExchangeFilters = new IntentFilter[]{ndefDetected};

    }


    /**
     * This is where the new nfc information is sent to when a tag/device is read.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (!writemode) {

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
        } else { //we are writing to a tag
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

                if (notPaused) { //if the app is paused, we can't change the hardware settings until we not not paused.
                    disableTagWriteMode();
                    enableNdefExchangeMode();
                }
            }
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


    @Override
    protected void onResume() {
        super.onResume();
        notPaused = true;
//        // Sticky notes received from Android
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            Log.wtf(TAG, "on resume got something?");
//            NdefMessage[] messages = getNdefMessages(getIntent());
//            byte[] payload = messages[0].getRecords()[0].getPayload();
//            setNoteBody(new String(payload));
//            setIntent(new Intent()); // Consume this intent.
//        }
        if (writeDone) {
            disableTagWriteMode();
        }
        if (!writemode)
          enableNdefExchangeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        notPaused = false;
        mNfcAdapter.disableForegroundNdefPush(this); //deprecated
        // the setNdefPushMessage version understands onPause/onResume, so no need to turn off/on.
    }

    /**
     * This will turn off reading of devices/tags.
     */
    private void disableNdefExchangeMode() {
        //This turns off read of nfc devices/tags.  We are only writing to tags in this example.
        mNfcAdapter.disableForegroundNdefPush(this);  //deprecated, removed in api 34.
        mNfcAdapter.setNdefPushMessage(null, MainActivity.this);  //turn it off.
        mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * Turns on reading of devices/tags
     */
    private void enableNdefExchangeMode() {
        byte[] textBytes = "What?".getBytes(); //honesty don't know why this is needed.
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte[]{}, textBytes);
        NdefMessage Ndefmsg = new NdefMessage(new NdefRecord[]{textRecord});

        mNfcAdapter.enableForegroundNdefPush(MainActivity.this, Ndefmsg);  //deprecated, take this out and it won't write.
        mNfcAdapter.setNdefPushMessage(Ndefmsg, MainActivity.this);
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
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
     * Turns off the write mode.  We still get new TAGS because of the intent filter.  but the write is off.
     */
    private void disableTagWriteMode() {
        logthis("Disabling writing to tag");
        mNfcAdapter.disableForegroundDispatch(this);
    }


    /**
     * General helper function to write to the screen and logcat.
     */
    void logthis(String item) {
        logger.append(item + "\n");
        Log.d(TAG, item);
    }


}