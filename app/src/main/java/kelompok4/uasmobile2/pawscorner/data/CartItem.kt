package kelompok4.uasmobile2.pawscorner.data

import kelompok4.uasmobile2.pawscorner.R

data class CartItem(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val quantity: Int = 1,
    val weight: String = "",
    val imageRes: Int = R.drawable.paws_corner_removebg_preview
)