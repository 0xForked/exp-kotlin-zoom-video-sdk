package com.github.bakode.zoomintexample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import us.zoom.sdk.*

class MainActivity : AppCompatActivity()
{
    companion object
    {
        val TAG: String = MainActivity::class.java.simpleName as String

        const val requestVideoAudioCode = 1010

        var hasAudioPermission = true
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.newMeetingBtn)
            .setOnClickListener {
                if (requestPermissions()) {
                    onPermissionGranted()
                } else {
                    Log.e(TAG, "NEED PERMISSION (OPEN SETTINGS)")
                }
            }
    }

    private fun requestPermissions(): Boolean
    {
        if (Build.VERSION.SDK_INT >= 31) {
            hasAudioPermission = (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED)
        }

        if (Build.VERSION.SDK_INT >= 23 && ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) || !hasAudioPermission) {
            var permissions = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (Build.VERSION.SDK_INT >= 31) {
                permissions = arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.BLUETOOTH_CONNECT"
                )
            }

            ActivityCompat.requestPermissions(this, permissions, requestVideoAudioCode)

            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestVideoAudioCode) {
            if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= 31 && ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED))) {
                onPermissionGranted()
            }
        }
    }

    private fun onPermissionGranted()
    {
        val sdkInstance = ZoomVideoSDK.getInstance()

        val initSDK = sdkInstance.initialize(this, ZoomOptions.zoomSDKParams())
        if (initSDK != ZoomVideoSDKErrors.Errors_Success) {
            val errorMessage = ZoomErrorMessage.getMessageByCode(initSDK)
            Log.e(TAG, "SOMETHING WENT WRONG WHEN INITIALIZE ZOOM SDK: $errorMessage")
            return
        }

        val sdkVersion = sdkInstance.sdkVersion
        Log.d(TAG, "ZOOM VIDEO SDK CONNECTED WITH CURRENT VERSION: $sdkVersion")

        startActivity(Intent(this, ZoomMeetingActivity::class.java))
    }
}