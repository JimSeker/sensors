package edu.cs4730.fingerprintdemo;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**
 * This code is based on the very complex code in the FingerPrintDialog example provided by
 * android/google.  This is just a simple version to show the fingerprint scanner and not much else.
 *
 * FingerprintManager has been deprecated in API 28 for the BiometricPrompt.  So this example is going to
 * be marked as legacy and will not be updated anymore.
 *
 */

public class MainActivity extends AppCompatActivity {

    Button buyNow;
    TextView logger;
    String TAG = "MainActivity";
    private static final String SECRET_MESSAGE = "Very secret message";

    //Alias for our key in the Android Key Store
    private static final String KEY_NAME = "my_key";

    KeyStore mKeyStore;
    KeyGenerator mKeyGenerator;
    Cipher mCipher;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintManager mFingerprintManager;

    CancellationSignal mCancellationSignal;
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = findViewById(R.id.logger);
        buyNow = findViewById(R.id.buynow);
        buyNow.setEnabled(false);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPurchaseConfirmation();
            }
        });
        mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);


        setupCypto();
        createKey();
        //Is fingerprint scanner?
        //noinspection ResourceType
        if (mFingerprintManager.isHardwareDetected()) {
            logger.append("There is finger print scanner\n");
            //noinspection ResourceType
            if (mFingerprintManager.hasEnrolledFingerprints()) {
                logger.append("And at least one finger printer enrolled.  Demo ready");
                buyNow.setEnabled(true);
            } else {
                logger.append("Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint");
            }
        } else {
            logger.append("No Scanner detected");
        }


    }

    public void showPurchaseConfirmation() {
        if (initCipher()) {

            //note the dialog is not necessary.  It's just so the user knows to do something.
            mAlertDialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("Purchase something")
                .setMessage("Use the finger print scanner")
                .setIcon(R.mipmap.ic_fp_40px)
                .create();
            mAlertDialog.show();

            //create the crypto object needed by the finger print authenticator (at least I think it needs one).
            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

            mCancellationSignal = new CancellationSignal();

            //create the "listener".  ie call on the sensor to start.
            //authenticate(FingerprintManager.CryptoObject crypto, CancellationSignal cancel, int flags, FingerprintManager.AuthenticationCallback callback, Handler handler)
            //Request authentication of a crypto object.
            //studio thinks I need to call for permissions, except I don't.   very odd, disabled it.  Maybe in N?
            //noinspection ResourceType
            mFingerprintManager.authenticate(
                mCryptoObject,
                mCancellationSignal,
                0, /* flags */
                new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        logger.append("FingerPrint Error\n");
                        mAlertDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "Finger Print Err", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        logger.append("FingerPrint success\n");
                        tryEncrypt();
                        mAlertDialog.dismiss();
                        // Toast.makeText(getApplicationContext(), "Finger Print SUCCESS!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        logger.append("FingerPrint failed\n");
                        mCancellationSignal.cancel();  //used to exit the authenticator.  In real, should say Try Again or something.
                        mAlertDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "Finger Print Fail", Toast.LENGTH_SHORT).show();
                    }
                },
                null
            );

        }
    }

    /**
     * setup method to get all the crypto variables setup to use later one.
     */
    public void setupCypto() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }

        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        try {
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
    }


    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private boolean initCipher() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
            | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    public void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT |
                    KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                // Require the user to authenticate with a fingerprint to authorize every use
                // of the key
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
            | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt() {
        try {
            byte[] encrypted = mCipher.doFinal(SECRET_MESSAGE.getBytes());
            if (encrypted != null) {
                logger.append("Encrypted text is: \n");
                logger.append(Base64.encodeToString(encrypted, 0 /* flags */));
                logger.append("\n");
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            logger.append("Failed to encrypt the data with generated key, see log");
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }
}
