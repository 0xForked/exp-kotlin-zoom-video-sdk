package com.github.bakode.zoomintexample

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.bakode.zoomintexample.utils.ZoomOption
import com.google.android.material.floatingactionbutton.FloatingActionButton
import us.zoom.sdk.*


class MeetingActivity : AppCompatActivity(), View.OnTouchListener
{
    companion object
    {
        val TAG: String = MeetingActivity::class.simpleName as String

        var isMicrophoneMuted: Boolean = false

        var secondaryVideoContainerXPos = 0f
        var secondaryVideoContainerYPos = 0f
        var secondaryVideoLastAction = 0

        val videoAspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Full_Filled
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        val instance = ZoomVideoSDK.getInstance()

        val joinSession = instance.joinSession(ZoomOption.zoomSessionCtx())
        if (joinSession == null) {
            Log.d(MainActivity.TAG, "NO ZOOM SESSION")
            return
        }

        instance.session.mySelf.videoCanvas.let {
            val customerCanvas = findViewById<ZoomVideoSDKVideoView>(R.id.secondaryVideoView)
            customerCanvas.visibility = View.VISIBLE
            it.subscribe(customerCanvas, videoAspect)
        }

        instance.session.remoteUsers.let {
            Log.d(TAG, it.toString())
        }

        instance.videoHelper.rotateMyVideo(display?.rotation as Int)

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
}