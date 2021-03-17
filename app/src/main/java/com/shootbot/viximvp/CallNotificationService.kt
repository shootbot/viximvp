package com.shootbot.viximvp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shootbot.viximvp.activities.IncomingInvitationActivity
import com.shootbot.viximvp.utilities.Constants


class CallNotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val callReceiver = Intent(applicationContext, CallNotificationReceiver::class.java)
        callReceiver.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, intent?.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE))
        callReceiver.putExtra(Constants.KEY_FIRST_NAME, intent?.getStringExtra(Constants.KEY_FIRST_NAME))
        callReceiver.putExtra(Constants.KEY_LAST_NAME, intent?.getStringExtra(Constants.KEY_LAST_NAME))
        callReceiver.putExtra(Constants.KEY_EMAIL, intent?.getStringExtra(Constants.KEY_EMAIL))
        callReceiver.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, intent?.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
        callReceiver.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, intent?.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))

        val receiveCallIntent = Intent(callReceiver)
        receiveCallIntent.action = "RECEIVE_CALL"

        val cancelCallIntent = Intent(callReceiver)
        cancelCallIntent.action = "CANCEL_CALL"

        val activityIntent = Intent(applicationContext, IncomingInvitationActivity::class.java)
        activityIntent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, intent?.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE))
        activityIntent.putExtra(Constants.KEY_FIRST_NAME, intent?.getStringExtra(Constants.KEY_FIRST_NAME))
        activityIntent.putExtra(Constants.KEY_LAST_NAME, intent?.getStringExtra(Constants.KEY_LAST_NAME))
        activityIntent.putExtra(Constants.KEY_EMAIL, intent?.getStringExtra(Constants.KEY_EMAIL))
        activityIntent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, intent?.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN))
        activityIntent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, intent?.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))

        val incomingCallPendingIntent = PendingIntent.getActivity(applicationContext, 1200, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val receiveCallPendingIntent = PendingIntent.getBroadcast(applicationContext, 1201, receiveCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val cancelCallPendingIntent = PendingIntent.getBroadcast(applicationContext, 1202, cancelCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        val firstName = intent?.getStringExtra(Constants.KEY_FIRST_NAME)
        val lastName = intent?.getStringExtra(Constants.KEY_LAST_NAME)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("$firstName $lastName")
                .setContentText("Входящий звонок")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(longArrayOf(0))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_accept, "Принять вызов", receiveCallPendingIntent)
                .addAction(R.drawable.ic_reject, "Отменить вызов", cancelCallPendingIntent)
                .setSound(alarmSound)
                .setFullScreenIntent(incomingCallPendingIntent, true)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            buildChannel()

            val notification = builder.build()

            startForeground(120, notification)
        }



        return START_STICKY
    }


    private fun NotificationManager.buildChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Notification Channel"
            val descriptionText = "This is used to demonstrate the Full Screen Intent"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val att = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            channel.setSound(alarmSound, att)

            createNotificationChannel(channel)
        }
    }


    companion object {
        private const val CHANNEL_ID = "channelId"
    }
}
