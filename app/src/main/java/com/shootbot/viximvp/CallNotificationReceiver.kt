package com.shootbot.viximvp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.shootbot.viximvp.activities.IncomingInvitationActivity
import com.shootbot.viximvp.network.ApiClient
import com.shootbot.viximvp.network.ApiService
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
            val body = JSONObject()
            val data = JSONObject()
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)
            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
            sendRemoteMessage(context, body.toString(), type, meetingType, meetingRoom)
        } catch (_: JSONException) {

        }
    }

    private fun sendRemoteMessage(context: Context?, remoteMessageBody: String, type: String, meetingType: String?, meetingRoom: String?) {
        ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),
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
                                } catch (_: MalformedURLException) {
                                    Log.d("TEST", "TEST")
                                }
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}