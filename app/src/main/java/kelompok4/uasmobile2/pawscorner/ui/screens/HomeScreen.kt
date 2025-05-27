package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import kelompok4.uasmobile2.pawscorner.viewmodel.LoginViewModel

@Composable
fun HomeScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    val scrollState = rememberScrollState()
    var searchText by remember { mutableStateOf("") }
    val selectedItem = remember { mutableStateOf("Home") }

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
                            when (item) {
                                "Notif" -> {
                                    // navController.navigate("notification_route")
                                }
                                "Profil" -> {
                                    navController.navigate("profile")
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = item,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(text = item, fontSize = 10.sp)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Jika user pilih profil, tampilkan ProfileScreen
        if (selectedItem.value == "Profil") {
            ProfileScreen(navController, loginViewModel)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                HeaderSection(searchText) { searchText = it }
                CategorySection()
                RecommendedSection()
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                loginViewModel.logout()  // panggil fungsi logout di viewmodel
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true } // supaya backstack dihapus
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}

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
                    .size(100.dp)
                    .height(40.dp)
                    .padding(end = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = "Cart",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 12.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
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
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search__1_),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.objects_column),
                    contentDescription = "Filter",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        )
    }
}

@Composable
fun CategorySection() {
    val categories = listOf(
        Pair("Makanan", R.drawable.fish),
        Pair("Mainan", R.drawable.bowling_ball),
        Pair("Obat", R.drawable.first_aid_kit),
        Pair("Kebersihan", R.drawable.broom)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categories.forEach { (name, iconRes) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
fun RecommendedSection() {
    Text(
        text = "Recommended Food",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )

    val products = listOf(
        Triple("Josera Mini Deluxe", "900g", R.drawable.paws_corner_removebg_preview),
        Triple("Pedigree Chicken & Vege", "3kg", R.drawable.paws_corner_removebg_preview),
        Triple("BlackHawk Puppy Lamb", "20kg", R.drawable.paws_corner_removebg_preview),
        Triple("Royal Canin Labrador P", "3kg", R.drawable.paws_corner_removebg_preview)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(max = 500.dp)
    ) {
        items(products) { (title, weight, imageRes) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = title,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = weight, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { /* TODO: Tambahkan ke keranjang */ },
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
