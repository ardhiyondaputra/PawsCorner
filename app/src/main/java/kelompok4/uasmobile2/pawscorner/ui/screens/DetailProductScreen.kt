package kelompok4.uasmobile2.pawscorner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.Product
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
                product = Product(
                    title = doc.getString("title") ?: "",
                    weight = doc.getString("weight") ?: "",
                    category = doc.getString("category") ?: "",
                    price = doc.getLong("price")?.let {
                        "Rp ${String.format("%,d", it).replace(',', '.')}"
                    } ?: "",
                    quantity = doc.getLong("quantity")?.toInt() ?: 1,
                    description = doc.getString("description") ?: "",
                    imageRes = R.drawable.paws_corner_removebg_preview,
                    documentId = documentId
                )
            } else error = "Produk tidak ditemukan"
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

@Composable
fun DetailProductContent(product: Product, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Detail Produk",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // IMAGE
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INFORMASI PRODUK
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Kategori: ${product.category}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Berat: ${product.weight}", color = Color.Gray, fontSize = 14.sp)

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

            // TOMBOL TAMBAH KE KERANJANG
            Button(
                onClick = {
                    val firestore = FirebaseFirestore.getInstance()
                    val cartItem = hashMapOf(
                        "productId" to product.documentId,
                        "title" to product.title,
                        "price" to product.price,
                        "imageRes" to product.imageRes,
                        "quantity" to 1
                    )
                    firestore.collection("cart").add(cartItem)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0E8FF)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = null,
                    tint = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tambah ke Keranjang",
                    color = Color(0xFF0D47A1),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
