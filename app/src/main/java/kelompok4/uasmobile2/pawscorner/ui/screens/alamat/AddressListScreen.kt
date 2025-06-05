package kelompok4.uasmobile2.pawscorner.ui.screens.alamat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
            CenterAlignedTopAppBar(
                title = { Text("Alamat Anda") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_address") },
                modifier = Modifier
                    .padding(bottom = 32.dp, end = 24.dp)
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
                    items(addresses) { (id, address) ->
                        AddressItem(
                            address = address,
                            isSelected = selectedAddressId == id,
                            onSelected = { selectedAddressId = id },
                            onEdit = { navController.navigate("edit_address/$id") },
                            onDeleteConfirmed = { addressViewModel.deleteAddress(id) }
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
    onEdit: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus alamat ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirmed()
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kabupaten/Kota: ${address.regency}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kecamatan: ${address.district}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kelurahan/Desa: ${address.village}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Alamat"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Alamat"
                        )
                    }
                }
            }
        }
    }
}
