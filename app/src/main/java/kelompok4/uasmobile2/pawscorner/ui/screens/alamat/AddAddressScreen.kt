package kelompok4.uasmobile2.pawscorner.ui.screens.alamat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable
import kelompok4.uasmobile2.pawscorner.data.Address
import kelompok4.uasmobile2.pawscorner.ui.components.AddressDropdown
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    addressId: String? = null,
    addressViewModel: AddressViewModel = viewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var province by rememberSaveable { mutableStateOf("") }
    var regency by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var village by rememberSaveable { mutableStateOf("") }
    var isPrimary by rememberSaveable { mutableStateOf(false) }

    // Store selected codes for API calls
    var provinceCode by rememberSaveable { mutableStateOf("") }
    var regencyCode by rememberSaveable { mutableStateOf("") }
    var districtCode by rememberSaveable { mutableStateOf("") }

    val provinces by addressViewModel.provinces.collectAsState()
    val regencies by addressViewModel.regencies.collectAsState()
    val districts by addressViewModel.districts.collectAsState()
    val villages by addressViewModel.villages.collectAsState()
    val addressList by addressViewModel.addresses.collectAsState()

    LaunchedEffect(addressId) {
        addressViewModel.loadProvinces()

        if (addressId != null) {
            val existingAddress = addressList.find { it.first == addressId }?.second
            existingAddress?.let {
                name = it.name
                address = it.address
                province = it.province
                regency = it.regency
                district = it.district
                village = it.village
                isPrimary = it.isPrimary

                // Find codes for existing data
                val selectedProvince = provinces.find { p -> p.name == it.province }
                selectedProvince?.let { prov ->
                    provinceCode = prov.code
                    addressViewModel.loadRegencies(prov.code)
                }
            }
        }
    }

    // Update regency when provinces change and we have existing data
    LaunchedEffect(regencies) {
        if (addressId != null && regency.isNotEmpty()) {
            val selectedRegency = regencies.find { r -> r.name == regency }
            selectedRegency?.let { reg ->
                regencyCode = reg.code
                addressViewModel.loadDistricts(reg.code)
            }
        }
    }

    // Update district when districts change and we have existing data
    LaunchedEffect(districts) {
        if (addressId != null && district.isNotEmpty()) {
            val selectedDistrict = districts.find { d -> d.name == district }
            selectedDistrict?.let { dist ->
                districtCode = dist.code
                addressViewModel.loadVillages(dist.code)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (addressId != null) "Edit Alamat" else "Tambah Alamat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            AddressDropdown(
                label = "Provinsi",
                value = province,
                options = provinces.map { it.name },
                onSelect = { selected ->
                    province = selected
                    // Find the province code from the selected name
                    val selectedProvince = provinces.find { it.name == selected }
                    selectedProvince?.let {
                        provinceCode = it.code
                        addressViewModel.loadRegencies(it.code) // Use code, not name
                    }
                    regency = ""
                    district = ""
                    village = ""
                    regencyCode = ""
                    districtCode = ""
                }
            )

            AddressDropdown(
                label = "Kabupaten",
                value = regency,
                options = regencies.map { it.name },
                onSelect = { selected ->
                    regency = selected
                    // Find the regency code from the selected name
                    val selectedRegency = regencies.find { it.name == selected }
                    selectedRegency?.let {
                        regencyCode = it.code
                        addressViewModel.loadDistricts(it.code) // Use code, not name
                    }
                    district = ""
                    village = ""
                    districtCode = ""
                }
            )

            AddressDropdown(
                label = "Kecamatan",
                value = district,
                options = districts.map { it.name },
                onSelect = { selected ->
                    district = selected
                    // Find the district code from the selected name
                    val selectedDistrict = districts.find { it.name == selected }
                    selectedDistrict?.let {
                        districtCode = it.code
                        addressViewModel.loadVillages(it.code) // Use code, not name
                    }
                    village = ""
                }
            )

            AddressDropdown(
                label = "Desa",
                value = village,
                options = villages.map { it.name },
                onSelect = { selected ->
                    village = selected
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = isPrimary, onCheckedChange = { isPrimary = it })
                Text("Jadikan sebagai alamat utama")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank() || address.isBlank() || province.isBlank() || regency.isBlank()) {
                        println("Mohon lengkapi semua data")
                        return@Button
                    }

                    val newAddress = Address(
                        name = name,
                        address = address,
                        province = province,
                        regency = regency,
                        district = district,
                        village = village,
                        isPrimary = isPrimary,
                        createdAt = if (addressId == null) Timestamp.now() else null,
                        updatedAt = Timestamp.now()
                    )

                    if (addressId != null) {
                        addressViewModel.updateAddress(addressId, newAddress)
                    } else {
                        addressViewModel.createAddress(newAddress)
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (addressId != null) "Update" else "Simpan")
            }
        }
    }
}
