package com.github.bakode.zoomintexample

interface Constants {
    companion object {
        var WEB_DOMAIN = "zoom.us"

        var USER_ID = "Your user ID from REST API"

        var ZOOM_ACCESS_TOKEN = "Your zak from REST API"

        var MEETING_ID: String? = null

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
         * "tokenExp": long // token expire time
         * }
         */
        val SDK_JWT_TOKEN = ""
    }
}