package com.github.bakode.zoomintexample.utils

import us.zoom.sdk.ZoomVideoSDKRawDataMemoryMode

object Constants
{
    val WEB_DOMAIN = "https://zoom.us"

    val DEFAULT_MEMORY_MODE = ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap

    /**
     * We recommend that, you can generate jwttoken on your own server instead of hardcore in the code.
     * We hardcore it here, just to run the demo.
     *
     * You can generate a jwttoken on the https://jwt.io/
     * with this payload:
     * {
     * "appKey": "string", // app key
     * "iat": long, // access token issue timestamp
     * "exp": long, // access token expire time
     * "tokenExp": long // token expire time 1800
     * }
     */
    val JWT_TOKEN = ""
}