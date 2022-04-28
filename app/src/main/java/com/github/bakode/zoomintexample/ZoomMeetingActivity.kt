package com.github.bakode.zoomintexample

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import us.zoom.sdk.*

class ZoomMeetingActivity : AppCompatActivity(), View.OnTouchListener, ZoomVideoSDKDelegate
{
    companion object
    {
        val TAG: String = ZoomMeetingActivity::class.simpleName as String

        var isMicrophoneMuted: Boolean = false

        var secondaryVideoContainerXPos = 0f
        var secondaryVideoContainerYPos = 0f
        var secondaryVideoLastAction = 0
    }

    private lateinit var zoomInstance: ZoomVideoSDK

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        this.zoomInstance = ZoomVideoSDK.getInstance()

        val sessionToken = applicationContext.getString(R.string.zoom_jwt_token)
        this.zoomInstance.joinSession(ZoomOptions.zoomSessionCtx(
            appointmentToken = sessionToken,
            appointmentSessionName = "tesName123",
            appointmentSessionPassword = "",
            customerFullName =  "A. A. Sumitro"
        )).let { session ->
            if (session == null) {
                Log.d(TAG, "NO ZOOM SESSION")
                return
            }
        }

        this.zoomInstance.videoHelper.rotateMyVideo(display?.rotation as Int)

