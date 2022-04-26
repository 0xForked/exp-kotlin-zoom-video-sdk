package com.github.bakode.zoomintexample

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MeetingActivity : AppCompatActivity(), View.OnTouchListener {
    companion object {
        val TAG: String = MeetingActivity::class.simpleName as String

        var muteStatus: Boolean = false

        var secondaryVideoContainerXPos = 0f
        var secondaryVideoContainerYPos = 0f
        var secondaryVideoLastAction = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

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

    private fun onMicrophoneMuted(view: FloatingActionButton) {
        muteStatus = !muteStatus

        val micOn = ContextCompat.getDrawable(this, R.drawable.ic_baseline_mic_none)
        val micOff = ContextCompat.getDrawable(this, R.drawable.ic_baseline_mic_off)
        val icon: () -> Drawable = { if (muteStatus) micOn as Drawable else micOff as Drawable }

        view.setImageDrawable(icon())

        Log.d(TAG, "MUTE_MICROPHONE $muteStatus")
    }

    private fun onMeetingDismissed() {
        Log.d(TAG, "CALL_DISMISSED")
    }

    private fun onVideoFrameSwitched() {
        Log.d(TAG, "FRAME_SWITCHED")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
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