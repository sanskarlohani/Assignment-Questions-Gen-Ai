package day12

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

            }
        }
    }
}

//Auth View Model
class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // ✅ Email/Password Auth
    fun registerUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    // ✅ Google Sign-In
    fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    // ✅ Phone Auth
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential) { success ->
                        Log.d("PhoneAuth", if (success) "Auto login success" else "Auto login failed")
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.localizedMessage ?: "Verification failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token
                    onCodeSent()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String, onResult: (Boolean) -> Unit) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential, onResult)
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        onResult: (Boolean) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }
}



@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as Activity

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Phone Auth UI states
    var showPhoneDialog by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.registerUser(username, password) { success ->
                    Log.d("Firebase", if (success) "Registration successful" else "Registration failed")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Button
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.firebaseAuthWithGoogle(idToken) { success ->
                        Log.d("Firebase", if (success) "Google Sign-In successful" else "Google Sign-In failed")
                    }
                }
            } catch (e: ApiException) {
                Log.e("Firebase", "Google sign in failed", e)
            }
        }

        Button(
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("524287457224-gmh9hrqrh4eu401e8op56jnmafhr5k0u.apps.googleusercontent.com") // Replace with actual client ID
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Auth Button
        Button(
            onClick = { showPhoneDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Phone")
        }
    }

    // Phone Dialog
    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text("Phone Authentication") },
            text = {
                Column {
                    if (!isOtpSent) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Enter phone number") },
                            placeholder = { Text("+91XXXXXXXXXX") }
                        )
                    } else {
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            label = { Text("Enter OTP") }
                        )
                    }
                    error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (!isOtpSent) {
                        viewModel.sendOtp(phoneNumber, activity,
                            onCodeSent = {
                                isOtpSent = true
                            },
                            onError = { error = it }
                        )
                    } else {
                        viewModel.verifyOtp(otp) { success ->
                            if (success) {
                                Log.d("PhoneAuth", "Phone Sign-In successful")
                                showPhoneDialog = false
                            } else {
                                error = "Invalid OTP"
                            }
                        }
                    }
                }) {
                    Text(if (!isOtpSent) "Send OTP" else "Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhoneDialog = false
                    isOtpSent = false
                    phoneNumber = ""
                    otp = ""
                    error = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Preview
@Composable
private fun AuthScreenPreview() {
    MaterialTheme{
        //AuthScreen()
    }

}