        this.zoomInstance.addListener(this)
    }

    override fun onStart()
    {
        super.onStart()
        this.initViewListener()
    }

    private fun initViewListener()
    {
        findViewById<FloatingActionButton>(R.id.fabFinishActivity)
            .setOnClickListener { this.finish() }

        findViewById<FloatingActionButton>(R.id.fabMuteMicrophone)
            .setOnClickListener { view -> this.onMicrophoneMuted(view as FloatingActionButton) }

        findViewById<FloatingActionButton>(R.id.fabDismissMeeting)
            .setOnClickListener { this.onMeetingDismissed() }

        findViewById<FloatingActionButton>(R.id.fabSwitchVideoFrame)
            .setOnClickListener { this.onVideoFrameSwitched() }

        findViewById<View>(R.id.secondaryVideoContainer)
            .setOnTouchListener(this)
    }

    private fun onMicrophoneMuted(view: FloatingActionButton)
    {
        isMicrophoneMuted = !isMicrophoneMuted

        val micOnIcon = ContextCompat.getDrawable(
            this, R.drawable.ic_baseline_mic_none)
        val micOffIcon = ContextCompat.getDrawable(
            this, R.drawable.ic_baseline_mic_off)
        val getMicrophoneStatusIcon: () -> Drawable = {
            if (isMicrophoneMuted) micOnIcon as Drawable
            else micOffIcon as Drawable
        }

        view.setImageDrawable(getMicrophoneStatusIcon())
    }

    private fun onMeetingDismissed()
    {
        Log.d(TAG, "CALL_DISMISSED")
    }

    private fun onVideoFrameSwitched()
    {
        Log.d(TAG, "FRAME_SWITCHED")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean
    {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                secondaryVideoContainerXPos = view.x - event.rawX
                secondaryVideoContainerYPos = view.y - event.rawY
                secondaryVideoLastAction = MotionEvent.ACTION_DOWN
            }

            MotionEvent.ACTION_MOVE -> {
                view.y = event.rawY + secondaryVideoContainerYPos
                view.x = event.rawX + secondaryVideoContainerXPos
                secondaryVideoLastAction = MotionEvent.ACTION_MOVE
            }

            MotionEvent.ACTION_UP -> {
                if (secondaryVideoLastAction == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "CONTAINER_CLICKED")
                }
            }

            else -> return false
        }

        return true
    }

    override fun onError(errorCode: Int) {
        Log.d(TAG, "ON_ERROR $errorCode")
    }

    override fun onSessionJoin() {
        Log.d(TAG, "onSessionJoin")
    }

    override fun onSessionLeave() {
        Log.d(TAG, "onSessionLeave")
    }

    override fun onUserJoin(
        userHelper: ZoomVideoSDKUserHelper?,
        userList: MutableList<ZoomVideoSDKUser>?
    ) {
        if (userList != null) {
            findViewById<TextView>(R.id.doctorName).text = userList[0].userName
            userList[0].videoCanvas.let { canvas ->
                val doctorCanvas = findViewById<ZoomVideoSDKVideoView>(R.id.primaryVideoView)
                doctorCanvas.visibility = View.VISIBLE
                val videoAspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
                canvas.subscribe(doctorCanvas, videoAspect)
            }
        }

        // userList?.forEach { user ->
        //     if (user.userName.toString()  == "Chrome-298") {
        //         findViewById<TextView>(R.id.doctorName).text = user.userName
        //         user.videoCanvas.let { canvas ->
        //             val doctorCanvas = findViewById<ZoomVideoSDKVideoView>(R.id.primaryVideoView)
        //             doctorCanvas.visibility = View.VISIBLE
        //             val videoAspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
        //             canvas.subscribe(doctorCanvas, videoAspect)
        //         }
        //     }
        // }

        this.zoomInstance.session.mySelf.videoCanvas.let {
            val customerCanvas = findViewById<ZoomVideoSDKVideoView>(R.id.secondaryVideoView)
            customerCanvas.visibility = View.VISIBLE
            val videoAspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Full_Filled
            it.subscribe(customerCanvas, videoAspect)
        }
    }

    override fun onUserVideoStatusChanged(
        videoHelper: ZoomVideoSDKVideoHelper?,
        userList: MutableList<ZoomVideoSDKUser>?
    ) {
        Log.d(TAG, "onUserVideoStatusChanged")
    }

    override fun onUserAudioStatusChanged(
        audioHelper: ZoomVideoSDKAudioHelper?,
        userList: MutableList<ZoomVideoSDKUser>?
    ) {
        Log.d(TAG, "onUserAudioStatusChanged")
    }

    override fun onUserActiveAudioChanged(
        audioHelper: ZoomVideoSDKAudioHelper?,
        list: MutableList<ZoomVideoSDKUser>?
    ) {
        Log.d(TAG, "onUserActiveAudioChanged")
    }

    override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
        Log.d(TAG, "onMixedAudioRawDataReceived")
    }

    override fun onOneWayAudioRawDataReceived(
        rawData: ZoomVideoSDKAudioRawData?,
        user: ZoomVideoSDKUser?
    ) {
        Log.d(TAG, "onOneWayAudioRawDataReceived")
    }

    override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
        Log.d(TAG, "onShareAudioRawDataReceived")
    }

    override fun onHostAskUnmute() {
        Log.d(TAG, "onHostAskUnmute")
    }

    override fun onUserLeave(userHelper: ZoomVideoSDKUserHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
    override fun onUserShareStatusChanged(shareHelper: ZoomVideoSDKShareHelper?, userInfo: ZoomVideoSDKUser?, status: ZoomVideoSDKShareStatus?) {}
    override fun onLiveStreamStatusChanged(liveStreamHelper: ZoomVideoSDKLiveStreamHelper?, status: ZoomVideoSDKLiveStreamStatus?) { }
    override fun onChatNewMessageNotify(chatHelper: ZoomVideoSDKChatHelper?, messageItem: ZoomVideoSDKChatMessage?) {}
    override fun onUserHostChanged(userHelper: ZoomVideoSDKUserHelper?, userInfo: ZoomVideoSDKUser?) {}
    override fun onUserManagerChanged(user: ZoomVideoSDKUser?) {}
    override fun onUserNameChanged(user: ZoomVideoSDKUser?) { }
    override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) { }
    override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {}
    override fun onCommandReceived(sender: ZoomVideoSDKUser?, strCmd: String?) {}
    override fun onCommandChannelConnectResult(isSuccess: Boolean) {}
    override fun onCloudRecordingStatus(status: ZoomVideoSDKRecordingStatus?) {}
    override fun onInviteByPhoneStatus(status: ZoomVideoSDKPhoneStatus?, reason: ZoomVideoSDKPhoneFailedReason?) {}
}