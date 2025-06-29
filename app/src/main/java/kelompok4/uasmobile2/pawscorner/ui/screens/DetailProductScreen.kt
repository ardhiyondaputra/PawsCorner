package kelompok4.uasmobile2.pawscorner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.Product
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@Composable
fun DetailProductScreen(documentId: String, navController: NavHostController) {
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Ambil data produk
    LaunchedEffect(documentId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("products")
                .document(documentId).get().await()

            if (doc.exists()) {
                val imageUrl = doc.getString("imageUrl") ?: ""
                product = Product(
                    title = doc.getString("title") ?: "",
                    weight = doc.getString("weight") ?: "",
                    category = doc.getString("category") ?: "",
                    price = doc.getLong("price")?.let {
                        "Rp ${String.format("%,d", it).replace(',', '.')}"
                    } ?: "",
                    stock = doc.getLong("stock")?.toInt() ?: 1,
                    description = doc.getString("description") ?: "",
                    imageUrl = imageUrl,
                    documentId = documentId
                )
            } else {
                error = "Produk tidak ditemukan"
            }
        } catch (e: Exception) {
            error = "Gagal mengambil produk: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error!!, color = Color.Red)
        }
        product != null -> {
            DetailProductContent(product!!, navController)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailProductContent(product: Product, navController: NavHostController) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val coroutineScope = rememberCoroutineScope()
                var addedToCart by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        val auth = FirebaseAuth.getInstance()
                        val firestore = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid

                        if (uid != null) {
                            val cartItem = hashMapOf(
                                "productId" to product.documentId,
                                "title" to product.title,
                                "price" to product.price,
                                "imageUrl" to product.imageUrl, // Perbarui ke imageUrl
                                "quantity" to 1,
                                "primary" to false
                            )

                            firestore.collection("users")
                                .document(uid)
                                .collection("cart")
                                .add(cartItem)
                                .addOnSuccessListener {
                                    addedToCart = true
                                    coroutineScope.launch {
                                        delay(1500)
                                        addedToCart = false
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0E8FF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (addedToCart) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF0D47A1),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ditambahkan",
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.shopping_cart),
                            contentDescription = null,
                            tint = Color(0xFF0D47A1),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Keranjang",
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        val auth = FirebaseAuth.getInstance()
                        val firestore = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid

                        if (uid != null) {
                            val orderItem = hashMapOf(
                                "productId" to product.documentId,
                                "title" to product.title,
                                "price" to product.price,
                                "imageUrl" to product.imageUrl,
                                "quantity" to 1,
                                "primary" to true,
                                "timestamp" to System.currentTimeMillis()
                            )

                            // Tambahkan ke koleksi orders user
                            firestore.collection("users")
                                .document(uid)
                                .collection("orders")
                                .add(orderItem)
                                .addOnSuccessListener {
                                    // Navigasi ke PaymentScreen dengan ID order sebagai parameter
                                    navController.navigate("payment/oneOrder")
                                }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Beli Sekarang",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Beli Sekarang",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Detail Produk",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }

            GlideImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Kategori: ${product.category}", color = Color.Gray, fontSize = 14.sp)
                Text(text = "Berat: ${product.weight}", color = Color.Gray, fontSize = 14.sp)
                Text(text = "Stock: ${product.stock}", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Deskripsi:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(product.description ?: "-", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}