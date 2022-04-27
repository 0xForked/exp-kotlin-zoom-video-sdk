package com.github.bakode.zoomintexample.utils

import com.github.bakode.zoomintexample.MeetingActivity
import us.zoom.sdk.ZoomVideoSDKAudioOption
import us.zoom.sdk.ZoomVideoSDKInitParams
import us.zoom.sdk.ZoomVideoSDKSessionContext
import us.zoom.sdk.ZoomVideoSDKVideoOption

object ZoomOption {
    val zoomSDKParams: () -> ZoomVideoSDKInitParams = {
        ZoomVideoSDKInitParams().apply {
            this.domain = Constants.WEB_DOMAIN
            this.videoRawDataMemoryMode = Constants.DEFAULT_MEMORY_MODE
            this.audioRawDataMemoryMode = Constants.DEFAULT_MEMORY_MODE
            this.logFilePrefix = MeetingActivity.TAG
            this.enableLog = true
        }
    }

    val zoomAudioOpts: () -> ZoomVideoSDKAudioOption = {
        ZoomVideoSDKAudioOption().apply {
            this.connect = true
            this.mute = false
        }
    }

    val zoomVideoOpts: () -> ZoomVideoSDKVideoOption = {
        ZoomVideoSDKVideoOption().apply {
            this.localVideoOn = true
        }
    }

    // TODO - INJECT DATA: NAME, USER, TOKEN, PASSWORD, IDLE
    val zoomSessionCtx: () -> ZoomVideoSDKSessionContext = {
        ZoomVideoSDKSessionContext().apply {
            this.audioOption = zoomAudioOpts()
            this.videoOption = zoomVideoOpts()
            this.token = Constants.JWT_TOKEN // TODO CHANGE THIS
            this.sessionIdleTimeoutMins = 10 // TODO CHANGE THIS
            this.sessionName = "tpcName1" // TODO CHANGE THIS
            this.sessionPassword = "" // TODO CHANGE THIS
            this.userName = "Alpha Test" // TODO CHANGE THIS
        }
    }
}
