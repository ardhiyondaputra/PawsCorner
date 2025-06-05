package kelompok4.uasmobile2.pawscorner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kelompok4.uasmobile2.pawscorner.data.Address
import kelompok4.uasmobile2.pawscorner.data.District
import kelompok4.uasmobile2.pawscorner.data.Province
import kelompok4.uasmobile2.pawscorner.data.Regency
import kelompok4.uasmobile2.pawscorner.data.Village
import kelompok4.uasmobile2.pawscorner.network.RetrofitClient
import kelompok4.uasmobile2.pawscorner.network.WilayahApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    // Dummy list address disimpan di memori
    private val addressList = mutableListOf<Address>()

    fun loadAddresses() {
        viewModelScope.launch {
            _addresses.value = addressList
        }
    }

    fun createAddress(address: Address) {
        viewModelScope.launch {
            addressList.add(address)
            loadAddresses()
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch {
            val index = addressList.indexOfFirst { it.id == address.id }
            if (index != -1) {
                addressList[index] = address
                loadAddresses()
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            addressList.removeIf { it.id == addressId }
            loadAddresses()
        }
    }

    // Untuk API wilayah tetap pakai Retrofit
    private val wilayahApi: WilayahApiService = RetrofitClient.instance.create(WilayahApiService::class.java)

    private val _provinces = MutableStateFlow<List<Province>>(emptyList())
    val provinces: StateFlow<List<Province>> = _provinces

    private val _regencies = MutableStateFlow<List<Regency>>(emptyList())
    val regencies: StateFlow<List<Regency>> = _regencies

    private val _districts = MutableStateFlow<List<District>>(emptyList())
    val districts: StateFlow<List<District>> = _districts

    private val _villages = MutableStateFlow<List<Village>>(emptyList())
    val villages: StateFlow<List<Village>> = _villages

    fun loadProvinces() {
        viewModelScope.launch {
            try {
                _provinces.value = wilayahApi.getProvinces().data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadRegencies(provinceCode: String) {
        viewModelScope.launch {
            _regencies.value = wilayahApi.getRegencies(provinceCode).data
        }
    }

    fun loadDistricts(regencyCode: String) {
        viewModelScope.launch {
            _districts.value = wilayahApi.getDistricts(regencyCode).data
        }
    }

    fun loadVillages(districtCode: String) {
        viewModelScope.launch {
            _villages.value = wilayahApi.getVillages(districtCode).data
        }
    }
}
