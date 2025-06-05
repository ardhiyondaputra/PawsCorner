package kelompok4.uasmobile2.pawscorner.data

import com.google.gson.annotations.SerializedName

data class Address(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val province: String = "",
    val provinceCode: String = "",
    val regency: String = "",
    val regencyCode: String = "",
    val district: String = "",
    val districtCode: String = "",
    val village: String = "",
    val villageCode: String = "",
    val isDefault: Boolean = false
)

data class ProvinceResponse(
    val data: List<Province>, // Daftar provinsi
    val meta: Meta // Metadata
)

data class Province(
    val code: String, // Kode provinsi
    val name: String // Nama provinsi
)

data class RegencyResponse(
    val data: List<Regency> // Daftar kabupaten
)

data class Regency(
    val code: String, // Kode kabupaten
    val name: String // Nama kabupaten
)

data class DistrictResponse(
    val data: List<District> // Daftar kecamatan
)

data class District(
    val code: String, // Kode kecamatan
    val name: String, // Nama kecamatan
    @SerializedName("postal_code") val postalCode: String? // Kode pos (nullable)
)

data class VillageResponse(
    val data: List<Village> // Daftar desa
)

data class Village(
    val code: String, // Kode desa
    val name: String // Nama desa
)

// Metadata yang mengandung informasi administratif
data class Meta(
    @SerializedName("administrative_area_level") val administrativeAreaLevel: Int, // Tingkat wilayah administratif
    @SerializedName("updated_at") val updatedAt: String // Waktu pembaruan data
)