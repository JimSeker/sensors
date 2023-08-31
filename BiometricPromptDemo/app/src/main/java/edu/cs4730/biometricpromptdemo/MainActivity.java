package edu.cs4730.biometricpromptdemo;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.hardware.biometrics.BiometricPrompt;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;

import edu.cs4730.biometricpromptdemo.databinding.ActivityMainBinding;

/**
 * Note, if there is no fingerprint registered in the system, then example will not work.
 * It requires a fingerprint or face  android already registered first into order to work.
 */

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    ActivityMainBinding binding;
    private BiometricPrompt mBiometricPrompt;
    private String mToBeSignedMessage;

    // Unique identifier of a key pair
    private static final String KEY_NAME = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSupportBiometricPrompt()) {
                    register();
                } else
                    logthis("No fingerprint sensor!");
            }
        });

        binding.btnAuth.setEnabled(false);  //need to register first!
        binding.btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSupportBiometricPrompt()) {
                    authen();
                } else
                    logthis("No fingerprint sensor!");
            }
        });

    }

    /**
     * Before generating a key pair with biometric prompt, we need to check system feature to ensure that the device supports fingerprint, iris, or face.
     * Currently, there is no FEATURE_IRIS constant on PackageManager
     * So, only check FEATURE_FINGERPRINT and PackageManager.FEATURE_FACE
     *
     * @return
     */
    private boolean isSupportBiometricPrompt() {
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
                packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
        ) {
            return true;
        }
        return false;
    }

    private void logthis(String item) {
        Log.d(TAG, item);
        binding.logger.append(item + "\n");
    }

    void register() {
        logthis("Try registration");
        // Generate keypair and init signature
        Signature signature;
        try {
            // Before generating a key pair, we have to check enrollment of biometrics on the device
            // But, there is no such method on new biometric prompt API

            // Note that this method will throw an exception if there is no enrolled biometric on the device
            // This issue is reported to Android issue tracker
            // https://issuetracker.google.com/issues/112495828
            KeyPair keyPair = generateKeyPair(KEY_NAME, true);

            // Send public key part of key pair to the server, this public key will be used for authentication

            mToBeSignedMessage = new StringBuilder()
                    .append(Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.URL_SAFE))
                    .append(":")
                    .append(KEY_NAME)
                    .append(":")
                    // Generated by the server to protect against replay attack
                    .append("12345")
                    .toString();

            signature = initSignature(KEY_NAME);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            logthis("No fingerprint or face on file with android phone.  Go add one and then run this example again.");
            Toast.makeText(getApplicationContext(), "No fingerprint or face already registered.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create biometricPrompt
        mBiometricPrompt = new BiometricPrompt.Builder(this)
                .setDescription("Description")
                .setTitle("Title")
                .setSubtitle("Subtitle")
                .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancel button clicked");
                    }
                })
                .build();
        CancellationSignal cancellationSignal = getCancellationSignal();
        BiometricPrompt.AuthenticationCallback authenticationCallback = getAuthenticationCallback();

        // Show biometric prompt
        if (signature != null) {
            logthis("Show biometric prompt");
            mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(signature), cancellationSignal, getMainExecutor(), authenticationCallback);
            binding.btnAuth.setEnabled(true);
        }

    }

    private void authen() {
        logthis("Try authentication");

        // Init signature
        Signature signature;
        try {
            // Send key name and challenge to the server, this message will be verified with registered public key on the server
            mToBeSignedMessage = new StringBuilder()
                    .append(KEY_NAME)
                    .append(":")
                    // Generated by the server to protect against replay attack
                    .append("12345")
                    .toString();
            signature = initSignature(KEY_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create biometricPrompt
        mBiometricPrompt = new BiometricPrompt.Builder(this)
                .setDescription("Description")
                .setTitle("Title")
                .setSubtitle("Subtitle")
                .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logthis("Cancel button clicked");
                    }
                })
                .build();
        CancellationSignal cancellationSignal = getCancellationSignal();
        BiometricPrompt.AuthenticationCallback authenticationCallback = getAuthenticationCallback();

        // Show biometric prompt
        if (signature != null) {
            logthis("Show biometric prompt");
            mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(signature), cancellationSignal, getMainExecutor(), authenticationCallback);
        }

    }


    private CancellationSignal getCancellationSignal() {
        // With this cancel signal, we can cancel biometric prompt operation
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                logthis("Canceled");
            }
        });
        return cancellationSignal;
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        // Callback for biometric authentication result
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Log.i(TAG, "onAuthenticationSucceeded");
                super.onAuthenticationSucceeded(result);
                Signature signature = result.getCryptoObject().getSignature();
                try {
                    signature.update(mToBeSignedMessage.getBytes());
                    String signatureString = Base64.encodeToString(signature.sign(), Base64.URL_SAFE);
                    // Normally, ToBeSignedMessage and Signature are sent to the server and then verified
                    logthis("Message: " + mToBeSignedMessage);
                    logthis("Signature (Base64 EncodeD): " + signatureString);
                    Toast.makeText(getApplicationContext(), mToBeSignedMessage + ":" + signatureString, Toast.LENGTH_SHORT).show();
                } catch (SignatureException e) {
                    throw new RuntimeException();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };
    }

    /**
     * Generate NIST P-256 EC Key pair for signing and verification
     *
     * @param keyName
     * @param invalidatedByBiometricEnrollment
     * @return
     * @throws Exception
     */
    private KeyPair generateKeyPair(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512)
                // Require the user to authenticate with a biometric to authorize every use of the key
                .setUserAuthenticationRequired(true)
                // Generated keys will be invalidated if the biometric templates are added more to user device
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);

        keyPairGenerator.initialize(builder.build());

        return keyPairGenerator.generateKeyPair();
        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */
            /*KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA512)
                .build());

            KeyPair keyPair = kpg.generateKeyPair();
*/
    }

    @Nullable
    private KeyPair getKeyPair(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            // Get public key
            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
            // Get private key
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
            // Return a key pair
            return new KeyPair(publicKey, privateKey);
        }
        return null;
    }

    @Nullable
    private Signature initSignature(String keyName) throws Exception {
        KeyPair keyPair = getKeyPair(keyName);

        if (keyPair != null) {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPair.getPrivate());
            return signature;
        }
        return null;
    }
}
