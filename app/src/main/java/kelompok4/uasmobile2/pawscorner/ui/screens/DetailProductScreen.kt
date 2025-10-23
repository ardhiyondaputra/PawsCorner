package kelompok4.uasmobile2.pawscorner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kelompok4.uasmobile2.pawscorner.ui.components.CustomPopup
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
    val coroutineScope = rememberCoroutineScope()

    // 🎯 State untuk Pop-up
    var showSuccessPopup by remember { mutableStateOf(false) }
    var showErrorPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isAddingToCart by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🛒 TOMBOL ADD TO CART
                Button(
                    onClick = {
                        // Validasi stock
                        if (product.stock <= 0) {
                            errorMessage = "Maaf, produk ini sedang habis"
                            showErrorPopup = true
                            return@Button
                        }

                        val auth = FirebaseAuth.getInstance()
                        val firestore = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid

                        if (uid == null) {
                            errorMessage = "Anda harus login terlebih dahulu"
                            showErrorPopup = true
                            return@Button
                        }

                        isAddingToCart = true

                        // Cek apakah produk sudah ada di cart
                        firestore.collection("users")
                            .document(uid)
                            .collection("cart")
                            .whereEqualTo("productId", product.documentId)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (!snapshot.isEmpty) {
                                    // Produk sudah ada, update quantity
                                    val doc = snapshot.documents.first()
                                    val currentQty = doc.getLong("quantity")?.toInt() ?: 1

                                    doc.reference.update("quantity", currentQty + 1)
                                        .addOnSuccessListener {
                                            isAddingToCart = false
                                            showSuccessPopup = true
                                        }
                                        .addOnFailureListener { e ->
                                            isAddingToCart = false
                                            errorMessage = "Gagal menambahkan: ${e.message}"
                                            showErrorPopup = true
                                        }
                                } else {
                                    // Produk belum ada, tambahkan baru
                                    val cartItem = hashMapOf(
                                        "productId" to product.documentId,
                                        "title" to product.title,
                                        "price" to product.price,
                                        "imageUrl" to product.imageUrl,
                                        "weight" to product.weight,
                                        "quantity" to 1,
                                        "primary" to false
                                    )

                                    firestore.collection("users")
                                        .document(uid)
                                        .collection("cart")
                                        .add(cartItem)
                                        .addOnSuccessListener {
                                            isAddingToCart = false
                                            showSuccessPopup = true
                                        }
                                        .addOnFailureListener { e ->
                                            isAddingToCart = false
                                            errorMessage = "Gagal menambahkan: ${e.message}"
                                            showErrorPopup = true
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                isAddingToCart = false
                                errorMessage = "Gagal mengecek cart: ${e.message}"
                                showErrorPopup = true
                            }
                    },
                    enabled = !isAddingToCart && product.stock > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (product.stock > 0) Color(0xFFD0E8FF) else Color.Gray
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isAddingToCart) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF0D47A1),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.shopping_cart),
                            contentDescription = null,
                            tint = if (product.stock > 0) Color(0xFF0D47A1) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (product.stock > 0) "Keranjang" else "Stok Habis",
                            color = if (product.stock > 0) Color(0xFF0D47A1) else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // 💰 TOMBOL BELI SEKARANG
                Button(
                    onClick = {
                        // Validasi stock
                        if (product.stock <= 0) {
                            errorMessage = "Maaf, produk ini sedang habis"
                            showErrorPopup = true
                            return@Button
                        }

                        val auth = FirebaseAuth.getInstance()
                        val firestore = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid

                        if (uid == null) {
                            errorMessage = "Anda harus login terlebih dahulu"
                            showErrorPopup = true
                            return@Button
                        }

                        val orderItem = hashMapOf(
                            "productId" to product.documentId,
                            "title" to product.title,
                            "price" to product.price,
                            "imageUrl" to product.imageUrl,
                            "quantity" to 1,
                            "primary" to true,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("users")
                            .document(uid)
                            .collection("orders")
                            .add(orderItem)
                            .addOnSuccessListener {
                                navController.navigate("payment/oneOrder")
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Gagal membuat pesanan: ${e.message}"
                                showErrorPopup = true
                            }
                    },
                    enabled = product.stock > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (product.stock > 0) Color(0xFF4CAF50) else Color.Gray
                    ),
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

                // Stock indicator dengan warna
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Stock: ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "${product.stock}",
                        color = when {
                            product.stock == 0 -> Color.Red
                            product.stock < 10 -> Color(0xFFFF9800)
                            else -> Color(0xFF4CAF50)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

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

    // 🎉 POP-UP SUCCESS - Auto redirect ke Home
    if (showSuccessPopup) {
        CustomPopup(
            title = "Berhasil!",
            message = "Produk berhasil ditambahkan ke keranjang",
            confirmText = "OK",
            onDismiss = {
                showSuccessPopup = false
                // Auto redirect ke home setelah popup ditutup
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            onConfirm = {
                showSuccessPopup = false
                // Auto redirect ke home
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            isError = false
        )

        // 🕐 Auto redirect setelah 2 detik
        LaunchedEffect(Unit) {
            delay(2000)
            showSuccessPopup = false
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // ❌ POP-UP ERROR
    if (showErrorPopup) {
        CustomPopup(
            title = "Gagal",
            message = errorMessage,
            confirmText = "OK",
            onDismiss = { showErrorPopup = false },
            isError = true
        )
    }
}