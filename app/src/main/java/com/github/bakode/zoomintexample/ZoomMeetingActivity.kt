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

        var secondaryVideoContainerXPos = 0f
        var secondaryVideoContainerYPos = 0f
        var secondaryVideoLastAction = 0
    }

    private lateinit var zoomInstance: ZoomVideoSDK
    private lateinit var remoteUser: ZoomVideoSDKUser

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

        this.zoomInstance.videoHelper
            .rotateMyVideo(display?.rotation as Int)

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
            .setOnClickListener { this.finish() }

        findViewById<FloatingActionButton>(R.id.fabSwitchVideoFrame)
            .setOnClickListener { this.onVideoFrameSwitched() }

        findViewById<View>(R.id.secondaryVideoContainer)
            .setOnTouchListener(this)
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

    private fun onMicrophoneMuted(view: FloatingActionButton)
    {
        val customer = this.zoomInstance.session.mySelf

        val isMuted = customer.audioStatus.isMuted
        val audioHelper = this.zoomInstance.audioHelper
        if (!isMuted) audioHelper.muteAudio(customer)
        else audioHelper.unMuteAudio(customer)

        view.setImageDrawable(getMicrophoneStatusIcon(isMuted))
    }

    private fun getMicrophoneStatusIcon(isMuted: Boolean): Drawable
    {
        val iconOn = ContextCompat.getDrawable(
            this, R.drawable.ic_baseline_mic_none)
        val iconOff = ContextCompat.getDrawable(
            this, R.drawable.ic_baseline_mic_off)

        return if (isMuted) iconOn as Drawable
        else iconOff as Drawable
    }

    private fun onVideoFrameSwitched()
    {
        this.zoomInstance.videoHelper.switchCamera()
    }

    private fun setupVideoFrame()
    {
        findViewById<TextView>(R.id.doctorName).text = this.remoteUser.userName
        val primaryVideoView = findViewById<ZoomVideoSDKVideoView>(R.id.primaryVideoView)
        val secondaryVideoView = findViewById<ZoomVideoSDKVideoView>(R.id.secondaryVideoView)

        primaryVideoView.visibility = View.VISIBLE
        secondaryVideoView.visibility = View.VISIBLE

        val originalRatio = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
        val fullFilledRatio = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Full_Filled
        this.remoteUser.videoCanvas.subscribe(primaryVideoView, originalRatio)
        this.zoomInstance.session.mySelf.videoCanvas
            .subscribe(secondaryVideoView, fullFilledRatio)
    }

    override fun onUserJoin(
        userHelper: ZoomVideoSDKUserHelper?,
        userList: MutableList<ZoomVideoSDKUser>?
    ) {
        if (userList != null) {
            this.remoteUser = userList[0]
            this.setupVideoFrame()
        }
    }

    override fun onError(errorCode: Int)
    {
        Log.d(TAG, "ON_ERROR $errorCode")
    }


    // =============================================================================================
    override fun onUserVideoStatusChanged(videoHelper: ZoomVideoSDKVideoHelper?, userList: MutableList<ZoomVideoSDKUser>?) {}
    override fun onUserAudioStatusChanged(audioHelper: ZoomVideoSDKAudioHelper?, userList: MutableList<ZoomVideoSDKUser>?) { }
    override fun onUserActiveAudioChanged(audioHelper: ZoomVideoSDKAudioHelper?, list: MutableList<ZoomVideoSDKUser>?) {}
    override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) { }
    override fun onOneWayAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?, user: ZoomVideoSDKUser?) {}
    override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {}
    override fun onHostAskUnmute() {}
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
    override fun onSessionLeave() {}
    override fun onSessionJoin() {}
    // =============================================================================================


    override fun onDestroy()
    {
        super.onDestroy()
        zoomInstance.leaveSession(false)
    }
}