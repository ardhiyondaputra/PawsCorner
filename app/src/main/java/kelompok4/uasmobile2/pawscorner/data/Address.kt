package kelompok4.uasmobile2.pawscorner.data

import com.google.gson.annotations.SerializedName
import com.google.firebase.Timestamp

data class Address(
    val name: String = "",
    val address: String = "",
    val province: String = "",
    val regency: String = "",
    val district: String = "",
    val village: String = "",
    val isPrimary: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

// Wilayah response dari API EMSIFA
data class ProvinceResponse(
    val data: List<Province>,
    val meta: Meta
)

data class Province(
    val code: String,
    val name: String
)

data class RegencyResponse(
    val data: List<Regency>
)

data class Regency(
    val code: String,
    val name: String
)

data class DistrictResponse(
    val data: List<District>
)

data class District(
    val code: String,
    val name: String,
    @SerializedName("postal_code") val postalCode: String?
)

data class VillageResponse(
    val data: List<Village>
)

data class Village(
    val code: String,
    val name: String
)

data class Meta(
    @SerializedName("administrative_area_level") val administrativeAreaLevel: Int,
    @SerializedName("updated_at") val updatedAt: String
)