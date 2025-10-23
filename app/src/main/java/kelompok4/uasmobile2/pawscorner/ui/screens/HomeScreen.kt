package kelompok4.uasmobile2.pawscorner.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.Product
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.ProductCard
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    navController: NavHostController
) {
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("Home") }
    var selectedCategory by remember { mutableStateOf("Makanan") }

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // 🛒 State untuk cart count
    var cartItemCount by remember { mutableIntStateOf(0) }

    // 🔥 Load data dari Firebase
    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("products").get().await()
            val fetchedProducts = snapshot.documents.mapNotNull { document ->
                try {
                    val title = document.getString("title") ?: ""
                    val weight = document.getString("weight") ?: ""
                    val category = document.getString("category") ?: ""
                    val price = document.getLong("price")?.let {
                        "Rp ${String.format("%,d", it).replace(',', '.')}"
                    } ?: ""
                    val stock = document.getLong("stock")?.toInt() ?: 0
                    val description = document.getString("description") ?: ""
                    val documentId = document.id
                    val imageUrl = document.getString("imageUrl") ?: ""

                    Product(
                        title = title,
                        weight = weight,
                        category = category,
                        imageUrl = imageUrl,
                        price = price,
                        stock = stock,
                        description = description,
                        documentId = documentId
                    )
                } catch (_: Exception) {
                    null
                }
            }
            products = fetchedProducts
            isLoading = false
        } catch (e: Exception) {
            error = "Gagal memuat produk: ${e.message}"
            isLoading = false
        }
    }

    // 🛒 Real-time listener untuk cart count (TOTAL QUANTITY)
    DisposableEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val listener = uid?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(it)
                .collection("cart")
                .addSnapshotListener { snapshot, _ ->
                    // ✅ Hitung total quantity, bukan jumlah document
                    cartItemCount = snapshot?.documents?.sumOf { doc ->
                        doc.getLong("quantity")?.toInt() ?: 0
                    } ?: 0
                }
        }
        onDispose { listener?.remove() }
    }

    val filteredProducts = products.filter {
        it.category == selectedCategory && it.title.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "Notif", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = {
                            selectedItem = item
                            when (item) {
                                "Notif" -> navController.navigate("notification")
                                "Profil" -> navController.navigate("profile")
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        label = { Text(text = item, fontSize = 13.sp) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🛒 Pass cartItemCount ke HeaderSection
            HeaderSection(searchText, { searchText = it }, navController, cartItemCount)
            CategorySection(selectedCategory) { selectedCategory = it }

            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error!!, color = Color.Red)
                }
                filteredProducts.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada produk ditemukan.")
                }
                else -> RecommendedSection(filteredProducts, navController)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ===============================
// Header dengan Cart Badge
// ===============================
@Composable
fun HeaderSection(
    searchText: String,
    onSearchChange: (String) -> Unit,
    navController: NavController,
    cartItemCount: Int // ← TAMBAHKAN PARAMETER INI
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6F5EA))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.paws_corner_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            // 🛒 Cart Icon dengan Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("cart") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = "Cart",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )

                // 🔴 Badge Counter
                if (cartItemCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Red, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Cari Makanan, Mainan, DLL",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = "Search",
            leadingIcon = R.drawable.search__1_,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.objects_column),
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}

// ===============================
// Kategori tetap sama
// ===============================
@Composable
fun CategorySection(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf(
        "Makanan" to R.drawable.fish,
        "Mainan" to R.drawable.bowling_ball,
        "Obat" to R.drawable.first_aid_kit,
        "Kebersihan" to R.drawable.broom
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        categories.forEach { (name, iconRes) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selectedCategory == name) Color(0xFFB2DFDB) else Color.Transparent)
                    .clickable { onCategorySelected(name) }
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = name,
                    modifier = Modifier.size(32.dp)
                )
                Text(text = name, fontSize = 13.sp)
            }
        }
    }
}

// ===============================
// Grid Produk pakai ProductCard
// ===============================
@Composable
fun RecommendedSection(products: List<Product>, navController: NavHostController) {
    Text(
        text = "Produk Rekomendasi",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { navController.navigate("detail/${product.documentId}") }
            )
        }
    }
}