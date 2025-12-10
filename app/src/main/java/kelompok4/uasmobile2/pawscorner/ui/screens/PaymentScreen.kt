package kelompok4.uasmobile2.pawscorner.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import java.util.*

fun generateTransactionId(): String {
    val now = Calendar.getInstance()
    val date =
        "${now.get(Calendar.YEAR)}${(now.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}${now.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}"
    val random = (1000..9999).random()
    return "TRX${date}_$random"
}

@Composable
fun PaymentHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Payment",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun PaymentScreen(
    singleOrder: Boolean = false,
    addressViewModel: AddressViewModel,
    navController: NavHostController
) {
    val firestore = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var selectedMethod by remember { mutableStateOf("") }
    val danaPhone = "081912591801"
    val briRekening = "1234 5678 9012 3456"
    val transactionId = remember { generateTransactionId() }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<String>>(emptyList()) }
    var totalItems by remember { mutableIntStateOf(0) }
    var totalPrice by remember { mutableIntStateOf(0) }
    val serviceFee = 10000
    val totalWithServiceFee = totalPrice + serviceFee

    val addresses by addressViewModel.addresses.collectAsState()
    val selectedAddress = addresses.firstOrNull()?.second
    var showExitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        uid?.let {
            addressViewModel.loadAddresses()
            firestore.collection("users").document(it).collection("orders")
                .whereEqualTo("primary", true)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val tempList = mutableListOf<String>()
                        var itemCount = 0
                        var total = 0
                        for (doc in result.documents) {
                            val title = doc.getString("title") ?: "Produk"
                            val price =
                                doc.getString("price")?.replace(Regex("\\D"), "")?.toIntOrNull()
                                    ?: 0
                            val qty = doc.getLong("quantity")?.toInt() ?: 1
                            itemCount += qty
                            total += price * qty
                            tempList.add("• $title (x$qty)")
                        }
                        products = tempList
                        totalItems = itemCount
                        totalPrice = total
                    } else {
                        uploadStatus = "Tidak ada pesanan untuk dibayar."
                    }
                }
                .addOnFailureListener {
                    uploadStatus = "Gagal mengambil data order."
                }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            uploadStatus = "Bukti pembayaran berhasil diupload"
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            PaymentHeader(onBack = { showExitConfirmation = true })

            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp,
                    tonalElevation = 2.dp,
                    color = Color(0xFFF5F5F5)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detail Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Produk:")
                        products.forEach { Text(it) }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Jumlah: $totalItems")
                        Text("Subtotal: Rp ${"%,d".format(totalPrice).replace(',', '.')}")
                        Text("Biaya Layanan: Rp ${"%,d".format(serviceFee).replace(',', '.')}")
                        Text(
                            "Total: Rp ${"%,d".format(totalWithServiceFee).replace(',', '.')}",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ID Transaksi: $transactionId")
                        selectedAddress?.let { address ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Alamat Pengiriman", fontWeight = FontWeight.Bold)
                            Text(address.name)
                            Text(address.address)
                            Text("${address.village}, ${address.district}")
                            Text("${address.regency}, ${address.province}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Pilih Metode Pembayaran", fontWeight = FontWeight.Bold)
                Text("Silahkan Bayarkan Dengan Transfer Pada Nomor Yang Tertera")
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("DANA", "BRI").forEach { method ->
                        val isSelected = selectedMethod == method
                        val isDisabled = imageUri != null

                        OutlinedButton(
                            onClick = { if (!isDisabled) selectedMethod = method },
                            enabled = !isDisabled,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 2.dp,
                                brush = if (isSelected) androidx.compose.ui.graphics.SolidColor(Color(0xFF3F51B5))
                                else androidx.compose.ui.graphics.SolidColor(Color.Gray)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) Color(0xFF3F51B5) else Color.White,
                                contentColor = if (isSelected) Color.White else Color.Black,
                                disabledContainerColor = if (isSelected) Color(0xFF3F51B5) else Color.White,
                                disabledContentColor = if (isSelected) Color.White else Color.Gray
                            )
                        ) {
                            Text(method, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

                if (selectedMethod == "DANA") {
                    Text("No. Handphone Toko: $danaPhone")
                } else if (selectedMethod == "BRI") {
                    Text("No. Rekening BRI: $briRekening")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = imageUri == null
                ) {
                    Text(if (imageUri == null) "Upload Bukti Pembayaran" else "Sudah Diupload")
                }

                uploadStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        it,
                        color = if (it.contains("berhasil")) Color(0xFF4CAF50) else Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedMethod.isBlank() || imageUri == null) {
                            uploadStatus = "Silakan pilih metode & unggah gambar."
                            return@Button
                        }

                        uid?.let { userId ->
                            val ordersRef = firestore.collection("users").document(userId)
                                .collection("orders")
                            val cartRef =
                                firestore.collection("users").document(userId).collection("cart")
                            val productsRef = firestore.collection("products")

                            ordersRef.whereEqualTo("primary", true)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    for (doc in snapshot.documents) {
                                        doc.reference.update("primary", false)
                                        val productId = doc.getString("productId")
                                        val quantity = doc.getLong("quantity")?.toInt() ?: 0
                                        if (!productId.isNullOrEmpty() && quantity > 0) {
                                            val productDocRef = productsRef.document(productId)
                                            productDocRef.get()
                                                .addOnSuccessListener { productSnapshot ->
                                                    val currentStock =
                                                        productSnapshot.getLong("stock")?.toInt()
                                                            ?: 0
                                                    val newStock =
                                                        (currentStock - quantity).coerceAtLeast(0)
                                                    productDocRef.update("stock", newStock)
                                                }
                                        }
                                    }
                                    cartRef.get().addOnSuccessListener { cartSnapshot ->
                                        for (cartDoc in cartSnapshot.documents) {
                                            cartDoc.reference.delete()
                                        }
                                        navController.navigate("payment_success_screen") {
                                            popUpTo("payment_screen") { inclusive = true }
                                        }
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = imageUri != null && selectedMethod.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (imageUri != null) Color(0xFF4CAF50) else Color.Gray,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text("Kirim", color = Color.White)
                }
            }
        }
    }
}
