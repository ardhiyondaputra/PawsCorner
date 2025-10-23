package kelompok4.uasmobile2.pawscorner.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf("Notif") }

    // 🔹 Ambil data order dari Firestore
    LaunchedEffect(Unit) {
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid).collection("orders")
                .get()
                .addOnSuccessListener { snapshot ->
                    orders = snapshot.documents.mapNotNull { doc ->
                        val priceValue = doc.get("price")
                        val price = when (priceValue) {
                            is Number -> priceValue.toInt()
                            is String -> priceValue.toIntOrNull() ?: 0
                            else -> 0
                        }

                        Order(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            quantity = (doc.getLong("quantity") ?: 1L).toInt(),
                            price = price,
                            status = doc.getString("status") ?: "Diproses"
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 🔹 Teks History ditengah
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black // 🔹 Ubah jadi hitam
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE6F5EA) // 🔹 Hapus background biru, ubah jadi putih
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "History", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = {
                            selectedItem = item
                            when (item) {
                                "Home" -> {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                "History" -> {
                                    navController.navigate("notification") {
                                        popUpTo("notification") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                "Profil" -> {
                                    navController.navigate("profile") {
                                        popUpTo("profile") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                modifier = Modifier.size(32.dp) // ✅ sama kayak di HomeScreen
                            )
                        },
                        label = {
                            Text(
                                text = item,
                                fontSize = 13.sp // ✅ sama juga
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF6A76AB)
                    )
                }

                orders.isEmpty() -> {
                    Text(
                        text = "Belum ada riwayat pesanan",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp) // 🔹 Tambah jarak atas & bawah
                    ) {
                        items(orders) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .clickable {
                                        navController.navigate("detail_status/${order.id}")
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(order.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Jumlah: ${order.quantity}", fontSize = 14.sp)
                                    Text("Status: ${order.status}", fontSize = 14.sp, color = Color(0xFF6A76AB))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
