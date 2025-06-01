package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Ambil data user dari AuthViewModel
    val userData by authViewModel.userData.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "Notif", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = item == "Profil",
                        onClick = {
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "Notif" -> {}
                                "Profil" -> {}
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
                            Text(text = item, fontSize = 10.sp)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .padding(top = 18.dp)
        ) {
            Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6A76AB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "User Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.clickable {
                            navController.navigate("profile_detail")
                        }
                    ) {
                        Text(
                            text = userData?.username ?: "Loading...",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = userData?.email ?: "",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                icon = R.drawable.paws_corner_removebg_preview,
                title = "Profile Saya",
                desc = "Make changes to your account"
            ) {
                navController.navigate("profile_detail")
            }

            ProfileOption(
                icon = R.drawable.paws_corner_removebg_preview,
                title = "Alamat Anda",
                desc = "Alamat paket anda"
            ) {}

            ProfileOption(
                icon = R.drawable.paws_corner_removebg_preview,
                title = "Status Pesanan",
                desc = ""
            ) {}

            ProfileOption(
                icon = R.drawable.paws_corner_removebg_preview,
                title = "Log out",
                desc = "Further secure your account for safety"
            ) {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) // Clear backstack
                }
            }
        }
    }
}

@Composable
fun ProfileOption(icon: Int, title: String, desc: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color(0xFF6A76AB),
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            if (desc.isNotEmpty()) Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(
            painter = painterResource(id = R.drawable.paws_corner_removebg_preview),
            contentDescription = "Arrow",
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )
    }
}