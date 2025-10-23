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
                    Text(
                        "Riwayat Pesanan",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A76AB)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "Notif", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = item == "Notif",
                        onClick = {
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "Notif" -> navController.navigate("notification")
                                "Profil" -> navController.navigate("profile")
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(text = item, fontSize = 10.sp) }
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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(orders) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
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
