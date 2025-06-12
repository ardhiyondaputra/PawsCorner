package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kelompok4.uasmobile2.pawscorner.R
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavHostController) {
    val selectedItem = remember { mutableStateOf("Notif") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "Notif", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem.value == item,
                        onClick = {
                            selectedItem.value = item
                            when (item) {
                                "Home" -> navController.navigate("home") {
                                    popUpTo("notif") { inclusive = true }
                                }
                                "Profil" -> navController.navigate("profile") {
                                    popUpTo("notif") { inclusive = true }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(item, fontSize = 10.sp)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(3) {
                NotificationItemPlaceholder()
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun NotificationItemPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Circle avatar with initials
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFCBD5E1), shape = MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SJ",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("New Feature Alert!", fontWeight = FontWeight.Bold)
                Text("14h", fontSize = 12.sp, color = Color.Gray)
            }

            Text(
                "Kami Menghadirkan Fitur terbaru",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Try now", color = Color.Black)
            }
        }
    }
}