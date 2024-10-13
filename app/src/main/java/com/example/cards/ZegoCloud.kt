package com.example.cards

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.callback.IZegoEventHandler
import im.zego.zegoexpress.constants.ZegoPlayerState
import im.zego.zegoexpress.constants.ZegoPublisherState
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import im.zego.zegoexpress.constants.ZegoScenario
import im.zego.zegoexpress.constants.ZegoUpdateType
import im.zego.zegoexpress.entity.ZegoEngineProfile
import im.zego.zegoexpress.entity.ZegoRoomConfig
import im.zego.zegoexpress.entity.ZegoStream
import im.zego.zegoexpress.entity.ZegoUser
import org.json.JSONObject

//      https://www.youtube.com/watch?v=182OiRhU6Kk&ab_channel=PapayaCoders
class ZegoCloud {

    fun createEngine(application: Application,context: Context,gameCode:String){
        val profile = ZegoEngineProfile()
        profile.appID = Utils.APP_ID.toLong()
        profile.appSign = Utils.APP_SIGN_KEY
        profile.scenario = ZegoScenario.DEFAULT // General scenario.
        profile.application = application

        var pref:SharedPreferences = context.getSharedPreferences("userdata",AppCompatActivity.MODE_PRIVATE)
        var userId = pref.getString("userId", "Guesttt").toString()
        var userName = pref.getString("username", "Guest").toString()

        ZegoExpressEngine.createEngine(profile, null)

        loginRoom(userId,userName,gameCode)

        startEventListener()
    }

    fun destroyEngine(){
        ZegoExpressEngine.destroyEngine(null)
    }

    fun startEventListener(){
        ZegoExpressEngine.getEngine().setEventHandler(object : IZegoEventHandler() {
            override fun onRoomStreamUpdate(
                roomID: String?,
                updateType: ZegoUpdateType?,
                streamList: ArrayList<ZegoStream>?,
                extendedData: JSONObject?
            ) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData)
                if (updateType == ZegoUpdateType.ADD) {
                    startPlayStream(streamList!!.get(0).streamID);
                } else {
                    stopPlayStream(streamList!!.get(0).streamID);
                }
            }

            override fun onRoomUserUpdate(
                roomID: String?,
                updateType: ZegoUpdateType?,
                userList: ArrayList<ZegoUser>?
            ) {
                super.onRoomUserUpdate(roomID, updateType, userList)

            }

            override fun onRoomStateChanged(
                roomID: String?,
                reason: ZegoRoomStateChangedReason?,
                errorCode: Int,
                extendedData: JSONObject?
            ) {
                super.onRoomStateChanged(roomID, reason, errorCode, extendedData)
            }

            override fun onPublisherStateUpdate(
                streamID: String?,
                state: ZegoPublisherState?,
                errorCode: Int,
                extendedData: JSONObject?
            ) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData)
            }

            override fun onPlayerStateUpdate(
                streamID: String?,
                state: ZegoPlayerState?,
                errorCode: Int,
                extendedData: JSONObject?
            ) {
                super.onPlayerStateUpdate(streamID, state, errorCode, extendedData)
            }


        })
    }

    fun stopEventListener(){
        ZegoExpressEngine.getEngine().setEventHandler(null);
    }

    fun loginRoom(userId:String, userName:String,gameCode: String){
        var user = ZegoUser(userId,userName)
        var roomConfig = ZegoRoomConfig()
        roomConfig.isUserStatusNotify = true

        GameDataFirebase.fetchGameModel(gameCode){}
        var model = GameDataFirebase.gamemodel.value?.roomId

        ZegoExpressEngine.getEngine().loginRoom(model,user,roomConfig) { error: Int, extendedData: JSONObject ->
            if (error == 0) {
                startPublish()
            } else {
                Log.e("ZegoCloud", "Failed to log in with error code: $error")
            }
        }
    }

    fun logoutRoom() {
        ZegoExpressEngine.getEngine().logoutRoom()
    }

    fun startPublish() {
        val streamID: String = "room_${(100..999).random()}"
        ZegoExpressEngine.getEngine().enableCamera(false)
        ZegoExpressEngine.getEngine().startPublishingStream(streamID)
    }

    fun stopPublish() {
        ZegoExpressEngine.getEngine().stopPublishingStream()
    }

    fun startPlayStream(streamID: String?) {
        ZegoExpressEngine.getEngine().startPlayingStream(streamID)
    }

    fun stopPlayStream(streamID: String?) {
        ZegoExpressEngine.getEngine().stopPlayingStream(streamID)
    }

    fun stopCall(){
        stopEventListener()
        stopPublish()
        logoutRoom()
        destroyEngine()
    }
}