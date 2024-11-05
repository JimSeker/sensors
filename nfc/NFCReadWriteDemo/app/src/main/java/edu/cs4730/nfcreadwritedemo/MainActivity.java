package edu.cs4730.nfcreadwritedemo;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import edu.cs4730.nfcreadwritedemo.databinding.ActivityMainBinding;

/**
 * This example uses the new enablereader method, instead of the older (mostly depreciated methods)
 * It uses it own receiver and only works whent the up is running (and start is pressed).
 *   it could easily to be changed to onResume and onPause, instead of start/stop buttons.
 *
 *   there is are 2 writers writetag() is the older method.  it works just fine.
 *   writetag2() is a newer method is writes out en, and the text.  either work just fine.
 *
 *   these all assume a nfcA tag is used, I think. well that's what I own, so I don't know if
 *   about the other tags.
 */


public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback{

    private NfcAdapter mNfcAdapter;
    ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    boolean writeOn = false;
    String writeText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableNfc();
            }
        });
        binding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNfc();
            }
        });
        binding.write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etWrite.getText().length() >0) {
                    writeText = binding.etWrite.getText().toString();
                    writeOn = true;
                    logthis("Writer is enabled.");
                }
            }
        });
    }



    private void disableNfc() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableReaderMode(this);
            logthis("reader mode is turned off.");
        }
    }

    private void enableNfc() {
        logthis("enableNfc method");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {

            // Work around some buggy hardware that checks for cards too fast
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);


            // Listen for all types of card when this App is in the foreground
            // Turn platform sounds off as they misdirect users when writing to the card
            // Turn of the platform decoding any NDEF data
            mNfcAdapter.enableReaderMode(this,
                this,
                NfcAdapter.FLAG_READER_NFC_A |
                    NfcAdapter.FLAG_READER_NFC_B |
                    NfcAdapter.FLAG_READER_NFC_F |
                    NfcAdapter.FLAG_READER_NFC_V |
                    NfcAdapter.FLAG_READER_NFC_BARCODE |
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options);
            logthis("reader mode is turned on");
        } else {
            logthis("nfc is not turned on");
        }
    }


    @Override
    public void onTagDiscovered(Tag tag) {
        logthis("Found a tag");
        readTag(tag);
        if (writeOn)
            writeTag(tag);

    }

    boolean writeTag2(Tag tag) {
        Ndef mNdef = Ndef.get(tag);
        if (mNdef != null) {

            // Create a Ndef Record
            NdefRecord mRecord = NdefRecord.createTextRecord("en", writeText);

            // Add to a NdefMessage
            NdefMessage mMsg = new NdefMessage(mRecord);

            // Catch errors
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mMsg);
                logthis("Success, wrote tag");

                // Make a Sound
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                        notification);
                    r.play();
                } catch (Exception e) {
                    // Some error playing sound
                }

            } catch (FormatException e) {
                // if the NDEF Message to write is malformed
                logthis("NDEF Message to write is malformed");
            } catch (TagLostException e) {
                // Tag went out of range before operations were complete
                logthis("Tag out of range");
            } catch (IOException e) {
                // if there is an I/O failure, or the operation is cancelled
                logthis("IO exception" + e.getMessage());
            } catch (SecurityException e) {
                // The SecurityException is only for Android 12L and above
                // The Tag object might have gone stale by the time
                // the code gets to process it, with a new one been
                // delivered (for the same or different Tag)
                // The SecurityException is thrown if you are working on
                // a stale Tag
                logthis("Security Exception?");
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close();
                } catch (IOException e) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }
            writeOn = false;
            logthis("Turning of writer");
            return true;
        } else {
            logthis("Tag is null, turning of writer.");
            writeOn = false;
            return false;
        }
    }

    /**
     * Figures out which type of tag we have and proceeds to write to it.  if it can.
     * from the older example, but it might still work.
     */
    boolean writeTag(Tag tag) {

        byte[] textBytes = writeText.getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte[]{}, textBytes);
        NdefMessage message = new NdefMessage(new NdefRecord[]{textRecord});
        int size = message.toByteArray().length;
        writeOn = false;  //turn it off now, to many return statements.
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
                        // Make a Sound
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                                notification);
                            r.play();
                        } catch (Exception e) {
                            // Some error playing sound
                        }
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

    void readTag(Tag tag) {
        logthis("Attempting to read tag");
        Ndef mNdef = Ndef.get(tag);
        if (mNdef != null) {
            // As we did not turn on the NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            // We can get the cached Ndef message the system read for us.
            NdefMessage mNdefMessage = mNdef.getCachedNdefMessage();
            NdefRecord[] msg = mNdefMessage.getRecords();
            for (int j = 0; j < msg.length; j++) {
                String body = new String(msg[j].getPayload());
                logthis("record " + j + " is " + body);
            }
        } else {
            logthis("mNdf is null");
        }
    }

    void logthis(String item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
                binding.logger.append(item + "\n");
                Log.d(TAG, item);
            }
        });
    }

}