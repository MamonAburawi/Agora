package com.example.agora.screens.encounter

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agora.R
import com.example.agora.databinding.EncounterBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.IRtcEngineEventHandler.RemoteAudioStats
import io.agora.rtc.IRtcEngineEventHandler.RemoteVideoStats
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration


@SuppressLint("SetTextI18n")
class Encounter : Fragment() {

    companion object {
        private const val TAG = "Encounter"

        private const val APP_ID = "1c46ddd6505f4e51972bd10f622359d8"
        private const val TOKEN =  "007eJxTYPg3W10rUTXqos3BpsmKH9Ya3u+Z1WJx55JJ5hKXf1l5b1UUGAyTTcxSUlLMTA1M00xSTQ0tzY2SUgwN0syMjIxNLVMsXubKJGful00W513NxMgAgSA+I4MRAwMAJHEfSg=="
        private const val CHANNEL = "2"
    }


    private lateinit var binding: EncounterBinding
    private lateinit var viewModel: EncounterViewModel

    private var mTxQuality = -1
    private var mRxQuality:Int = -1
    private var mAudioStats: RemoteAudioStats? = null
    private var mVideoStats: RemoteVideoStats? = null

    private var mRtcEngine: RtcEngine?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this)[EncounterViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater, R.layout.encounter, container, false)


        checkPermission()
        setViews()
        setObserves()


        return binding.root
    }


    private fun setViews() {
        binding.apply {


            /** button microphone **/
            btnMicrophone.setOnClickListener {
                enableAudio()
            }


            /** button switch camera **/
            btnCameraSwitch.setOnClickListener {
                onSwitchCamera()
            }

            /** button video **/
            btnVideo.setOnClickListener {
                enableLocalCamera()
            }

            /** button end call **/
            btnEndCall.setOnClickListener {
                onEndCall()
            }



        }
    }


    private fun setObserves(){
        viewModel.navigateToHome.observe(viewLifecycleOwner){
            if (it == true){
                findNavController().navigateUp()
                viewModel.navigateToHomeDone()
            }
        }
    }

    private fun checkPermission() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    initializeAndJoinChannel()
                    initAgoraEngineAndJoinChannel()

                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {

                }
            }).check()
    }


    private fun enableLocalCamera() {
        val iv = binding.btnVideo as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.setImageResource(R.drawable.ic_video_on)
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setImageResource(R.drawable.ic_video_off)
//            iv.setColorFilter(resources.getColor(R.color.purple_200), PorterDuff.Mode.MULTIPLY)
        }
        mRtcEngine!!.muteLocalVideoStream(iv.isSelected)
        val container = binding.localVideoViewContainer
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.setZOrderMediaOverlay(!iv.isSelected)
//        surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
    }


    private fun enableAudio() {
        val iv = binding.btnMicrophone as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.setImageResource(R.drawable.ic_mic_on)
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setImageResource(R.drawable.ic_mic_off)
        }
        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    private fun onSwitchCamera() {
        mRtcEngine!!.switchCamera()
    }

    private fun onEndCall() {
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        requireActivity().onBackPressed()
    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {


        // remote user
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.d(TAG,"onUserJoined..")
            requireActivity().runOnUiThread { setupRemoteVideo(uid) }

        }


        // remote user
        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d(TAG,"onUserOffline..")
            requireActivity().runOnUiThread { onRemoteUserLeft() }
        }


        // remote user
        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            Log.d(TAG,"onUserMuteVideo..")
            requireActivity().runOnUiThread{ onRemoteUserVideoMuted(uid, muted) }
        }


        // remote user
        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            super.onUserMuteAudio(uid, muted)
            requireActivity().runOnUiThread { onRemoteUserAudioMuted(uid,muted)}
        }



    }

    private fun onRemoteUserAudioMuted(uid: Int, muted: Boolean) {
        if (muted) {
            Log.d(TAG,"onUserMuteAudio: muted")
            binding.remoteMicStatus.setImageResource(R.drawable.ic_mic_off)
        }else{
            Log.d(TAG,"onUserMuteAudio: unMuted")
            binding.remoteMicStatus.setImageResource(R.drawable.ic_mic_on)
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo()
        joinChannel()
    }


    private fun onRemoteUserLeft() {
        val container = binding.remoteVideoViewContainer
        container.removeAllViews()
        Toast.makeText(requireContext(),"user left the call",Toast.LENGTH_SHORT).show()
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        if (muted) {
            binding.remoteVideoViewContainer.visibility = View.GONE
            binding.remoteVideoStatus.setImageResource(R.drawable.ic_video_off)
        }else{
            binding.remoteVideoViewContainer.visibility = View.VISIBLE
            binding.remoteVideoStatus.setImageResource(R.drawable.ic_video_on)
        }
    }


    private fun setupRemoteVideo(uid: Int) {
        val remoteFrame = RtcEngine.CreateRendererView(requireActivity().baseContext)
        remoteFrame.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
        updateStatsDisplay()
    }


    private fun setupVideoProfile() {
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }


    private fun joinChannel() {
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0) // if you do not specify the uid, we will generate the uid for you
    }


    private fun setupLocalVideo() {
        val container = binding.localVideoViewContainer
        val surfaceView = RtcEngine.CreateRendererView(requireActivity().baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }


    private fun qualityStringFromQualityNumber(number: Int): String {
        return when (number) {
            IRtcEngineEventHandler.Quality.EXCELLENT -> "Excellent 👌"
            IRtcEngineEventHandler.Quality.GOOD -> "Good 🙂"
            IRtcEngineEventHandler.Quality.POOR -> "Poor 🙁"
            IRtcEngineEventHandler.Quality.BAD -> "Bad 😢"
            IRtcEngineEventHandler.Quality.VBAD -> "Very Bad 🤮"
            IRtcEngineEventHandler.Quality.DOWN -> "Network Down"
            else -> "Unknown"
        }
    }

    private fun updateStatsDisplay() {
        val statsDisplay = binding.statsDisplay
        val audioQuality =
            if (mAudioStats == null) IRtcEngineEventHandler.Quality.UNKNOWN else mAudioStats!!.quality
        val videoBitrate = if (mVideoStats == null) 0 else mVideoStats!!.receivedBitrate
        val stats = """
            Transmission: ${qualityStringFromQualityNumber(mTxQuality)}
            Receiving: ${qualityStringFromQualityNumber(mRxQuality)}
            Audio: ${qualityStringFromQualityNumber(audioQuality)}
            Video Bitrate: ${videoBitrate}Kbps
            """.trimIndent()
        statsDisplay.text = stats
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(requireActivity().baseContext, APP_ID, mRtcEventHandler)
            mRtcEngine!!.addHandler(object : IRtcEngineEventHandler() {
                override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
                    mTxQuality = txQuality
                    mRxQuality = txQuality
                    updateStatsDisplay()
                }

                override fun onRemoteAudioStats(stats: RemoteAudioStats) {
                    mAudioStats = stats
                    updateStatsDisplay()


                }

                override fun onRemoteVideoStats(stats: RemoteVideoStats) {
                    mVideoStats = stats
                    updateStatsDisplay()
                }



            })
        } catch (e: java.lang.Exception) {
            Log.e("Agora", Log.getStackTraceString(e))
            throw RuntimeException(
                """
           
            NEED TO check rtc sdk init fatal error
              ${Log.getStackTraceString(e)}
                """.trimIndent()
            )
        }
    }

    // Kotlin
    override fun onDestroy() {
        super.onDestroy()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }




}