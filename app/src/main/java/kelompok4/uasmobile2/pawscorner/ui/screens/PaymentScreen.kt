package kelompok4.uasmobile2.pawscorner.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val date = "${now.get(Calendar.YEAR)}${(now.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}${now.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}"
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
                    imageVector = Icons.Default.ArrowBack,
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
            Spacer(modifier = Modifier.width(48.dp)) // kanan kosong untuk keseimbangan
        }
    }
}


@Composable
fun PaymentScreen(
    addressViewModel: AddressViewModel,
    navController: NavHostController,
    productId: String
) {
    val context = LocalContext.current
    val firestore = Firebase.firestore
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var selectedMethod by remember { mutableStateOf("") }
    val danaPhone = "081912591801"
    val briRekening = "1234 5678 9012 3456"
    val transactionId = remember { generateTransactionId() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }

    var products by remember { mutableStateOf<List<String>>(emptyList()) }
    var totalItems by remember { mutableStateOf(0) }
    var totalPrice by remember { mutableStateOf(0) }

    LaunchedEffect(uid) {
        uid?.let {
            firestore.collection("users").document(it).collection("orders")
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val tempList = mutableListOf<String>()
                        var itemCount = 0
                        var total = 0

                        for (doc in result.documents) {
                            val title = doc.getString("title") ?: "Produk"
                            val price = doc.getString("price")?.replace(Regex("[^\\d]"), "")?.toIntOrNull() ?: 0
                            val qty = doc.getLong("quantity")?.toInt() ?: 1

                            itemCount += qty
                            total += price * qty
                            tempList.add("• $title")
                        }

                        products = tempList
                        totalItems = itemCount
                        totalPrice = total
                    } else {
                        uploadStatus = "Tidak ada data order ditemukan."
                    }
                }
                .addOnFailureListener {
                    uploadStatus = "Gagal mengambil data order."
                }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PaymentHeader(onBack = { navController.popBackStack() })

        Column(modifier = Modifier.padding(16.dp)) {
            // Detail Transaksi
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Total: Rp ${"%,d".format(totalPrice).replace(',', '.')}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("ID Transaksi: $transactionId")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Pilih Metode Pembayaran", fontWeight = FontWeight.Bold)
            Text("Silahkan Bayarkan Dengan Transfer Pada Nomor Yang Tertera",fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { selectedMethod = "DANA" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMethod == "DANA") Color(0xFF3F51B5) else Color.Gray
                    )
                ) {
                    Text("DANA", color = Color.White)
                }

                Button(
                    onClick = { selectedMethod = "BRI" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMethod == "BRI") Color(0xFF757575) else Color.Gray
                    )
                ) {
                    Text("BRI", color = Color.White)
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
                enabled = true
            ) {
                Text("Upload Bukti Pembayaran")
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gambar dipilih: ${it.lastPathSegment}")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (selectedMethod.isBlank() || imageUri == null) {
                        uploadStatus = "Silakan pilih metode & unggah gambar."
                        return@Button
                    }

                    uploadStatus = "Data siap dikirim (belum implement upload)."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Kirim", color = Color.White)
            }

            uploadStatus?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = if (it.contains("berhasil")) Color.Green else Color.Red)
            }
        }
    }
}
