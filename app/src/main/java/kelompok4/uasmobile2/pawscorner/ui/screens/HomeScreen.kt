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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.Product
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("Home") }
    var selectedCategory by remember { mutableStateOf("Makanan") }

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

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
                } catch (e: Exception) {
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
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(text = item, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (selectedItem == "Profil") {
            ProfileScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HeaderSection(searchText, { searchText = it }, navController)
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
}

@Composable
fun HeaderSection(searchText: String, onSearchChange: (String) -> Unit, navController: NavController) {
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
                modifier = Modifier
                    .size(120.dp)
                    .height(25.dp)
                    .padding(end = 2.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = "Cart",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 12.dp)
                        .clickable {
                            navController.navigate("cart")
                        }
                )
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

        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = { Text("Search", fontSize = 18.sp) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search__1_),
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.objects_column),
                    contentDescription = "Filter",
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        )
    }
}

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
                    modifier = Modifier.size(30.dp)
                )
                Text(text = name, fontSize = 10.sp)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable {
                        navController.navigate("detail/${product.documentId}")
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GlideImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(product.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 2)
                        Text(product.weight, fontSize = 11.sp, color = Color.Gray)
                        Text(product.price, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF388E3C))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile")
    }
}