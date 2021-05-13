package com.shootbot.viximvp.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.shootbot.viximvp.activities.IncomingInvitationActivity
import com.shootbot.viximvp.utilities.Constants
import com.shootbot.viximvp.utilities.Ut
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.MalformedURLException

class CallNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CallNoteReceiver", "onReceive intent first name: " + intent?.getStringExtra(Constants.KEY_FIRST_NAME))
        Log.d("CallNoteReceiver", "onReceive intent inviter token: " + intent?.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
        Log.d("CallNoteReceiver", "onReceive intent meeting room: " + intent?.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))

        intent?.action?.let { action ->
            when (action) {
                "RECEIVE_CALL" -> {
                    val callActivity = Intent(context, IncomingInvitationActivity::class.java)
                    callActivity.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE))
                    callActivity.putExtra(Constants.KEY_FIRST_NAME, intent.getStringExtra(Constants.KEY_FIRST_NAME))
                    callActivity.putExtra(Constants.KEY_LAST_NAME, intent.getStringExtra(Constants.KEY_LAST_NAME))
                    callActivity.putExtra(Constants.KEY_EMAIL, intent.getStringExtra(Constants.KEY_EMAIL))
                    callActivity.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
                    callActivity.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                    callActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    callActivity.putExtra(Constants.IS_CALL_ACCEPTED, true)
                    context?.startActivity(callActivity)
                }
                "CANCEL_CALL" -> {
                    sendInvitationResponse(
                            context,
                            Constants.REMOTE_MSG_INVITATION_REJECTED,
                            intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN),
                            intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE),
                            intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM)
                    )
                }
                else -> {
                    val callActivity = Intent(context, IncomingInvitationActivity::class.java)
                    callActivity.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE))
                    callActivity.putExtra(Constants.KEY_FIRST_NAME, intent.getStringExtra(Constants.KEY_FIRST_NAME))
                    callActivity.putExtra(Constants.KEY_LAST_NAME, intent.getStringExtra(Constants.KEY_LAST_NAME))
                    callActivity.putExtra(Constants.KEY_EMAIL, intent.getStringExtra(Constants.KEY_EMAIL))
                    callActivity.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
                    callActivity.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                    callActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(callActivity)
                }
            }
        }

        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        with(context!!) {
            sendBroadcast(closeIntent)
            stopService(Intent(context, CallNotificationService::class.java))
        }
    }

    private fun sendInvitationResponse(context: Context?, type: String, receiverToken: String?, meetingType: String?, meetingRoom: String?) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val data = JSONObject()
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

            val body = JSONObject()
            body.put(Constants.REMOTE_MSG_TO, tokens)
            body.put(Constants.REMOTE_MSG_DATA, data)

            Log.d("calls", "send remote, body: " + body.toString())
            sendRemoteMessage(context, body.toString(), type, meetingType, meetingRoom)
        } catch (ex: JSONException) {
            Log.d("calls", "catch 1: " + ex.toString())
        }
    }

    private fun sendRemoteMessage(context: Context?, remoteMessageBody: String, type: String, meetingType: String?, meetingRoom: String?) {
        Ut.pubMessage(remoteMessageBody)

        ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
                Ut.getPushRequestHeaders(),
                remoteMessageBody)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.isSuccessful) {
                            if (type == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
                                try {
                                    Ut.launchConference(
                                            context,
                                            meetingRoom,
                                            meetingType)
                                } catch (ex: MalformedURLException) {
                                    Log.d("calls", "catch 2: " + ex.toString())
                                }
                            }
                        } else {
                            Log.d("calls", "catch 3: " + response.message())
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.d("calls", "catch 4: " + t.toString())
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}