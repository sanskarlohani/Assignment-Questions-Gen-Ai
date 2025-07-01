package day14

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sanskar.firebase_integration.ui.theme.Firebase_IntegrationTheme


//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        // Handle FCM message
//        Log.d("FCM", "Message data: ${remoteMessage.data}")
//        remoteMessage.notification?.let {
//            Log.d("FCM", "Notification: ${it.title} - ${it.body}")
//            showNotification(it.title, it.body)
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("FCM", "New token: $token")
//        // Send this token to your server if needed
//    }
//
//    private fun showNotification(title: String?, body: String?) {
//        val builder = NotificationCompat.Builder(this, "fcm_channel")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "fcm_channel",
//                "FCM Channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        notificationManager.notify(0, builder.build())
//    }
//}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request notification permission (for Android 13+)
        NotificationUtils.requestNotificationPermission(this)

        // Create channel
        NotificationUtils.createNotificationChannel(this)

        // Show a test notification
        NotificationUtils.showNotification(this, "Hello!", "This is a test notification")
        // Turn off the decor fitting system windows, which allows us to handle insets,enableEdgeToEdge()
        setContent {
            Firebase_IntegrationTheme {

                //AuthScreen()
                StudentScreen()
                // NotificationScreen(this)
            }
        }
    }
}




object NotificationUtils {

    private const val CHANNEL_ID = "my_channel_id"
    private const val CHANNEL_NAME = "My Channel"
    private const val CHANNEL_DESC = "My Channel Description"
    private const val NOTIFICATION_ID = 1001

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}

@Composable
fun NotificationScreen(context: Context, modifier: Modifier = Modifier) {
    Column {
        Button(
            onClick ={
                NotificationUtils.createNotificationChannel(context)
                NotificationUtils.showNotification(context, "Hello", "Notification Screen")}
        ) {
            Text(text = "Get Notification", modifier = modifier)
        }
    }
}