//package jetpack_day6
//
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import com.sanskar.jetpackcompse.ui.theme.JetpackCompseTheme
//
//class MainActivity : ComponentActivity() {
//    private val TAG = "MainActivity"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate: Application is created.")
//
//        setContent {
//            JetpackCompseTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Sanskar",
//                        modifier = Modifier.padding(innerPadding)
//                    ) {
//                        // onNameChanged callback
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        Log.d(TAG, "onStart: Application is started.")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume: Application is resumed.")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d(TAG, "onPause: Application is paused.")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d(TAG, "onStop: Application is stopped.")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d(TAG, "onDestroy: Application is destroyed.")
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        Log.d(TAG, "onRestart: Application is restarted.")
//    }
//}
//
//@Composable
//fun Greeting(
//    name: String,
//    modifier: Modifier,
//    onNameChanged: (String) -> Unit
//) {
//
//}
