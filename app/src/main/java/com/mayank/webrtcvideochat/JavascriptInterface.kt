package com.mayank.webrtcvideochat

import android.webkit.JavascriptInterface


class JavascriptInterface(val videoActivity: videoActivity) {

    @JavascriptInterface
    public fun onPeerConnected(){
        videoActivity.onPeerConnected()
    }
    @JavascriptInterface
    public fun tost(text:String){
        videoActivity.tost(text)
    }
}