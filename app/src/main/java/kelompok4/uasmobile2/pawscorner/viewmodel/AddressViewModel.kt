package kelompok4.uasmobile2.pawscorner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kelompok4.uasmobile2.pawscorner.data.Address
import kelompok4.uasmobile2.pawscorner.data.District
import kelompok4.uasmobile2.pawscorner.data.Province
import kelompok4.uasmobile2.pawscorner.data.Regency
import kelompok4.uasmobile2.pawscorner.data.Village
import kelompok4.uasmobile2.pawscorner.network.RetrofitClient
import kelompok4.uasmobile2.pawscorner.network.RegionApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ID dokumen Firestore + isi Address
    private val _addresses = MutableStateFlow<List<Pair<String, Address>>>(emptyList())
    val addresses: StateFlow<List<Pair<String, Address>>> = _addresses

    fun loadAddresses() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("addresses")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val address = doc.toObject(Address::class.java)
                    if (address != null) {
                        doc.id to address
                    } else null
                }
                _addresses.value = list
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun createAddress(address: Address) {
        val userId = auth.currentUser?.uid ?: return
        val addressRef = firestore.collection("users").document(userId).collection("addresses").document()

        val now = Timestamp.now()
        val data = address.copy(
            createdAt = now,
            updatedAt = now
        )

        if (data.isPrimary) {
            // Jika isPrimary true, set semua lainnya jadi false dulu
            firestore.collection("users").document(userId).collection("addresses")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = firestore.batch()
                    for (doc in snapshot.documents) {
                        batch.update(doc.reference, "isPrimary", false)
                    }
                    batch.commit().addOnSuccessListener {
                        addressRef.set(data)
                            .addOnSuccessListener { loadAddresses() }
                    }
                }
        } else {
            addressRef.set(data)
                .addOnSuccessListener { loadAddresses() }
        }
    }

    fun updateAddress(addressId: String, address: Address) {
        val userId = auth.currentUser?.uid ?: return
        val updatedAddress = address.copy(updatedAt = Timestamp.now())

        if (updatedAddress.isPrimary) {
            firestore.collection("users").document(userId).collection("addresses")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = firestore.batch()
                    for (doc in snapshot.documents) {
                        batch.update(doc.reference, "isPrimary", false)
                    }
                    batch.commit().addOnSuccessListener {
                        firestore.collection("users").document(userId)
                            .collection("addresses").document(addressId)
                            .set(updatedAddress)
                            .addOnSuccessListener { loadAddresses() }
                    }
                }
        } else {
            firestore.collection("users").document(userId)
                .collection("addresses").document(addressId)
                .set(updatedAddress)
                .addOnSuccessListener { loadAddresses() }
        }
    }

    fun deleteAddress(addressId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .collection("addresses").document(addressId)
            .delete()
            .addOnSuccessListener { loadAddresses() }
    }

    // ===== Wilayah Indonesia (API) =====

    private val regionApi: RegionApiService = RetrofitClient.instance.create(RegionApiService::class.java)

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
                _provinces.value = regionApi.getProvinces().data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadRegencies(provinceCode: String) {
        viewModelScope.launch {
            try {
                _regencies.value = regionApi.getRegencies(provinceCode).data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadDistricts(regencyCode: String) {
        viewModelScope.launch {
            try {
                _districts.value = regionApi.getDistricts(regencyCode).data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadVillages(districtCode: String) {
        viewModelScope.launch {
            try {
                _villages.value = regionApi.getVillages(districtCode).data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}