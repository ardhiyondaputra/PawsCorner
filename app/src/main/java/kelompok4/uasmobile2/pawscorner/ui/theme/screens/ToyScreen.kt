package kelompok4.uasmobile2.pawscorner.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kelompok4.uasmobile2.pawscorner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToysScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mainan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        val toys = listOf(
            Triple("Boneka Teddy", "30 cm", R.drawable.bowling_ball), // contoh icon bisa diganti
            Triple("Mobil Remote Control", "20 cm", R.drawable.bowling_ball),
            Triple("Puzzle Edukasi", "50 pcs", R.drawable.bowling_ball),
            Triple("Ball Set", "1 set", R.drawable.bowling_ball),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(innerPadding)
        ) {
            items(toys) { (name, size, imageRes) ->
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
                            contentDescription = name,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = name, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(text = size, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* TODO: Tambahkan ke keranjang */ },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.shopping_cart),
                                contentDescription = "Add to cart",
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
}
