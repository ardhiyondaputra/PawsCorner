package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.navigation.NavHostController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel

data class Product(
    val title: String,
    val weight: String,
    val category: String,
    val imageRes: Int,
    val price: String
)

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    var searchText by remember { mutableStateOf("") }
    val selectedItem = remember { mutableStateOf("Home") }
    var selectedCategory by remember { mutableStateOf("Makanan") }

    val allProducts = listOf(
        Product("Josera Mini Deluxe", "900g", "Makanan", R.drawable.paws_corner_removebg_preview, "Rp 85.000"),
        Product("Pedigree Chicken & Vege", "3kg", "Makanan", R.drawable.paws_corner_removebg_preview, "Rp 120.000"),
        Product("BlackHawk Puppy Lamb", "20kg", "Makanan", R.drawable.paws_corner_removebg_preview, "Rp 950.000"),
        Product("Royal Canin Labrador P", "3kg", "Makanan", R.drawable.paws_corner_removebg_preview, "Rp 350.000"),
        Product("Squeaky Bone Toy", "1pc", "Mainan", R.drawable.paws_corner_removebg_preview, "Rp 25.000"),
        Product("Catnip Ball", "2pcs", "Mainan", R.drawable.paws_corner_removebg_preview, "Rp 30.000"),
        Product("Flea Treatment", "30ml", "Obat", R.drawable.paws_corner_removebg_preview, "Rp 75.000"),
        Product("Shampoo CleanPaws", "500ml", "Kebersihan", R.drawable.paws_corner_removebg_preview, "Rp 60.000")
    )

    val filteredProducts = allProducts.filter {
        it.category == selectedCategory && it.title.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val items = listOf("Home", "Notif", "Profil")
                val icons = listOf(R.drawable.home, R.drawable.bell, R.drawable.user)

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem.value == item,
                        onClick = {
                            selectedItem.value = item
                            if (item == "Notif") {
                                navController.navigate("notification")
                            }
                            if (item == "Profil") {
                                navController.navigate("profile")
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
    ) { innerPadding ->
        if (selectedItem.value == "Profil") {
            ProfileScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                HeaderSection(searchText) { searchText = it }
                CategorySection(selectedCategory) { selectedCategory = it }
                RecommendedSection(filteredProducts)
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ProfileScreen() {}

@Composable
fun HeaderSection(searchText: String, onSearchChange: (String) -> Unit) {
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
                    modifier = Modifier.size(30.dp).padding(end = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Search Food, Toy, Etc",
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
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = name, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun RecommendedSection(products: List<Product>) {
    Text(
        text = "Recommended Product",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 500.dp)
    ) {
        items(products) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.title,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = product.weight, fontSize = 11.sp)
                    Text(text = product.price, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF388E3C))
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { /* TODO: Tambah ke keranjang */ },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.shopping_cart),
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}