Android Hardware Sensor and input Examples LEGACY
===========

These are legacy projects and are no longer updated.

<b>FingerprintDialog</b> is google's example. See https://github.com/googlesamples/android-FingerprintDialog/   deprecated example in API 28, this is a legacy example.  see BiometricPromptDemo for API 28+

<b>FingerPrintDemo</b> is a simple version to verify a "purchase" using the finger printer scanner.  It's based on android's FingerprintDialog, but far simpler.  It needs to be updated to use API 24/25 as well, but currently uses just the 23 methods.  deprecated example as of API 28, this is a legacy example. see BiometricPromptDemo for API 28+


<b>nfcDemo</b> This shows an example of how to use beam.  You will need to two devices with nfc.  This app will beam a message to the other devices app.  beam was removed in android 10.

<b>nfcDemo3</b> well send a file via nfc beam.  It only needs to be installed on one device.  fair warning, beaming is pretty slow, so don't try this with large files unless you can set the devices correctly on top of each other.  beam was removed in android 10.

<b>nfc3</b> is an example of to set a nfc tag, I don't think it app works anymore past api 29.

<b>StickyNotes</b> This example code is more complex, but can read and write nfc tags.

<b>NFCwriteDemo</b> (java) Shows how to write to  NFC devices/tags.  Use the readDemo on another phone to read the tag to see if worked. writing now fails in api 34, still example can't move beyond api 33

<b>NFCReadWriteDemo</b> (java) combines the other examples, so you can read and write to tags in the same app.  it's a little more complex 
since you can not read and write at the same time.    writing now fails in api 34, still example can't move beyond api 33


These are example code for University of Wyoming, Cosc 4730 Mobile Programming course and Cosc 4735 Advanced Mobile Programming course.
All examples are for Android.

