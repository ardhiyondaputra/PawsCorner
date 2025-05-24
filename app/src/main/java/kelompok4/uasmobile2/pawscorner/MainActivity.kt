package kelompok4.uasmobile2.pawscorner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kelompok4.uasmobile2.pawscorner.ui.theme.PawsCornerTheme
import kelompok4.uasmobile2.pawscorner.ui.theme.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.theme.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawsCornerTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}