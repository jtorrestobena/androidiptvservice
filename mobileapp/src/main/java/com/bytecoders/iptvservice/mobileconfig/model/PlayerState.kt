package com.bytecoders.iptvservice.mobileconfig.model

sealed class PlayerState
class PlayerStatePlaying(val title: String) : PlayerState()
class PlayerStatePlayError(val title: String, val error: Throwable) : PlayerState()