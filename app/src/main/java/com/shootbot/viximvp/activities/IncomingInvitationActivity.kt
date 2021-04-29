package com.shootbot.viximvp.activities

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shootbot.viximvp.network.CallNotificationService
import com.shootbot.viximvp.R
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

class IncomingInvitationActivity : AppCompatActivity() {
    private var meetingType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        turnScreenOnAndKeyguardOff()

        val imageAcceptInvitation = findViewById<ImageView>(R.id.imageAcceptInvitation)
        imageAcceptInvitation.setOnClickListener {
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
        }
        val imageRejectInvitation = findViewById<ImageView>(R.id.imageRejectInvitation)
        imageRejectInvitation.setOnClickListener {
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
        }

        val isCallAccepted = intent.getBooleanExtra(Constants.IS_CALL_ACCEPTED, false)

        if (isCallAccepted) {
            imageAcceptInvitation.visibility = View.GONE
            imageRejectInvitation.visibility = View.GONE
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))

        }

        val imageMeetingType = findViewById<ImageView>(R.id.imageMeetingType)
        meetingType = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)
        if ("video" == meetingType) {
            imageMeetingType.setImageResource(R.drawable.ic_video)
        } else {
            imageMeetingType.setImageResource(R.drawable.ic_audio)
        }
        val textFirstChar = findViewById<TextView>(R.id.textFirstChar)
        val textUsername = findViewById<TextView>(R.id.textUsername)
        val textEmail = findViewById<TextView>(R.id.textEmail)
        val firstName = intent.getStringExtra(Constants.KEY_FIRST_NAME)
        if (firstName != null) {
            textFirstChar.text = firstName.substring(0, 1)
        }
        textUsername.text = String.format(
                "%s %s",
                firstName,
                intent.getStringExtra(Constants.KEY_LAST_NAME)
        )
        textEmail.text = intent.getStringExtra(Constants.KEY_EMAIL)

    }

    private fun sendInvitationResponse(type: String, receiverToken: String?) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val data = JSONObject()
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

            val body = JSONObject()
            body.put(Constants.REMOTE_MSG_TO, tokens)
            body.put(Constants.REMOTE_MSG_DATA, data)

            sendRemoteMessage(body.toString(), type)
        } catch (e: JSONException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cleanNotification() {
        val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        with(this) {
            sendBroadcast(closeIntent)
            stopService(Intent(this, CallNotificationService::class.java))
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
                Ut.getPushRequestHeaders(),
                remoteMessageBody)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.isSuccessful) {
                            if (type == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
                                try {
                                    Ut.launchConference(
                                            this@IncomingInvitationActivity,
                                            intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM),
                                            meetingType)
                                    cleanNotification()
                                    finish()
                                } catch (e: MalformedURLException) {
                                    Toast.makeText(this@IncomingInvitationActivity, e.message, Toast.LENGTH_SHORT).show()
                                    cleanNotification()
                                    finish()
                                }
                            } else {
                                // Toast.makeText(IncomingInvitationActivity.this, "Invitation rejected", Toast.LENGTH_SHORT).show();
                                cleanNotification()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@IncomingInvitationActivity, response.message(), Toast.LENGTH_SHORT).show()
                            cleanNotification()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(this@IncomingInvitationActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (Constants.REMOTE_MSG_INVITATION_CANCELED == type) {
                // Toast.makeText(context, "Invitation canceled", Toast.LENGTH_SHORT).show();
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager
                .getInstance(applicationContext)
                .registerReceiver(invitationResponseReceiver, IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager
                .getInstance(applicationContext)
                .unregisterReceiver(invitationResponseReceiver)
    }

    override fun onDestroy() {
        turnScreenOffAndKeyguardOn()
        super.onDestroy()
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        with(getSystemService(KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@IncomingInvitationActivity, null)
            }
        }
    }

    private fun turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

}