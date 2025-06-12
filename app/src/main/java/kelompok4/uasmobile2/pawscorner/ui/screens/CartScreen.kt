package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kelompok4.uasmobile2.pawscorner.data.CartItem

@Composable
fun CartScreen(navController: NavController) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var subtotal by remember { mutableIntStateOf(0) }
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    DisposableEffect(Unit) {
        val listener: ListenerRegistration? = uid?.let {
            db.collection("users").document(it).collection("cart")
                .addSnapshotListener { value, _ ->
                    value?.let {
                        val items = it.documents.mapNotNull { doc ->
                            val priceStr = doc.getString("price")?.replace(Regex("\\D"), "") ?: "0"
                            val quantity = doc.getLong("quantity")?.toInt() ?: 1
                            val imageUrl = doc.getString("imageUrl") ?: "" // Ambil imageUrl dari Firestore

                            CartItem(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                price = priceStr,
                                quantity = quantity,
                                weight = doc.getString("weight") ?: "1kg",
                                imageUrl = imageUrl
                            )
                        }
                        cartItems = items
                        subtotal = items.sumOf { (it.price.toIntOrNull() ?: 0) * it.quantity }
                    }
                }
        }

        onDispose { listener?.remove() }
    }

    val serviceFee = 10000
    val total = subtotal + serviceFee

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFFFF))
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal")
                        Text("Rp ${"%,d".format(subtotal).replace(',', '.')}")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Layanan")
                        Text("Rp ${"%,d".format(serviceFee).replace(',', '.')}")
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(
                            "Rp ${"%,d".format(total).replace(',', '.')}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val firestore = FirebaseFirestore.getInstance()
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                val cartRef = firestore.collection("users").document(uid).collection("cart")
                                val ordersRef = firestore.collection("users").document(uid).collection("orders")

                                cartRef.get().addOnSuccessListener { snapshot ->
                                    if (snapshot.isEmpty) return@addOnSuccessListener

                                    for (doc in snapshot.documents) {
                                        val order = hashMapOf(
                                            "productId" to doc.getString("productId"),
                                            "title" to doc.getString("title"),
                                            "price" to doc.getString("price"),
                                            "imageUrl" to doc.getString("imageUrl"), // Ganti imageRes dengan imageUrl
                                            "quantity" to doc.getLong("quantity"),
                                            "orderTime" to System.currentTimeMillis(),
                                            "primary" to true
                                        )
                                        ordersRef.add(order)
                                    }

                                    val firstProductId = snapshot.documents.firstOrNull()?.getString("productId")
                                    if (firstProductId != null) {
                                        navController.navigate("payment/$firstProductId")
                                    } else {
                                        navController.navigate("payment/unknown")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E68FF))
                    ) {
                        Text(
                            text = "Checkout",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Keranjang",
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

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang kamu kosong", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(cartItems) { item ->
                        CartItemRow(item, uid)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CartItemRow(item: CartItem, uid: String?) {
    val db = FirebaseFirestore.getInstance()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Text(text = item.weight, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = "Rp ${item.price} x ${item.quantity}",
                color = Color(0xFF43A047),
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    if (item.quantity > 1 && uid != null) {
                        db.collection("users").document(uid).collection("cart")
                            .document(item.id)
                            .update("quantity", item.quantity - 1)
                    }
                }) {
                    Text("-", fontSize = 18.sp)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    if (uid != null) {
                        db.collection("users").document(uid).collection("cart")
                            .document(item.id)
                            .update("quantity", item.quantity + 1)
                    }
                }) {
                    Text("+", fontSize = 18.sp)
                }
            }
            IconButton(onClick = {
                if (uid != null) {
                    db.collection("users").document(uid).collection("cart")
                        .document(item.id)
                        .delete()
                }
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
            }
        }
    }
}
