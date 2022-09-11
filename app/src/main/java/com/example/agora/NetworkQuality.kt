package com.example.agora

enum class NetworkQuality {
    UNKNOWN, // The quality is excellent.
    EXCELLENT, // The quality is quite good, but the bitrate may be slightly lower than excellent.
    GOOD, // Users can feel the communication slightly impaired.
    POOR, //  Users can communicate not very smoothly.
    BAD, // The quality is so bad that users can barely communicate.
    VBAD, // The network is disconnected and users cannot communicate at all.
    DOWN, // The network is disconnected and users cannot communicate at all.
    DETECTING // The SDK is detecting the network quality.




}

