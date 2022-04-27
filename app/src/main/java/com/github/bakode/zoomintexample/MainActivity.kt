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
import com.github.bakode.zoomintexample.utils.ErrorMessage
import com.github.bakode.zoomintexample.utils.ZoomOption
import us.zoom.sdk.*

class MainActivity : AppCompatActivity(), ZoomVideoSDKDelegate
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

        val initSDK = sdkInstance.initialize(this, ZoomOption.zoomSDKParams())
        if (initSDK != ZoomVideoSDKErrors.Errors_Success) {
            val errorMessage = ErrorMessage.getMessageByCode(initSDK)
            Log.e(TAG, "SOMETHING WENT WRONG WHEN INITIALIZE ZOOM SDK: $errorMessage")
            return
        }

        val sdkVersion = sdkInstance.sdkVersion
        Log.d(TAG, "ZOOM VIDEO SDK CONNECTED WITH CURRENT VERSION: $sdkVersion")

        // TODO ADD INTENT DATA/EXTRA
        startActivity(Intent(
            this,
            MeetingActivity::class.java
        ))
    }

    override fun onError(errorCode: Int) {
        Log.e(TAG, "SESSION ERROR: $errorCode")
    }

    override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {
        Log.e(TAG, "SESSION PASSWORD WRONG")
    }

    override fun onSessionJoin() { }
    override fun onSessionLeave() {  }
    override fun onUserJoin(userHelper: ZoomVideoSDKUserHelper?, userList: MutableList<ZoomVideoSDKUser>?) { }
    override fun onUserLeave(userHelper: ZoomVideoSDKUserHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
    override fun onUserVideoStatusChanged(videoHelper: ZoomVideoSDKVideoHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
    override fun onUserAudioStatusChanged(audioHelper: ZoomVideoSDKAudioHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
    override fun onUserShareStatusChanged(shareHelper: ZoomVideoSDKShareHelper?, userInfo: ZoomVideoSDKUser?, status: ZoomVideoSDKShareStatus?) {}
    override fun onLiveStreamStatusChanged(liveStreamHelper: ZoomVideoSDKLiveStreamHelper?, status: ZoomVideoSDKLiveStreamStatus?) {}
    override fun onChatNewMessageNotify(chatHelper: ZoomVideoSDKChatHelper?, messageItem: ZoomVideoSDKChatMessage?) {}
    override fun onUserHostChanged(userHelper: ZoomVideoSDKUserHelper?, userInfo: ZoomVideoSDKUser?) {}
    override fun onUserManagerChanged(user: ZoomVideoSDKUser?) {}
    override fun onUserNameChanged(user: ZoomVideoSDKUser?) {}
    override fun onUserActiveAudioChanged(audioHelper: ZoomVideoSDKAudioHelper?, list: MutableList<ZoomVideoSDKUser>?) {}
    override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) {}
    override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {}
    override fun onOneWayAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?, user: ZoomVideoSDKUser?) {}
    override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {}
    override fun onCommandReceived(sender: ZoomVideoSDKUser?, strCmd: String?) {}
    override fun onCommandChannelConnectResult(isSuccess: Boolean) {}
    override fun onCloudRecordingStatus(status: ZoomVideoSDKRecordingStatus?) {}
    override fun onHostAskUnmute() {}
    override fun onInviteByPhoneStatus(status: ZoomVideoSDKPhoneStatus?, reason: ZoomVideoSDKPhoneFailedReason?) {}
}