package com.example.agora.screens

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.example.agora.R


@BindingAdapter("setAudio")
fun setAudio(button: ImageButton, isEnabled: Boolean?) {
    if (isEnabled == true){
        button.setImageResource(R.drawable.ic_mic_on)
    }else if(isEnabled == false){
        button.setImageResource(R.drawable.ic_mic_off)
    }
}

@BindingAdapter("setVideo")
fun setVideo(button: ImageButton, isEnabled: Boolean?) {
    if (isEnabled == true){
        button.setImageResource(R.drawable.ic_video_on)
    }else if(isEnabled == false){
        button.setImageResource(R.drawable.ic_video_off)
    }
}

@BindingAdapter("setMic")
fun setMicIcon(button: ImageButton, isEnabled: Boolean?) {
    if (isEnabled == true){
        button.setImageResource(R.drawable.ic_mic_on)
    }else if(isEnabled == false){
        button.setImageResource(R.drawable.ic_mic_off)
    }
}