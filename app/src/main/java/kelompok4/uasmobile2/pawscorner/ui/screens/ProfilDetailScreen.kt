package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.ProfileViewModel

@Composable
fun ProfileDetailScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val name by profileViewModel.userName.collectAsState()
    val email by profileViewModel.userEmail.collectAsState()
    val phone by profileViewModel.userPhone.collectAsState()
    val password by profileViewModel.userPassword.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header dengan tombol kembali dan judul di tengah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali"
                )
            }

            Text(
                text = "Akun Saya",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "Foto Profil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = email, fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        ProfileDataLabel(label = "Nama Pengguna", value = name)
        ProfileDataLabel(label = "Email", value = email)
        ProfileDataLabel(label = "Password", value = password)
        ProfileDataLabel(label = "Nomor Telepon", value = phone, withFlag = true)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Tambahkan aksi update profile jika perlu */ },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A76AB)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Update Profile", color = Color.White)
        }
    }
}

@Composable
fun ProfileDataLabel(label: String, value: String, withFlag: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (withFlag) {
                Text("🇮🇩", modifier = Modifier.padding(end = 8.dp))
            }
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Divider(modifier = Modifier.padding(top = 4.dp))
    }
}
