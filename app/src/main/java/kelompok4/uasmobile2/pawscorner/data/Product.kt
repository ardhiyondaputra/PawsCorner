package kelompok4.uasmobile2.pawscorner.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val title: String,
    val weight: String,
    val category: String,
    val imageUrl: String,
    val price: String,
    val stock : Int,
    val description: String? = null,
    val documentId: String = ""
) : Parcelable
