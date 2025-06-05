package kelompok4.uasmobile2.pawscorner.network

import kelompok4.uasmobile2.pawscorner.data.DistrictResponse
import kelompok4.uasmobile2.pawscorner.data.ProvinceResponse
import kelompok4.uasmobile2.pawscorner.data.RegencyResponse
import kelompok4.uasmobile2.pawscorner.data.VillageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WilayahApiService {
    @GET("api/provinces.json")
    suspend fun getProvinces(): ProvinceResponse

    @GET("api/regencies/{provinceCode}.json")
    suspend fun getRegencies(@Path("provinceCode") provinceCode: String): RegencyResponse

    @GET("api/districts/{regencyCode}.json")
    suspend fun getDistricts(@Path("regencyCode") regencyCode: String): DistrictResponse

    @GET("api/villages/{districtCode}.json")
    suspend fun getVillages(@Path("districtCode") districtCode: String): VillageResponse
}