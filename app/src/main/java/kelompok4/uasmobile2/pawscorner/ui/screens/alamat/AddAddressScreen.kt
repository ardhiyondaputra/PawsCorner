package kelompok4.uasmobile2.pawscorner.ui.screens.alamat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.data.Address
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import kelompok4.uasmobile2.pawscorner.ui.components.AddressDropdown

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
    var provinceCode by rememberSaveable { mutableStateOf("") }
    var regency by rememberSaveable { mutableStateOf("") }
    var regencyCode by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var districtCode by rememberSaveable { mutableStateOf("") }
    var village by rememberSaveable { mutableStateOf("") }
    var villageCode by rememberSaveable { mutableStateOf("") }
    var isDefault by rememberSaveable { mutableStateOf(false) }

    val provinces by addressViewModel.provinces.collectAsState()
    val regencies by addressViewModel.regencies.collectAsState()
    val districts by addressViewModel.districts.collectAsState()
    val villages by addressViewModel.villages.collectAsState()

    LaunchedEffect(addressId) {
        addressViewModel.loadProvinces()

        if (addressId != null) {
            val existingAddress = addressViewModel.addresses.value.find { it.id == addressId }
            existingAddress?.let {
                name = it.name
                address = it.address
                province = it.province
                provinceCode = it.provinceCode
                regency = it.regency
                regencyCode = it.regencyCode
                district = it.district
                districtCode = it.districtCode
                village = it.village
                villageCode = it.villageCode
                isDefault = it.isDefault

                if (provinceCode.isNotEmpty()) addressViewModel.loadRegencies(provinceCode)
                if (regencyCode.isNotEmpty()) addressViewModel.loadDistricts(regencyCode)
                if (districtCode.isNotEmpty()) addressViewModel.loadVillages(districtCode)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
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
                provinceCode = provinces.find { it.name == selected }?.code ?: ""
                addressViewModel.loadRegencies(provinceCode)
                regency = ""
                regencyCode = ""
                district = ""
                districtCode = ""
                village = ""
                villageCode = ""
            }
        )

        AddressDropdown(
            label = "Kabupaten",
            value = regency,
            options = regencies.map { it.name },
            onSelect = { selected ->
                regency = selected
                regencyCode = regencies.find { it.name == selected }?.code ?: ""
                addressViewModel.loadDistricts(regencyCode)
                district = ""
                districtCode = ""
                village = ""
                villageCode = ""
            }
        )

        AddressDropdown(
            label = "Kecamatan",
            value = district,
            options = districts.map { it.name },
            onSelect = { selected ->
                district = selected
                districtCode = districts.find { it.name == selected }?.code ?: ""
                addressViewModel.loadVillages(districtCode)
                village = ""
                villageCode = ""
            }
        )

        AddressDropdown(
            label = "Desa",
            value = village,
            options = villages.map { it.name },
            onSelect = { selected ->
                village = selected
                villageCode = villages.find { it.name == selected }?.code ?: ""
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
            Text("Jadikan sebagai alamat utama")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isBlank() || address.isBlank() || province.isBlank() || regency.isBlank()) {
                    // Ganti dengan snackbar/toast jika ingin
                    println("Mohon lengkapi semua data")
                    return@Button
                }

                val newAddress = Address(
                    id = addressId ?: "",
                    name = name,
                    address = address,
                    province = province,
                    provinceCode = provinceCode,
                    regency = regency,
                    regencyCode = regencyCode,
                    district = district,
                    districtCode = districtCode,
                    village = village,
                    villageCode = villageCode,
                    isDefault = isDefault
                )

                if (addressId != null) {
                    addressViewModel.updateAddress(newAddress)
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
