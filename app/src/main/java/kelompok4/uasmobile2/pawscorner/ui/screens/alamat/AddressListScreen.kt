package kelompok4.uasmobile2.pawscorner.ui.screens.alamat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import kelompok4.uasmobile2.pawscorner.data.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    navController: NavController,
    addressViewModel: AddressViewModel
) {
    val addresses by addressViewModel.addresses.collectAsState()
    var selectedAddressId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alamat Anda") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("tambah_alamat") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Alamat")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada alamat.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 72.dp)
                ) {
                    items(addresses) { address ->
                        AddressItem(
                            address = address,
                            isSelected = selectedAddressId == address.id,
                            onSelected = { selectedAddressId = address.id },
                            onEdit = {
                                navController.navigate("edit_address/{addressId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelected() }
                )
                Text(
                    text = "Gunakan alamat ini",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = address.address,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Provinsi: ${address.province}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Alamat"
                    )
                }
            }
        }
    }
}
