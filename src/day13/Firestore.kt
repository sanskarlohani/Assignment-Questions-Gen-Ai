package day13

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sanskar.firebase_integration.ui.theme.Firebase_IntegrationTheme
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.lazy.items
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


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

// DATA CLASS
data class SiliconStudent(
    val name: String = " ",
    val rollNo: String = " ",
    val branch: String = " "
)

// FIREBASE REPOSITORY
object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    private val studentsCollection = db.collection("students-29")

    fun addStudent(student: SiliconStudent, ) {
        studentsCollection.add(student)
            .addOnSuccessListener {
                Log.d("Firebase Repository", "Student added with ID: ${it.id}")
            }
            .addOnFailureListener {
                Log.d("Firebase Repository", "Failed to add student")
            }
    }

    fun getStudents(onDataChange: (List<Pair<String, SiliconStudent>>) -> Unit) {
        studentsCollection.addSnapshotListener { snapshot, _ ->
            val students = snapshot?.documents?.mapNotNull {
                val student = it.toObject(SiliconStudent::class.java)
                student?.let { s -> it.id to s }
            } ?: emptyList()
            onDataChange(students)
        }
    }

    fun deleteStudent(docId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        studentsCollection.document(docId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }
}

// VIEWMODEL
class StudentViewModel : ViewModel() {

    var students = mutableStateListOf<Pair<String, SiliconStudent>>()
        private set

    fun addStudent(name: String, rollNo: String, branch: String) {
        val student = SiliconStudent(name, rollNo, branch)
        FirebaseRepository.addStudent(student,)
    }

    fun getStudents() {
        FirebaseRepository.getStudents {
            students.clear()
            students.addAll(it)
        }
    }

    fun deleteStudent(docId: String) {
        FirebaseRepository.deleteStudent(docId, {}, {})
    }
}

// UI SCREEN
@Composable
fun StudentScreen(viewModel: StudentViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Add Student", fontSize = 24.sp)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = rollNo, onValueChange = { rollNo = it }, label = { Text("Roll No") })
        OutlinedTextField(value = branch, onValueChange = { branch = it }, label = { Text("Branch") })

        Button(onClick = {
            if (name.isNotBlank() && rollNo.isNotBlank() && branch.isNotBlank())
                viewModel.addStudent(name, rollNo, branch)
        }) {
            Text(text = "Add Student")
        }

        // Button to Fetch Students
        Button(onClick = {
            viewModel.getStudents()
        }) {
            Text(text = "Fetch Students")
        }

        Text(text = "Students", fontSize = 24.sp)

        LazyColumn {
            items(viewModel.students) { (docId, student) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Name: ${student.name}, Roll: ${student.rollNo}, Branch: ${student.branch}")


                    IconButton(onClick = {
                        viewModel.deleteStudent(docId)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}



