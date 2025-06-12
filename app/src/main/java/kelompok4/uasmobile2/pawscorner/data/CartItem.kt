package kelompok4.uasmobile2.pawscorner.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    val id: String,
    val title: String,
    val price: String,
    val quantity: Int,
    val weight: String,
    val imageUrl: String // Ganti imageRes dengan imageUrl
) : Parcelable