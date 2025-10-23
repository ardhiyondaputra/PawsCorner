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
import com.google.firebase.Timestamp
import kelompok4.uasmobile2.pawscorner.data.Address
import kelompok4.uasmobile2.pawscorner.ui.components.*
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel

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

    // kode wilayah
    var provinceCode by rememberSaveable { mutableStateOf("") }
    var regencyCode by rememberSaveable { mutableStateOf("") }
    var districtCode by rememberSaveable { mutableStateOf("") }

    val provinces by addressViewModel.provinces.collectAsState()
    val regencies by addressViewModel.regencies.collectAsState()
    val districts by addressViewModel.districts.collectAsState()
    val villages by addressViewModel.villages.collectAsState()
    val addressList by addressViewModel.addresses.collectAsState()

    // state untuk popup konfirmasi
    var showPopup by remember { mutableStateOf(false) }
    var popupTitle by remember { mutableStateOf("") }
    var popupMessage by remember { mutableStateOf("") }

    // load data ketika edit
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

                provinces.find { p -> p.name == it.province }?.let { prov ->
                    provinceCode = prov.code
                    addressViewModel.loadRegencies(prov.code)
                }
            }
        }
    }

    LaunchedEffect(regencies) {
        if (addressId != null && regency.isNotEmpty()) {
            val selectedRegency = regencies.find { r -> r.name == regency }
            selectedRegency?.let {
                regencyCode = it.code
                addressViewModel.loadDistricts(it.code)
            }
        }
    }

    LaunchedEffect(districts) {
        if (addressId != null && district.isNotEmpty()) {
            val selectedDistrict = districts.find { d -> d.name == district }
            selectedDistrict?.let {
                districtCode = it.code
                addressViewModel.loadVillages(it.code)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (addressId != null) "Edit Alamat" else "Tambah Alamat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Gunakan CustomInputField
            CustomInputField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Nama Penerima"
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomInputField(
                value = address,
                onValueChange = { address = it },
                placeholder = "Alamat Lengkap"
            )
            Spacer(modifier = Modifier.height(8.dp))

            AddressDropdown(
                label = "Provinsi",
                value = province,
                options = provinces.map { it.name },
                onSelect = { selected ->
                    province = selected
                    provinces.find { it.name == selected }?.let {
                        provinceCode = it.code
                        addressViewModel.loadRegencies(it.code)
                    }
                    regency = ""
                    district = ""
                    village = ""
                }
            )
            AddressDropdown(
                label = "Kabupaten/Kota",
                value = regency,
                options = regencies.map { it.name },
                onSelect = { selected ->
                    regency = selected
                    regencies.find { it.name == selected }?.let {
                        regencyCode = it.code
                        addressViewModel.loadDistricts(it.code)
                    }
                    district = ""
                    village = ""
                }
            )
            AddressDropdown(
                label = "Kecamatan",
                value = district,
                options = districts.map { it.name },
                onSelect = { selected ->
                    district = selected
                    districts.find { it.name == selected }?.let {
                        districtCode = it.code
                        addressViewModel.loadVillages(it.code)
                    }
                    village = ""
                }
            )
            AddressDropdown(
                label = "Desa/Kelurahan",
                value = village,
                options = villages.map { it.name },
                onSelect = { village = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isPrimary,
                    onCheckedChange = { isPrimary = it }
                )
                Text("Jadikan sebagai alamat utama")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gunakan PrimaryButton
            PrimaryButton(
                text = if (addressId != null) "Update" else "Simpan",
                onClick = {
                    if (name.isBlank() || address.isBlank() || province.isBlank() || regency.isBlank()) {
                        popupTitle = "Data Tidak Lengkap"
                        popupMessage = "Mohon lengkapi semua data sebelum menyimpan."
                        showPopup = true
                        return@PrimaryButton
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
                        popupTitle = "Alamat Diperbarui"
                        popupMessage = "Alamat berhasil diperbarui."
                    } else {
                        addressViewModel.createAddress(newAddress)
                        popupTitle = "Alamat Disimpan"
                        popupMessage = "Alamat berhasil disimpan."
                    }

                    showPopup = true
                }
            )
        }

        // Gunakan CustomPopup untuk konfirmasi
        if (showPopup) {
            CustomPopup(
                title = popupTitle,
                message = popupMessage,
                onDismiss = { showPopup = false },
                onConfirm = {
                    showPopup = false
                    navController.popBackStack()
                },
                isError = false
            )
        }
    }
}
