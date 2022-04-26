package com.github.bakode.zoomintexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG: String = MainActivity::class.simpleName as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.newMeetingBtn).setOnClickListener {
            Log.d(TAG, "NEXT_MEETING_ACTIVITY")

            startActivity(Intent(
                this,
                MeetingActivity::class.java
            ))
        }
    }
}