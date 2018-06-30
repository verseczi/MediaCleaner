package com.mediacleaner.DataModels.Emby

data class SessionInfo(
        val SupportedCommands: List<Any>,
        val PlayableMediaTypes: List<Any>,
        val Id: String,
        val ServerId: String,
        val UserId: String,
        val RemoteEndPoint: String,
        val UserName: String,
        val AdditionalUsers: List<Any>,
        val ApplicationVersion: String,
        val Client: String,
        val LastActivityDate: String,
        val DeviceName: String,
        val DeviceId: String,
        val SupportsRemoteControl: Boolean,
        val PlayState: PlayState
)