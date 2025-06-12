package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.layout.*
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
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import kelompok4.uasmobile2.pawscorner.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailStatusScreen(
    navController: NavHostController,
    orderId: String,
    addressViewModel: AddressViewModel
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = Firebase.firestore

    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val addresses by addressViewModel.addresses.collectAsState()
    val selectedAddress = addresses.firstOrNull()?.second

    val serviceFee = 10000  // Tambahkan service fee
    val totalWithServiceFee = order?.let { it.price * it.quantity + serviceFee } ?: 0

    LaunchedEffect(orderId) {
        addressViewModel.loadAddresses()

        uid?.let {
            firestore.collection("users").document(it).collection("orders").document(orderId)
                .get()
                .addOnSuccessListener { doc ->
                    order = Order(
                        id = doc.id,
                        title = doc.getString("title") ?: "Produk",
                        quantity = doc.getLong("quantity")?.toInt() ?: 1,
                        price = doc.getString("price")?.replace(Regex("\\D"), "")?.toIntOrNull() ?: 0,
                        status = doc.getString("status") ?: "Diproses"
                    )
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
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            order?.let { ord ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 4.dp,
                        color = Color(0xFFF5F5F5)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Produk: ${ord.title}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Jumlah: ${ord.quantity}")
                            Text("Harga Satuan: Rp ${"%,d".format(ord.price).replace(',', '.')}")
                            Text("Total Harga: Rp ${"%,d".format(ord.price * ord.quantity).replace(',', '.')}")
                            Text("Biaya Layanan: Rp ${"%,d".format(serviceFee).replace(',', '.')}")
                            Text(
                                "Total Keseluruhan: Rp ${"%,d".format(totalWithServiceFee).replace(',', '.')}",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Status: ${ord.status}", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)

                            selectedAddress?.let { address ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Alamat Pengiriman", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(address.name)
                                Text(address.address)
                                Text("${address.village}, ${address.district}")
                                Text("${address.regency}, ${address.province}")
                            }
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pesanan tidak ditemukan.")
                }
            }
        }
    }
}
