package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kelompok4.uasmobile2.pawscorner.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(navController: NavHostController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = Firebase.firestore
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Diproses", "Dikirim", "Selesai")


    LaunchedEffect(uid) {
        uid?.let {
            firestore.collection("users").document(it).collection("orders")
                .get()
                .addOnSuccessListener { snapshot ->
                    val orderList = snapshot.documents.map { doc ->
                        Order(
                            id = doc.id,
                            title = doc.getString("title") ?: "Produk",
                            quantity = doc.getLong("quantity")?.toInt() ?: 1,
                            price = doc.getString("price")?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0,
                            status = doc.getString("status") ?: "Diproses"
                        )
                    }
                    orders = orderList
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status Pesanan") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Tab untuk filter status
            TabRow(selectedTabIndex = selectedTab, containerColor = Color(0xFF4CAF50), contentColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredOrders = orders.filter { it.status == tabs[selectedTab] }

                if (filteredOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada pesanan ${tabs[selectedTab]}")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredOrders) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("detail_status/${order.id}")
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(order.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Jumlah: ${order.quantity}")
                                    Text("Harga: Rp ${"%,d".format(order.price).replace(',', '.')}")

                                    val serviceFee = 10000
                                    val totalWithFee = (order.price * order.quantity) + serviceFee

                                    Text("Biaya Layanan: Rp ${"%,d".format(serviceFee).replace(',', '.')}")
                                    Text("Total: Rp ${"%,d".format(totalWithFee).replace(',', '.')}", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Status: ${order.status}", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
