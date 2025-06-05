package kelompok4.uasmobile2.pawscorner.ui.screens.alamat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    navController: NavController,
    addressViewModel: AddressViewModel,
    addressId: String
) {
    val addresses by addressViewModel.addresses.collectAsState()
    val address = addresses.find { it.id == addressId }

    // Local form state
    var name by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }

    // Load addresses when entering screen
    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses()
    }

    // Once address is loaded, initialize the form
    LaunchedEffect(address) {
        if (address != null) {
            name = address.name
            detailAddress = address.address
            province = address.province
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Alamat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (address == null) {
            // Show loading state or not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Memuat data alamat...")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Alamat") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = detailAddress,
                    onValueChange = { detailAddress = it },
                    label = { Text("Detail Alamat") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("Provinsi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        address.copy(
                            name = name,
                            address = detailAddress,
                            province = province
                        ).let {
                            addressViewModel.updateAddress(it)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}
