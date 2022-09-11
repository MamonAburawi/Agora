package com.example.agora.screens.encounter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.R
import com.example.agora.setQuality
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class EncounterViewModel : ViewModel() {

    companion object {
        private const val TAG = "EncounterViewModel"

        private const val APP_ID = "3b561a2f14954f4aacc036c882050537"
        private const val TOKEN =  "007eJxTYJh3bb1eyOQ1F+IthBZ733z20HGmr/M1rwVbyj7MODTlxqccBQbjJFMzw0SjNEMTS1OTNJPExORkA2OzZAsLIwNTA1Njc7PbksnbdKST6z6JMTEyQCCIz8hgyMAAAKOaIMQ="

    }

    val isVideoEnabled = MutableLiveData<Boolean>(true)
//    val isVideoEnabled: LiveData<Boolean> = _isVideoEnabled

    val isAudioEnabled = MutableLiveData<Boolean>(true)
//    val isAudioEnabled: LiveData<Boolean> = _isAudioEnabled

    val isMicEnabled = MutableLiveData<Boolean>(false)


//    private val _userJoined = MutableLiveData<Int>()
//    val userJoined: LiveData<Int> = _userJoined

    val txQuality = MutableLiveData<Int>()

    val rxQuality = MutableLiveData<Int>()


    private val _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?> = _navigateToHome

//
//
////    private var mRtcEngine: RtcEngine? = null
//    private var mRtcEngine = MutableLiveData<RtcEngine>()
//
//    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
//
//
//        override fun onUserJoined(uid: Int, elapsed: Int) {
//            _userJoined.value = uid
//            Log.d(TAG,"user is joined")
//        }
//
//
//        @Deprecated("Deprecated in Java")
//        override fun onMicrophoneEnabled(enabled: Boolean) {
//            super.onMicrophoneEnabled(enabled)
//            _isAudioEnabled.value = enabled
//        }
//
//
//
//        override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
//            super.onNetworkQuality(uid, txQuality, rxQuality)
//            //txQuality -> for up link
//            // rxQuality -> for down link
//            _txQuality.value = txQuality
//            _rxQuality.value = rxQuality
//
//        }
//
//
//        override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
//            super.onUserEnableLocalVideo(uid, enabled)
//            _isVideoEnabled.value = enabled
//        }
//
//
//        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
//            super.onUserMuteAudio(uid, muted)
//            Log.d("RtcEventHandler","userId: $uid , user mute audio: $muted")
//        }
//
//
//        override fun onUserEnableVideo(uid: Int, enabled: Boolean) {
//            super.onUserEnableVideo(uid, enabled)
//            Log.d("RtcEventHandler","userId: $uid , user enable video: $enabled")
//        }
//
//    }
//
//
//
//    fun initCall(context: Context, localVideoViewContainer: FrameLayout,channel: String) {
//        try {
//            mRtcEngine.value = RtcEngine.create(context, APP_ID, mRtcEventHandler)
//            mRtcEngine.value!!.enableVideo()
//            val localFrame = RtcEngine.CreateRendererView(context)
//            localVideoViewContainer.addView(localFrame)
//            mRtcEngine.value!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))
//            mRtcEngine.value!!.joinChannel(TOKEN, channel, "", 0)
//
//        }
//        catch (e: Exception) { }
//
//
//
//    }
//
//
//    fun setupRemoteVideo(context: Context,uid: Int, remoteVideoViewContainer: FrameLayout) {
//        val remoteFrame = RtcEngine.CreateRendererView(context)
//        remoteFrame.setZOrderMediaOverlay(true)
//        remoteVideoViewContainer.addView(remoteFrame)
//        mRtcEngine.value!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
//    }
//


    fun endCall(rtcEngine: RtcEngine) {
        viewModelScope.launch() {
            rtcEngine.leaveChannel()
            RtcEngine.destroy()
            _navigateToHome.value = true
        }
    }


    fun navigateToHomeDone() {
        _navigateToHome.value = null
    }



}